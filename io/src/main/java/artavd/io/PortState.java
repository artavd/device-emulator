package artavd.io;

public final class PortState {

    public static final PortState CONNECTED = new PortState("connected", null, false, true);
    public static final PortState DISCONNECTED = new PortState("disconnected", null, false, false);
    public static final PortState CONNECTING = new PortState("connecting", null, false, false);
    public static final PortState RECONNECTING = new PortState("reconnecting", null, false, false);
    public static final PortState DISCONNECTING = new PortState("disconnecting", null, false, false);
    public static final PortState ERROR = new PortState("error", null, true, false);

    private final String name;
    private final String description;
    private final boolean isError;
    private final boolean canTransmit;

    private PortState(String name, String description, boolean isError, boolean canTransmit) {
        this.name = name;
        this.description = description;
        this.isError = isError;
        this.canTransmit = canTransmit;
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
}
