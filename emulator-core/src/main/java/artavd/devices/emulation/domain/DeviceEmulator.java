package artavd.devices.emulation.domain;

import artavd.devices.controllers.DeviceController;
import artavd.devices.controllers.MessageController;
import artavd.devices.core.Device;
import artavd.devices.core.DeviceMessage;
import artavd.devices.core.DeviceState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Scheduler;
import rx.subjects.BehaviorSubject;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class DeviceEmulator implements Device, DeviceController {

    private static final Logger logger = LoggerFactory.getLogger(DeviceEmulator.class);

    private final String name;
    private final Scheduler scheduler;
    private final BehaviorSubject<DeviceState> stateSubject = BehaviorSubject.create(DeviceState.STOPPED);

    private DeviceEmulator(String name, Scheduler scheduler) {
        this.name = name;
        this.scheduler = scheduler;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DeviceState getCurrentState() {
        return stateSubject.getValue();
    }

    @Override
    public Observable<DeviceState> getStateFeed() {
        return stateSubject.asObservable();
    }

    @Override
    public Observable<DeviceMessage> getMessageFeed() {
        return Observable.interval(5, TimeUnit.SECONDS, scheduler).map(i -> new DeviceMessage() {
            @Override
            public String getName() {
                return "test message";
            }

            @Override
            public String getText() {
                return "test message text / " + i;
            }
        });
    }

    @Override
    public Future<DeviceState> start() {
        if (getCurrentState() == DeviceState.STOPPED) {
            updateState(DeviceState.STARTED);
        }

        return CompletableFuture.completedFuture(DeviceState.STARTED);
    }

    @Override
    public Future<DeviceState> stop() {
        if (getCurrentState() != DeviceState.STOPPED) {
            updateState(DeviceState.STOPPED);
        }

        return CompletableFuture.completedFuture(DeviceState.STOPPED);
    }

    @Override
    public Device getDevice() {
        return this;
    }

    @Override
    public List<String> getProvidedMessages() {
        return null;
    }

    @Override
    public MessageController getMessageController(String messageName) {
        return null;
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    private void updateState(DeviceState state) {
        DeviceState currentState = getCurrentState();
        if (currentState != state) {
            logger.info("Device [ {} ] moved from {} to {} state", getName(), currentState, state);
            stateSubject.onNext(state);
        }
    }

    public final static class Builder {
        private String name;
        private Scheduler scheduler;

        private Builder(String name) {
            this.name = name;
        }

        public Builder withScheduler(Scheduler scheduler) {
            this.scheduler = scheduler;
            return this;
        }

        public DeviceEmulator build() {
            return new DeviceEmulator(name, scheduler);
        }
    }
}
