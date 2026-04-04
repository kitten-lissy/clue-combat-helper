package com.cluecombathelper;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.CoordinateClue;
import net.runelite.client.plugins.cluescrolls.clues.EmoteClue;
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

        // Coordinate clues spawn enemies when you dig
        if (clue instanceof CoordinateClue) {
            WorldPoint location = getClueLocation(clue);
            boolean wildy = location != null && isWilderness(location);
            String enemy = getEnemyName(clue);

            if (enemy != null) {
                switch (enemy) {
                    case "SARADOMIN_WIZARD":
                        currentEncounter = CombatEncounterData.SARADOMIN_WIZARD;
                        break;
                    case "ZAMORAK_WIZARD":
                        currentEncounter = CombatEncounterData.ZAMORAK_WIZARD;
                        break;
                    case "BRASSICAN_MAGE":
                        currentEncounter = CombatEncounterData.BRASSICAN_MAGE;
                        break;
                    case "ANCIENT_WIZARDS":
                        currentEncounter = CombatEncounterData.ANCIENT_WIZARDS;
                        break;
                    case "BRASSICAN_OR_WIZARDS":
                        currentEncounter = wildy
                                ? CombatEncounterData.MASTER_COORDINATE_WILDY
                                : CombatEncounterData.MASTER_COORDINATE;
                        break;
                    case "ARMADYLEAN_GUARD":
                        currentEncounter = CombatEncounterData.ARMADYLEAN_GUARD;
                        break;
                    case "BANDOSIAN_GUARD":
                        currentEncounter = CombatEncounterData.BANDOSIAN_GUARD;
                        break;
                    case "ARMADYLEAN_OR_BANDOSIAN_GUARD":
                        currentEncounter = wildy
                                ? CombatEncounterData.ELITE_GUARD_WILDY
                                : CombatEncounterData.ELITE_GUARD;
                        break;
                }
            } else {
                // Fallback if reflection fails — use tier-based detection
                String tier = getCoordinateClueTier(clue);
                if ("master".equals(tier)) {
                    currentEncounter = wildy
                            ? CombatEncounterData.MASTER_COORDINATE_WILDY
                            : CombatEncounterData.MASTER_COORDINATE;
                } else if ("elite".equals(tier)) {
                    currentEncounter = wildy
                            ? CombatEncounterData.ELITE_GUARD_WILDY
                            : CombatEncounterData.ELITE_GUARD;
                } else if ("hard".equals(tier)) {
                    currentEncounter = wildy
                            ? CombatEncounterData.ZAMORAK_WIZARD
                            : CombatEncounterData.SARADOMIN_WIZARD;
                }
            }
        }

        // Emote clues spawn Double Agents
        if (clue instanceof EmoteClue) {
            WorldPoint location = getClueLocation(clue);
            boolean wildy = location != null && isWilderness(location);

            if (isMasterClue(clue)) {
                currentEncounter = CombatEncounterData.DOUBLE_AGENT_MASTER;
            } else {
                if (wildy) {
                    currentEncounter = CombatEncounterData.DOUBLE_AGENT_HARD_WILDY;
                } else {
                    currentEncounter = CombatEncounterData.DOUBLE_AGENT_HARD;
                }
            }
        }

        // Hot/Cold clues (strange device / locator orb) — show combat prep
        if (clue instanceof HotColdClue) {
            currentEncounter = CombatEncounterData.MASTER_COORDINATE;
        }
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

    /**
     * Read the enemy field from CoordinateClue via reflection.
     * Returns the enum name (e.g. "BRASSICAN_MAGE", "ARMADYLEAN_OR_BANDOSIAN_GUARD").
     */
    private String getEnemyName(ClueScroll clue) {
        try {
            java.lang.reflect.Field f = clue.getClass().getDeclaredField("enemy");
            f.setAccessible(true);
            Object enemy = f.get(clue);
            return enemy != null ? enemy.toString() : null;
        } catch (Exception e) {
            return null;
        }
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
     * Detect coordinate clue tier from the itemId field.
     * Item IDs contain MEDIUM, HARD, ELITE, or MASTER in their names.
     */
    private String getCoordinateClueTier(ClueScroll clue) {
        try {
            java.lang.reflect.Field f = clue.getClass().getDeclaredField("itemId");
            f.setAccessible(true);
            int itemId = f.getInt(clue);
            // Check RuneLite ItemID name patterns via known ID ranges
            // Use reflection to check the item name from ItemID constants
            String itemName = getItemIdName(itemId);
            if (itemName != null) {
                String lower = itemName.toLowerCase();
                if (lower.contains("master")) return "master";
                if (lower.contains("elite")) return "elite";
                if (lower.contains("hard")) return "hard";
                if (lower.contains("medium")) return "medium";
            }
        } catch (Exception ignored) {
        }
        // Fallback: check if it's a master clue via other means
        if (isMasterClue(clue)) return "master";
        return "hard"; // default assumption
    }

    /**
     * Try to find the ItemID constant name for a given ID value.
     */
    private String getItemIdName(int itemId) {
        try {
            for (java.lang.reflect.Field f : net.runelite.api.ItemID.class.getDeclaredFields()) {
                if (f.getType() == int.class && f.getInt(null) == itemId) {
                    return f.getName();
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * but master coordinate clues use the strange device, and master emote clues
     * have higher-tier requirements. We check the clue class name for hints.
     */
    private boolean isMasterClue(ClueScroll clue) {
        // CoordinateClue has a challengeText field for master clues
        // EmoteClue for masters typically has specific item requirements
        // This is a best-effort detection
        String className = clue.getClass().getSimpleName();
        if (clue instanceof CoordinateClue) {
            // Master coordinate clues often have specific challenge text
            // We can try to access this via reflection or known patterns
            try {
                java.lang.reflect.Method m = clue.getClass().getMethod("getChallengeText");
                Object result = m.invoke(clue);
                if (result != null) {
                    return true; // Has challenge text = master
                }
            } catch (Exception e) {
                // Not available, fall through
            }
        }
        if (clue instanceof HotColdClue) {
            return true; // Hot/cold is always master
        }
        return false;
    }

    public CombatEncounterData getCurrentEncounter() {
        return currentEncounter;
    }
}
