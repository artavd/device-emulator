package artavd.devices.emulation;

import artavd.devices.emulation.domain.DeviceEmulator;
import artavd.devices.emulation.domain.MessageProducer;
import artavd.devices.emulation.domain.MessageValueProducer;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static artavd.devices.utils.CommonUtils.asStream;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

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
            logger.debug("Storage doesn't contain configuration for '{}' device: {}", name, devicePath);
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

        DeviceEmulator emulator = DeviceEmulator.builder()
                .withName(name)
                .withScheduler(emulatorScheduler)
                .addMessageProducers(createMessageProducers(configuration.messages))
                .build();

        return Optional.of(emulator);
    }

    private List<MessageProducer> createMessageProducers(MessageConfiguration[] messageConfigurations) {
        return asStream(messageConfigurations)
                .map(configuration -> MessageProducer.builder()
                        .withName(configuration.name)
                        .withFormatString(configuration.message)
                        .withInterval(configuration.interval, TimeUnit.MILLISECONDS)
                        .addValueProducers(createValueProducers(configuration.values))
                        .build())
                .collect(toList());
    }

    private List<MessageValueProducer> createValueProducers(MessageValueConfiguration[] valueConfigurations) {
        return asStream(valueConfigurations)
                .map(configuration -> MessageValueProducer.builder()
                        .withName(configuration.name)
                        .addValues(configuration.source)
                        .build())
                .collect(toList());
    }

    private static final class DeviceEmulatorConfiguration {
        @JsonProperty
        String name;

        @JsonProperty
        MessageConfiguration[] messages;
    }

    private static final class MessageConfiguration {

        @JsonProperty
        String name;

        @JsonProperty
        long interval;

        @JsonProperty
        String message;

        @JsonProperty
        MessageValueConfiguration[] values;
    }

    private static final class MessageValueConfiguration {

        @JsonProperty
        String name;

        @JsonProperty
        String[] source;
    }
}
