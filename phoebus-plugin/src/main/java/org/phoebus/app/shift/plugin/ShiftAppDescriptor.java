package org.phoebus.app.shift.plugin;

import com.google.auto.service.AutoService;
import org.phoebus.framework.spi.AppDescriptor;
import org.phoebus.framework.spi.AppInstance;

/**
 * Registers the Shift Viewer as a Phoebus application.
 *
 * Phoebus discovers this via ServiceLoader (META-INF/services/org.phoebus.framework.spi.AppDescriptor).
 * Users open it from the Phoebus application menu or by typing "Shift Viewer" in the search bar.
 */
@AutoService(AppDescriptor.class)
public class ShiftAppDescriptor implements AppDescriptor {

    public static final String NAME = "shift_viewer";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDisplayName() {
        return "Shift Viewer";
    }

    @Override
    public AppInstance create() {
        return new ShiftAppInstance(this);
    }
}
