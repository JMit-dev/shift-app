package org.phoebus.ui.docking;

import javafx.scene.control.TabPane;

// Stub matching the real org.phoebus.ui.docking.DockPane.
public class DockPane extends TabPane {

    private static DockPane activePane;

    public static DockPane getActiveDockPane() {
        return activePane;
    }

    public static void setActiveDockPane(final DockPane pane) {
        activePane = pane;
    }
}
