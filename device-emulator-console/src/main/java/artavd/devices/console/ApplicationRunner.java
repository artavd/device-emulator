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
import rx.Observable;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ApplicationRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);

    @Autowired
    private Options options;

    @Autowired
    private Dispatcher dispatcher;

    @Autowired
    private DispatcherLoaderFactory dispatcherLoaderFactory;

    @Autowired
    @Qualifier("emulator")
    private ExecutorService emulatorExecutorService;

    @Autowired
    @Qualifier("ui")
    private ExecutorService uiExecutorService;

    @Override
    public void run(String... args) throws Exception {
        loadDispatcher();
        uiExecutorService.submit(this::waitForStop);

        List<Observable<DeviceState>> dispatchedDevicesStateFeeds = dispatcher.getDispatchedDevices().stream()
                .map(controller -> controller.getDevice().getStateFeed())
                .collect(toList());

        Observable.combineLatest(dispatchedDevicesStateFeeds, ApplicationRunner::combineStates)
                .takeUntil(states -> states.allMatch(s -> s == DeviceState.STOPPED))
                .toBlocking()
                .last();

        logger.info("All emulators has been stopped. Application is being closed...");

        uiExecutorService.shutdown();
        emulatorExecutorService.shutdown();
    }

    private void loadDispatcher() {
        DispatcherLoader loader;
        loader = options.getConfigurationFile() == null
                ? dispatcherLoaderFactory.createSingleLoader(options.getDeviceName(), options.getPortName())
                : dispatcherLoaderFactory.createFileLoader(Paths.get(options.getConfigurationFile()));

        Map<DeviceController, Port[]> toDispatch = loader.load();
        DispatcherUtils.bindAll(dispatcher, toDispatch);
    }

    private void waitForStop() {
        Scanner scanner = new Scanner(System.in);

        logger.info("Press <Enter> to stop all emulators and close application");
        scanner.nextLine();

        logger.info("Emulators are being stopped...");
        dispatcher.getDispatchedDevices().stream().forEach(DeviceController::stop);
    }

    private static Stream<DeviceState> combineStates(Object... states) {
        return Arrays.stream(states).map(state -> (DeviceState)state);
    }
}
