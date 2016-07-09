package artavd.devices.core;

import artavd.devices.controllers.DeviceController;
import org.springframework.stereotype.Repository;

@Repository
final class DevicesRepositoryImpl implements DevicesRepository {
    @Override
    public Device getDevice(String name) {
        return null;
    }

    @Override
    public DeviceController getController(Device device) {
        return null;
    }
}
