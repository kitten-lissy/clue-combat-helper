package com.cluecombathelper;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.EnumID;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.NPC;
import net.runelite.api.ParamID;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.StructComposition;
import net.runelite.api.WorldType;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemEquipmentStats;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStats;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.ClueScrollService;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.EmoteClue;
import net.runelite.client.plugins.cluescrolls.clues.Enemy;
import net.runelite.client.plugins.cluescrolls.clues.HotColdClue;
import net.runelite.client.plugins.cluescrolls.clues.LocationClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.hotcold.HotColdLocation;
import net.runelite.client.plugins.cluescrolls.clues.hotcold.HotColdSolver;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
        name = "Clue Combat Helper",
        description = "Shows combat advice for treasure trail encounters",
        tags = {"clue", "combat", "helper", "treasure-trail"}
)
@PluginDependency(ClueScrollPlugin.class)
public class ClueCombatHelperPlugin extends Plugin {

    private static final int WILDERNESS_Y_MIN = 3525;
    private static final int WILDERNESS_Y_MAX = 3968;
    private static final int WILDERNESS_X_MIN = 2944;
    private static final int WILDERNESS_X_MAX = 3392;

    /** What kind of clue step the encounter belongs to; decides the after-kill reminder. */
    enum StepKind {
        COORDINATE,
        EMOTE,
        HOT_COLD
    }

    @Inject
    private Client client;

    @Inject
    private ClueScrollService clueScrollService;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ClueCombatHelperOverlay overlay;

    @Inject
    private ClueCombatHelperConfig config;

    @Inject
    private ItemManager itemManager;

    // Clue step state, rebuilt when the clue (or the hot/cold candidate list) changes
    private ClueScroll lastClue;
    private int lastHotColdCandidates = -1;
    private CombatEncounterData currentEncounter;
    private String displayName;
    private String triggerLabel;
    private StepKind stepKind;
    private boolean wildernessStep;
    private boolean maybeWilderness;
    private List<WorldPoint> spots = Collections.emptyList();

    // Fight tracking
    private final Set<NPC> candidates = new HashSet<>();
    private final Set<NPC> engaged = new HashSet<>();
    private boolean enemySpawned;
    private boolean enemyKilled;

    // Per-tick player state
    private boolean playerInWilderness;
    private boolean pvpWorld;
    private boolean nearSpot;
    private boolean lowHp;
    private boolean weaponWarning;
    private boolean noFlyerWeaponWarning;

    // Inventory state (updated when inventory or equipment changes)
    private int foodCount;
    private boolean hasAntipoison;
    private boolean hasWildernessTeleport;
    private boolean hasFlyerWeaponInInventory;

