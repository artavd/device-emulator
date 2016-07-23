package artavd.io;

import rx.Observable;

import java.util.concurrent.Future;

public class TcpServerPort extends AbstractPort {

    public TcpServerPort(String name) {
        super(PortTypes.TCP_SERVER, name);
    }

    @Override
    protected Future<PortState> doConnect() {
        return null;
    }

    @Override
    protected Future<PortState> doDisconnect() {
        return null;
    }

    @Override
    protected boolean doTransmit(byte[] data) {
        return false;
    }

    @Override
    protected Observable<byte[]> doReceive() {
        return Observable.never();
    }
}
