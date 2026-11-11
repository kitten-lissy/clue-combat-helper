package com.cluecombathelper;

import java.awt.Color;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigDefaultsTest {
    // An empty implementation returns every default value
    private final ClueCombatHelperConfig config = new ClueCombatHelperConfig() {
    };

    @Test
    public void defaultsShowThePanelLikeBefore() {
        assertTrue(config.showOverlay());
        assertEquals(ClueCombatHelperConfig.ShowMode.ALWAYS, config.showMode());
        assertFalse(config.wildernessOnly());
        assertFalse(config.compactMode());
        assertTrue(config.showTips());
        assertTrue(config.showBring());
        assertTrue(config.showWildernessWarning());
    }

    @Test
    public void defaultColoursAreTheOriginalPinkTheme() {
        assertEquals(new Color(0, 80, 80, 200), config.backgroundColor());
        assertEquals(200, config.backgroundColor().getAlpha());
        assertEquals(new Color(255, 105, 180), config.titleColor());
        assertEquals(new Color(255, 182, 193), config.monsterColor());
        assertEquals(new Color(255, 160, 200), config.gearColor());
        assertEquals(new Color(255, 200, 220), config.bringColor());
        assertEquals(new Color(255, 220, 230), config.tipColor());
        assertEquals(new Color(100, 220, 220), config.labelColor());
        assertEquals(new Color(255, 50, 50), config.warningColor());
    }

    @Test
    public void showModeLabelsReadNicely() {
        assertEquals("Always", ClueCombatHelperConfig.ShowMode.ALWAYS.toString());
        assertEquals("Only near the spot", ClueCombatHelperConfig.ShowMode.NEAR_SPOT.toString());
        assertEquals("Only once the enemy has spawned", ClueCombatHelperConfig.ShowMode.AFTER_SPAWN.toString());
    }
}
