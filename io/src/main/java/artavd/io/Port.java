package artavd.io;

import rx.Observable;

public interface Port {

    String getType();

    String getName();

    String getParameters();

    PortState getCurrentState();

    Observable<PortState> getStateFeed();

    void connect();

    void disconnect();

    boolean transmit(byte[] data);

    Observable<byte[]> getDataFeed();
}
