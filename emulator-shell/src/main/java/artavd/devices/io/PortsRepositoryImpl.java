package artavd.devices.io;

import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public class PortsRepositoryImpl implements PortsRepository {

    private final List<Port> ports;

    public PortsRepositoryImpl() {

        this.ports = Arrays.asList(
                new TcpServerPort("TCP3001"),
                new TcpServerPort("TCP3002"),
                new TcpServerPort("TCP3003"),
                new TcpServerPort("TCP3004")
        );
    }

    @Override
    public List<Port> getPorts() {
        return ports;
    }

    @Override
    public boolean registerPort(Port port) {
        if (ports.contains(port)) {
            return false;
        }

        ports.add(port);
        return true;
    }
}
