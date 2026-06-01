package org.phoebus.app.shift.plugin;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.phoebus.framework.spi.AppDescriptor;
import org.phoebus.framework.spi.AppInstance;
import org.phoebus.ui.docking.DockItem;
import org.phoebus.ui.docking.DockPane;

/**
 * A running instance of the Shift Viewer inside a Phoebus DockItem (tab).
 * Multiple instances can be opened simultaneously.
 */
public class ShiftAppInstance implements AppInstance {

    private final ShiftAppDescriptor app;
    private DockItem tab;

    ShiftAppInstance(final ShiftAppDescriptor app) {
        this.app = app;
        Platform.runLater(this::createTab);
    }

    private void createTab() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/phoebus/app/shift/ui/ShiftTable.fxml"));
            Node content = loader.load();
            tab = new DockItem(this, content);
            DockPane.getActiveDockPane().getTabs().add(tab);
            tab.select();
        } catch (Exception e) {
            throw new RuntimeException("Failed to open Shift Viewer tab", e);
        }
    }

    @Override
    public AppDescriptor getAppDescriptor() {
        return app;
    }

    @Override
    public void raise() {
        Platform.runLater(() -> {
            if (tab != null) tab.select();
        });
    }

    @Override
    public void dispose() {
        Platform.runLater(() -> {
            if (tab != null && tab.getTabPane() != null) {
                tab.getTabPane().getTabs().remove(tab);
            }
        });
    }
}
