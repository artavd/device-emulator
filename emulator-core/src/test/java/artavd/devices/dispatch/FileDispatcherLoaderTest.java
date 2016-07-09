package artavd.devices.dispatch;

import artavd.devices.controllers.DeviceController;
import artavd.devices.core.DevicesRepository;
import artavd.io.Port;
import artavd.io.PortsRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileDispatcherLoaderTest {

    @InjectMocks
    private FileDispatcherLoader loader;

    @Mock
    private PortsRepository portsRepository;

    @Mock
    private DevicesRepository devicesRepository;

    public FileDispatcherLoaderTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldBeCorrectlyLoadedFromFile() {
        // Given
        Map<String, String> parameters = new HashMap<>();
        parameters.put(FileDispatcherLoader.FILENAME_PARAMETER, "src/test/resources/dispatch/correct.json");

        DeviceController cl31 = Mockito.mock(DeviceController.class);
        when(devicesRepository.getController("CL31")).thenReturn(cl31);
        DeviceController lt31 = Mockito.mock(DeviceController.class);
        when(devicesRepository.getController("LT31")).thenReturn(lt31);
        DeviceController ptb220 = Mockito.mock(DeviceController.class);
        when(devicesRepository.getController("PTB220")).thenReturn(ptb220);

        Port tcp3001 = Mockito.mock(Port.class);
        when(portsRepository.getOrCreatePort("TCP3001")).thenReturn(tcp3001);
        Port tcp3002 = Mockito.mock(Port.class);
        when(portsRepository.getOrCreatePort("TCP3002")).thenReturn(tcp3002);
        Port com2 = Mockito.mock(Port.class);
        when(portsRepository.getOrCreatePort("COM2")).thenReturn(com2);
        Port com3 = Mockito.mock(Port.class);
        when(portsRepository.getOrCreatePort("COM3")).thenReturn(com3);
        Port com4 = Mockito.mock(Port.class);
        when(portsRepository.getOrCreatePort("COM4")).thenReturn(com4);

        // When
        Dispatcher dispatcher = loader.load(parameters);

        // Then
        assertEquals(Arrays.asList(cl31, lt31, ptb220), dispatcher.getDispatchedDevices());

        assertEquals(Arrays.asList(tcp3001, tcp3002, com2), dispatcher.getBoundPorts(cl31));
        assertEquals(Arrays.asList(com2, com4), dispatcher.getBoundPorts(lt31));
        assertEquals(Arrays.asList(com3), dispatcher.getBoundPorts(ptb220));

        assertEquals(Arrays.asList(cl31), dispatcher.getBoundDevices(tcp3001));
        assertEquals(Arrays.asList(cl31), dispatcher.getBoundDevices(tcp3002));
        assertEquals(Arrays.asList(cl31, lt31), dispatcher.getBoundDevices(com2));
        assertEquals(Arrays.asList(ptb220), dispatcher.getBoundDevices(com3));
        assertEquals(Arrays.asList(lt31), dispatcher.getBoundDevices(com4));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfAnyDevicesWithEmptyPortList() {
        // Given
        Map<String, String> parameters = new HashMap<>();
        parameters.put(FileDispatcherLoader.FILENAME_PARAMETER, "src/test/resources/dispatch/with-empty-ports.json");

        // When
        loader.load(parameters);

        // Then
        // Exception is thrown
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfAnyDevicesWithoutPortList() {
        // Given
        Map<String, String> parameters = new HashMap<>();
        parameters.put(FileDispatcherLoader.FILENAME_PARAMETER, "src/test/resources/dispatch/without-ports.json");

        // When
        loader.load(parameters);

        // Then
        // Exception is thrown
    }

    @Bean
    public DispatcherLoader dispatcherLoader() {
        return new FileDispatcherLoader();
    }


}
