package artavd.devices;

import artavd.io.IOConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan({ "artavd.devices.emulation", "artavd.devices.dispatch" })
@Import({ DevicesCoreConfiguration.class, IOConfiguration.class })
public class EmulatorCoreConfiguration {
}
