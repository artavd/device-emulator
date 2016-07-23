package artavd.devices.dispatch;

import artavd.devices.controllers.DeviceController;
import artavd.io.Port;

import java.util.Map;

public interface DispatcherLoader {

    Map<DeviceController, Port[]> load();
}
