package artavd.devices;

import artavd.devices.core.DevicesRepository;
import artavd.devices.dispatch.Dispatcher;
import artavd.devices.dispatch.DispatcherImpl;
import artavd.devices.dispatch.DispatcherLoaderFactory;
import artavd.devices.emulation.DeviceEmulatorsRepository;
import artavd.io.IOConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ExecutorService;

@Configuration
@Import({ DevicesCoreConfiguration.class, IOConfiguration.class })
public class EmulatorCoreConfiguration {

    @Bean
    public Dispatcher dispatcher() {
        return new DispatcherImpl();
    }

    @Bean
    public DispatcherLoaderFactory dispatcherLoaderFactory() {
        return new DispatcherLoaderFactory();
    }

    @Bean
    public DevicesRepository emulatorsRepository(
            @Qualifier("emulator") ExecutorService emulatorExecutorService) {
        return new DeviceEmulatorsRepository(emulatorExecutorService);
    }
}
