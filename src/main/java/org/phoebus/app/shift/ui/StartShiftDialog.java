package org.phoebus.app.shift.ui;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.phoebus.shift.client.ShiftClient;
import org.phoebus.shift.client.ShiftClientException;
import org.phoebus.shift.client.model.Shift;
import org.phoebus.shift.client.model.ShiftType;

import java.util.List;

/**
 * Dialog for starting a new shift. Calls the shift service on OK and returns
 * the created Shift, or empty if the user cancelled or the call failed.
 */
public class StartShiftDialog extends Dialog<Shift> {

    private final ComboBox<String> typeCombo = new ComboBox<>();
    private final TextField ownerField = new TextField();
    private final ShiftClient client;

    public StartShiftDialog(ShiftClient client) {
        this.client = client;
        setTitle("Start Shift");
        setHeaderText("Enter details for the new shift");

        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        try {
            List<ShiftType> types = client.listTypes();
            types.forEach(t -> typeCombo.getItems().add(t.getName()));
        } catch (Exception e) {
            typeCombo.getItems().addAll("Operations", "Safety", "Commissioning");
        }
        if (!typeCombo.getItems().isEmpty()) {
            typeCombo.getSelectionModel().selectFirst();
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("Type:"),  0, 0);
        grid.add(typeCombo,           1, 0);
        grid.add(new Label("Owner:"), 0, 1);
        grid.add(ownerField,          1, 1);

        getDialogPane().setContent(grid);

        setResultConverter(button -> {
            if (button != ButtonType.OK) return null;
            try {
                return client.startShift(typeCombo.getValue(), ownerField.getText().trim());
            } catch (ShiftClientException e) {
                new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR,
                        "Failed to start shift: " + e.getMessage()).showAndWait();
                return null;
            }
        });
    }
}
