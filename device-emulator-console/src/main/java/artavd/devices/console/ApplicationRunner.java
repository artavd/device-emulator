package artavd.devices.console;

import artavd.devices.controllers.DeviceController;
import artavd.devices.core.DeviceState;
import artavd.devices.dispatch.Dispatcher;
import artavd.devices.dispatch.DispatcherLoader;
import artavd.devices.dispatch.DispatcherLoaderFactory;
import artavd.devices.dispatch.DispatcherUtils;
import artavd.io.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rx.Observable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

import static artavd.devices.EmulatorCoreConfiguration.EMULATOR_EXECUTOR;
import static java.util.stream.Collectors.toList;

@Component
public class ApplicationRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);

    @Autowired
    private Options options;

    @Autowired
    private Dispatcher dispatcher;

    @Autowired
    private DispatcherLoaderFactory dispatcherLoaderFactory;

    @Autowired
    @Qualifier(EMULATOR_EXECUTOR)
    private ExecutorService emulatorExecutorService;

    @Override
    public void run(String... args) throws Exception {
        try {
            doRun();
        }
        catch (Exception ex) {
            logger.error("Device Emulator Console application FAILED with error: {}", ex.getMessage(), ex);
        }
        finally {
            emulatorExecutorService.shutdown();
        }
    }

    private void doRun() throws Exception {
        configureDispatcher();
        startEmulators();
        waitForUserStop();
        waitForEmulatorsStop();
        logger.info("All emulators has been stopped. Application is being closed...");
    }

    private void configureDispatcher() {
        DispatcherLoader loader = options.getConfigurationFile() != null
                ? dispatcherLoaderFactory.createFileLoader(Paths.get(options.getConfigurationFile()))
                : (options.getDeviceName() != null || options.getPortName() != null)
                    ? dispatcherLoaderFactory.createSingleLoader(options.getDeviceName(), options.getPortName())
                    : null;

        if (loader == null) {
            return;
        }

        logger.info("Dispatcher configuration is being loaded...");
        Map<DeviceController, Port[]> toDispatch = loader.load();
        DispatcherUtils.bindAll(dispatcher, toDispatch);
    }

    private void startEmulators() {
        logger.info("Device emulators are being started...");
        DispatcherUtils.startAll(dispatcher);
    }

    private void waitForUserStop() {
        logger.info("Press <Enter> to stop all emulators and close application");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            // Used this rude implementation instead because Scanner.nextLine doesn't support interruption
            while (!br.ready()) {
                Thread.sleep(200);
            }

            logger.info("Emulators are being stopped...");
            dispatcher.getDispatchedDevices().forEach(DeviceController::stop);
        } catch (InterruptedException | IOException e) {
            // Stop waiting of user input in case of thread interruption
        }
    }

    private void waitForEmulatorsStop() {
        List<Observable<DeviceState>> dispatchedDevicesStateFeeds = dispatcher.getDispatchedDevices().stream()
                .map(controller -> controller.getDevice().getStateFeed())
                .collect(toList());

        Observable.combineLatest(dispatchedDevicesStateFeeds, ApplicationRunner::combineStates)
                .takeUntil(states -> states.allMatch(s -> s == DeviceState.STOPPED))
                .toBlocking()
                .last();
    }

    private static Stream<DeviceState> combineStates(Object... states) {
        return Arrays.stream(states).map(state -> (DeviceState)state);
    }
}
