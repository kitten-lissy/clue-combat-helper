package com.cluecombathelper;

import java.util.Locale;

/**
 * Recognises items by their in-game name. Names are used so that every dose,
 * charge and trimmed variant is covered without a long list of item IDs.
 * Item names were checked against the Old School RuneScape Wiki.
 */
final class ItemNames {

    // Jewellery and other items that teleport up to level 30 Wilderness (members).
    // Charged versions have a number in the name, e.g. "Amulet of glory(4)", "Ring of wealth (i5)".
    private static final String[] CHARGED_TELEPORTS_TO_30 = {
            "amulet of glory",
            "combat bracelet",
            "skills necklace",
            "ring of wealth",
            "slayer ring",
    };

    // Items that teleport up to level 30 Wilderness and carry no charge number.
    private static final String[] TELEPORTS_TO_30 = {
            "amulet of eternal glory",
            "slayer ring (eternal)",
            "pharaoh's sceptre",
            "royal seed pod",
            "grand seed pod",
    };

    private ItemNames() {
    }

    private static String clean(String name) {
        return name == null ? "" : name.toLowerCase(Locale.ROOT).trim();
    }

    /** Any potion or mix that cures poison: antipoison, superantipoison, antidote, sanfew, anti-venom. */
    static boolean isAntipoison(String name) {
        String n = clean(name);
        return n.contains("antipoison")
                || n.contains("anti-poison")
                || n.contains("antidote")
                || n.contains("anti-venom")
                || n.contains("antivenom")
                || n.startsWith("sanfew serum");
    }

    /** A charged serpentine helm gives poison immunity while worn. */
    static boolean isPoisonImmunityHelm(String name) {
        return clean(name).equals("serpentine helm");
    }

    /** Teleports that still work up to level 30 Wilderness. */
    static boolean isWildernessTeleport(String name) {
        String n = clean(name);
        for (String exact : TELEPORTS_TO_30) {
            if (n.equals(exact)) {
                return true;
            }
        }
        for (String prefix : CHARGED_TELEPORTS_TO_30) {
            if (n.startsWith(prefix) && n.matches(".*\\(.*\\d.*\\).*")) {
                return true;
            }
        }
        return false;
    }

    /** Halberds and salamanders hit flying monsters even though they are not ranged or magic weapons. */
    static boolean canHitFlyerByName(String name) {
        String n = clean(name);
        return n.contains("halberd") || n.contains("salamander") || n.equals("swamp lizard");
    }

    /**
     * Combat style names from the game's weapon style data. Returns TRUE for a style that
     * attacks from range (ranged or magic), FALSE for a melee style, and null if unknown.
     */
    static Boolean styleReaches(String styleName) {
        switch (clean(styleName)) {
            case "ranging":
            case "longrange":
            case "casting":
            case "defensive casting":
                return Boolean.TRUE;
            case "accurate":
            case "aggressive":
            case "defensive":
            case "controlled":
                return Boolean.FALSE;
            default:
                return null;
        }
    }
}
