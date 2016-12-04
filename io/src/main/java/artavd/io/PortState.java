package artavd.io;

import java.util.Arrays;
import java.util.Objects;

public final class PortState {

    public static final PortState CONNECTED = builder("CONNECTED").canTransmit(true).build();
    public static final PortState DISCONNECTED = builder("DISCONNECTED").build();
    public static final PortState CONNECTING = builder("CONNECTING").isTerminal(false).build();
    public static final PortState DISCONNECTING = builder("DISCONNECTING").isTerminal(false).build();

    private final String name;
    private final String description;
    private final boolean isError;
    private final boolean canTransmit;
    private final boolean isTerminal;

    private PortState(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.isError = builder.isError;
        this.canTransmit = builder.canTransmit;
        this.isTerminal = builder.isTerminal;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isError() {
        return isError;
    }

    public boolean canTransmit() {
        return canTransmit;
    }

    public boolean isTerminal() { return isTerminal; }

    public boolean in(PortState... states) {
        return Arrays.stream(states).anyMatch(state -> name.equals(state.name));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        PortState portState = (PortState) other;
        return Objects.equals(name, portState.name)
                && Objects.equals(description, portState.description)
                && isError == portState.isError
                && canTransmit == portState.canTransmit
                && isTerminal == portState.isTerminal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, isError, canTransmit, isTerminal);
    }

    public Builder builder() {
        return new Builder(this);
    }

    public Builder withDescription(String description) {
        return builder().withDescription(description);
    }

    public Builder withError(String message) {
        return builder().withError(message);
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder {
        public String name;
        public String description = null;
        public boolean isError = false;
        public boolean canTransmit = false;
        public boolean isTerminal = true;

        public Builder(PortState portState) {
            this.name = portState.name;
            this.description = portState.description;
            this.isError = portState.isError;
            this.isTerminal = portState.isTerminal;
            this.canTransmit = portState.canTransmit;
        }

        public Builder(String name) {
            this.name = name;
        }

        public Builder canTransmit(boolean canTransmit) {
            this.canTransmit = canTransmit;
            return this;
        }

        public Builder isTerminal(boolean isTerminal) {
            this.isTerminal = isTerminal;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withError(String description) {
            this.isError = true;
            this.description = description;
            return this;
        }

        public PortState build() {
            return new PortState(this);
        }
    }
}
