package artavd.devices.emulation;

import artavd.devices.controllers.DeviceController;
import artavd.devices.core.Device;
import artavd.devices.core.DevicesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;

@Repository
public final class DeviceEmulatorsRepository implements DevicesRepository {

    private static final Logger logger = LoggerFactory.getLogger(DeviceEmulatorsRepository.class);

    private final List<DeviceEmulatorLoader> deviceEmulatorLoaders;
    private final Set<DeviceEmulator> registeredEmulators = new HashSet<>();

    @Autowired
    public DeviceEmulatorsRepository(List<DeviceEmulatorLoader> deviceEmulatorLoaders) {
        this.deviceEmulatorLoaders = deviceEmulatorLoaders;
    }

    @Override
    public Device getDevice(String name) {
        return registeredEmulators.stream()
                .filter(emulator -> emulator.getName().equals(name))
                .findAny()
                .orElseGet(() -> loadDevice(name));
    }

    @Override
    public DeviceController getController(Device device) {
        //noinspection SuspiciousMethodCalls
        if (!registeredEmulators.contains(device)) {
            throw new IllegalArgumentException("Unknown device: " + device);
        }

        return (DeviceController)device;
    }

    @PreDestroy
    private void onDestroy() {
        logger.info("Device Emulator repository closing...");
        registeredEmulators.forEach(DeviceEmulator::stop);
    }

    // filter is present ensure
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private DeviceEmulator loadDevice(String deviceName) {
        DeviceEmulator loaded = deviceEmulatorLoaders.stream()
                .map(loader -> loader.load(deviceName))
                .filter(Optional::isPresent)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        format("Device Emulator with '%s' name cannot be found", deviceName)))
                .get();

        registeredEmulators.add(loaded);
        return loaded;
    }
}
