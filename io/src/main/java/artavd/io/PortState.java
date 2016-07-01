package artavd.io;

public class PortState {

    public static PortState OPENED = new PortState("opened", null, false);
    public static PortState CLOSED = new PortState("closed", null, false);
    public static PortState CONNECTING = new PortState("connecting", null, false);
    public static PortState RECONNECTING = new PortState("reconnecting", null, false);
    public static PortState ERROR = new PortState("error", null, true);

    private String name;
    private String description;
    private boolean isError;

    private PortState(String name, String description, boolean isError) {
        this.name = name;
        this.description = description;
        this.isError = isError;
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
}
