package artavd.devices.emulation;

import artavd.devices.controllers.DeviceController;
import artavd.devices.core.Device;
import artavd.devices.emulation.domain.DeviceEmulator;
import artavd.devices.emulation.domain.MessageProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Scheduler;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class DeviceEmulatorRepositoryTest {

    @Mock
    private DeviceEmulatorLoader firstLoader;

    @Mock
    private DeviceEmulatorLoader secondLoader;

    @Mock
    private DeviceEmulatorLoader thirdLoader;

    public DeviceEmulatorRepositoryTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldTakeFirstFoundDeviceEmulatorAsDevice() {
        // Given
        DeviceEmulator firstMatched = createMockDeviceEmulator("first");
        DeviceEmulator secondMatched = createMockDeviceEmulator("first");

        when(firstLoader.load(anyString())).thenReturn(Optional.empty());
        when(secondLoader.load(anyString())).thenReturn(Optional.of(firstMatched));
        when(thirdLoader.load(anyString())).thenReturn(Optional.of(secondMatched));

        DeviceEmulatorsRepository repository = new DeviceEmulatorsRepository(
                Arrays.asList(firstLoader, secondLoader, thirdLoader));

        // When
        Device loadedEmulator = repository.getDevice("test").get();

        // Then
        assertEquals(firstMatched, loadedEmulator);
        verify(firstLoader, times(1)).load("test");
        verify(secondLoader, times(1)).load("test");
        verify(thirdLoader, times(0)).load("test");
    }

    @Test
    public void shouldReturnNothingIfAllLoadersHaveNotLoadedDeviceEmulator() {
        // Given
        when(firstLoader.load(anyString())).thenReturn(Optional.empty());
        when(secondLoader.load(anyString())).thenReturn(Optional.empty());
        when(thirdLoader.load(anyString())).thenReturn(Optional.empty());

        DeviceEmulatorsRepository repository = new DeviceEmulatorsRepository(
                Arrays.asList(firstLoader, secondLoader, thirdLoader));

        // When
        Optional<Device> emulator = repository.getDevice("unknown");

        // Then
        assertFalse(emulator.isPresent());
    }

    @Test
    public void shouldTakeFirstFoundDeviceEmulatorAsController() {
        // Given
        DeviceEmulator firstMatched = createMockDeviceEmulator("first");
        DeviceEmulator secondMatched = createMockDeviceEmulator("first");

        when(firstLoader.load(anyString())).thenReturn(Optional.empty());
        when(secondLoader.load(anyString())).thenReturn(Optional.of(firstMatched));
        when(thirdLoader.load(anyString())).thenReturn(Optional.of(secondMatched));

        DeviceEmulatorsRepository repository = new DeviceEmulatorsRepository(
                Arrays.asList(firstLoader, secondLoader, thirdLoader));

        // When
        DeviceController loadedEmulator = repository.getController("test").get();

        // Then
        assertEquals(firstMatched, loadedEmulator);
        verify(firstLoader, times(1)).load("test");
        verify(secondLoader, times(1)).load("test");
        verify(thirdLoader, times(0)).load("test");
    }

    @Test
    public void shouldNotReloadAlreadyLoadedDeviceEmulator() {
        // Given
        final String deviceName = "test";
        DeviceEmulator loaded = createMockDeviceEmulator(deviceName);
        when(firstLoader.load(anyString())).thenReturn(Optional.of(loaded));

        DeviceEmulatorsRepository repository = new DeviceEmulatorsRepository(Arrays.asList(firstLoader));

        // When
        Device firstLoadedEmulator = repository.getDevice(deviceName).get();
        Device secondLoadedEmulator = repository.getDevice(deviceName).get();

        // Then
        assertEquals(loaded, firstLoadedEmulator);
        assertEquals(firstLoadedEmulator, secondLoadedEmulator);
        verify(firstLoader, times(1)).load(deviceName);
    }

    private DeviceEmulator createMockDeviceEmulator(String name) {
        return DeviceEmulator.builder()
                .withName(name)
                .withScheduler(mock(Scheduler.class))
                .addMessageProducer(MessageProducer.builder().withName("message").withFormatString("test").build())
                .build();
    }
}
