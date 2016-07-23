package artavd.devices.emulation;

import artavd.devices.controllers.DeviceController;
import artavd.devices.core.Device;
import artavd.devices.core.DevicesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Repository
public final class DeviceEmulatorsRepository implements DevicesRepository {

    private static final Logger logger = LoggerFactory.getLogger(DeviceEmulatorsRepository.class);

    private final List<DeviceEmulator> registeredEmulators = new ArrayList<>();

    public DeviceEmulatorsRepository(ExecutorService emulatorExecutorService) {
        Scheduler scheduler = Schedulers.from(emulatorExecutorService);
        registeredEmulators.add(new DeviceEmulator("test", scheduler));
    }

    @Override
    public List<Device> getDevices() {
        return Collections.unmodifiableList(registeredEmulators);
    }

    @Override
    public Device getDevice(String name) {
        return registeredEmulators.stream()
                .filter(emulator -> emulator.getName().equals(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unknown device: " + name));
    }

    @Override
    @SuppressWarnings("SuspiciousMethodCalls")
    public DeviceController getController(Device device) {
        int index = registeredEmulators.indexOf(device);
        if (index == -1) {
            throw new IllegalArgumentException("Unknown device: " + device);
        }

        return registeredEmulators.get(index);
    }

    @PreDestroy
    private void onDestroy() {
        logger.info("Device Emulator repository closing...");
        registeredEmulators.forEach(DeviceEmulator::stop);
    }
}
