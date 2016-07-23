package artavd.devices.emulation;

import java.util.Optional;

public interface DeviceEmulatorLoader {
    Optional<DeviceEmulator> load(String name);
}
