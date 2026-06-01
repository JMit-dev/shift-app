package org.phoebus.app.shift.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.phoebus.shift.client.ShiftClient;
import org.phoebus.shift.client.ShiftClientException;
import org.phoebus.shift.client.model.Shift;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ShiftTableController {

    @FXML private TableView<Shift>            shiftTable;
    @FXML private TableColumn<Shift, Integer> colId;
    @FXML private TableColumn<Shift, String>  colType;
    @FXML private TableColumn<Shift, String>  colStatus;
    @FXML private TableColumn<Shift, String>  colOwner;
    @FXML private TableColumn<Shift, Date>    colStart;
    @FXML private Button                      startShiftBtn;
    @FXML private Button                      endShiftBtn;
    @FXML private Button                      closeShiftBtn;
    @FXML private Label                       statusLabel;
    @FXML private Label                       activeBannerLabel;
    @FXML private HBox                        activeBanner;
    @FXML private ComboBox<String>            filterStatus;
    @FXML private ComboBox<String>            filterType;

    private ShiftClient shiftClient;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private final ObservableList<Shift> allShifts = FXCollections.observableArrayList();
    private FilteredList<Shift> filteredShifts;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colOwner.setCellValueFactory(new PropertyValueFactory<>("owner"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colType.setCellValueFactory(data -> {
            Shift s = data.getValue();
            String name = (s.getType() != null) ? s.getType().getName() : "";
            return new javafx.beans.property.SimpleStringProperty(name);
        });

        colStart.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colStart.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(Date date, boolean empty) {
                super.updateItem(date, empty);
                setText((!empty && date != null) ? dateFormat.format(date) : null);
            }
        });

        filteredShifts = new FilteredList<>(allShifts, s -> true);
        shiftTable.setItems(filteredShifts);

        shiftTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, selected) -> updateButtonState(selected));

        filterStatus.getItems().add("All");
        filterType.getItems().add("All");
        filterStatus.getSelectionModel().selectFirst();
        filterType.getSelectionModel().selectFirst();

        shiftClient = ShiftClient.builder()
                .baseUrl(System.getProperty("shift.url", "http://localhost:8282/Shift/resources"))
                .username(System.getProperty("shift.username", ""))
                .password(System.getProperty("shift.password", ""))
                .build();

        handleRefresh();
    }

    private void updateButtonState(Shift selected) {
        endShiftBtn.setDisable(selected == null || !"Active".equalsIgnoreCase(selected.getStatus()));
        closeShiftBtn.setDisable(selected == null || !"Ended".equalsIgnoreCase(selected.getStatus()));
    }

    @FXML
    private void handleRefresh() {
        setStatus("Loading…");
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                List<Shift> shifts = shiftClient.listShifts();
                Platform.runLater(() -> {
                    allShifts.setAll(shifts);
                    populateFilters(shifts);
                    updateActiveBanner(shifts);
                    setStatus("Loaded " + shifts.size() + " shift(s)");
                });
            } catch (ShiftClientException e) {
                Platform.runLater(() -> {
                    updateActiveBanner(List.of());
                    setStatus("Error: " + e.getMessage());
                });
            }
        });
    }

    private void populateFilters(List<Shift> shifts) {
        String prevStatus = filterStatus.getValue();
        String prevType   = filterType.getValue();

        List<String> statuses = shifts.stream()
                .map(Shift::getStatus).filter(s -> s != null)
                .distinct().sorted().collect(Collectors.toList());
        filterStatus.getItems().setAll("All");
        filterStatus.getItems().addAll(statuses);
        filterStatus.setValue(statuses.contains(prevStatus) ? prevStatus : "All");

        List<String> types = shifts.stream()
                .filter(s -> s.getType() != null).map(s -> s.getType().getName())
                .distinct().sorted().collect(Collectors.toList());
        filterType.getItems().setAll("All");
        filterType.getItems().addAll(types);
        filterType.setValue(types.contains(prevType) ? prevType : "All");
    }

    private void updateActiveBanner(List<Shift> shifts) {
        Shift active = shifts.stream()
                .filter(s -> "Active".equalsIgnoreCase(s.getStatus()))
                .findFirst().orElse(null);

        if (active != null) {
            String type  = active.getType() != null ? active.getType().getName() : "—";
            String start = active.getStartDate() != null ? dateFormat.format(active.getStartDate()) : "—";
            activeBannerLabel.setText(
                    "Active Shift  |  Type: " + type +
                    "  |  Owner: " + active.getOwner() +
                    "  |  Started: " + start);
            activeBanner.setStyle("-fx-padding: 6 10 6 10; -fx-background-color: #2e7d32;");
        } else {
            activeBannerLabel.setText("No active shift");
            activeBanner.setStyle("-fx-padding: 6 10 6 10; -fx-background-color: #757575;");
        }
        activeBannerLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
    }

    @FXML
    private void handleFilterChange() {
        String statusVal = filterStatus.getValue();
        String typeVal   = filterType.getValue();
        filteredShifts.setPredicate(shift -> {
            boolean statusOk = statusVal == null || "All".equals(statusVal)
                    || statusVal.equals(shift.getStatus());
            boolean typeOk = typeVal == null || "All".equals(typeVal)
                    || (shift.getType() != null && typeVal.equals(shift.getType().getName()));
            return statusOk && typeOk;
        });
    }

    @FXML
    private void handleClearFilters() {
        filterStatus.getSelectionModel().selectFirst();
        filterType.getSelectionModel().selectFirst();
        filteredShifts.setPredicate(s -> true);
    }

    @FXML
    private void handleStartShift() {
        new StartShiftDialog(shiftClient).showAndWait().ifPresent(created -> {
            setStatus("Shift started: id=" + created.getId() + ", type=" +
                    (created.getType() != null ? created.getType().getName() : ""));
            handleRefresh();
        });
    }

    @FXML
    private void handleEndShift() {
        Shift selected = shiftTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        new EndShiftDialog(selected, shiftClient).showAndWait().ifPresent(ended -> {
            setStatus("Shift ended: id=" + ended.getId());
            handleRefresh();
        });
    }

    @FXML
    private void handleCloseShift() {
        Shift selected = shiftTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        new CloseShiftDialog(selected, shiftClient).showAndWait().ifPresent(closed -> {
            setStatus("Shift closed: id=" + closed.getId());
            handleRefresh();
        });
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }
}
