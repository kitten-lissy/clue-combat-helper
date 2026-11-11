package com.cluecombathelper;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ItemNamesTest {
    @Test
    public void antipoisons() {
        assertTrue(ItemNames.isAntipoison("Antipoison(4)"));
        assertTrue(ItemNames.isAntipoison("Superantipoison(1)"));
        assertTrue(ItemNames.isAntipoison("Antidote++(2)"));
        assertTrue(ItemNames.isAntipoison("Anti-venom+(4)"));
        assertTrue(ItemNames.isAntipoison("Sanfew serum(3)"));
        assertFalse(ItemNames.isAntipoison("Prayer potion(4)"));
        assertTrue(ItemNames.isPoisonImmunityHelm("Serpentine helm"));
        assertFalse(ItemNames.isPoisonImmunityHelm("Serpentine helm (uncharged)"));
    }

    @Test
    public void wildernessTeleports() {
        assertTrue(ItemNames.isWildernessTeleport("Amulet of glory(4)"));
        assertTrue(ItemNames.isWildernessTeleport("Amulet of eternal glory"));
        assertTrue(ItemNames.isWildernessTeleport("Ring of wealth (5)"));
        assertTrue(ItemNames.isWildernessTeleport("Combat bracelet(1)"));
        assertTrue(ItemNames.isWildernessTeleport("Skills necklace(6)"));
        assertTrue(ItemNames.isWildernessTeleport("Slayer ring (8)"));
        assertTrue(ItemNames.isWildernessTeleport("Royal seed pod"));
        assertTrue(ItemNames.isWildernessTeleport("Pharaoh's sceptre"));
        // Uncharged, or only works to level 20
        assertFalse(ItemNames.isWildernessTeleport("Amulet of glory"));
        assertFalse(ItemNames.isWildernessTeleport("Ring of wealth"));
        assertFalse(ItemNames.isWildernessTeleport("Pharaoh's sceptre (uncharged)"));
        assertFalse(ItemNames.isWildernessTeleport("Games necklace(8)"));
        assertFalse(ItemNames.isWildernessTeleport("Varrock teleport"));
    }

    @Test
    public void flyerWeapons() {
        assertTrue(ItemNames.canHitFlyerByName("Rune halberd"));
        assertTrue(ItemNames.canHitFlyerByName("Black salamander"));
        assertTrue(ItemNames.canHitFlyerByName("Swamp lizard"));
        assertFalse(ItemNames.canHitFlyerByName("Dragon scimitar"));
        assertEquals(Boolean.TRUE, ItemNames.styleReaches("Longrange"));
        assertEquals(Boolean.TRUE, ItemNames.styleReaches("Casting"));
        assertEquals(Boolean.FALSE, ItemNames.styleReaches("Aggressive"));
        assertNull(ItemNames.styleReaches("Other"));
    }
}
