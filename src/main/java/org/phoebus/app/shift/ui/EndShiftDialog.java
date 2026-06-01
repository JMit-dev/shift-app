package org.phoebus.app.shift.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.phoebus.shift.client.ShiftClient;
import org.phoebus.shift.client.ShiftClientException;
import org.phoebus.shift.client.model.Shift;

import java.text.SimpleDateFormat;

/**
 * Confirmation dialog for ending the selected shift. Calls the shift service
 * on OK and returns the updated Shift, or empty if cancelled or call failed.
 */
public class EndShiftDialog extends Dialog<Shift> {

    public EndShiftDialog(Shift shift, ShiftClient client) {
        setTitle("End Shift");
        setHeaderText("End this shift?");

        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        String typeName = (shift.getType() != null) ? shift.getType().getName() : "—";
        String startStr = (shift.getStartDate() != null)
                ? new SimpleDateFormat("yyyy-MM-dd HH:mm").format(shift.getStartDate())
                : "—";

        VBox content = new VBox(8,
                new Label("ID:     " + shift.getId()),
                new Label("Type:   " + typeName),
                new Label("Owner:  " + shift.getOwner()),
                new Label("Start:  " + startStr));
        content.setPadding(new Insets(16));

        getDialogPane().setContent(content);

        setResultConverter(button -> {
            if (button != ButtonType.OK) return null;
            try {
                return client.endShift(shift.getId());
            } catch (ShiftClientException e) {
                new Alert(Alert.AlertType.ERROR,
                        "Failed to end shift: " + e.getMessage()).showAndWait();
                return null;
            }
        });
    }
}
