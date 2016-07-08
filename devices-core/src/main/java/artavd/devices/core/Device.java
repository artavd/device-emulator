package artavd.devices.core;

import rx.Observable;

public interface Device {

    Observable<DeviceMessage> getMessages();
}
