package artavd.devices.controllers;

import java.util.List;

public interface DeviceController {

    void start();

    void stop();

    List<String> getProvidedMessages ();

    MessageController getMessageController(String messageName);
}
