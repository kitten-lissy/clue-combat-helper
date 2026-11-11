package com.cluecombathelper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class ClueCombatHelperOverlay extends Overlay {

    static final String DIG_AGAIN = "Enemy down. Dig again to get the clue.";
    static final String TALK_TO_URI = "Enemy down. Talk to Uri with the clue items equipped. "
            + "If he didn't appear, re-equip them and emote again.";
    static final String DEVICE_DAMAGE = "Each Feel of the strange device costs 3-8 HP. Bring extra food.";

    private static final int PANEL_WIDTH = 280;

    private final ClueCombatHelperPlugin plugin;
    private final ClueCombatHelperConfig config;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    public ClueCombatHelperOverlay(ClueCombatHelperPlugin plugin, ClueCombatHelperConfig config) {
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setPriority(Overlay.PRIORITY_HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        CombatEncounterData encounter = plugin.getCurrentEncounter();
        if (encounter == null || !config.showOverlay()) {
            return null;
        }

        boolean dangerZone = plugin.isWildernessStep() || plugin.isPlayerInWilderness() || plugin.isPvpWorld();
        if (config.wildernessOnly() && !dangerZone) {
            return null;
        }

        panelComponent.getChildren().clear();
        panelComponent.setPreferredSize(new Dimension(PANEL_WIDTH, 0));
        panelComponent.setBackgroundColor(config.backgroundColor());

        // After the kill: one line telling the player what to do next
        if (plugin.isEnemyKilled() && config.afterKillReminder()) {
            String reminder = plugin.getStepKind() == ClueCombatHelperPlugin.StepKind.EMOTE ? TALK_TO_URI : DIG_AGAIN;
            addText(reminder, config.titleColor());
            return panelComponent.render(graphics);
        }

        if (!shouldShowPanel()) {
            return null;
        }

        boolean compact = config.compactMode();
        Color warning = config.warningColor();

        // Title: the enemy itself
        panelComponent.getChildren().add(TitleComponent.builder()
                .text(plugin.getDisplayName() == null ? "Clue Combat Guide" : plugin.getDisplayName())
                .color(config.titleColor())
                .build());

        // Level + clue info
        String level = encounter.getLevelText();
        String clueInfo = clueInfo(encounter);
        if (!level.isEmpty() || (!compact && !clueInfo.isEmpty())) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(level.isEmpty() ? "" : "Level " + level)
                    .leftColor(config.monsterColor())
                    .right(compact ? "" : clueInfo)
                    .rightColor(config.labelColor())
                    .build());
        }

        // Wilderness / PvP warning
        if (config.showWildernessWarning()) {
            if (plugin.isWildernessStep() || plugin.isPlayerInWilderness()) {
                addPair("WILDERNESS", "Risk your gear!", warning);
            } else if (plugin.isPvpWorld()) {
                addPair("PVP WORLD", "Risk your gear!", warning);
            } else if (plugin.isMaybeWilderness()) {
                addText("Some possible spots are in the Wilderness.", warning);
            }
        }

        // Armadylean guard weapon reach
        if (config.weaponReachWarning()) {
            if (plugin.isWeaponWarning()) {
                addText("Your weapon can't hit the Armadylean guard (it flies). "
                        + "Use ranged, magic, a halberd or a salamander.", warning);
            } else if (plugin.isNoFlyerWeaponWarning()) {
                addText("No ranged, magic, halberd or salamander weapon found. "
                        + "If the Armadylean guard spawns, most melee can't hit it.", warning);
            }
        }

        // Master hot/cold: the device itself hurts
        if (plugin.getStepKind() == ClueCombatHelperPlugin.StepKind.HOT_COLD) {
            if (!compact) {
                addText(DEVICE_DAMAGE, config.bringColor());
            }
            if (config.lowHpWarning() && plugin.isLowHp()) {
                addText("Low HP. Eat before using the device again.", warning);
            }
        }

        // Gear
        if (!encounter.getGear().isEmpty()) {
            if (!compact) {
                addText("Gear:", config.labelColor());
            }
            addText(encounter.getGear(), config.gearColor());
        }

        if (compact) {
            return panelComponent.render(graphics);
        }

        // Bring
        if (config.showBring() && !encounter.getBring().isEmpty()) {
            addText("Bring:", config.labelColor());
            addText(encounter.getBring(), config.bringColor());
            if (config.checkInventory()) {
                addInventoryCheck(encounter, warning);
            }
        }

        // Tip
        if (config.showTips() && !encounter.getTip().isEmpty()) {
            addText(encounter.getTip(), config.tipColor());
        }

        return panelComponent.render(graphics);
    }

    private boolean shouldShowPanel() {
        if (plugin.isEnemySpawned()) {
            return true;
        }
        switch (config.showMode()) {
            case NEAR_SPOT:
                return plugin.isNearSpot();
            case AFTER_SPAWN:
                return false;
            case ALWAYS:
            default:
                return true;
        }
    }

    private String clueInfo(CombatEncounterData encounter) {
        String trigger = plugin.getTriggerLabel();
        if (encounter.getClueTier().isEmpty() || trigger == null || trigger.isEmpty()) {
            return encounter.getClueTier();
        }
        return encounter.getClueTier() + " - " + trigger;
    }

    private void addInventoryCheck(CombatEncounterData encounter, Color warning) {
        List<String> have = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        int food = plugin.getFoodCount();
        if (food > 0) {
            have.add(food + " food");
        } else {
            missing.add("food");
        }

        if (encounter.needsAntipoison()) {
            (plugin.hasAntipoison() ? have : missing).add("antipoison");
        }

        if (plugin.isWildernessStep() || encounter.isWilderness()) {
            (plugin.hasWildernessTeleport() ? have : missing).add("level 30 teleport");
        }

        if (!have.isEmpty()) {
            addText("Have: " + String.join(", ", have), config.bringColor());
        }
        if (!missing.isEmpty()) {
            addText("Missing: " + String.join(", ", missing), warning);
        }
    }

    private void addText(String text, Color color) {
        panelComponent.getChildren().add(LineComponent.builder()
                .left(text)
                .leftColor(color)
                .build());
    }

    private void addPair(String left, String right, Color color) {
        panelComponent.getChildren().add(LineComponent.builder()
                .left(left)
                .leftColor(color)
                .right(right)
                .rightColor(color)
                .build());
    }
}
