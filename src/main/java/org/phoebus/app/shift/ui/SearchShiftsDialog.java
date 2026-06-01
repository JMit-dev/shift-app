package org.phoebus.app.shift.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.phoebus.shift.client.ShiftClient;
import org.phoebus.shift.client.ShiftClientException;
import org.phoebus.shift.client.model.Shift;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Modal search dialog. Queries the shift service with optional filters and
 * displays results in an inline table. Returns the list of matching shifts
 * when the user clicks OK, or null on cancel.
 */
public class SearchShiftsDialog extends Dialog<List<Shift>> {

    private final ObservableList<Shift> results = FXCollections.observableArrayList();

    public SearchShiftsDialog(ShiftClient client) {
        setTitle("Search Shifts");
        setHeaderText(null);
        getDialogPane().setPrefSize(740, 480);

        // --- filter form ---
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().add("All");
        try { client.listTypes().forEach(t -> typeCombo.getItems().add(t.getName())); }
        catch (Exception ignored) { typeCombo.getItems().addAll("Operations", "Safety", "Commissioning"); }
        typeCombo.getSelectionModel().selectFirst();

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("All", "Active", "Ended", "Closed");
        statusCombo.getSelectionModel().selectFirst();

        TextField ownerField = new TextField();
        ownerField.setPromptText("Any owner");

        DatePicker fromPicker = new DatePicker();
        fromPicker.setPromptText("Start from");
        DatePicker toPicker = new DatePicker();
        toPicker.setPromptText("Start to");

        Button searchBtn = new Button("Search");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(6);
        form.setPadding(new Insets(8));
        form.add(new Label("Type:"),   0, 0); form.add(typeCombo,   1, 0);
        form.add(new Label("Status:"), 2, 0); form.add(statusCombo, 3, 0);
        form.add(new Label("Owner:"),  0, 1); form.add(ownerField,  1, 1);
        form.add(new Label("From:"),   2, 1); form.add(fromPicker,  3, 1);
        form.add(new Label("To:"),     4, 1); form.add(toPicker,    5, 1);
        form.add(searchBtn,            5, 0);

        // --- results table ---
        TableView<Shift> table = new TableView<>(results);
        table.setPlaceholder(new Label("Run a search to see results"));

        TableColumn<Shift, Integer> colId     = new TableColumn<>("ID");
        TableColumn<Shift, String>  colType   = new TableColumn<>("Type");
        TableColumn<Shift, String>  colStatus = new TableColumn<>("Status");
        TableColumn<Shift, String>  colOwner  = new TableColumn<>("Owner");
        TableColumn<Shift, Date>    colStart  = new TableColumn<>("Start Date");

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(60);
        colOwner.setCellValueFactory(new PropertyValueFactory<>("owner"));
        colOwner.setPrefWidth(130);
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setPrefWidth(100);
        colType.setCellValueFactory(data -> {
            Shift s = data.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    s.getType() != null ? s.getType().getName() : "");
        });
        colType.setPrefWidth(130);

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        colStart.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colStart.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            @Override protected void updateItem(Date d, boolean empty) {
                super.updateItem(d, empty);
                setText((!empty && d != null) ? fmt.format(d) : null);
            }
        });
        colStart.setPrefWidth(160);

        //noinspection unchecked
        table.getColumns().addAll(colId, colType, colStatus, colOwner, colStart);

        // --- search action ---
        searchBtn.setOnAction(e -> {
            String type   = "All".equals(typeCombo.getValue())   ? null : typeCombo.getValue();
            String status = "All".equals(statusCombo.getValue()) ? null : statusCombo.getValue();
            String owner  = ownerField.getText().trim().isEmpty() ? null : ownerField.getText().trim();
            Date from = fromPicker.getValue() != null
                    ? Date.from(fromPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
            Date to   = toPicker.getValue() != null
                    ? Date.from(toPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
            try {
                List<Shift> found = client.findShifts(type, status, owner, from, to);
                results.setAll(found);
            } catch (ShiftClientException ex) {
                new Alert(Alert.AlertType.ERROR, "Search failed: " + ex.getMessage()).showAndWait();
            }
        });

        // --- layout ---
        VBox content = new VBox(8, form, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        content.setPadding(new Insets(8));
        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(button ->
                button == ButtonType.OK ? new ArrayList<>(results) : null);
    }
}
