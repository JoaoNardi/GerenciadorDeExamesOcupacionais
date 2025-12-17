package com.joaonardi.gerenciadorocupacional.util;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;


public class ComboBoxCustom<T> extends ComboBox<T> {

    private List<Function<T, String>> displayFunctions = List.of();
    private FilteredList<T> filteredItems;
    private boolean isCommitting = false;
    private boolean isNavigating = false;

    public ComboBoxCustom() {
        setEditable(true);
        setupEventHandlers();
    }

    public void setItemsAndDisplay(ObservableList<T> items, List<Function<T, String>> functions) {
        this.displayFunctions = functions != null ? functions : List.of();
        this.filteredItems = new FilteredList<>(items != null ? items : FXCollections.observableArrayList());
        setItems(filteredItems);
        setupConverter();
    }

    private void setupEventHandlers() {
        getEditor().addEventFilter(KeyEvent.KEY_TYPED, this::handleKeyTyped);
        getEditor().addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
        setOnShowing(e -> handlePopupShowing());
        setOnHidden(e -> handlePopupHidden());
        focusedProperty().addListener((obs, oldVal, isFocused) -> {
            if (!isFocused && !isCommitting) {
                handleFocusLost();
            }
        });
    }

    private void handleKeyTyped(KeyEvent evt) {
        if (filteredItems == null || isCommitting || isNavigating) {
            return;
        }
        if (evt.getCharacter().chars().allMatch(Character::isISOControl)) {
            return;
        }
        Platform.runLater(() -> {
            String currentText = getEditor().getText();
            applyFilter(currentText);

            if (!isShowing() && !currentText.isEmpty()) {
                show();
            }
        });
    }

    private void handleKeyPressed(KeyEvent evt) {
        KeyCode code = evt.getCode();

        switch (code) {
            case ENTER:
                handleEnterKey(evt);
                break;

            case TAB:
                handleTabKey();
                break;

            case ESCAPE:
                handleEscapeKey(evt);
                break;

            case DOWN:
            case UP:
                handleNavigationKeys(evt);
                break;

            case BACK_SPACE:
            case DELETE:
                break;
        }
    }

    private void handleEnterKey(KeyEvent evt) {
        evt.consume();

        if (isShowing()) {
            commitSelection();
        } else {
            T matchingItem = findMatchingItem(getEditor().getText());
            if (matchingItem != null) {
                selectItem(matchingItem);
            }
        }
    }

    private void handleTabKey() {
        if (isShowing()) {
            commitSelection();
        }
    }

    private void handleEscapeKey(KeyEvent evt) {
        if (isShowing()) {
            hide();
            evt.consume();
        }
        if (getValue() != null) {
            getEditor().setText(buildDisplayText(getValue()));
        } else {
            getEditor().clear();
        }
    }

    private void handleNavigationKeys(KeyEvent evt) {
        isNavigating = true;

        if (!isShowing()) {
            show();
            evt.consume();
        }

        Platform.runLater(() -> isNavigating = false);
    }

    private void handlePopupShowing() {
        if (filteredItems != null) {
            filteredItems.setPredicate(item -> true);
        }
        configureListView();
    }

    private void handlePopupHidden() {
        isNavigating = false;
    }

    private void handleFocusLost() {
        String editorText = getEditor().getText();

        if (editorText == null || editorText.trim().isEmpty()) {
            setValue(null);
            getEditor().clear();
        } else if (getValue() == null) {
            T matchingItem = findMatchingItem(editorText);
            if (matchingItem != null) {
                selectItem(matchingItem);
            } else {
                getEditor().clear();
            }
        }
    }

    private void configureListView() {
        if (!(getSkin() instanceof ComboBoxListViewSkin<?> skin)) {
            return;
        }
        ListView<?> listView = (ListView<?>) skin.getPopupContent();
        if (listView == null) {
            return;
        }
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setOnMouseReleased(null);
        listView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                commitSelection();
            }
        });

        if (getValue() != null) {
            listView.getSelectionModel().select((Integer) getValue());
            listView.scrollTo((Integer) getValue());
        }
    }

    private void commitSelection() {
        if (isCommitting) {
            return;
        }

        T selectedItem = getSelectedItemFromListView();

        if (selectedItem == null && !filteredItems.isEmpty()) {
            selectedItem = filteredItems.get(0);
        }

        if (selectedItem != null) {
            selectItem(selectedItem);
        } else {
            hide();
        }
    }

    @SuppressWarnings("unchecked")
    private T getSelectedItemFromListView() {
        if (!(getSkin() instanceof ComboBoxListViewSkin<?> skin)) {
            return null;
        }

        ListView<?> listView = (ListView<?>) skin.getPopupContent();
        if (listView == null) {
            return null;
        }

        return (T) listView.getSelectionModel().getSelectedItem();
    }

    private void selectItem(T item) {
        if (item == null) {
            return;
        }

        isCommitting = true;

        try {
            if (filteredItems != null) {
                filteredItems.setPredicate(t -> true);
            }
            getSelectionModel().select(item);
            setValue(item);
            String displayText = buildDisplayText(item);
            getEditor().setText(displayText);
            getEditor().positionCaret(displayText.length());

        } finally {
            isCommitting = false;
            hide();
        }
    }

    private void setupConverter() {
        setConverter(new StringConverter<T>() {
            @Override
            public String toString(T item) {
                if (item == null) {
                    return "";
                }
                return buildDisplayText(item);
            }

            @Override
            public T fromString(String text) {
                if (isCommitting) {
                    return getValue();
                }
                return findMatchingItem(text);
            }
        });
    }

    private void applyFilter(String text) {
        if (filteredItems == null) {
            return;
        }
        String searchText = text == null ? "" : text.trim().toLowerCase();
        filteredItems.setPredicate(item -> {
            if (searchText.isEmpty()) {
                return true;
            }
            String itemText = buildDisplayText(item).toLowerCase();
            return itemText.contains(searchText);
        });
    }

    private T findMatchingItem(String text) {
        if (filteredItems == null || text == null) {
            return null;
        }
        String searchText = text.trim();
        return filteredItems.stream()
                .filter(item -> buildDisplayText(item).equalsIgnoreCase(searchText))
                .findFirst()
                .orElse(null);
    }

    private String buildDisplayText(T item) {
        if (item == null) {
            return "";
        }

        return displayFunctions.stream()
                .map(function -> function.apply(item))
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(" - "));
    }

    public void clear() {
        setValue(null);
        getEditor().clear();
        if (filteredItems != null) {
            filteredItems.setPredicate(item -> true);
        }
    }

    public void requestEditorFocus() {
        Platform.runLater(() -> {
            getEditor().requestFocus();
            getEditor().selectAll();
        });
    }
}