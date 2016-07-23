package artavd.devices.dispatch;

import artavd.devices.controllers.DeviceController;
import artavd.io.Port;

import java.util.Arrays;
import java.util.Map;

public class DispatcherUtils {

    public static void bindAll(Dispatcher dispatcher, Map<DeviceController, Port[]> bindings) {
        bindings.forEach((device, ports) -> bindAll(dispatcher, device, ports));
    }

    public static void unbindAll(Dispatcher dispatcher, Map<DeviceController, Port[]> bindings) {
        bindings.forEach((device, ports) -> unbindAll(dispatcher, device, ports));
    }

    public static void bindAll(Dispatcher dispatcher, DeviceController device, Port[] ports) {
        Arrays.stream(ports).forEach(port -> dispatcher.bind(device, port));
    }

    public static void unbindAll(Dispatcher dispatcher, DeviceController device, Port[] ports) {
        Arrays.stream(ports).forEach(port -> dispatcher.unbind(device, port));
    }
}
