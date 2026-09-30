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
            "Best melee gear",
            "Food, Antipoison (backup)",
            "Melee it down fast. Only poisons via its melee attack.",
            false
    ),

    // Hard coordinate clues - wilderness
    ZAMORAK_WIZARD(
            "Zamorak Wizard",
            65,
            "Hard",
            "Coordinate (wilderness)",
            "Black d'hide, d scim or crossbow",
            "Food, Clue box, 1-click teleport (below 30 Wildy)",
            "Easy fight. Bring a clue box if you have one! Watch for PKers.",
            true
    ),

    // Hard emote clues - outside wilderness
    DOUBLE_AGENT_HARD(
            "Double Agent",
            65,
            "Hard",
            "Emote (non-wildy)",
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
            "Black d'hide, d scim",
            "Food, Clue box, 1-click teleport (below 30 Wildy)",
            "Hard+ only. Won't appear on first step. Use STASH unit! Bring clue box! Watch for PKers.",
            true
    ),

    // Elite coordinate clues - outside wilderness (random: Armadylean OR Bandosian)
    ELITE_GUARD(
            "Armadylean OR Bandosian Guard",
            125,
            "Elite",
            "Coordinate (non-wildy)",
            "Bring ranged gear (Armadylean flies — CAN'T melee it!)",
            "Food",
            "Random spawn! Armadylean (ranged only, max 10) or Bandosian (melee, max 13). Bring a crossbow just in case.",
            false
    ),

    // Elite coordinate - specific spawns
    ARMADYLEAN_GUARD(
            "Armadylean Guard",
            97,
            "Elite",
            "Coordinate",
            "Ranged or magic gear (CAN'T melee — it flies!)",
            "Food",
            "Flies! Must use ranged/magic/halberd. Max hit 10.",
            false
    ),

    BANDOSIAN_GUARD(
            "Bandosian Guard",
            125,
            "Elite",
            "Coordinate",
            "Best melee gear",
            "Food",
            "Melee only. Max hit 13.",
            false
    ),

    // Elite coordinate clues - wilderness
    ELITE_GUARD_WILDY(
            "Armadylean OR Bandosian Guard",
            125,
            "Elite",
            "Coordinate (wilderness)",
            "Black d'hide, crossbow (Armadylean flies — CAN'T melee!)",
            "Food, Clue box, 1-click teleport (below 30 Wildy)",
            "Random spawn! Bring crossbow for Armadylean. Bring clue box! Watch for PKers.",
            true
    ),

    // Master emote clues
    DOUBLE_AGENT_MASTER(
            "Double Agent",
            141,
            "Master",
            "Emote",
            "Best melee gear",
            "Food",
            "Won't appear on first step of trail. Max hit 19. Kill it, then Uri appears.",
            false
    ),

    // Master coordinate - preparing (not at location yet)
    MASTER_COORDINATE(
            "Brassican Mage OR Ancient Wizards",
            140,
            "Master",
            "Coordinate (non-wildy)",
            "Best melee gear + bring Antipoison",
            "Plenty of food, Antipoison (the Ancient melee wizard poisons)",
            "Brassican: just DPS. Ancient Wizards: DDS spec melee first (poisons!), then mage, then ranger.",
            false
    ),

    // Master coordinate - single combat (Brassican Mage confirmed)
    BRASSICAN_MAGE(
            "Brassican Mage",
            140,
            "Master",
            "Coordinate (single combat)",
            "Best melee or ranged gear (low Defence)",
            "Plenty of food (max hit 16, fast attacks)",
            "Deals typeless damage. Has low Defence so just DPS it down fast.",
            false
    ),

    // Master coordinate - multicombat (Ancient Wizards confirmed)
    ANCIENT_WIZARDS(
            "Ancient Wizards (x3)",
            112,
            "Master",
            "Coordinate (multicombat)",
            "Best melee gear, high Defence",
            "Food, Antipoison (the melee wizard poisons)",
            "DDS spec the melee wizard first (poisons!), then mage, then ranger.",
            false
    ),

    // Master coordinate - wilderness (always show OR since risky to linger)
    MASTER_COORDINATE_WILDY(
            "Brassican Mage OR Ancient Wizards",
            140,
            "Master",
            "Coordinate (wilderness)",
            "Black d'hide, d scim",
            "Food, Antipoison, Clue box, 1-click teleport",
            "Brassican: just DPS. Ancients: DDS spec melee first (poisons!), then mage, then ranger. Bring clue box! Watch for PKers.",
            true
    );

    private final String monsterName;
    private final int combatLevel;
    private final String clueTier;
    private final String triggerType;
    private final String gear;
    private final String bring;
    private final String tip;
    private final boolean wilderness;

    CombatEncounterData(String monsterName, int combatLevel, String clueTier,
                        String triggerType, String gear,
                        String bring, String tip, boolean wilderness) {
        this.monsterName = monsterName;
        this.combatLevel = combatLevel;
        this.clueTier = clueTier;
        this.triggerType = triggerType;
        this.gear = gear;
        this.bring = bring;
        this.tip = tip;
        this.wilderness = wilderness;
    }

    public String getMonsterName() { return monsterName; }
    public int getCombatLevel() { return combatLevel; }
    public String getClueTier() { return clueTier; }
    public String getTriggerType() { return triggerType; }
    public String getGear() { return gear; }
    public String getBring() { return bring; }
    public String getTip() { return tip; }
    public boolean isWilderness() { return wilderness; }
}
