package artavd.devices.core;

public final class DeviceMessage {

    private final String name;
    private final String text;

    public DeviceMessage(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }
}
