package artavd.io;

import rx.Observable;

import java.util.concurrent.Future;

public interface Port {

    String getType();

    String getName();

    PortState getCurrentState();

    Observable<PortState> getStateFeed();

    Future<PortState> connect();

    Future<PortState> disconnect();

    boolean transmit(byte[] data);

    Observable<byte[]> getDataFeed();
}
