package artavd.devices.emulation.domain;

import org.junit.Test;
import rx.Scheduler;
import rx.schedulers.TestScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeviceEmulatorTest {

    @Test
    public void shouldNotProduceMessageIfNotStarted() {
        // Given
        TestScheduler scheduler = new TestScheduler();
        DeviceEmulator emulator = createEmulator(scheduler);

        // When
        List<String> messages = new ArrayList<>();
        emulator.getMessageFeed().subscribe(m -> messages.add(m.getText()));
        scheduler.advanceTimeBy(1, TimeUnit.HOURS);

        // Then
        assertTrue(messages.isEmpty());
    }

    @Test
    public void shouldProduceMessagesWhenStarted() {
        // Given
        TestScheduler scheduler = new TestScheduler();
        DeviceEmulator emulator = createEmulator(scheduler);

        List<String> messages = new ArrayList<>();
        emulator.getMessageFeed().subscribe(m -> messages.add(m.getText()));

        // When
        emulator.start();
        scheduler.advanceTimeBy(5, TimeUnit.SECONDS);

        // Then
        assertEquals(asList("yyy 1"), messages);

        // When
        messages.clear();
        scheduler.advanceTimeBy(5, TimeUnit.SECONDS);

        // Then
        assertEquals(asList("xxx", "yyy 2"), messages);

        // When
        messages.clear();
        scheduler.advanceTimeBy(5, TimeUnit.SECONDS);

        // Then
        assertEquals(asList("yyy 1"), messages);
    }

    @Test
    public void shouldStopProduceMessagesWhenStopped() {
        // Given
        TestScheduler scheduler = new TestScheduler();
        DeviceEmulator emulator = createEmulator(scheduler);

        List<String> messages = new ArrayList<>();
        emulator.getMessageFeed().subscribe(m -> messages.add(m.getText()));

        // When
        emulator.start();
        scheduler.advanceTimeBy(20, TimeUnit.SECONDS);

        // Then
        assertEquals(asList("yyy 1", "xxx", "yyy 2", "yyy 1", "xxx", "yyy 2"), messages);

        // When
        emulator.stop();
        messages.clear();
        scheduler.advanceTimeBy(1, TimeUnit.HOURS);

        // Then
        assertTrue(messages.isEmpty());
    }

    @Test
    public void shouldProduceMessagesAfterStartAndStopAndStartAgain() {
        // Given
        TestScheduler scheduler = new TestScheduler();
        DeviceEmulator emulator = createEmulator(scheduler);

        List<String> messages = new ArrayList<>();
        emulator.getMessageFeed().subscribe(m -> messages.add(m.getText()));

        // When
        emulator.start();
        scheduler.advanceTimeBy(10, TimeUnit.SECONDS);
        emulator.stop();
        scheduler.advanceTimeBy(1, TimeUnit.HOURS);
        messages.clear();
        emulator.start();
        scheduler.advanceTimeBy(20, TimeUnit.SECONDS);

        // Then
        assertEquals(asList("yyy 1", "xxx", "yyy 2", "yyy 1", "xxx", "yyy 2"), messages);
    }

    private DeviceEmulator createEmulator(Scheduler scheduler) {
        return DeviceEmulator.builder()
                .withName("emulator")
                .withScheduler(scheduler)
                .addMessageProducer(MessageProducer.builder()
                        .withName("message-1")
                        .withFormatString("xxx")
                        .withPeriod(10, TimeUnit.SECONDS)
                        .build())
                .addMessageProducer(MessageProducer.builder()
                        .withName("message-2")
                        .withFormatString("yyy %s")
                        .withPeriod(5, TimeUnit.SECONDS)
                        .addValueProducer(MessageValueProducer.builder()
                                .withName("value")
                                .addValues("1", "2")
                                .build())
                        .build())
                .build();
    }
}
