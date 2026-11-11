package com.cluecombathelper;

/**
 * All possible combat encounters in treasure trail clue scrolls.
 *
 * Levels and max hits are taken from the Old School RuneScape Wiki.
 */
public enum CombatEncounterData {

    // Hard coordinate clues - outside wilderness
    SARADOMIN_WIZARD(
            "Saradomin Wizard",
            "108",
            "Hard",
            "Coordinate (non-wildy)",
            "Best melee gear",
            "Food, antipoison",
            "Max hit 20. Its dagger poisons, so keep antipoison handy.",
            false,
            true,
            "Saradomin wizard"
    ),

    // Hard coordinate clues - wilderness
    ZAMORAK_WIZARD(
            "Zamorak Wizard",
            "65",
            "Hard",
            "Coordinate (wilderness)",
            "Black d'hide, d scim or crossbow (a slash weapon also cuts webs)",
            "Food, " + Text.WILDY_TELEPORT + ". " + Text.CLUE_BOX,
            "Max hit 20. Low Defence except against Magic. Watch for PKers.",
            true,
            false,
            "Zamorak wizard"
    ),

    // Hard emote clues - outside wilderness
    DOUBLE_AGENT_HARD(
            "Double Agent",
            "108",
            "Hard",
            "Emote (non-wildy)",
            "Best melee gear",
            "Food",
            "Max hit 14. " + Text.URI_TIP,
            false,
            false,
            "Double agent"
    ),

    // Hard emote clues - wilderness
    DOUBLE_AGENT_HARD_WILDY(
            "Double Agent",
            "65",
            "Hard",
            "Emote (wilderness)",
            "Black d'hide, d scim",
            "Food, " + Text.WILDY_TELEPORT + ". " + Text.CLUE_BOX,
            "Max hit 5. Use the STASH unit so the clue items aren't at risk. "
                    + Text.URI_TIP + " Watch for PKers.",
            true,
            false,
            "Double agent"
    ),

    // Elite coordinate clues - outside wilderness (random: Armadylean OR Bandosian)
    ELITE_GUARD(
            "Armadylean or Bandosian Guard",
            "97 / 125",
            "Elite",
            "Coordinate (non-wildy)",
            "Ranged or magic gear, a halberd or a salamander (the Armadylean guard flies)",
            "Food",
            "Random spawn. Armadylean: max hit 10, most melee weapons can't hit it. Bandosian: max hit 13.",
            false,
            false,
            "Armadylean guard", "Bandosian guard"
    ),

    // Elite coordinate - specific spawns
    ARMADYLEAN_GUARD(
            "Armadylean Guard",
            "97",
            "Elite",
            "Coordinate",
            "Ranged or magic gear, a halberd or a salamander",
            "Food",
            "It flies, so most melee weapons can't hit it. Max hit 10.",
            false,
            false,
            "Armadylean guard"
    ),

    BANDOSIAN_GUARD(
            "Bandosian Guard",
            "125",
            "Elite",
            "Coordinate",
            "Best melee gear",
            "Food",
            "Max hit 13.",
            false,
            false,
            "Bandosian guard"
    ),

    // Elite coordinate clues - wilderness
    ELITE_GUARD_WILDY(
            "Armadylean or Bandosian Guard",
            "97 / 125",
            "Elite",
            "Coordinate (wilderness)",
            "Black d'hide and a crossbow or salamander (the Armadylean guard flies, most melee can't hit it)",
            "Food, " + Text.WILDY_TELEPORT + ". " + Text.CLUE_BOX,
            "Random spawn. Armadylean max hit 10, Bandosian max hit 13. Watch for PKers.",
            true,
            false,
            "Armadylean guard", "Bandosian guard"
    ),

    // Master emote clues
    DOUBLE_AGENT_MASTER(
            "Double Agent",
            "141",
            "Master",
            "Emote",
            "Best melee gear",
            "Food",
            "Max hit 19. " + Text.URI_TIP,
            false,
            false,
            "Double agent"
    ),

    // Master coordinate or hot/cold - enemy not known yet
    MASTER_COORDINATE(
            "Brassican Mage or Ancient Wizards",
            "140 / 98-112",
            "Master",
            "Coordinate (non-wildy)",
            "Melee or ranged gear with magic defence (black d'hide, Karil's)",
            "Plenty of food, antipoison, a strong special attack (dragon dagger or granite maul)",
            "Brassican: max hit 16, low Defence, kill it fast. "
                    + "Ancient Wizards: " + Text.ANCIENT_ORDER,
            false,
            true,
            "Brassican Mage", "Ancient Wizard"
    ),

