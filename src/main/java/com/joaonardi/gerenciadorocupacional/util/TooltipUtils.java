package com.joaonardi.gerenciadorocupacional.util;

import javafx.animation.PauseTransition;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

public class TooltipUtils {

    public static void installWithDelay(Control control, Tooltip tooltip, int delayMillis) {
        control.setOnMouseEntered(e -> {
            PauseTransition delay = new PauseTransition(Duration.millis(delayMillis));
            delay.setOnFinished(event -> tooltip.show(control,
                    e.getScreenX() + 10, e.getScreenY() + 10));
            delay.play();
        });

        control.setOnMouseExited(e -> tooltip.hide());
    }
}
