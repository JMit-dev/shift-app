package org.phoebus.framework.autocomplete;

// Stub matching the real org.phoebus.framework.autocomplete.Proposal.
public class Proposal {

    private final String value;
    private final String description;

    public Proposal(final String value) {
        this(value, "");
    }

    public Proposal(final String value, final String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() { return value; }
    public String getDescription() { return description; }

    @Override
    public String toString() { return value; }
}
