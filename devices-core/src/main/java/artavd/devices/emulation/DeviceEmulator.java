package artavd.devices.emulation;

import artavd.devices.controllers.DeviceController;
import artavd.devices.controllers.MessageController;
import artavd.devices.core.Device;
import artavd.devices.core.DeviceMessage;
import rx.Observable;

import java.util.List;

public class DeviceEmulator implements Device, DeviceController {
    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public List<String> getProvidedMessages() {
        return null;
    }

    @Override
    public MessageController getMessageController(String messageName) {
        return null;
    }

    @Override
    public Observable<DeviceMessage> getMessages() {
        return null;
    }
}
