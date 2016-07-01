package artavd.devices.dispatch;

import artavd.devices.core.Device;
import artavd.io.Port;

import java.util.List;

public interface DeviceDispatcher {

    List<Port> getBoundsPorts(Device device);

    List<Device> getBoundsDevices(Port port);

    boolean bind(Device device, Port port);
}
