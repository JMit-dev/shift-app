package org.phoebus.app.shift.ui;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Dialog for editing shift app preferences. Returns true if the user saved,
 * false/empty if cancelled. Changes are persisted via ShiftPreferences.
 */
public class PreferencesDialog extends Dialog<Boolean> {

    public PreferencesDialog() {
        setTitle("Shift Preferences");
        setHeaderText("Configure shift service connection");
        getDialogPane().setPrefWidth(480);

        TextField urlField            = new TextField(ShiftPreferences.getShiftUrl());
        TextField typesField          = new TextField(ShiftPreferences.getShiftTypes());
        TextField usernameField       = new TextField(ShiftPreferences.getUsername());
        PasswordField passwordField   = new PasswordField();
        passwordField.setText(ShiftPreferences.getPassword());
        TextField cacheTtlField       = new TextField(String.valueOf(ShiftPreferences.getCacheTtlSeconds()));
        TextField connectTimeoutField = new TextField(String.valueOf(ShiftPreferences.getConnectTimeoutMs()));
        TextField readTimeoutField    = new TextField(String.valueOf(ShiftPreferences.getReadTimeoutMs()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(16));

        int row = 0;
        grid.add(new Label("Service URL:"),              0, row); grid.add(urlField,            1, row++);
        grid.add(new Label("Shift types (comma-sep):"),  0, row); grid.add(typesField,          1, row++);
        grid.add(new Label("Username:"),                 0, row); grid.add(usernameField,       1, row++);
        grid.add(new Label("Password:"),                 0, row); grid.add(passwordField,       1, row++);
        grid.add(new Label("Cache TTL (seconds):"),      0, row); grid.add(cacheTtlField,       1, row++);
        grid.add(new Label("Connect timeout (ms):"),     0, row); grid.add(connectTimeoutField, 1, row++);
        grid.add(new Label("Read timeout (ms):"),        0, row); grid.add(readTimeoutField,    1, row++);

        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(button -> {
            if (button != ButtonType.OK) return false;
            ShiftPreferences.setShiftUrl(urlField.getText().trim());
            ShiftPreferences.setShiftTypes(typesField.getText().trim());
            ShiftPreferences.setUsername(usernameField.getText().trim());
            ShiftPreferences.setPassword(passwordField.getText());
            try { ShiftPreferences.setCacheTtlSeconds(Integer.parseInt(cacheTtlField.getText())); } catch (NumberFormatException ignored) {}
            try { ShiftPreferences.setConnectTimeoutMs(Integer.parseInt(connectTimeoutField.getText())); } catch (NumberFormatException ignored) {}
            try { ShiftPreferences.setReadTimeoutMs(Integer.parseInt(readTimeoutField.getText())); } catch (NumberFormatException ignored) {}
            return true;
        });
    }
}
