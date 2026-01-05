package com.joaonardi.gerenciadorocupacional.util;

import javafx.scene.control.DatePicker;
import javafx.scene.control.IndexRange;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatePickerCustom extends DatePicker {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String PLACEHOLDER = "  /  /    ";

    public DatePickerCustom() {
        super();
        if (!isSceneBuilder()) {
            setup();
        }
    }

    private boolean isSceneBuilder() {
        String name = getClass().getName();
        return name.contains("com.oracle.javafx.scenebuilder")
                || System.getProperty("java.class.path", "").contains("scenebuilder");
    }

    private void setup() {

        setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                if (!isEditable()) {
                    return date != null ? FORMATTER.format(date) : "";
                }
                return date != null ? FORMATTER.format(date) : PLACEHOLDER;
            }

            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.contains(" ")) return null;
                try {
                    return LocalDate.parse(string, FORMATTER);
                } catch (Exception e) {
                    return null;
                }
            }
        });

        getEditor().setText(PLACEHOLDER);
        getEditor().positionCaret(0);

        keyControl();
        stateControl();
    }


    private void keyControl() {

        getEditor().addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            int caret = getEditor().getCaretPosition();
            String text = getEditor().getText();

            if (e.getCode() == KeyCode.BACK_SPACE) {
                for (int i = caret - 1; i >= 0; i--) {
                    if (text.charAt(i) != '/') {
                        replaceAt(i, ' ');
                        getEditor().positionCaret(i);
                        break;
                    }
                }
                e.consume();
            }

            if (e.getCode() == KeyCode.DELETE) {
                if (caret >= text.length()) return;
                for (int i = caret; i >= 0; i--) {
                    if (text.charAt(i) != '/') {
                        replaceAt(i, ' ');
                        getEditor().positionCaret(caret + 1);
                        break;
                    }
                }
                e.consume();
            }
        });

        getEditor().addEventFilter(KeyEvent.KEY_TYPED, e -> {

            if (!isEditable() || !e.getCharacter().matches("\\d")) {
                e.consume();
                return;
            }

            if (getEditor().getSelection().getLength() > 0) {
                clearSelection();
            }

            String text = getEditor().getText();

            int pos = getEditor().getCaretPosition();
            while (pos < text.length() && text.charAt(pos) == '/') {
                pos++;
            }

            if (pos < text.length()) {
                replaceAt(pos, e.getCharacter().charAt(0));
                getEditor().positionCaret(pos + 1);
            }

            e.consume();
        });

    }

    private void stateControl() {

        showingProperty().addListener((obs, o, s) -> {
            if (!s) restauraPlaceholder();
        });

        getEditor().focusedProperty().addListener((obs, oldVal, focused) -> {
            if (!focused) {
                String text = getEditor().getText();

                if (!isDataValida(text)) {
                    setValue(null);
                    getEditor().clear();
                    restauraPlaceholder();
                }
            }
        });

    }

    private void restauraPlaceholder() {
        if (!isEditable()) return;
        if (getEditor().getText() == null || getEditor().getText().isBlank()) {
            getEditor().setText(PLACEHOLDER);
            getEditor().positionCaret(0);
        }
    }

    private void replaceAt(int index, char c) {
        StringBuilder sb = new StringBuilder(getEditor().getText());
        sb.setCharAt(index, c);
        getEditor().setText(sb.toString());
    }

    private void clearSelection() {
        IndexRange sel = getEditor().getSelection();
        if (sel.getLength() == 0) return;

        String text = getEditor().getText();
        StringBuilder sb = new StringBuilder(text);

        for (int i = sel.getStart(); i < sel.getEnd(); i++) {
            if (sb.charAt(i) != '/') {
                sb.setCharAt(i, ' ');
            }
        }
        getEditor().setText(sb.toString());
        getEditor().positionCaret(sel.getStart());
    }

    private boolean isDataValida(String text) {
        if (text == null) return false;
        if (text.contains(" ")) return false;

        try {
            LocalDate.parse(text, FORMATTER);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
