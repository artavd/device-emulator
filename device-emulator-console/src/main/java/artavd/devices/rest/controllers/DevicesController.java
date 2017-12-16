package artavd.devices.rest.controllers;

import artavd.devices.controllers.DeviceController;
import artavd.devices.core.DeviceState;
import artavd.devices.core.DevicesRepository;
import artavd.devices.rest.response.DeviceDto;
import artavd.devices.rest.response.NoDataFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/devices")
public class DevicesController {

    private static final Logger logger = LoggerFactory.getLogger(DevicesController.class);

    @Value("${rest.timeouts.startDevice:${rest.timeouts.default}}")
    private int startDeviceTimeout;

    @Value("${rest.timeouts.stopDevice:${rest.timeouts.default}}")
    private int stopDeviceTimeout;

    private DevicesRepository repository;

    public DevicesController(DevicesRepository repository) {
        this.repository = repository;
    }

    @GetMapping("")
    public List<DeviceDto> getDevices() {
        return repository.getDevices().stream()
                .map(DeviceDto::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/{name}")
    public DeviceDto getDevice(@PathVariable String name) {
        DeviceController requestedDevice = ControllerUtils.getDevice(name, repository);
        return DeviceDto.from(requestedDevice);
    }

    @PutMapping("/{name}/load")
    public DeviceDto loadDevice(@PathVariable String name) {
        Optional<DeviceController> requestedDevice = repository.getController(name);
        if (!requestedDevice.isPresent()) {
            throw new NoDataFoundException(String.format(
                    "Device [ %s ] cannot be loaded", name));
        }

        return DeviceDto.from(requestedDevice.get());
    }

    @PostMapping("/{name}/start")
    public DeviceDto startDevice(@PathVariable String name) throws Exception {
        DeviceController requestedDevice = ControllerUtils.getDevice(name, repository);
        doStartDevice(requestedDevice);
        return DeviceDto.from(requestedDevice);
    }

    @PostMapping("/{name}/stop")
    public DeviceDto stopDevice(@PathVariable String name) throws Exception {
        DeviceController requestedDevice = ControllerUtils.getDevice(name, repository);
        doStopDevice(requestedDevice);
        return DeviceDto.from(requestedDevice);
    }

    private void doStartDevice(DeviceController device) throws Exception {
        Future<DeviceState> state = device.start();
        try {
            state.get(startDeviceTimeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            logger.warn(
                    "Device [ {} ] is not started after timeout {} seconds",
                    device.getDevice().getName(), startDeviceTimeout);
        }
    }

    private void doStopDevice(DeviceController device) throws Exception {
        Future<DeviceState> state = device.stop();
        try {
            state.get(stopDeviceTimeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            logger.warn(
                    "Device [ {} ] is not stopped after timeout {} seconds",
                    device.getDevice().getName(), stopDeviceTimeout);
        }
    }
}
