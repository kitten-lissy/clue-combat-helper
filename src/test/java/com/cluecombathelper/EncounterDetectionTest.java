package com.cluecombathelper;

import java.util.EnumSet;
import java.util.Set;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.CoordinateClue;
import net.runelite.client.plugins.cluescrolls.clues.EmoteClue;
import net.runelite.client.plugins.cluescrolls.clues.Enemy;
import net.runelite.client.plugins.cluescrolls.clues.HotColdClue;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class EncounterDetectionTest {
    private static final int MAX_ITEM_ID = 40000;

    @Test
    public void everyEnemyMapsToAnEncounter() {
        for (Enemy enemy : Enemy.values()) {
            assertNotNull(enemy.name(), ClueCombatHelperPlugin.encounterFor(enemy, false));
            assertNotNull(enemy.name(), ClueCombatHelperPlugin.encounterFor(enemy, true));
        }
    }

    @Test
    public void everyRealClueEnemyIsCovered() {
        Set<Enemy> seen = EnumSet.noneOf(Enemy.class);
        for (int id = 1; id < MAX_ITEM_ID; id++) {
            ClueScroll clue = CoordinateClue.forItemId(id);
            if (clue == null) {
                clue = EmoteClue.forItemId(id);
            }
            if (clue != null && clue.getEnemy() != null) {
                seen.add(clue.getEnemy());
                assertNotNull(ClueCombatHelperPlugin.encounterFor(clue.getEnemy(), false));
            }
        }
        // Hard, elite and master coordinate/emote clues all carry enemies in RuneLite's data
        assertTrue(seen.contains(Enemy.SARADOMIN_WIZARD));
        assertTrue(seen.contains(Enemy.ARMADYLEAN_OR_BANDOSIAN_GUARD));
        assertTrue(seen.contains(Enemy.DOUBLE_AGENT_108));
    }

    @Test
    public void masterEmoteClueShowsMasterDoubleAgent() {
        // Master clues share one item ID, so RuneLite identifies them by text
        EmoteClue clue = EmoteClue.forText("Flap at the death altar. Beware of double agents! "
            + "Equip a death tiara, a legend's cape and any ring of wealth.");
        assertNotNull(clue);
        assertEquals(Enemy.DOUBLE_AGENT_141, clue.getEnemy());
        assertEquals(CombatEncounterData.DOUBLE_AGENT_MASTER, ClueCombatHelperPlugin.encounterFor(clue.getEnemy(), false));
    }

    @Test
    public void doubleAgentTiers() {
        assertEquals(CombatEncounterData.DOUBLE_AGENT_HARD_WILDY, ClueCombatHelperPlugin.encounterFor(Enemy.DOUBLE_AGENT_65, true));
        assertEquals(CombatEncounterData.DOUBLE_AGENT_HARD, ClueCombatHelperPlugin.encounterFor(Enemy.DOUBLE_AGENT_108, false));
        assertEquals(CombatEncounterData.DOUBLE_AGENT_MASTER, ClueCombatHelperPlugin.encounterFor(Enemy.DOUBLE_AGENT_141, false));
    }

    @Test
    public void wildernessVariants() {
        assertEquals(CombatEncounterData.ELITE_GUARD, ClueCombatHelperPlugin.encounterFor(Enemy.ARMADYLEAN_OR_BANDOSIAN_GUARD, false));
        assertEquals(CombatEncounterData.ELITE_GUARD_WILDY, ClueCombatHelperPlugin.encounterFor(Enemy.ARMADYLEAN_OR_BANDOSIAN_GUARD, true));
        assertEquals(CombatEncounterData.MASTER_COORDINATE, ClueCombatHelperPlugin.encounterFor(Enemy.BRASSICAN_OR_WIZARDS, false));
        assertEquals(CombatEncounterData.MASTER_COORDINATE_WILDY, ClueCombatHelperPlugin.encounterFor(Enemy.BRASSICAN_OR_WIZARDS, true));
    }

    @Test
    public void hotColdTiers() {
        HotColdClue beginner = HotColdClue.forText(
            "Buried beneath the ground, who knows where it's found. Lucky for you, A man called Reldo may have a clue.", -1);
        HotColdClue master = HotColdClue.forText(
            "Buried beneath the ground, who knows where it's found. Lucky for you, A man called Jorral may have a clue.", -1);
        assertNotNull(beginner);
        assertNotNull(master);
        assertFalse(ClueCombatHelperPlugin.isMasterHotCold(beginner));
        assertTrue(ClueCombatHelperPlugin.isMasterHotCold(master));
    }

    @Test
    public void mediumEmoteCluesHaveNoEnemy() {
        int medium = 0;
        for (int id = 1; id < MAX_ITEM_ID; id++) {
            EmoteClue clue = EmoteClue.forItemId(id);
            if (clue != null && clue.getEnemy() == null) {
                medium++;
            }
        }
        // Beginner/easy/medium emote steps have no Double Agent, so no panel is shown
        assertTrue(medium > 0);
    }
}
