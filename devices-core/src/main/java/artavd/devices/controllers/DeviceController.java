package artavd.devices.controllers;

import artavd.devices.core.Device;

import java.util.List;

public interface DeviceController {

    Device getDevice();

    void start();

    void stop();

    List<String> getProvidedMessages();

    MessageController getMessageController(String messageName);
}
