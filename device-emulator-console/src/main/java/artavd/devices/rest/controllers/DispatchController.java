package artavd.devices.rest.controllers;

import artavd.devices.controllers.DeviceController;
import artavd.devices.core.DevicesRepository;
import artavd.devices.dispatch.Dispatcher;
import artavd.devices.dispatch.DispatcherUtils;
import artavd.devices.rest.response.DeviceDto;
import artavd.devices.rest.response.DispatchDto;
import artavd.devices.rest.response.PortDto;
import artavd.io.Port;
import artavd.io.PortsRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class DispatchController {

    private Dispatcher dispatcher;
    private DevicesRepository devicesRepository;
    private PortsRepository portsRepository;

    public DispatchController(Dispatcher dispatcher,
                              DevicesRepository devicesRepository,
                              PortsRepository portsRepository) {
        this.dispatcher = dispatcher;
        this.devicesRepository = devicesRepository;
        this.portsRepository = portsRepository;
    }

    @GetMapping("/dispatch")
    public List<DispatchDto> getDispatch() {
        return dispatcher.getDispatchedDevices().stream()
                .map(device -> DispatchDto.from(device, dispatcher.getBoundPorts(device)))
                .collect(Collectors.toList());
    }

    @GetMapping("/dispatch/{deviceName}")
    public DispatchDto getDispatch(@PathVariable String deviceName) {
        DeviceController requestedDevice = ControllerUtils.getDevice(deviceName, devicesRepository);
        return DispatchDto.from(requestedDevice, dispatcher.getBoundPorts(requestedDevice));
    }

    @GetMapping("/devices/{deviceName}/ports")
    public List<PortDto> getBoundPorts(@PathVariable String deviceName) {
        DeviceController requestedDevice = ControllerUtils.getDevice(deviceName, devicesRepository);
        return dispatcher.getBoundPorts(requestedDevice).stream()
                .map(PortDto::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/ports/{portName}/devices")
    public List<DeviceDto> getBoundDevicesForPort(@PathVariable String portName) {
        Port requestedPort = ControllerUtils.getPort(portName, portsRepository);
        return dispatcher.getBoundDevices(requestedPort).stream()
                .map(DeviceDto::from)
                .collect(Collectors.toList());
    }

    @PostMapping("/dispatch/bind")
    public DispatchDto bind(
            @RequestParam("device") String deviceName,
            @RequestParam("port") String portName) {
        DeviceController requestedDevice = ControllerUtils.getDevice(deviceName, devicesRepository);
        Port requestedPorts = ControllerUtils.getPort(portName, portsRepository);

        dispatcher.bind(requestedDevice, requestedPorts);
        return getDispatch(deviceName);
    }

    @PostMapping("/dispatch/unbind")
    public DispatchDto unbind(
            @RequestParam("device") String deviceName,
            @RequestParam("port") String portName) {
        DeviceController requestedDevice = ControllerUtils.getDevice(deviceName, devicesRepository);
        Port requestedPorts = ControllerUtils.getPort(portName, portsRepository);

        dispatcher.unbind(requestedDevice, requestedPorts);
        return getDispatch(deviceName);
    }

    @PostMapping("/devices/{deviceName}/unbind")
    public DispatchDto unbindPorts(@PathVariable String deviceName) {
        DeviceController requestedDevice = ControllerUtils.getDevice(deviceName, devicesRepository);
        DispatcherUtils.unbindAll(dispatcher, requestedDevice);
        return getDispatch(deviceName);
    }

    @PostMapping("/ports/{portName}/unbind")
    public void unbindDevices(@PathVariable String portName) {
        Port port = ControllerUtils.getPort(portName, portsRepository);
        DispatcherUtils.unbindAll(dispatcher, port);
    }
}
