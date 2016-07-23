package artavd.devices.emulation;

import artavd.devices.emulation.domain.DeviceEmulator;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
        assertEquals("test-device", loadedDevice.get().getName());
    }
}
