package artavd.devices.emulation;

import artavd.devices.core.DeviceMessage;
import artavd.devices.core.DeviceState;
import artavd.devices.emulation.domain.DeviceEmulator;
import artavd.devices.emulation.domain.MessageProducer;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class FileSystemDeviceEmulatorLoaderTest {

    private static final Path STORAGE_PATH = Paths.get("src/test/resources/storage");

    @Test
    public void shouldCorrectlyLoadDeviceFromProvidedDirectory() {
        // Given
        ExecutorService executorService = mock(ExecutorService.class);
        FileSystemDeviceEmulatorLoader loader = new FileSystemDeviceEmulatorLoader(STORAGE_PATH, executorService);

        // When
        Optional<DeviceEmulator> loadedDevice = loader.load("test-device");

        // Then
        assertTrue(loadedDevice.isPresent());

        DeviceEmulator emulator = loadedDevice.get();
        assertEquals("test-device", emulator.getName());
        assertTrue(emulator.getCurrentState() == DeviceState.STOPPED);
        assertEquals(asList("dynamic message", "constant message"), emulator.getProvidedMessages());

        MessageProducer dynamic = (MessageProducer)emulator.getMessageController("dynamic message");
        assertEquals(asList("first-value", "second-value"), dynamic.getValues());
        assertEquals(MessageProducer.DEFAULT_INTERVAL, dynamic.getIntervalInMilliseconds());
        assertArrayEquals(new String[] {
                    "test one message first",
                    "test two message second",
                    "test three message third",
                    "test four message first",
                    "test one message second"
                },
                produceValues(dynamic, 5));

        MessageProducer constant = (MessageProducer)emulator.getMessageController("constant message");
        assertTrue(constant.getValues().isEmpty());
        assertEquals(5000, constant.getIntervalInMilliseconds());
        assertArrayEquals(new String[] {
                    "constant text",
                    "constant text",
                    "constant text",
                    "constant text",
                    "constant text",
                },
                produceValues(constant, 5));
    }

    private String[] produceValues(MessageProducer producer, int count) {
        return IntStream.range(0, count)
                .mapToObj(_unused_ -> producer.nextMessage())
                .map(DeviceMessage::getText)
                .toArray(String[]::new);
    }
}
