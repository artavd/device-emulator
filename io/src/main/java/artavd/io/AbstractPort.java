package artavd.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.subjects.BehaviorSubject;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public abstract class AbstractPort implements Port {

    private static final Logger logger = LoggerFactory.getLogger(AbstractPort.class);

    private final String type;
    private final String name;
    private final BehaviorSubject<PortState> currentState = BehaviorSubject.create(PortState.DISCONNECTED);

    protected AbstractPort(String type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final PortState getCurrentState() {
        return currentState.getValue();
    }

    @Override
    public final Observable<PortState> getStateFeed() {
        return currentState.asObservable();
    }

    @Override
    public final Future<PortState> connect() {
        if (getCurrentState() == PortState.CONNECTED) {
            logger.warn("Port [ {} ]: is already connected");
            return CompletableFuture.completedFuture(PortState.CONNECTED);
        }

        logger.debug("Port [ {} ]: connecting...", getName());
        updateState(PortState.CONNECTING);
        return doConnect();
    }

    @Override
    public final Future<PortState> disconnect() {
        if (getCurrentState() == PortState.DISCONNECTED) {
            logger.warn("Port [ {} ]: is already disconnected");
            return CompletableFuture.completedFuture(PortState.DISCONNECTED);
        }

        logger.debug("Port [ {} ]: disconnecting...", getName());
        updateState(PortState.DISCONNECTING);
        return doDisconnect();
    }

    @Override
    public final boolean transmit(byte[] data) {
        PortState currentState = getCurrentState();
        if (!currentState.canTransmit()) {
            logger.warn("Port [ {} ]: can't transmit data to port with '{}' state", getName(), currentState.getName());
            return false;
        }

        logger.debug("Port [ {} ]: data transmitting. Package length: {}", getName(), data.length);
        return doTransmit(data);
    }

    @Override
    public final Observable<byte[]> getDataFeed() {
        return doReceive()
                .doOnError(error -> logger.error("Port [ {} ]: error while data receive", getName(), error))
                .doOnNext(data -> logger.debug("Port [ {} ]: data received: {}", getName(), data))
                .doOnCompleted(() -> logger.warn("Port [ {} ]: data receiving finished", getName()))
                .retry();

    }

    protected final void updateState(PortState state) {
        currentState.onNext(state);
    }

    protected abstract Future<PortState> doConnect();

    protected abstract Future<PortState> doDisconnect();

    protected abstract boolean doTransmit(byte[] data);

    protected abstract Observable<byte[]> doReceive();
}
