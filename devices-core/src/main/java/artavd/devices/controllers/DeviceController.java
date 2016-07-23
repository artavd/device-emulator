package artavd.devices.controllers;

import artavd.devices.core.Device;
import artavd.devices.core.DeviceState;

import java.util.List;
import java.util.concurrent.Future;

public interface DeviceController {

    Device getDevice();

    Future<DeviceState> start();

    Future<DeviceState> stop();

    List<String> getProvidedMessages();

    MessageController getMessageController(String messageName);
}
