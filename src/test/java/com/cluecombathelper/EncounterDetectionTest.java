package com.cluecombathelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.runelite.client.plugins.cluescrolls.clues.ClueScroll;
import net.runelite.client.plugins.cluescrolls.clues.CoordinateClue;
import net.runelite.client.plugins.cluescrolls.clues.EmoteClue;
import net.runelite.client.plugins.cluescrolls.clues.Enemy;
import net.runelite.client.plugins.cluescrolls.clues.HotColdClue;
import net.runelite.client.plugins.cluescrolls.clues.hotcold.HotColdLocation;
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
    public void doubleAgentLevelsMatchTheWiki() {
        // Wiki: level 108 outside the Wilderness, 65 inside, 141 on master clues
        assertEquals("108", ClueCombatHelperPlugin.encounterFor(Enemy.DOUBLE_AGENT_108, false).getLevelText());
        assertEquals("65", ClueCombatHelperPlugin.encounterFor(Enemy.DOUBLE_AGENT_65, true).getLevelText());
        assertEquals("141", ClueCombatHelperPlugin.encounterFor(Enemy.DOUBLE_AGENT_141, false).getLevelText());
        assertFalse(ClueCombatHelperPlugin.encounterFor(Enemy.DOUBLE_AGENT_108, false).isWilderness());
        assertTrue(ClueCombatHelperPlugin.encounterFor(Enemy.DOUBLE_AGENT_65, true).isWilderness());
        assertTrue(CombatEncounterData.DOUBLE_AGENT_HARD.getTip().contains("Max hit 14"));
        assertTrue(CombatEncounterData.DOUBLE_AGENT_HARD_WILDY.getTip().contains("Max hit 5"));
    }

    private static List<HotColdLocation> masterLocations() {
        List<HotColdLocation> result = new ArrayList<>();
        for (HotColdLocation location : HotColdLocation.values()) {
            if (!location.isBeginnerClue()) {
                result.add(location);
            }
        }
        return result;
    }

    @Test
    public void hotColdEnemyFollowsPossibleSpots() {
        // Every master spot has an enemy in RuneLite's data
        for (HotColdLocation location : masterLocations()) {
            assertNotNull(location.name(), location.getEnemy());
        }

        // Before the device narrows things down it could be either enemy
        assertEquals(Enemy.BRASSICAN_OR_WIZARDS, ClueCombatHelperPlugin.enemyForHotCold(masterLocations()));

        // One spot left: that spot's enemy
        assertEquals(Enemy.ANCIENT_WIZARDS, ClueCombatHelperPlugin.enemyForHotCold(
                Arrays.asList(HotColdLocation.WILDERNESS_5)));
        assertEquals(Enemy.BRASSICAN_MAGE, ClueCombatHelperPlugin.enemyForHotCold(
                Arrays.asList(HotColdLocation.MISTHALIN_DRAYNOR_BANK, HotColdLocation.MISTHALIN_LUMBER_YARD)));

        // Spots that disagree fall back to the either/or entry
        assertEquals(Enemy.BRASSICAN_OR_WIZARDS, ClueCombatHelperPlugin.enemyForHotCold(
                Arrays.asList(HotColdLocation.WILDERNESS_5, HotColdLocation.WILDERNESS_27)));
    }

    @Test
    public void hotColdEncounterUsesSpecificEntries() {
        assertEquals(CombatEncounterData.ANCIENT_WIZARDS, ClueCombatHelperPlugin.encounterFor(
                ClueCombatHelperPlugin.enemyForHotCold(Arrays.asList(HotColdLocation.ZEAH_LIZARDMAN)), false));
        assertEquals(CombatEncounterData.MASTER_COORDINATE_WILDY, ClueCombatHelperPlugin.encounterFor(
                ClueCombatHelperPlugin.enemyForHotCold(Arrays.asList(HotColdLocation.WILDERNESS_5, HotColdLocation.WILDERNESS_27)), true));
    }

    @Test
    public void hotColdWildernessSpotsAreFlagged() {
        for (HotColdLocation location : masterLocations()) {
            boolean named = location.name().startsWith("WILDERNESS_");
            assertEquals(location.name(), named, ClueCombatHelperPlugin.isWilderness(location.getWorldPoint()));
        }
        assertTrue(ClueCombatHelperPlugin.allInWilderness(
                Arrays.asList(HotColdLocation.WILDERNESS_5, HotColdLocation.WILDERNESS_54)));
        assertFalse(ClueCombatHelperPlugin.allInWilderness(
                Arrays.asList(HotColdLocation.WILDERNESS_5, HotColdLocation.MISTHALIN_DRAYNOR_BANK)));
        assertTrue(ClueCombatHelperPlugin.anyInWilderness(
                Arrays.asList(HotColdLocation.WILDERNESS_5, HotColdLocation.MISTHALIN_DRAYNOR_BANK)));
        assertFalse(ClueCombatHelperPlugin.allInWilderness(new ArrayList<HotColdLocation>()));
    }

    @Test
    public void spawnedEnemyNarrowsEitherOrEntries() {
        assertEquals(CombatEncounterData.ARMADYLEAN_GUARD,
                ClueCombatHelperPlugin.specificEncounterForNpc(CombatEncounterData.ELITE_GUARD, "Armadylean guard"));
        assertEquals(CombatEncounterData.BANDOSIAN_GUARD,
                ClueCombatHelperPlugin.specificEncounterForNpc(CombatEncounterData.ELITE_GUARD_WILDY, "Bandosian guard"));
        assertEquals(CombatEncounterData.ANCIENT_WIZARDS,
                ClueCombatHelperPlugin.specificEncounterForNpc(CombatEncounterData.MASTER_COORDINATE, "Ancient Wizard"));
        assertEquals(CombatEncounterData.SARADOMIN_WIZARD,
                ClueCombatHelperPlugin.specificEncounterForNpc(CombatEncounterData.SARADOMIN_WIZARD, "Saradomin wizard"));
    }

    @Test
    public void everyEncounterHasTextAndFollowsTheRules() {
        for (CombatEncounterData data : CombatEncounterData.values()) {
            if (data == CombatEncounterData.UNKNOWN) {
                continue;
            }
            for (String text : new String[]{data.getMonsterName(), data.getLevelText(), data.getGear(), data.getBring(), data.getTip()}) {
                assertFalse(data.name(), text.isEmpty());
                // Overlay font has no arrow glyph
                assertFalse(data.name(), text.contains("\u2192"));
                // No prayer advice (Plugin Hub rule)
                assertFalse(data.name() + ": " + text, text.toLowerCase(Locale.ROOT).contains("pray"));
            }
            assertTrue(data.name(), data.getNpcNames().length > 0);
        }
    }

    @Test
    public void unknownEnemyHasFallbackEntry() {
        assertEquals(CombatEncounterData.UNKNOWN.getGear(), "");
        assertNull(ClueCombatHelperPlugin.encounterFor(null, false));
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
