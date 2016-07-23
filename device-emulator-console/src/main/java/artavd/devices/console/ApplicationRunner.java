package artavd.devices.console;

import artavd.devices.controllers.DeviceController;
import artavd.devices.core.DeviceState;
import artavd.devices.core.DevicesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;

public class ApplicationRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);

    @Autowired
    private DevicesRepository devicesRepository;

    @Autowired
    @Qualifier("emulator")
    private ExecutorService emulatorExecutorService;

    @Autowired
    @Qualifier("ui")
    private ExecutorService uiExecutorService;

    @Override
    public void run(String... args) throws Exception {
        uiExecutorService.submit(this::waitForStop);

        devicesRepository.getDevices().stream()
                .map(device -> device.getStateFeed().toBlocking().last())
                .allMatch(state -> state == DeviceState.STOPPED);

        logger.info("All emulators has been stopped. Application is being closed...");

        uiExecutorService.shutdown();
        emulatorExecutorService.shutdown();
    }

    private void waitForStop() {
        Scanner scanner = new Scanner(System.in);

        logger.info("Press <Enter> to stop all emulators and close application");
        scanner.nextLine();

        logger.info("You've pressed <Enter>. Emulators are being stopped...");
        devicesRepository.getDevices().stream()
                .map(device -> devicesRepository.getController(device))
                .forEach(DeviceController::stop);
    }
}
