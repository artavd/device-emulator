package artavd.devices;

import artavd.devices.core.DevicesRepository;
import artavd.devices.dispatch.Dispatcher;
import artavd.devices.dispatch.DispatcherImpl;
import artavd.devices.dispatch.DispatcherLoaderFactory;
import artavd.devices.emulation.DeviceEmulatorLoader;
import artavd.devices.emulation.DeviceEmulatorsRepository;
import artavd.io.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Configuration
public class EmulatorCoreConfiguration {

    public static final String EMULATOR_EXECUTOR = "emulator";
    public static final String IO_EXECUTOR = "io";

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
    public PortsFactory portsFactory(Collection<PortsFactory.PortCreator> portCreators) {
        return new PortsFactoryImpl(portCreators);
    }

    @Bean
    public PortsRepository portsRepository() {
        return new PortsRepositoryImpl();
    }

    @Bean
    public PortsFactory.PortCreator consolePortCreator() {
        return new PortsFactory.PortCreator() {
            private static final String DESCRIPTOR = "CONSOLE";

            @Override
            public boolean match(String name) {
                return name.toUpperCase().startsWith(DESCRIPTOR);
            }

            @Override
            public Port create(Map<String, String> parameters) {
                String name = parameters.get(PortParameters.NAME);
                String descriptor = name.toUpperCase().equals(DESCRIPTOR) ? null : name;
                return new ConsolePort(name, descriptor);
            }
        };
    }

    @Bean
    public PortsFactory.PortCreator tcpServerPortCreator(@Qualifier(IO_EXECUTOR) ExecutorService executorService) {
        return new PortsFactory.PortCreator() {
            private static final String DESCRIPTOR = "TCP";

            @Override
            public boolean match(String name) {
                return name.toUpperCase().matches(DESCRIPTOR + "\\d{1,5}");
            }

            @Override
            public Port create(Map<String, String> parameters) {
                String name = parameters.get(PortParameters.NAME);
                int port = Integer.valueOf(name.substring(DESCRIPTOR.length()));
                return new TcpServerPort(name, port, executorService);
            }
        };
    }
}
