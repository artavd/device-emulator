package artavd.io;

import rx.Observable;

import java.io.PrintStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class ConsolePort extends AbstractPort {

    private static final PrintStream out = System.out;

    private final String descriptor;

    public ConsolePort(String name, String descriptor) {
        super(PortTypes.CONSOLE, name);
        this.descriptor = descriptor;
    }

    @Override
    protected Future<PortState> doConnect() {
        updateState(PortState.CONNECTED);
        return CompletableFuture.completedFuture(getCurrentState());
    }

    @Override
    protected Future<PortState> doDisconnect() {
        updateState(PortState.DISCONNECTED);
        return CompletableFuture.completedFuture(getCurrentState());
    }

    @Override
    protected boolean doTransmit(byte[] data) {
        String output = (descriptor == null ? "" : "[" + descriptor + "]: ") + new String(data);
        out.println(output);
        return true;
    }

    @Override
    protected Observable<byte[]> doReceive() {
        return Observable.never();
    }
}
