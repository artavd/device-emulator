package artavd.devices.emulation;

import artavd.devices.emulation.domain.DeviceEmulator;

import java.util.Optional;

public interface DeviceEmulatorLoader {
    Optional<DeviceEmulator> load(String name);
}
