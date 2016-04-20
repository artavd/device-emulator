package artavd.devices;

import artavd.devices.io.Port;

import java.util.List;

public interface DeviceDispatcher {

    List<Port> getBoundsPorts(Device device);

    List<Device> getBoundsDevices(Port port);

    boolean bind(Device device, Port port);
}
