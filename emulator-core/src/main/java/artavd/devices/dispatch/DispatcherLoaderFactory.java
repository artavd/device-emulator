package artavd.devices.dispatch;

import artavd.devices.controllers.DeviceController;
import artavd.devices.core.DevicesRepository;
import artavd.devices.core.DevicesUtils;
import artavd.io.Port;
import artavd.io.PortsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Component
public final class DispatcherLoaderFactory {

    @Autowired
    private DevicesRepository devicesRepository;

    @Autowired
    private PortsRepository portsRepository;

    public DispatcherLoader createFileLoader(Path configFile) {
        return new FileDispatcherLoader(configFile, portsRepository, devicesRepository);
    }

    public DispatcherLoader createSingleLoader(String deviceName, String portName) {
        return () -> {
            DeviceController device = DevicesUtils.getDeviceController(devicesRepository, deviceName);
            Port port = portsRepository.getOrCreatePort(portName);
            Map<DeviceController, Port[]> resultMap = new HashMap<>();
            resultMap.put(device, new Port[] { port} );
            return resultMap;
        };
    }
}
