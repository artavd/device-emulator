package artavd.devices.dispatch;

import artavd.devices.controllers.DeviceController;
import artavd.devices.core.DevicesRepository;
import artavd.io.Port;
import artavd.io.PortsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import static artavd.devices.utils.CommonUtils.asArray;
import static artavd.devices.utils.CommonUtils.asSet;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileDispatcherLoaderTest {

    @Mock
    private PortsRepository portsRepository;

    @Mock
    private DevicesRepository devicesRepository;

    @Test
    public void shouldBeCorrectlyLoadedFromFile() {
        // Given
        // devices mock configuration
        DeviceController cl31 = Mockito.mock(DeviceController.class);
        DeviceController lt31 = Mockito.mock(DeviceController.class);
        DeviceController ptb220 = Mockito.mock(DeviceController.class);

        when(devicesRepository.getController("CL31")).thenReturn(Optional.of(cl31));
        when(devicesRepository.getController("LT31")).thenReturn(Optional.of(lt31));
        when(devicesRepository.getController("PTB220")).thenReturn(Optional.of(ptb220));

        // ports mock configuration
        Port tcp3001 = Mockito.mock(Port.class);
        Port tcp3002 = Mockito.mock(Port.class);
        Port com2 = Mockito.mock(Port.class);
        Port com3 = Mockito.mock(Port.class);
        Port com4 = Mockito.mock(Port.class);

        when(portsRepository.getOrCreatePort("TCP3001")).thenReturn(tcp3001);
        when(portsRepository.getOrCreatePort("TCP3002")).thenReturn(tcp3002);
        when(portsRepository.getOrCreatePort("COM2")).thenReturn(com2);
        when(portsRepository.getOrCreatePort("COM3")).thenReturn(com3);
        when(portsRepository.getOrCreatePort("COM4")).thenReturn(com4);

        Path configFile = Paths.get("src/test/resources/dispatch/correct.json");
        FileDispatcherLoader loader = new FileDispatcherLoader(configFile, portsRepository, devicesRepository);

        // When
        Map<DeviceController, Port[]> loadedDispatchInfo = loader.load();

        // Then
        assertEquals(asSet(cl31, lt31, ptb220), loadedDispatchInfo.keySet());

        assertArrayEquals(asArray(tcp3001, tcp3002, com2), loadedDispatchInfo.get(cl31));
        assertArrayEquals(asArray(com2, com4), loadedDispatchInfo.get(lt31));
        assertArrayEquals(asArray(com3), loadedDispatchInfo.get(ptb220));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfAnyDevicesWithEmptyPortList() {
        // Given
        DeviceController device = Mockito.mock(DeviceController.class);
        when(devicesRepository.getController(anyString())).thenReturn(Optional.of(device));

        Path configFile = Paths.get("src/test/resources/dispatch/with-empty-ports.json");
        FileDispatcherLoader loader = new FileDispatcherLoader(configFile, portsRepository, devicesRepository);

        // When
        loader.load();

        // Then
        // Exception is thrown
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfAnyDevicesWithoutPortList() {
        // Given
        Path configFile = Paths.get("src/test/resources/dispatch/without-ports.json");
        FileDispatcherLoader loader = new FileDispatcherLoader(configFile, portsRepository, devicesRepository);

        // When
        loader.load();

        // Then
        // Exception is thrown
    }
}
