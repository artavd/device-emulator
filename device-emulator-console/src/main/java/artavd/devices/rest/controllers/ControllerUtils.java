package artavd.devices.rest.controllers;

import artavd.devices.controllers.DeviceController;
import artavd.devices.core.DevicesRepository;
import artavd.devices.rest.response.NoDataFoundException;
import artavd.io.Port;
import artavd.io.PortsRepository;

import java.util.Optional;

public class ControllerUtils {

    public static DeviceController getDevice(String name, DevicesRepository repository) {
        boolean loaded = repository.getDevices().stream().anyMatch(d -> d.getName().equals(name));
        if (!loaded) {
            throw new NoDataFoundException(String.format(
                    "Device [ %s ] is not loaded yet", name));
        }

        // No inspection due to loaded check above
        //noinspection OptionalGetWithoutIsPresent
        return repository.getController(name).get();
    }

    public static Port getPort(String name, PortsRepository repository) throws NoDataFoundException {
        Optional<Port> port = repository.getPort(name);
        if (!port.isPresent()) {
            throw new NoDataFoundException(String.format("Port [ %s ] is not exist", name));
        }

        return port.get();
    }
}
