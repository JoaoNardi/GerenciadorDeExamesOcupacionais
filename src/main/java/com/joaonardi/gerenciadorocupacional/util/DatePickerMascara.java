package com.joaonardi.gerenciadorocupacional.util;

import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatePickerMascara extends DatePicker {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public DatePickerMascara() {
        super();
        configurarMascara();
    }

    private void configurarMascara() {
        this.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? FORMATTER.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.trim().isEmpty()) return null;
                try {
                    return LocalDate.parse(string, FORMATTER);
                } catch (Exception e) {
                    return null;
                }
            }
        });

        this.getEditor().addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String text = this.getEditor().getText();
            String character = event.getCharacter();
            if (!character.matches("\\d")) {
                event.consume();
                return;
            }

            int caret = this.getEditor().getCaretPosition();
            StringBuilder sb = new StringBuilder(text);
            sb.insert(caret, character);

            String digits = sb.toString().replaceAll("\\D", "");

            if (digits.length() > 8) {
                event.consume();
                return;
            }

            StringBuilder masked = new StringBuilder();
            for (int i = 0; i < digits.length(); i++) {
                masked.append(digits.charAt(i));
                if ((i == 1 || i == 3) && i != digits.length() - 1) {
                    masked.append('/');
                }
            }

            this.getEditor().setText(masked.toString());
            this.getEditor().positionCaret(masked.length());
            event.consume();
        });
    }
}
