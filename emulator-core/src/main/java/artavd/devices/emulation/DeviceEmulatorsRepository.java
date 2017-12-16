package artavd.devices.emulation;

import artavd.devices.controllers.DeviceController;
import artavd.devices.core.Device;
import artavd.devices.core.DevicesRepository;
import artavd.devices.emulation.domain.DeviceEmulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public final class DeviceEmulatorsRepository implements DevicesRepository {

    private static final Logger logger = LoggerFactory.getLogger(DeviceEmulatorsRepository.class);

    private final List<DeviceEmulatorLoader> deviceEmulatorLoaders;
    private final Set<DeviceEmulator> registeredEmulators = new HashSet<>();

    public DeviceEmulatorsRepository(List<DeviceEmulatorLoader> deviceEmulatorLoaders) {
        this.deviceEmulatorLoaders = deviceEmulatorLoaders;
    }

    @Override
    public List<Device> getDevices() {
        return registeredEmulators.stream()
                .sorted(Comparator.comparing(DeviceEmulator::getName))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Device> getDevice(String name) {
        Optional<DeviceEmulator> result = registeredEmulators.stream()
                .filter(emulator -> emulator.getName().equals(name))
                .findAny();

        if (!result.isPresent()) {
            result = loadDevice(name);
        }

        return result.map(deviceEmulator -> (Device)deviceEmulator);
    }

    @Override
    public Optional<DeviceController> getController(Device device) {
        //noinspection SuspiciousMethodCalls
        if (!registeredEmulators.contains(device)) {
            return Optional.empty();
        }

        return Optional.of((DeviceController)device);
    }

    @PreDestroy
    private void onDestroy() {
        logger.debug("Device Emulator repository is being closed...");
        registeredEmulators.forEach(DeviceEmulator::stop);
    }

    // filter is present ensure
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private Optional<DeviceEmulator> loadDevice(String deviceName) {
        Optional<DeviceEmulator> loadedDevice = deviceEmulatorLoaders.stream()
                .map(loader -> loader.load(deviceName))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        loadedDevice.ifPresent(registeredEmulators::add);
        return loadedDevice;
    }
}