    @Provides
    ClueCombatHelperConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ClueCombatHelperConfig.class);
    }

    @Override
    protected void startUp() {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(overlay);
        resetStep();
        lastClue = null;
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        updateEncounter();
        if (currentEncounter == null) {
            return;
        }
        updatePlayerState();
        updateFight();
        updateWeaponWarning();
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        int id = event.getContainerId();
        if (id == InventoryID.INV || id == InventoryID.WORN) {
            updateInventory();
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        if (currentEncounter != null && currentEncounter.matchesNpcName(event.getNpc().getName())) {
            candidates.add(event.getNpc());
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        NPC npc = event.getNpc();
        candidates.remove(npc);
        if (engaged.contains(npc) && npc.isDead()) {
            onEngagedDeath(npc);
        }
        engaged.remove(npc);
    }

    @Subscribe
    public void onActorDeath(ActorDeath event) {
        Actor actor = event.getActor();
        if (actor instanceof NPC && engaged.contains(actor)) {
            onEngagedDeath((NPC) actor);
        }
    }

    private void onEngagedDeath(NPC npc) {
        engaged.remove(npc);
        candidates.remove(npc);
        for (NPC other : engaged) {
            if (!other.isDead()) {
                return;
            }
        }
        enemyKilled = true;
    }

    // ---- Clue step ----

    private void updateEncounter() {
        ClueScroll clue = clueScrollService.getClue();
        if (clue != lastClue) {
            lastClue = clue;
            resetStep();
            buildEncounter(clue);
            updateInventory();
        } else if (clue instanceof HotColdClue && currentEncounter != null) {
            // The hot/cold solver narrows its candidate spots as the device is used
            HotColdSolver solver = ((HotColdClue) clue).getHotColdSolver();
            int size = solver == null ? -1 : solver.getPossibleLocations().size();
            if (size != lastHotColdCandidates) {
                buildEncounter(clue);
            }
        }
    }

    private void resetStep() {
        currentEncounter = null;
        displayName = null;
        triggerLabel = null;
        stepKind = null;
        wildernessStep = false;
        maybeWilderness = false;
        spots = Collections.emptyList();
        lastHotColdCandidates = -1;
        candidates.clear();
        engaged.clear();
        enemySpawned = false;
        enemyKilled = false;
        weaponWarning = false;
        noFlyerWeaponWarning = false;
        lowHp = false;
        nearSpot = false;
    }

    private void buildEncounter(ClueScroll clue) {
        if (clue == null) {
            return;
        }

        if (clue instanceof HotColdClue) {
            HotColdClue hotCold = (HotColdClue) clue;
            // Beginner hot/cold clues (Reldo) have no combat
            if (!isMasterHotCold(hotCold)) {
                return;
            }
            Collection<HotColdLocation> possible = masterLocations(hotCold);
            lastHotColdCandidates = hotCold.getHotColdSolver() == null
                    ? -1 : hotCold.getHotColdSolver().getPossibleLocations().size();

            boolean allWild = allInWilderness(possible);
            Enemy enemy = enemyForHotCold(possible);
            CombatEncounterData data = encounterFor(enemy, allWild);
            if (data == null) {
                return;
            }
            // Keep the specific enemy if the fight has already shown which one it is
            if (!enemySpawned || currentEncounter == null) {
                currentEncounter = data;
                displayName = displayNameFor(data, enemy);
            }
            stepKind = StepKind.HOT_COLD;
            wildernessStep = allWild;
            // Only mention a possible Wilderness spot once the device has narrowed things down
            maybeWilderness = !allWild && anyInWilderness(possible)
                    && possible.size() < masterLocationCount();
            triggerLabel = "Hot/cold" + (allWild ? " (wilderness)" : "");
            List<WorldPoint> points = new ArrayList<>();
            for (HotColdLocation location : possible) {
                points.add(location.getWorldPoint());
            }
            spots = points;
            return;
        }

        // Coordinate and emote clues carry their enemy directly
        Enemy enemy = clue.getEnemy();
        if (enemy == null) {
            return;
        }

        WorldPoint location = getClueLocation(clue);
        boolean wildy = location != null && isWilderness(location);
        CombatEncounterData data = encounterFor(enemy, wildy);
        if (data == null) {
            return;
        }
        currentEncounter = data;
        displayName = displayNameFor(data, enemy);
        stepKind = clue instanceof EmoteClue ? StepKind.EMOTE : StepKind.COORDINATE;
        wildernessStep = wildy || data.isWilderness();
        triggerLabel = data == CombatEncounterData.UNKNOWN ? "" : data.getTriggerType();
        spots = location == null ? Collections.<WorldPoint>emptyList() : Collections.singletonList(location);
    }

    private static String displayNameFor(CombatEncounterData data, Enemy enemy) {
        // A future RuneLite enemy we don't know yet still gets a name from RuneLite itself
        if (data == CombatEncounterData.UNKNOWN || data.getMonsterName().isEmpty()) {
            return enemy == null ? "Clue enemy" : enemy.getText();
        }
        return data.getMonsterName();
    }

    /**
     * Maps the enemy RuneLite attaches to a clue step to the combat advice to show.
     */
    static CombatEncounterData encounterFor(Enemy enemy, boolean wildy) {
        if (enemy == null) {
            return null;
        }
        switch (enemy) {
            case SARADOMIN_WIZARD:
                return CombatEncounterData.SARADOMIN_WIZARD;
            case ZAMORAK_WIZARD:
                return CombatEncounterData.ZAMORAK_WIZARD;
            case DOUBLE_AGENT_65:
                return CombatEncounterData.DOUBLE_AGENT_HARD_WILDY;
            case DOUBLE_AGENT_108:
                return CombatEncounterData.DOUBLE_AGENT_HARD;
            case DOUBLE_AGENT_141:
                return CombatEncounterData.DOUBLE_AGENT_MASTER;
            case ARMADYLEAN_GUARD:
                return CombatEncounterData.ARMADYLEAN_GUARD;
            case BANDOSIAN_GUARD:
                return CombatEncounterData.BANDOSIAN_GUARD;
            case ARMADYLEAN_OR_BANDOSIAN_GUARD:
                return wildy
                        ? CombatEncounterData.ELITE_GUARD_WILDY
                        : CombatEncounterData.ELITE_GUARD;
            case BRASSICAN_MAGE:
                return CombatEncounterData.BRASSICAN_MAGE;
            case ANCIENT_WIZARDS:
                return CombatEncounterData.ANCIENT_WIZARDS;
            case BRASSICAN_OR_WIZARDS:
                return wildy
                        ? CombatEncounterData.MASTER_COORDINATE_WILDY
                        : CombatEncounterData.MASTER_COORDINATE;
            default:
                return CombatEncounterData.UNKNOWN;
        }
    }

    /**
     * Once an either/or enemy has spawned, switch to the advice for the one that actually appeared.
     */
    static CombatEncounterData specificEncounterForNpc(CombatEncounterData current, String npcName) {
        if (npcName == null) {
            return current;
        }
        if (current == CombatEncounterData.ELITE_GUARD || current == CombatEncounterData.ELITE_GUARD_WILDY) {
            if (CombatEncounterData.ARMADYLEAN_GUARD.matchesNpcName(npcName)) {
                return CombatEncounterData.ARMADYLEAN_GUARD;
            }
            if (CombatEncounterData.BANDOSIAN_GUARD.matchesNpcName(npcName)) {
                return CombatEncounterData.BANDOSIAN_GUARD;
            }
        }
        if (current == CombatEncounterData.MASTER_COORDINATE || current == CombatEncounterData.MASTER_COORDINATE_WILDY) {
            if (CombatEncounterData.BRASSICAN_MAGE.matchesNpcName(npcName)) {
                return CombatEncounterData.BRASSICAN_MAGE;
            }
            if (CombatEncounterData.ANCIENT_WIZARDS.matchesNpcName(npcName)) {
                return CombatEncounterData.ANCIENT_WIZARDS;
            }
        }
        return current;
    }

    // ---- Master hot/cold ----

    /**
     * Master hot/cold clues send you to Jorral; beginner ones send you to Reldo.
     */
    static boolean isMasterHotCold(HotColdClue clue) {
        return !clue.isBeginner();
    }

    private static Collection<HotColdLocation> masterLocations(HotColdClue clue) {
        HotColdSolver solver = clue.getHotColdSolver();
        Collection<HotColdLocation> source = solver == null
                ? EnumSet.allOf(HotColdLocation.class)
                : solver.getPossibleLocations();
        List<HotColdLocation> result = new ArrayList<>();
        for (HotColdLocation location : source) {
            if (!location.isBeginnerClue()) {
                result.add(location);
            }
        }
        return result;
    }

    private static int masterLocationCount() {
        int count = 0;
        for (HotColdLocation location : HotColdLocation.values()) {
            if (!location.isBeginnerClue()) {
                count++;
            }
        }
        return count;
    }

    /**
     * The enemy for a set of possible master hot/cold spots: the shared enemy if every spot
     * agrees, otherwise "Brassican Mage or Ancient Wizards".
     */
    static Enemy enemyForHotCold(Collection<HotColdLocation> possible) {
        Enemy shared = null;
        for (HotColdLocation location : possible) {
            Enemy enemy = location.getEnemy();
            if (enemy == null) {
                return Enemy.BRASSICAN_OR_WIZARDS;
            }
            if (shared == null) {
                shared = enemy;
            } else if (shared != enemy) {
                return Enemy.BRASSICAN_OR_WIZARDS;
            }
        }
        return shared == null ? Enemy.BRASSICAN_OR_WIZARDS : shared;
    }

    static boolean allInWilderness(Collection<HotColdLocation> possible) {
        if (possible.isEmpty()) {
            return false;
        }
        for (HotColdLocation location : possible) {
            if (!isWilderness(location.getWorldPoint())) {
                return false;
            }
        }
        return true;
    }

    static boolean anyInWilderness(Collection<HotColdLocation> possible) {
        for (HotColdLocation location : possible) {
            if (isWilderness(location.getWorldPoint())) {
                return true;
            }
        }
        return false;
    }

    // ---- Locations ----

    private WorldPoint getClueLocation(ClueScroll clue) {
        if (clue instanceof LocationClueScroll) {
            // RuneLite only shares ClueScrollService, not the plugin. Coordinate and emote
            // clue locations don't need it; anything that does is skipped safely.
            try {
                WorldPoint[] locations = ((LocationClueScroll) clue).getLocations(null);
                if (locations != null && locations.length > 0) {
                    return locations[0];
                }
            } catch (RuntimeException e) {
                return null;
            }
        }
        return null;
    }

    private boolean isPlayerNearLocation(WorldPoint location, int distance) {
        Player player = client.getLocalPlayer();
        if (location == null || player == null) {
            return false;
        }
        WorldPoint playerPos = player.getWorldLocation();
        return playerPos != null && playerPos.distanceTo2D(location) <= distance;
    }

    static boolean isWilderness(WorldPoint point) {
        return point.getX() >= WILDERNESS_X_MIN && point.getX() <= WILDERNESS_X_MAX
                && point.getY() >= WILDERNESS_Y_MIN && point.getY() <= WILDERNESS_Y_MAX
                && point.getPlane() == 0;
    }

    // ---- Per-tick state ----

    private void updatePlayerState() {
        playerInWilderness = client.getVarbitValue(VarbitID.INSIDE_WILDERNESS) == 1;
        pvpWorld = client.getWorldType().contains(WorldType.PVP);

        boolean near = false;
        for (WorldPoint spot : spots) {
            if (isPlayerNearLocation(spot, config.nearDistance())) {
                near = true;
                break;
            }
        }
        nearSpot = near;

        lowHp = stepKind == StepKind.HOT_COLD
                && client.getBoostedSkillLevel(Skill.HITPOINTS) <= config.lowHpThreshold();
    }

    private void updateFight() {
        Player local = client.getLocalPlayer();
        if (local == null) {
            return;
        }
        for (NPC npc : candidates) {
            if (engaged.contains(npc) || npc.isDead()) {
                continue;
            }
            if (npc.getInteracting() == local || local.getInteracting() == npc) {
                engaged.add(npc);
                enemySpawned = true;
                enemyKilled = false;
                CombatEncounterData specific = specificEncounterForNpc(currentEncounter, npc.getName());
                if (specific != currentEncounter) {
                    currentEncounter = specific;
                    displayName = specific.getMonsterName();
                }
            }
        }
    }

    private void updateWeaponWarning() {
        weaponWarning = false;
        noFlyerWeaponWarning = false;
        if (currentEncounter == CombatEncounterData.ARMADYLEAN_GUARD) {
            weaponWarning = equippedWeaponCanHitFlyer() == Boolean.FALSE;
        } else if (currentEncounter == CombatEncounterData.ELITE_GUARD
                || currentEncounter == CombatEncounterData.ELITE_GUARD_WILDY) {
            noFlyerWeaponWarning = equippedWeaponCanHitFlyer() != Boolean.TRUE && !hasFlyerWeaponInInventory;
        }
    }

    /**
     * Whether the equipped weapon can hit a flying monster: TRUE for ranged, magic, halberds
     * and salamanders, FALSE for other melee weapons and fists, null when unsure (no warning).
     * Uses the game's own weapon style data for the equipped weapon type, as the RuneLite
     * attack styles plugin does.
     */
    private Boolean equippedWeaponCanHitFlyer() {
        ItemContainer worn = client.getItemContainer(InventoryID.WORN);
        if (worn != null) {
            Item weapon = worn.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
            if (weapon != null && weapon.getId() > 0
                    && ItemNames.canHitFlyerByName(itemManager.getItemComposition(weapon.getId()).getName())) {
                return Boolean.TRUE;
            }
        }

        int weaponType = client.getVarbitValue(VarbitID.COMBAT_WEAPON_CATEGORY);
        EnumComposition weaponStyles = client.getEnum(EnumID.WEAPON_STYLES);
        if (weaponStyles == null) {
            return null;
        }
        int stylesEnum = weaponStyles.getIntValue(weaponType);
        if (stylesEnum == -1) {
            return null;
        }
        EnumComposition styles = client.getEnum(stylesEnum);
        if (styles == null) {
            return null;
        }
        int[] structs = styles.getIntVals();
        // Melee weapons have at most four styles; more means autocast slots (a magic weapon)
        if (structs.length > 4) {
            return Boolean.TRUE;
        }
        boolean sawMelee = false;
        boolean sawUnknown = false;
        for (int structId : structs) {
            StructComposition struct = client.getStructComposition(structId);
            Boolean reaches = struct == null ? null
                    : ItemNames.styleReaches(struct.getStringValue(ParamID.ATTACK_STYLE_NAME));
            if (reaches == null) {
                sawUnknown = true;
            } else if (reaches) {
                return Boolean.TRUE;
            } else {
                sawMelee = true;
            }
        }
        return sawMelee && !sawUnknown ? Boolean.FALSE : null;
    }

    // ---- Inventory ----

    private void updateInventory() {
        int food = 0;
        boolean antipoison = false;
        boolean teleport = false;
        boolean flyerWeapon = false;

        ItemContainer inventory = client.getItemContainer(InventoryID.INV);
        if (inventory != null) {
            for (Item item : inventory.getItems()) {
                if (item == null || item.getId() <= 0) {
                    continue;
                }
                ItemComposition comp = itemManager.getItemComposition(item.getId());
                if (comp.getNote() != -1) {
                    continue; // noted items can't be used
                }
                String name = comp.getName();
                if (isEdible(comp)) {
                    food += Math.max(1, item.getQuantity());
                }
                antipoison |= ItemNames.isAntipoison(name);
                teleport |= ItemNames.isWildernessTeleport(name);
                flyerWeapon |= ItemNames.canHitFlyerByName(name) || isRangedOrMagicWeapon(item.getId());
            }
        }

        ItemContainer worn = client.getItemContainer(InventoryID.WORN);
        if (worn != null) {
            for (Item item : worn.getItems()) {
                if (item == null || item.getId() <= 0) {
                    continue;
                }
                String name = itemManager.getItemComposition(item.getId()).getName();
                antipoison |= ItemNames.isPoisonImmunityHelm(name);
                teleport |= ItemNames.isWildernessTeleport(name);
            }
        }

        foodCount = food;
        hasAntipoison = antipoison;
        hasWildernessTeleport = teleport;
        hasFlyerWeaponInInventory = flyerWeapon;
    }

    private static boolean isEdible(ItemComposition comp) {
        String[] actions = comp.getInventoryActions();
        if (actions == null) {
            return false;
        }
        for (String action : actions) {
            if ("Eat".equalsIgnoreCase(action)) {
                return true;
            }
        }
        return false;
    }

    /** A weapon-slot item with a ranged or magic attack bonus, e.g. a bow, crossbow, staff or wand. */
    private boolean isRangedOrMagicWeapon(int itemId) {
        ItemStats stats = itemManager.getItemStats(itemId);
        if (stats == null || !stats.isEquipable() || stats.getEquipment() == null) {
            return false;
        }
        ItemEquipmentStats eq = stats.getEquipment();
        return eq.getSlot() == EquipmentInventorySlot.WEAPON.getSlotIdx()
                && (eq.getArange() > 0 || eq.getAmagic() > 0);
    }

    // ---- Overlay accessors ----

    public CombatEncounterData getCurrentEncounter() {
        return currentEncounter;
    }

    String getDisplayName() {
        return displayName;
    }

    String getTriggerLabel() {
        return triggerLabel;
    }

    StepKind getStepKind() {
        return stepKind;
    }

    boolean isWildernessStep() {
        return wildernessStep;
    }

    boolean isMaybeWilderness() {
        return maybeWilderness;
    }

    boolean isPlayerInWilderness() {
        return playerInWilderness;
    }

    boolean isPvpWorld() {
        return pvpWorld;
    }

    boolean isNearSpot() {
        return nearSpot;
    }

    boolean isEnemySpawned() {
        return enemySpawned;
    }

    boolean isEnemyKilled() {
        return enemyKilled;
    }

    boolean isLowHp() {
        return lowHp;
    }

    boolean isWeaponWarning() {
        return weaponWarning;
    }

    boolean isNoFlyerWeaponWarning() {
        return noFlyerWeaponWarning;
    }

    int getFoodCount() {
        return foodCount;
    }

    boolean hasAntipoison() {
        return hasAntipoison;
    }

    boolean hasWildernessTeleport() {
        return hasWildernessTeleport;
    }
}
