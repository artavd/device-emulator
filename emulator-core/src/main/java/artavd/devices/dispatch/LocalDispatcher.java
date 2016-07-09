package artavd.devices.dispatch;

import artavd.devices.controllers.DeviceController;
import artavd.io.Port;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class LocalDispatcher implements Dispatcher {

    private List<PortDeviceBinding> dispatchedItems = new ArrayList<>();

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
        private DeviceController controller;
        private Port port;

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

        }

        public void unbind() {

        }

        public boolean isBound() {
            return true;
        }
    }
}
