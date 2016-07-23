package artavd.devices.core;

import rx.Observable;

public interface Device {

    String getName();

    DeviceState getCurrentState();

    Observable<DeviceMessage> getMessageFeed();

    Observable<DeviceState> getStateFeed();
}
