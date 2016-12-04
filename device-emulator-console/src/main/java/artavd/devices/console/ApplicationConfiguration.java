package artavd.devices.console;

import artavd.devices.emulation.DeviceEmulatorLoader;
import artavd.devices.emulation.FileSystemDeviceEmulatorLoader;
import artavd.devices.emulation.domain.DeviceEmulator;
import artavd.devices.emulation.domain.MessageProducer;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static artavd.devices.EmulatorCoreConfiguration.EMULATOR_EXECUTOR;
import static artavd.devices.EmulatorCoreConfiguration.IO_EXECUTOR;

@Configuration
public class ApplicationConfiguration {

    @Bean(name = EMULATOR_EXECUTOR)
    public ExecutorService emulatorExecutorService() {
        ThreadFactory emulatorThreadFactory = new BasicThreadFactory.Builder()
                .namingPattern("emulator-thread-%d")
                .daemon(true)
                .build();
        return Executors.newCachedThreadPool(emulatorThreadFactory);
    }

    @Bean(name = IO_EXECUTOR)
    public ExecutorService ioExecutorService() {
        ThreadFactory emulatorThreadFactory = new BasicThreadFactory.Builder()
                .namingPattern("io-thread-%d")
                .daemon(true)
                .build();
        return Executors.newCachedThreadPool(emulatorThreadFactory);
    }

    @Bean
    @ConditionalOnProperty(name = "devices.mock", havingValue = "false", matchIfMissing = true)
    public DeviceEmulatorLoader fileSystemDeviceEmulatorLoader(Options options) {
        return new FileSystemDeviceEmulatorLoader(
                Paths.get(options.getStorageDirectory()),
                emulatorExecutorService());
    }

    @Bean
    @ConditionalOnProperty(name = "devices.mock", havingValue = "true")
    public DeviceEmulatorLoader mockDeviceLoader() {
        return name -> {
            MessageProducer testMessage = MessageProducer.builder()
                    .withInterval(2, TimeUnit.SECONDS)
                    .withFormatString("Hello, guys, from " + name + "!" + System.lineSeparator())
                    .withName("test message")
                    .build();

            DeviceEmulator emulator = DeviceEmulator.builder()
                    .withName(name)
                    .withScheduler(emulatorExecutorService())
                    .addMessageProducers(testMessage)
                    .build();

            return Optional.of(emulator);
        };
    }
}
