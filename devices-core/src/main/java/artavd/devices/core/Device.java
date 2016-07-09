package artavd.devices.core;

import rx.Observable;

public interface Device {

    String getName();

    Observable<DeviceMessage> getMessages();
}
