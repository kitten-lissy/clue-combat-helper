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

        // Coordinate clues spawn wizards when you dig
        if (clue instanceof CoordinateClue) {
            WorldPoint location = getClueLocation(clue);
            boolean wildy = location != null && isWilderness(location);

            // Check if this is a master clue by looking at combat level indicators
            // Master coordinate clues use the "strange device" or have specific patterns
            // For now, we detect tier by checking if it's a CoordinateClue
            // RuneLite doesn't directly expose tier, so we use heuristics
            if (isMasterClue(clue)) {
                // Master: Brassican Mage (single) or Ancient Wizards (multi)
                // We can't easily detect single vs multicombat from clue data,
                // so show both possibilities
                if (wildy) {
                    currentEncounter = CombatEncounterData.ANCIENT_WIZARDS_WILDY;
                } else {
                    currentEncounter = CombatEncounterData.ANCIENT_WIZARDS;
                }
            } else {
                // Hard: Saradomin Wizard (non-wildy) or Zamorak Wizard (wildy)
                if (wildy) {
                    currentEncounter = CombatEncounterData.ZAMORAK_WIZARD;
                } else {
                    currentEncounter = CombatEncounterData.SARADOMIN_WIZARD;
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

        // Hot/Cold clues (master) spawn Brassican/Ancient Wizards
        if (clue instanceof HotColdClue) {
            currentEncounter = CombatEncounterData.ANCIENT_WIZARDS;
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

    private boolean isWilderness(WorldPoint point) {
        return point.getX() >= WILDERNESS_X_MIN && point.getX() <= WILDERNESS_X_MAX
                && point.getY() >= WILDERNESS_Y_MIN && point.getY() <= WILDERNESS_Y_MAX
                && point.getPlane() == 0;
    }

    /**
     * Heuristic to detect master clues. RuneLite doesn't directly expose tier,
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
