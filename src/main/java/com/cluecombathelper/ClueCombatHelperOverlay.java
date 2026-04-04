package com.cluecombathelper;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class ClueCombatHelperOverlay extends Overlay {

    // Teal background
    private static final Color BACKGROUND_TEAL = new Color(0, 80, 80, 200);

    // Pink shades for different lines
    private static final Color TITLE_PINK = new Color(255, 105, 180);       // Hot pink - title
    private static final Color MONSTER_PINK = new Color(255, 182, 193);     // Light pink - monster name
    private static final Color PRAYER_PINK = new Color(255, 140, 170);      // Medium pink - prayer
    private static final Color GEAR_PINK = new Color(255, 160, 200);        // Soft pink - gear
    private static final Color BRING_PINK = new Color(255, 200, 220);       // Pale pink - bring items
    private static final Color TIP_PINK = new Color(255, 220, 230);         // Lightest pink - tips
    private static final Color LABEL_TEAL = new Color(100, 220, 220);       // Light teal - labels

    // Wilderness warning
    private static final Color WILDERNESS_RED = new Color(255, 50, 50);

    private final ClueCombatHelperPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    public ClueCombatHelperOverlay(ClueCombatHelperPlugin plugin) {
        this.plugin = plugin;
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        CombatEncounterData encounter = plugin.getCurrentEncounter();
        if (encounter == null) {
            return null;
        }

        panelComponent.getChildren().clear();
        panelComponent.setPreferredSize(new Dimension(280, 0));
        panelComponent.setBackgroundColor(BACKGROUND_TEAL);

        // Title
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Clue Combat Guide")
                .color(TITLE_PINK)
                .build());

        // Wilderness warning
        if (encounter.isWilderness()) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("WILDERNESS")
                    .leftColor(WILDERNESS_RED)
                    .right("Risk your gear!")
                    .rightColor(WILDERNESS_RED)
                    .build());
        }

        // Monster name + level
        panelComponent.getChildren().add(LineComponent.builder()
                .left(encounter.getMonsterName())
                .leftColor(MONSTER_PINK)
                .right("Level " + encounter.getCombatLevel())
                .rightColor(MONSTER_PINK)
                .build());

        // Clue info
        panelComponent.getChildren().add(LineComponent.builder()
                .left(encounter.getClueTier() + " - " + encounter.getTriggerType())
                .leftColor(LABEL_TEAL)
                .build());

        // Prayer
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Prayer:")
                .leftColor(LABEL_TEAL)
                .right(encounter.getPrayer())
                .rightColor(PRAYER_PINK)
                .build());

        // Gear
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Gear:")
                .leftColor(LABEL_TEAL)
                .right(encounter.getGear())
                .rightColor(GEAR_PINK)
                .build());

        // Bring
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Bring:")
                .leftColor(LABEL_TEAL)
                .right(encounter.getBring())
                .rightColor(BRING_PINK)
                .build());

        // Tip
        panelComponent.getChildren().add(LineComponent.builder()
                .left(encounter.getTip())
                .leftColor(TIP_PINK)
                .build());

        return panelComponent.render(graphics);
    }
}
