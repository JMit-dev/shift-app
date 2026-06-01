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
 * Confirmation dialog for closing a shift (final state after Ended).
 * Returns the updated Shift on confirm, or empty if cancelled or call failed.
 */
public class CloseShiftDialog extends Dialog<Shift> {

    public CloseShiftDialog(Shift shift, ShiftClient client) {
        setTitle("Close Shift");
        setHeaderText("Close this shift? This action is final.");

        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String typeName = (shift.getType() != null) ? shift.getType().getName() : "—";
        String startStr = (shift.getStartDate() != null) ? fmt.format(shift.getStartDate()) : "—";
        String endStr   = (shift.getEndDate()   != null) ? fmt.format(shift.getEndDate())   : "—";

        VBox content = new VBox(8,
                new Label("ID:     " + shift.getId()),
                new Label("Type:   " + typeName),
                new Label("Owner:  " + shift.getOwner()),
                new Label("Start:  " + startStr),
                new Label("End:    " + endStr));
        content.setPadding(new Insets(16));

        getDialogPane().setContent(content);

        setResultConverter(button -> {
            if (button != ButtonType.OK) return null;
            try {
                return client.closeShift(shift.getId());
            } catch (ShiftClientException e) {
                new Alert(Alert.AlertType.ERROR,
                        "Failed to close shift: " + e.getMessage()).showAndWait();
                return null;
            }
        });
    }
}
