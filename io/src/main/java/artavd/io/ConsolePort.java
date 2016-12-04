package artavd.io;

import rx.Observable;

import java.io.PrintStream;

public class ConsolePort extends AbstractPort {

    private static final PrintStream out = System.out;

    private final String descriptor;

    public ConsolePort(String name, String descriptor) {
        super(PortTypes.CONSOLE, name);
        this.descriptor = descriptor;
    }

    @Override
    protected void doConnect() {
        updateState(PortState.CONNECTED);
    }

    @Override
    protected void doDisconnect() {
        updateState(PortState.DISCONNECTED);
    }

    @Override
    protected boolean doTransmit(byte[] data) {
        String output = (descriptor == null ? "" : "[" + descriptor + "]: ") + new String(data);
        out.print(output);
        return true;
    }

    @Override
    protected Observable<byte[]> doReceive() {
        return Observable.never();
    }
}
