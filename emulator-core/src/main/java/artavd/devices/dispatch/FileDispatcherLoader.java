package artavd.devices.dispatch;

import artavd.devices.controllers.DeviceController;
import artavd.devices.core.DevicesRepository;
import artavd.io.PortsRepository;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class FileDispatcherLoader implements DispatcherLoader {

    public static final String FILENAME_PARAMETER = "FILENAME";

    @Autowired
    private PortsRepository portsRepository;

    @Autowired
    private DevicesRepository devicesRepository;

    @Override
    public Dispatcher load(Map<String, String> parameters) {
        String filename = parameters.get(FILENAME_PARAMETER);
        if (filename == null) {
            throw new IllegalArgumentException(String.format(
                    "Parameters for [%s] should contain '%s' parameter",
                    FileDispatcherLoader.class.getSimpleName(), FILENAME_PARAMETER));
        }

        DispatcherConfigurationItem[] configuration = loadFromFile(filename);
        return createDispatcher(configuration);
    }

    private Dispatcher createDispatcher(DispatcherConfigurationItem[] configuration) {
        Dispatcher dispatcher = new LocalDispatcher();
        for (DispatcherConfigurationItem item : configuration) {
            if (item.ports == null || item.ports.length == 0) {
                throw new IllegalArgumentException(String.format(
                        "Port list should be specified for all devices, but was empty for '%s'", item.device));
            }

            DeviceController device = devicesRepository.getController(item.device);
            Arrays.stream(item.ports)
                    .map(portName -> portsRepository.getOrCreatePort(portName))
                    .forEach(port -> dispatcher.bind(device, port));
        }

        return dispatcher;
    }

    private DispatcherConfigurationItem[] loadFromFile(String filename) {
        File configFile = new File(filename);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.reader(DispatcherConfigurationItem[].class).readValue(configFile);
        } catch (IOException ex) {
            throw new IllegalArgumentException(String.format(
                    "Cannot load device dispatcher configuration from file: '%s'", configFile.getAbsolutePath()), ex);
        }
    }

    @JsonPropertyOrder(value = { "device", "ports" })
    private static class DispatcherConfigurationItem {
        @JsonProperty
        String device;

        @JsonProperty
        String[] ports;
    }
}
