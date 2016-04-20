package artavd.devices.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPort implements Port {

    private static final Logger logger = LoggerFactory.getLogger(AbstractPort.class);

    private final String name;
    private PortState currentState;

    protected AbstractPort(String name) {
        this.name = name;
        setCurrentState(PortState.CLOSED);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PortState getCurrentState() {
        return currentState;
    }

    @Override
    public void open() {
        logger.info("Port [ {} ] opened", getName());
        setCurrentState(PortState.OPENED);
    }

    @Override
    public void close() {
        logger.info("Port [ {} ] closed", getName());
        setCurrentState(PortState.CLOSED);
    }

    @Override
    public void transmit(byte[] data) {
        logger.info("Port [ {} ]: data transmitted: {}", getName(), data);
    }

    private void setCurrentState(PortState state) {
        this.currentState = state;
    }
}
