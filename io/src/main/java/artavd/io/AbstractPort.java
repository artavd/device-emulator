package artavd.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.subjects.BehaviorSubject;

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
    public String getParameters() {
        return "";
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
    public final void connect() {
        PortState currentState = getCurrentState();
        if (!currentState.in(PortState.DISCONNECTED)) {
            logger.warn("Port [ {} ]: cannot connect to port in {} state", getName(), currentState.getName());
            return;
        }

        updateState(PortState.CONNECTING);
        doConnect();
    }

    @Override
    public final void disconnect() {
        PortState currentState = getCurrentState();
        if (currentState.in(PortState.DISCONNECTED, PortState.DISCONNECTING)) {
            logger.warn("Port [ {} ]: cannot disconnect from port in {} state", getName(), currentState.getName());
            return;
        }

        updateState(PortState.DISCONNECTING);
        doDisconnect();
    }

    @Override
    public final boolean transmit(byte[] data) {
        PortState currentState = getCurrentState();
        if (!currentState.canTransmit()) {
            logger.warn("Port [ {} ]: can't transmit data to port with {} state", getName(), currentState.getName());
            return false;
        }

        logger.trace("Port [ {} ]: data transmitting. Package length: {}", getName(), data.length);
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

    protected final void updateState(PortState.Builder state) {
        updateState(state.build());
    }

    protected final void updateState(PortState state) {
        PortState previousState = getCurrentState();
        if (!state.equals(previousState)) {
            logger.info("Port [ {} ] moved from {} to {} state", getName(), previousState.getName(), state.getName());
            currentState.onNext(state);
        }
    }

    protected abstract void doConnect();

    protected abstract void doDisconnect();

    protected abstract boolean doTransmit(byte[] data);

    protected Observable<byte[]> doReceive() {
        return Observable.never();
    }
}
