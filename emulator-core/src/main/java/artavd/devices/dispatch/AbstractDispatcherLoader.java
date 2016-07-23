package artavd.devices.dispatch;

import artavd.devices.controllers.DeviceController;
import artavd.devices.core.DevicesRepository;
import artavd.io.Port;
import artavd.io.PortsRepository;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public abstract class AbstractDispatcherLoader implements DispatcherLoader {

    private PortsRepository portsRepository;

    private DevicesRepository devicesRepository;

    protected AbstractDispatcherLoader(PortsRepository portsRepository, DevicesRepository devicesRepository) {
        this.portsRepository = portsRepository;
        this.devicesRepository = devicesRepository;
    }

    @Override
    public final Map<DeviceController, Port[]> load() {
        return loadNames()
                .entrySet().stream()
                .collect(toMap(
                        entry -> getDeviceController(entry.getKey(), devicesRepository),
                        entry -> parsePorts(entry.getValue())));
    }

    private Port[] parsePorts(String[] portNames) {
        return Arrays.stream(portNames)
                .map(portsRepository::getOrCreatePort)
                .toArray(Port[]::new);
    }

    protected abstract Map<String, String[]> loadNames();

    public static DeviceController getDeviceController(String name, DevicesRepository repository) {
        return repository.getController(name)
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "Device Controller for '%s' device is not registered!", name)));
    }
}
