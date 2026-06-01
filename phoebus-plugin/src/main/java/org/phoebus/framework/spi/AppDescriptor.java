package org.phoebus.framework.spi;

// Stub matching the real org.phoebus.framework.spi.AppDescriptor.
// Remove this file and add the real Phoebus framework-spi dependency when
// integrating into an actual Phoebus build.
public interface AppDescriptor {
    String getName();
    default String getDisplayName() { return getName(); }
    AppInstance create();
}
