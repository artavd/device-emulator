package artavd.devices.dispatch;

import artavd.devices.controllers.DeviceController;
import artavd.devices.core.Device;
import artavd.io.Port;
import rx.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class LocalDispatcher implements Dispatcher {

    private final List<PortDeviceBinding> dispatchedItems = new ArrayList<>();

    @Override
    public List<DeviceController> getDispatchedDevices() {
        return dispatchedItems.stream()
                .map(PortDeviceBinding::getController)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<DeviceController> getBoundDevices(Port port) {
        return dispatchedItems.stream()
                .filter(binding -> Objects.equals(binding.getPort(), port))
                .map(PortDeviceBinding::getController)
                .collect(Collectors.toList());
    }

    @Override
    public List<Port> getBoundPorts(DeviceController device) {
        return dispatchedItems.stream()
                .filter(binding -> Objects.equals(binding.getController(), device))
                .map(PortDeviceBinding::getPort)
                .collect(Collectors.toList());
    }

    @Override
    public boolean bind(DeviceController device, Port port) {
        Optional<PortDeviceBinding> portDevice = findBinding(device, port);
        if (portDevice.isPresent()) {
            assert portDevice.get().isBound();
            return false;
        }

        port.connect();
        device.start();

        PortDeviceBinding newBinding = createPortDeviceBinding(device, port);
        newBinding.bind();
        dispatchedItems.add(newBinding);

        return true;
    }

    @Override
    public boolean unbind(DeviceController device, Port port) {
        Optional<PortDeviceBinding> portDevice = findBinding(device, port);
        if (!portDevice.isPresent()) {
            return false;
        }

        dispatchedItems.remove(portDevice.get());
        portDevice.get().unbind();

        if (getBoundPorts(device).isEmpty()) {
            device.stop();
        }

        if (getBoundDevices(port).isEmpty()) {
            port.disconnect();
        }

        return true;
    }

    protected PortDeviceBinding createPortDeviceBinding(DeviceController device, Port port) {
        return new PortDeviceBinding(device, port);
    }

    private Optional<PortDeviceBinding> findBinding(DeviceController device, Port port) {
        return dispatchedItems.stream()
                .filter(item -> Objects.equals(item.getController(), device)
                        && Objects.equals(item.getPort(), port))
                .findFirst();
    }

    private static class PortDeviceBinding {
        private final DeviceController controller;
        private final Port port;

        private Subscription subscription = null;

        public PortDeviceBinding(DeviceController controller, Port port) {
            this.controller = controller;
            this.port = port;
        }

        public DeviceController getController() {
            return controller;
        }

        public Port getPort() {
            return port;
        }

        public void bind() {
            Device device = getController().getDevice();
            device.getMessageFeed()
                    .subscribe(m -> {
                        if (port.getCurrentState().canTransmit()) {
                            port.transmit(m.getText().getBytes());
                        }
                    });
        }

        public void unbind() {
            subscription.unsubscribe();
            subscription = null;
        }

        public boolean isBound() {
            return subscription != null && !subscription.isUnsubscribed();
        }
    }
}
