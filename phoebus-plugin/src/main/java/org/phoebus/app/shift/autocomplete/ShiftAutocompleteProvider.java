package org.phoebus.app.shift.autocomplete;

import com.google.auto.service.AutoService;
import org.phoebus.app.shift.ui.ShiftPreferences;
import org.phoebus.framework.autocomplete.Proposal;
import org.phoebus.framework.autocomplete.ProposalProvider;
import org.phoebus.shift.client.ShiftClient;
import org.phoebus.shift.client.model.Shift;
import org.phoebus.shift.client.model.ShiftType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides shift-related autocomplete proposals in the Phoebus search bar.
 *
 * Returns matching shift type names and shift owner names for any text the
 * user types. Phoebus discovers this via ServiceLoader
 * (META-INF/services/org.phoebus.framework.autocomplete.ProposalProvider).
 */
@AutoService(ProposalProvider.class)
public class ShiftAutocompleteProvider implements ProposalProvider {

    private final ShiftClient client = ShiftClient.builder()
            .baseUrl(ShiftPreferences.getShiftUrl())
            .username(ShiftPreferences.getUsername())
            .password(ShiftPreferences.getPassword())
            .build();

    @Override
    public String getName() {
        return "Shifts";
    }

    @Override
    public List<Proposal> lookup(final String text) {
        if (text == null || text.isBlank()) return List.of();
        String query = text.toLowerCase();

        List<Proposal> proposals = new ArrayList<>();

        try {
            List<ShiftType> types = client.listTypes();
            types.stream()
                    .filter(t -> t.getName() != null && t.getName().toLowerCase().contains(query))
                    .map(t -> new Proposal(t.getName(), "Shift type"))
                    .forEach(proposals::add);
        } catch (Exception ignored) {}

        try {
            List<Shift> shifts = client.listShifts();
            shifts.stream()
                    .filter(s -> s.getOwner() != null && s.getOwner().toLowerCase().contains(query))
                    .map(s -> new Proposal(s.getOwner(), "Shift owner"))
                    .distinct()
                    .forEach(proposals::add);
        } catch (Exception ignored) {}

        return proposals;
    }
}
