package org.phoebus.framework.spi;

// Stub matching the real org.phoebus.framework.spi.AppInstance.
public interface AppInstance {
    AppDescriptor getAppDescriptor();
    default void raise() {}
    default void dispose() {}
}
