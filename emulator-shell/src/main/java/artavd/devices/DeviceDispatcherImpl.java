package artavd.devices;

import artavd.devices.io.Port;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
final class DeviceDispatcherImpl implements DeviceDispatcher {

    @Override
    public List<Port> getBoundsPorts(Device device) {
        return Collections.emptyList();
    }

    @Override
    public List<Device> getBoundsDevices(Port port) {
        return Collections.emptyList();
    }

    @Override
    public boolean bind(Device device, Port port) {
        throw new UnsupportedOperationException();
    }
}
