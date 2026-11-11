package com.cluecombathelper;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup(ClueCombatHelperConfig.GROUP)
public interface ClueCombatHelperConfig extends Config {
    String GROUP = "cluecombathelper";

    enum ShowMode {
        ALWAYS("Always"),
        NEAR_SPOT("Only near the spot"),
        AFTER_SPAWN("Only once the enemy has spawned");

        private final String label;

        ShowMode(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    @ConfigSection(
        name = "Display",
        description = "When the panel shows and what it contains",
        position = 0
    )
    String displaySection = "display";

    @ConfigSection(
        name = "Warnings",
        description = "Extra warnings shown on the panel",
        position = 1
    )
    String warningSection = "warnings";

    @ConfigSection(
        name = "Colours",
        description = "Panel colours",
        position = 2,
        closedByDefault = true
    )
    String colourSection = "colours";

    // Display

    @ConfigItem(
        keyName = "showOverlay",
        name = "Show overlay",
        description = "Show the combat panel. Turn off to hide it without disabling the plugin",
        section = displaySection,
        position = 0
    )
    default boolean showOverlay() {
        return true;
    }

    @ConfigItem(
        keyName = "showMode",
        name = "When to show",
        description = "Always while the clue step is active, only near the dig or emote spot, or only once the enemy has spawned",
        section = displaySection,
        position = 1
    )
    default ShowMode showMode() {
        return ShowMode.ALWAYS;
    }

    @Range(min = 1, max = 100)
    @Units(" tiles")
    @ConfigItem(
        keyName = "nearDistance",
        name = "Near spot distance",
        description = "How close to the spot counts as near, for the 'Only near the spot' option",
        section = displaySection,
        position = 2
    )
    default int nearDistance() {
        return 20;
    }

    @ConfigItem(
        keyName = "wildernessOnly",
        name = "Only Wilderness steps",
        description = "Only show the panel for steps in the Wilderness (or while you are in the Wilderness or on a PvP world)",
        section = displaySection,
        position = 3
    )
    default boolean wildernessOnly() {
        return false;
    }

    @ConfigItem(
        keyName = "compactMode",
        name = "Compact mode",
        description = "Only show the enemy name, level, gear and warnings",
        section = displaySection,
        position = 4
    )
    default boolean compactMode() {
        return false;
    }

    @ConfigItem(
        keyName = "showTips",
        name = "Show tips",
        description = "Show the tips line",
        section = displaySection,
        position = 5
    )
    default boolean showTips() {
        return true;
    }

    @ConfigItem(
        keyName = "showBring",
        name = "Show Bring",
        description = "Show what to bring",
        section = displaySection,
        position = 6
    )
    default boolean showBring() {
        return true;
    }

    @ConfigItem(
        keyName = "checkInventory",
        name = "Check inventory",
        description = "Under Bring, show whether you have food, antipoison and (for Wilderness steps) a teleport",
        section = displaySection,
        position = 7
    )
    default boolean checkInventory() {
        return true;
    }

    @ConfigItem(
        keyName = "afterKillReminder",
        name = "After-kill reminder",
        description = "After the enemy dies, replace the panel with a one-line reminder (dig again, or talk to Uri)",
        section = displaySection,
        position = 8
    )
    default boolean afterKillReminder() {
        return true;
    }

    // Warnings

    @ConfigItem(
        keyName = "showWildernessWarning",
        name = "Wilderness warning",
        description = "Show a red warning for Wilderness steps, inside the Wilderness and on PvP worlds",
        section = warningSection,
        position = 0
    )
    default boolean showWildernessWarning() {
        return true;
    }

    @ConfigItem(
        keyName = "weaponReachWarning",
        name = "Armadylean guard weapon warning",
        description = "Warn when your weapon can't hit the flying Armadylean guard",
        section = warningSection,
        position = 1
    )
    default boolean weaponReachWarning() {
        return true;
    }

    @ConfigItem(
        keyName = "lowHpWarning",
        name = "Low HP warning (hot/cold)",
        description = "On master hot/cold steps, warn when your Hitpoints are low, since each Feel of the device hurts",
        section = warningSection,
        position = 2
    )
    default boolean lowHpWarning() {
        return true;
    }

    @Range(min = 1, max = 99)
    @ConfigItem(
        keyName = "lowHpThreshold",
        name = "Low HP below",
        description = "Hitpoints level at or below which the low HP warning shows",
        section = warningSection,
        position = 3
    )
    default int lowHpThreshold() {
        return 20;
    }

    // Colours (defaults are the original pink-on-teal theme)

    @Alpha
    @ConfigItem(
        keyName = "backgroundColor",
        name = "Background",
        description = "Panel background colour",
        section = colourSection,
        position = 0
    )
    default Color backgroundColor() {
        return new Color(0, 80, 80, 200);
    }

    @ConfigItem(
        keyName = "titleColor",
        name = "Title",
        description = "Colour of the enemy name",
        section = colourSection,
        position = 1
    )
    default Color titleColor() {
        return new Color(255, 105, 180);
    }

    @ConfigItem(
        keyName = "monsterColor",
        name = "Level",
        description = "Colour of the level line",
        section = colourSection,
        position = 2
    )
    default Color monsterColor() {
        return new Color(255, 182, 193);
    }

    @ConfigItem(
        keyName = "gearColor",
        name = "Gear text",
        description = "Colour of the gear text",
        section = colourSection,
        position = 3
    )
    default Color gearColor() {
        return new Color(255, 160, 200);
    }

    @ConfigItem(
        keyName = "bringColor",
        name = "Bring text",
        description = "Colour of the bring text",
        section = colourSection,
        position = 4
    )
    default Color bringColor() {
        return new Color(255, 200, 220);
    }

    @ConfigItem(
        keyName = "tipColor",
        name = "Tip text",
        description = "Colour of the tips",
        section = colourSection,
        position = 5
    )
    default Color tipColor() {
        return new Color(255, 220, 230);
    }

    @ConfigItem(
        keyName = "labelColor",
        name = "Labels",
        description = "Colour of the labels (Gear, Bring, clue info)",
        section = colourSection,
        position = 6
    )
    default Color labelColor() {
        return new Color(100, 220, 220);
    }

    @ConfigItem(
        keyName = "warningColor",
        name = "Warnings",
        description = "Colour of the Wilderness and other warnings",
        section = colourSection,
        position = 7
    )
    default Color warningColor() {
        return new Color(255, 50, 50);
    }
}
