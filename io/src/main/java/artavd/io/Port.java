package artavd.io;

public interface Port {

    String getName();

    PortState getCurrentState();

    void open();

    void close();

    void transmit(byte[] data);
}
