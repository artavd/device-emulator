package artavd.io;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IOConfiguration {

    @Bean
    public PortsRepository portsRepository() {
        return new PortsRepositoryImpl();
    }

    @Bean
    public PortsFactory portsFactory() {
        return new PortsFactoryImpl();
    }
}
