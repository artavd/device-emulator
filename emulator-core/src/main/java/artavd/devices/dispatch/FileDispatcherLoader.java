package artavd.devices.dispatch;

import artavd.devices.core.DevicesRepository;
import artavd.io.PortsRepository;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class FileDispatcherLoader extends AbstractDispatcherLoader {

    private final Path configFile;

    public FileDispatcherLoader(Path configFile,
                                PortsRepository portsRepository,
                                DevicesRepository devicesRepository) {
        super(portsRepository, devicesRepository);
        this.configFile = configFile;
    }

    @Override
    protected Map<String, String[]> loadNames() {
        return Arrays.stream(readConfigItems(configFile))
                .peek(FileDispatcherLoader::checkConfigItem)
                .collect(toMap(item -> item.device, item -> item.ports));
    }

    private static DispatcherConfigurationItem[] readConfigItems(Path configFile) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.reader(DispatcherConfigurationItem[].class).readValue(configFile.toFile());
        } catch (IOException ex) {
            throw new IllegalArgumentException(String.format(
                    "Cannot load device dispatcher configuration from file: '%s'", configFile.toAbsolutePath()), ex);
        }
    }

    private static void checkConfigItem(DispatcherConfigurationItem item) {
        if (item.ports == null || item.ports.length == 0) {
            throw new IllegalArgumentException(String.format(
                    "Port list should be specified for all devices, but was empty for '%s'", item.device));
        }

        if (item.device == null || item.device.isEmpty()) {
            throw new IllegalArgumentException("Device name should be specified for all devices, but was not");
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
