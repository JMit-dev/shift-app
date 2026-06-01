# shift-app

A JavaFX shift viewer application for the Phoebus control system. Displays the shift table, lets operators start and end shifts, and is structured for eventual integration as a native Phoebus app plugin.

## Requirements

- Java 11+
- Maven 3.6+
- `shift-client` installed to local Maven repo (see below)

## Setup

**Step 1** — build and install `shift-client` first:

```bash
cd ../shift-client   # or wherever you cloned it
mvn clean install
```

**Step 2** — build shift-app:

```bash
cd shift-app
mvn compile
```

## Running

```bash
mvn javafx:run -Dshift.url=http://localhost:8080/Shift/resources
```

Additional system properties:

| Property | Default | Description |
|---|---|---|
| `shift.url` | `http://localhost:8080/Shift/resources` | Shift service base URL |
| `shift.username` | *(none)* | HTTP Basic Auth username |
| `shift.password` | *(none)* | HTTP Basic Auth password |

## Features

- **Shift table** — lists all shifts with ID, Type, Status, Owner, and Start Date columns
- **Refresh** — reloads shift data from the service
- **Start Shift** — dialog to create a new shift; populates the Type dropdown from the service
- **End Shift** — confirmation dialog for ending the selected active shift; button is disabled unless an Active shift is selected

## Running against the Docker dev stack

The `phoebus-olog-shift-module` repo includes a WireMock stub that serves a fake active shift. Start it with:

```bash
cd phoebus-olog-shift-module
docker compose up -d shift-mock
```

Then run shift-app pointing at the mock:

```bash
mvn javafx:run -Dshift.url=http://localhost:8282/Shift/resources
```

## Phoebus integration (future)

The app is structured to become a Phoebus `AppDescriptor` / `AppInstance` plugin. The controller and dialogs will move into a `DockItem` once the Phoebus framework dependency is added. The `ShiftApp` entry point will be replaced by a Phoebus SPI registration.
