package artavd.devices.core;

import artavd.devices.controllers.DeviceController;

import java.util.Optional;

public interface DevicesRepository {

    Optional<Device> getDevice(String name);

    Optional<DeviceController> getController(Device device);

    default Optional<DeviceController> getController(String name) {
        return getDevice(name).flatMap(this::getController);
    }
}
