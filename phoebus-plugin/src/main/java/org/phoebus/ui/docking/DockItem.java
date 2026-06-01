package org.phoebus.ui.docking;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import org.phoebus.framework.spi.AppInstance;

// Stub matching the real org.phoebus.ui.docking.DockItem (extends Tab).
public class DockItem extends Tab {

    public DockItem(final AppInstance application, final Node content) {
        setText(application.getAppDescriptor().getDisplayName());
        setContent(content);
        setClosable(true);
    }

    public void select() {
        if (getTabPane() != null) {
            getTabPane().getSelectionModel().select(this);
        }
    }
}
