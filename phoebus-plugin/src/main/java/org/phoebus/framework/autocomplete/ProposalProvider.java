package org.phoebus.framework.autocomplete;

import java.util.List;

// Stub matching the real org.phoebus.framework.autocomplete.ProposalProvider.
public interface ProposalProvider {
    String getName();
    List<Proposal> lookup(String text);
}
