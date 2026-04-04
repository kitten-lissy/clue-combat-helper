package com.cluecombathelper;

/**
 * All possible combat encounters in treasure trail clue scrolls.
 */
public enum CombatEncounterData {

    // Hard coordinate clues - outside wilderness
    SARADOMIN_WIZARD(
            "Saradomin Wizard",
            108,
            "Hard",
            "Coordinate (non-wildy)",
            "Protect from Melee (blocks poison dagger)",
            "Best melee gear",
            "Food, Antipoison (backup)",
            "Melee it down fast with Protect from Melee. Only poisons via melee attack. Tank the magic hits.",
            false
    ),

    // Hard coordinate clues - wilderness
    ZAMORAK_WIZARD(
            "Zamorak Wizard",
            65,
            "Hard",
            "Coordinate (wilderness)",
            "Protect from Magic (blocks all damage)",
            "Black d'hide, d scim or crossbow",
            "Food, Clue box, 1-click teleport (below 30 Wildy)",
            "Easy fight. Pray Magic = zero damage taken. Bring clue box if you have one! Watch for PKers.",
            true
    ),

    // Hard emote clues - outside wilderness
    DOUBLE_AGENT_HARD(
            "Double Agent",
            65,
            "Hard",
            "Emote (non-wildy)",
            "Protect from Melee",
            "Best melee gear",
            "Food",
            "Hard+ only. Won't appear on first step of trail. Kill it, then Uri appears.",
            false
    ),

    // Hard emote clues - wilderness
    DOUBLE_AGENT_HARD_WILDY(
            "Double Agent",
            108,
            "Hard",
            "Emote (wilderness)",
            "Protect from Melee",
            "Black d'hide, d scim",
            "Food, Clue box, 1-click teleport (below 30 Wildy)",
            "Hard+ only. Won't appear on first step. Use STASH unit! Bring clue box! Watch for PKers.",
            true
    ),

    // Master emote clues
    DOUBLE_AGENT_MASTER(
            "Double Agent",
            141,
            "Master",
            "Emote",
            "Protect from Melee",
            "Best melee gear",
            "Food",
            "Won't appear on first step of trail. Max hit 19. Kill it, then Uri appears.",
            false
    ),

    // Master coordinate clues - single combat area
    BRASSICAN_MAGE(
            "Brassican Mage",
            140,
            "Master",
            "Coordinate (single combat)",
            "Prayer does NOT work (typeless damage)",
            "Best melee or ranged gear (low Defence)",
            "Plenty of food (max hit 16, fast attacks)",
            "Typeless magic ignores all prayers. Has low Defence so just DPS it down fast.",
            false
    ),

    // Master coordinate clues - multicombat area
    ANCIENT_WIZARDS(
            "Ancient Wizards (x3)",
            112,
            "Master",
            "Coordinate (multicombat)",
            "Protect from Melee (melee one hits hardest + poisons)",
            "Best melee gear, high Defence",
            "Food, Antipoison (melee wizard poisons through prayer!)",
            "3 wizards: Melee (max 36, poisons!), Mage (max 18), Ranger (max 23). Kill melee first!",
            false
    ),

    // Master coordinate clues - wilderness (Brassican)
    BRASSICAN_MAGE_WILDY(
            "Brassican Mage",
            140,
            "Master",
            "Coordinate (wilderness, single combat)",
            "Prayer does NOT work (typeless damage)",
            "Black d'hide, d scim",
            "Food, 1-click teleport",
            "Typeless magic ignores prayers. Low Defence, just DPS it. Bring clue box! Watch for PKers.",
            true
    ),

    // Master coordinate clues - wilderness (Ancient Wizards)
    ANCIENT_WIZARDS_WILDY(
            "Ancient Wizards (x3)",
            112,
            "Master",
            "Coordinate (wilderness, multicombat)",
            "Protect from Melee",
            "Black d'hide, d scim",
            "Food, Antipoison, Clue box, 1-click teleport",
            "Melee wizard poisons through prayer! Kill melee first. Bring clue box! Watch for PKers.",
            true
    );

    private final String monsterName;
    private final int combatLevel;
    private final String clueTier;
    private final String triggerType;
    private final String prayer;
    private final String gear;
    private final String bring;
    private final String tip;
    private final boolean wilderness;

    CombatEncounterData(String monsterName, int combatLevel, String clueTier,
                        String triggerType, String prayer, String gear,
                        String bring, String tip, boolean wilderness) {
        this.monsterName = monsterName;
        this.combatLevel = combatLevel;
        this.clueTier = clueTier;
        this.triggerType = triggerType;
        this.prayer = prayer;
        this.gear = gear;
        this.bring = bring;
        this.tip = tip;
        this.wilderness = wilderness;
    }

    public String getMonsterName() { return monsterName; }
    public int getCombatLevel() { return combatLevel; }
    public String getClueTier() { return clueTier; }
    public String getTriggerType() { return triggerType; }
    public String getPrayer() { return prayer; }
    public String getGear() { return gear; }
    public String getBring() { return bring; }
    public String getTip() { return tip; }
    public boolean isWilderness() { return wilderness; }
}
