package artavd.devices.rest.controllers;

import artavd.devices.controllers.DeviceController;
import artavd.devices.core.DevicesRepository;
import artavd.devices.rest.response.NoDataFoundException;
import artavd.io.Port;
import artavd.io.PortsRepository;

public class ControllerUtils {

    public static DeviceController getDevice(String name, DevicesRepository repository) {
        return repository
                .getController(name)
                .orElseThrow(() -> new NoDataFoundException(String.format( "Device [ %s ] is not loaded yet", name)));
    }

    public static Port getPort(String name, PortsRepository repository) throws NoDataFoundException {
        return repository
                .getPort(name)
                .orElseThrow(() -> new NoDataFoundException(String.format("Port [ %s ] is not exist", name)));
    }
}
