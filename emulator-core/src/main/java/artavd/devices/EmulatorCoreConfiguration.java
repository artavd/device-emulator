package artavd.devices;

import artavd.devices.core.DevicesRepository;
import artavd.devices.dispatch.Dispatcher;
import artavd.devices.dispatch.DispatcherImpl;
import artavd.devices.dispatch.DispatcherLoaderFactory;
import artavd.devices.emulation.DeviceEmulatorLoader;
import artavd.devices.emulation.DeviceEmulatorsRepository;
import artavd.io.PortsFactory;
import artavd.io.PortsFactoryImpl;
import artavd.io.PortsRepository;
import artavd.io.PortsRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
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
    public DevicesRepository emulatorsRepository(List<DeviceEmulatorLoader> loaders) {
        return new DeviceEmulatorsRepository(loaders);
    }

    @Bean
    public PortsFactory portsFactory() {
        return new PortsFactoryImpl();
    }

    @Bean
    public PortsRepository portsRepository() {
        return new PortsRepositoryImpl();
    }
}
