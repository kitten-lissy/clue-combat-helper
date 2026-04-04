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

    // Elite coordinate clues - outside wilderness (random: Armadylean OR Bandosian)
    ELITE_GUARD(
            "Armadylean OR Bandosian Guard",
            125,
            "Elite",
            "Coordinate (non-wildy)",
            "Armadylean: Protect from Missiles | Bandosian: Protect from Melee",
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
            "Protect from Missiles (blocks all damage)",
            "Ranged or magic gear (CAN'T melee — it flies!)",
            "Food",
            "Flies! Must use ranged/magic/halberd. Pray Missiles = zero damage. Max hit 10.",
            false
    ),

    BANDOSIAN_GUARD(
            "Bandosian Guard",
            125,
            "Elite",
            "Coordinate",
            "Protect from Melee (blocks all damage)",
            "Best melee gear",
            "Food",
            "Melee only. Pray Melee = zero damage. Max hit 13.",
            false
    ),

    // Elite coordinate clues - wilderness
    ELITE_GUARD_WILDY(
            "Armadylean OR Bandosian Guard",
            125,
            "Elite",
            "Coordinate (wilderness)",
            "Armadylean: Protect from Missiles | Bandosian: Protect from Melee",
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
            "Protect from Melee",
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
            "Brassican: Prayer DOESN'T work! | Ancients: Protect from Melee",
            "Best melee gear + bring Antipoison",
            "Plenty of food, Antipoison (Ancient melee wizard poisons through prayer!)",
            "Brassican: just DPS. Ancient Wizards: DDS spec melee first (poisons!), then mage, then ranger. Switch prayers each.",
            false
    ),

    // Master coordinate - single combat (Brassican Mage confirmed)
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

    // Master coordinate - multicombat (Ancient Wizards confirmed)
    ANCIENT_WIZARDS(
            "Ancient Wizards (x3)",
            112,
            "Master",
            "Coordinate (multicombat)",
            "Protect from Melee (melee one hits hardest + poisons)",
            "Best melee gear, high Defence",
            "Food, Antipoison (melee wizard poisons through prayer!)",
            "DDS spec the melee wizard first (poisons!), then mage, then ranger. Switch prayers each kill.",
            false
    ),

    // Master coordinate - wilderness (always show OR since risky to linger)
    MASTER_COORDINATE_WILDY(
            "Brassican Mage OR Ancient Wizards",
            140,
            "Master",
            "Coordinate (wilderness)",
            "Brassican: Prayer DOESN'T work! | Ancients: Protect from Melee",
            "Black d'hide, d scim",
            "Food, Antipoison, Clue box, 1-click teleport",
            "Brassican: just DPS. Ancients: DDS spec melee first (poisons!), then mage, then ranger. Bring clue box! Watch for PKers.",
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
