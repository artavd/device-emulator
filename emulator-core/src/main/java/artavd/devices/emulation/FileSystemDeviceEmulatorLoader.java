package artavd.devices.emulation;

import artavd.devices.emulation.domain.DeviceEmulator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static java.lang.String.format;

public final class FileSystemDeviceEmulatorLoader implements DeviceEmulatorLoader {

    private static final Logger logger = LoggerFactory.getLogger(FileSystemDeviceEmulatorLoader.class);

    private static final String FILE_NAME_PATTERN = "%s.json";
    private final Path emulatorConfigStoragePath;
    private final Scheduler emulatorScheduler;

    public FileSystemDeviceEmulatorLoader(Path emulatorConfigStoragePath, ExecutorService emulatorExecutorService) {
        this.emulatorConfigStoragePath = emulatorConfigStoragePath;
        this.emulatorScheduler = Schedulers.from(emulatorExecutorService);
    }

    @Override
    public Optional<DeviceEmulator> load(String name) {
        logger.debug("Loading of '{}' device configuration from storage: {}", name, emulatorConfigStoragePath);

        Path devicePath = emulatorConfigStoragePath.resolve(format(FILE_NAME_PATTERN, name));
        if (!Files.exists(devicePath)) {
            logger.debug("Storage doesn't contain configuration for '{}' device", name);
            return Optional.empty();
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            DeviceEmulatorConfiguration configuration = mapper
                    .reader(DeviceEmulatorConfiguration.class)
                    .readValue(devicePath.toFile());

            return createEmulatorFromConfiguration(name, configuration);
        } catch (IOException ex) {
            logger.error("Cannot load '{}' device configuration from storage: {}", name, emulatorConfigStoragePath, ex);
            return Optional.empty();
        }
    }

    private Optional<DeviceEmulator> createEmulatorFromConfiguration(String name, DeviceEmulatorConfiguration configuration) {
        if (!name.equals(configuration.name)) {
            logger.error("Loading of device configuration FAILED: device should have '{}' name, but has '{}'",
                    name, configuration.name);
            return Optional.empty();
        }

        DeviceEmulator emulator = DeviceEmulator.builder().withName(name).withScheduler(emulatorScheduler).build();
        return Optional.of(emulator);
    }

    private final static class DeviceEmulatorConfiguration {
        @JsonProperty
        String name;
    }
}