    // Master coordinate - single combat (Brassican Mage confirmed)
    BRASSICAN_MAGE(
            "Brassican Mage",
            "140",
            "Master",
            "Coordinate (single combat)",
            "Melee or ranged gear with magic defence (black d'hide, Karil's)",
            "Plenty of food, ring of recoil or charged ring of suffering, a strong special attack (dragon dagger)",
            "Max hit 16, typeless damage. Low Defence, so kill it fast. Its high Magic makes magic a poor choice.",
            false,
            false,
            "Brassican Mage"
    ),

    // Master coordinate - multicombat (Ancient Wizards confirmed)
    ANCIENT_WIZARDS(
            "Ancient Wizards (x3)",
            "98 / 98 / 112",
            "Master",
            "Coordinate (multicombat)",
            "Melee or ranged gear, dragonhide armour",
            "Food plus karambwans for combo eating, antipoison, dragon dagger or granite maul",
            "Kill the melee wizard first (max hit 36, poisons), then the ranger (23), then the mage (18).",
            false,
            true,
            "Ancient Wizard"
    ),

    // Master coordinate - wilderness (always show OR since risky to linger)
    MASTER_COORDINATE_WILDY(
            "Brassican Mage or Ancient Wizards",
            "140 / 98-112",
            "Master",
            "Coordinate (wilderness)",
            "Black d'hide, d scim",
            "Food, antipoison, " + Text.WILDY_TELEPORT + ". " + Text.CLUE_BOX,
            "Brassican: max hit 16, kill it fast. Ancient Wizards: " + Text.ANCIENT_ORDER
                    + " Watch for PKers.",
            true,
            true,
            "Brassican Mage", "Ancient Wizard"
    ),

    // Fallback for an enemy added to RuneLite after this plugin was written.
    // The panel shows RuneLite's own name for the enemy and no advice lines.
    UNKNOWN(
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            false,
            false
    );

    /** Shared text, kept in one place so it stays consistent. */
    static final class Text {
        static final String WILDY_TELEPORT =
                "1-click teleport (glory, ring of wealth and similar work to level 30 Wildy, spells and tabs only to 20)";
        static final String CLUE_BOX = "A clue box is optional, it only saves a trip to your grave";
        static final String URI_TIP =
                "You can swap gear to fight. Re-equip the clue items before the kill, or emote again after.";
        static final String ANCIENT_ORDER =
                "kill the melee wizard first (max hit 36, poisons), then the ranger (23), then the mage (18).";

        private Text() {
        }
    }

    private final String monsterName;
    private final String levelText;
    private final String clueTier;
    private final String triggerType;
    private final String gear;
    private final String bring;
    private final String tip;
    private final boolean wilderness;
    private final boolean needsAntipoison;
    private final String[] npcNames;

    CombatEncounterData(String monsterName, String levelText, String clueTier,
                        String triggerType, String gear,
                        String bring, String tip, boolean wilderness,
                        boolean needsAntipoison, String... npcNames) {
        this.monsterName = monsterName;
        this.levelText = levelText;
        this.clueTier = clueTier;
        this.triggerType = triggerType;
        this.gear = gear;
        this.bring = bring;
        this.tip = tip;
        this.wilderness = wilderness;
        this.needsAntipoison = needsAntipoison;
        this.npcNames = npcNames;
    }

    public String getMonsterName() { return monsterName; }
    /** Combat level as shown on the panel, e.g. "108" or "97 / 125" for either/or spawns. */
    public String getLevelText() { return levelText; }
    public String getClueTier() { return clueTier; }
    public String getTriggerType() { return triggerType; }
    public String getGear() { return gear; }
    public String getBring() { return bring; }
    public String getTip() { return tip; }
    public boolean isWilderness() { return wilderness; }
    public boolean needsAntipoison() { return needsAntipoison; }

    /** In-game NPC names that this encounter can spawn. */
    public boolean matchesNpcName(String name) {
        if (name == null) {
            return false;
        }
        for (String npcName : npcNames) {
            if (npcName.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    String[] getNpcNames() {
        return npcNames.clone();
    }
}
