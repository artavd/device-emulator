package artavd.devices.dispatch;

import artavd.devices.controllers.DeviceController;
import artavd.io.Port;

import java.util.List;

public interface Dispatcher {

    List<DeviceController> getDispatchedDevices();

    List<DeviceController> getBoundDevices(Port port);

    List<Port> getBoundPorts(DeviceController device);

    boolean bind(DeviceController device, Port port);

    boolean unbind(DeviceController device, Port port);
}
