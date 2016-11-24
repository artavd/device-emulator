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
import rx.Subscription;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

public final class DeviceEmulator implements Device, DeviceController {

    private static final Logger logger = LoggerFactory.getLogger(DeviceEmulator.class);

    private final String name;
    private final Scheduler scheduler;
    private final MessageProducer[] messageProducers;

    private final BehaviorSubject<DeviceState> stateSubject = BehaviorSubject.create(DeviceState.STOPPED);
    private final ConnectableObservable<DeviceMessage> messageFeed;

    private Subscription subscription;

    private DeviceEmulator(Builder builder) {
        this.name = builder.name;
        this.scheduler = builder.scheduler;
        this.messageProducers = builder.messageProducers.stream().toArray(MessageProducer[]::new);

        List<Observable<DeviceMessage>> feedsByProducers = Arrays.stream(messageProducers)
                .map(this::getFeedForProducer)
                .collect(toList());

        this.messageFeed = Observable
                .merge(feedsByProducers)
                .publish();
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
        return messageFeed;
    }

    @Override
    public Future<DeviceState> start() {
        if (getCurrentState() == DeviceState.STOPPED) {
            if (subscription == null) {
                subscription = messageFeed.connect();
            }

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
        return Arrays.stream(messageProducers)
                .map(MessageProducer::getName)
                .collect(toList());
    }

    @Override
    public MessageController getMessageController(String messageName) {
        return Arrays.stream(messageProducers)
                .filter(producer -> producer.getName().equals(messageName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "Unknown message: '%s'", messageName)));
    }

    private Observable<DeviceMessage> getFeedForProducer(MessageProducer producer) {
        long interval = producer.getIntervalInMilliseconds();
        return Observable
                .interval(interval, TimeUnit.MILLISECONDS, scheduler)
                .takeUntil(getStateFeed().filter(state -> state == DeviceState.STOPPED))
                .repeatWhen(_unused_ -> getStateFeed().filter(state -> state == DeviceState.STARTED), scheduler)
                .map(_unused_ -> producer.nextMessage());
    }

    private void updateState(DeviceState state) {
        DeviceState currentState = getCurrentState();
        if (currentState != state) {
            logger.info("Device [ {} ] moved from {} to {} state", getName(), currentState, state);
            stateSubject.onNext(state);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public final static class Builder {
        private String name;
        private Scheduler scheduler;
        private List<MessageProducer> messageProducers = new ArrayList<>();

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withScheduler(Scheduler scheduler) {
            this.scheduler = scheduler;
            return this;
        }

        public Builder withScheduler(Executor executor) {
            return withScheduler(Schedulers.from(executor));
        }

        public Builder addMessageProducers(MessageProducer... messageProducers) {
            Arrays.stream(messageProducers).forEach(this.messageProducers::add);
            return this;
        }

        public Builder addMessageProducers(Collection<MessageProducer> messageProducers) {
            this.messageProducers.addAll(messageProducers);
            return this;
        }

        public Builder addMessageProducer(MessageProducer messageProducer) {
            this.messageProducers.add(messageProducer);
            return this;
        }


        public DeviceEmulator build() {
            if (name == null || name.isEmpty()) {
                throw new IllegalStateException("Device name should be specified to build DeviceEmulator");
            }

            if (scheduler == null) {
                throw new IllegalStateException("Scheduler should be specified to build DeviceEmulator");
            }

            if (messageProducers.isEmpty()) {
                throw new IllegalStateException("At least one message producer should be specified to build DeviceEmulator");
            }

            return new DeviceEmulator(this);
        }
    }
}
