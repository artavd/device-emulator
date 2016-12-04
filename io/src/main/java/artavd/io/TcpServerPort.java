package artavd.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static artavd.io.PortState.*;

public class TcpServerPort extends AbstractPort {

    private static final Logger logger = LoggerFactory.getLogger(TcpServerPort.class);
    private static final PortState LISTENING = PortState.builder("LISTENING").build();

    private final Object closingLock = new Object();

    private final ExecutorService ioExecutorService;
    private final int port;

    private volatile AsynchronousServerSocketChannel listener;
    private volatile AsynchronousSocketChannel channel;

    public TcpServerPort(String name, int port, ExecutorService ioExecutorService) {
        super(PortTypes.TCP_SERVER, name);

        this.port = port;
        this.ioExecutorService = ioExecutorService;
    }

    @Override
    protected void doConnect() {
        try {
            listener = AsynchronousServerSocketChannel.open();
            listener.bind(new InetSocketAddress(port));
        } catch (IOException ex) {
            logger.error("Port [ {} ] cannot be connected", getName(), ex);
            updateState(DISCONNECTED.withError(ex.getMessage()));
            return;
        }

        accept();
    }

    @Override
    protected void doDisconnect() {
        closeChannel(true, null);
    }

    @Override
    protected boolean doTransmit(byte[] data) {
        ByteBuffer buffer = ByteBuffer.allocate(data.length);
        buffer.put(data);
        buffer.flip();

        Future<Integer> writtenFuture = channel.write(buffer);
        ioExecutorService.submit(() -> handleWrite(writtenFuture));

        return true;
    }

    private void accept() {
        Future<AsynchronousSocketChannel> acceptedFuture = listener.accept();
        ioExecutorService.submit(() -> handleAccept(acceptedFuture));
    }

    private void handleAccept(Future<AsynchronousSocketChannel> acceptedFuture) {
        try {
            updateState(LISTENING);
            channel = acceptedFuture.get();
            updateState(CONNECTED.withDescription("remote host: " + channel.getRemoteAddress()));
        } catch (Exception ex) {
            closeChannel(true, ex.getMessage());
        }
    }

    private void handleWrite(Future<Integer> writtenFuture) {
        try {
            writtenFuture.get();
        } catch (Exception ex) {
            String error = ex.getMessage();
            closeChannel(false, error);
            updateState(CONNECTING.withError(error));
            accept();
        }
    }

    private void closeChannel(boolean closeListener, String error) {
        synchronized (closingLock) {

            if (getCurrentState().in(DISCONNECTED)) {
                return;
            }

            try {
                if (channel != null) {
                    channel.close();
                }
            } catch (IOException ex) {
                logger.error("Port [ {} ] cannot close channel: {}", getName(), channel, ex);
            } finally {
                channel = null;
            }

            if (closeListener) {
                try {
                    if (listener != null) {
                        listener.close();
                    }
                } catch (IOException ex) {
                    logger.error("Port [ {} ] cannot stop listener: {}", getName(), listener, ex);
                } finally {
                    listener = null;
                }
            }

            PortState disconnectedState = error == null ? DISCONNECTED : DISCONNECTED.withError(error).build();
            updateState(disconnectedState);
        }
    }
}
