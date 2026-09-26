package com.cluecombathelper;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.Enemy;
import net.runelite.client.plugins.cluescrolls.clues.HotColdClue;
import net.runelite.client.plugins.cluescrolls.clues.LocationClueScroll;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import javax.inject.Inject;

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

    @Inject
    private Client client;

    @Inject
    private ClueScrollPlugin clueScrollPlugin;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ClueCombatHelperOverlay overlay;

    private CombatEncounterData currentEncounter = null;

    @Override
    protected void startUp() {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(overlay);
        currentEncounter = null;
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        updateEncounter();
    }

    private void updateEncounter() {
        currentEncounter = null;

        ClueScroll clue = clueScrollPlugin.getClue();
        if (clue == null) {
            return;
        }

        // Master hot/cold clues (strange device) spawn a Brassican Mage or Ancient Wizards.
        // Beginner hot/cold clues have no combat.
        if (clue instanceof HotColdClue) {
            if (isMasterHotCold((HotColdClue) clue)) {
                currentEncounter = CombatEncounterData.MASTER_COORDINATE;
            }
            return;
        }

        // Coordinate and emote clues carry their enemy directly
        Enemy enemy = clue.getEnemy();
        if (enemy == null) {
            return;
        }

        WorldPoint location = getClueLocation(clue);
        boolean wildy = location != null && isWilderness(location);
        currentEncounter = encounterFor(enemy, wildy);
    }

    /**
     * Maps the enemy RuneLite attaches to a clue step to the combat advice to show.
     */
    static CombatEncounterData encounterFor(Enemy enemy, boolean wildy) {
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
        }
        return null;
    }

    private WorldPoint getClueLocation(ClueScroll clue) {
        if (clue instanceof LocationClueScroll) {
            WorldPoint[] locations = ((LocationClueScroll) clue).getLocations(clueScrollPlugin);
            if (locations != null && locations.length > 0) {
                return locations[0];
            }
        }
        return null;
    }

    private boolean isPlayerNearLocation(WorldPoint location, int distance) {
        if (location == null) return false;
        WorldPoint playerPos = client.getLocalPlayer().getWorldLocation();
        return playerPos.distanceTo(location) <= distance;
    }

    private boolean isMulticombat() {
        return client.getVarbitValue(4605) == 1;
    }

    private boolean isWilderness(WorldPoint point) {
        return point.getX() >= WILDERNESS_X_MIN && point.getX() <= WILDERNESS_X_MAX
                && point.getY() >= WILDERNESS_Y_MIN && point.getY() <= WILDERNESS_Y_MAX
                && point.getPlane() == 0;
    }

    /**
     * Master hot/cold clues send you to Jorral; beginner ones send you to Reldo.
     */
    static boolean isMasterHotCold(HotColdClue clue) {
        String text = clue.getText();
        return text != null && !text.contains("Reldo");
    }

    public CombatEncounterData getCurrentEncounter() {
        return currentEncounter;
    }
}
