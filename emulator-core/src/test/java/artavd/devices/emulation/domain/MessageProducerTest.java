package artavd.devices.emulation.domain;

import artavd.devices.core.DeviceMessage;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MessageProducerTest {

    @Test
    public void shouldProduceMessageWithCyclicValues() {
        // Given
        MessageValueProducer first = MessageValueProducer.builder().withName("first").addValues("11").build();
        MessageValueProducer second = MessageValueProducer.builder().withName("second").addValues("21", "22", "23").build();
        MessageValueProducer third = MessageValueProducer.builder().withName("third").addValues("31", "32").build();

        MessageProducer producer = MessageProducer.builder()
                .withName("test message")
                .withFormatString("--- %s +++ %s === %s 000")
                .addValueProducers(first, second, third)
                .build();

        // When
        List<DeviceMessage> messages = produceMessages(producer, 5);

        // Then
        assertTrue(messages.stream().allMatch(m -> m.getName().equals("test message")));
        assertEquals(
                Arrays.asList(
                        "--- 11 +++ 21 === 31 000",
                        "--- 11 +++ 22 === 32 000",
                        "--- 11 +++ 23 === 31 000",
                        "--- 11 +++ 21 === 32 000",
                        "--- 11 +++ 22 === 31 000"),
                messages.stream().map(DeviceMessage::getText).collect(toList()));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfFormatStringDoesNotMatchValueProviders() {
        // Given
        MessageValueProducer first = MessageValueProducer.builder().withName("first").addValues("11").build();
        MessageValueProducer second = MessageValueProducer.builder().withName("second").addValues("21", "22", "23").build();
        MessageValueProducer third = MessageValueProducer.builder().withName("third").addValues("31", "32").build();

        MessageProducer producer = MessageProducer.builder()
                .withName("test message")
                .withFormatString("--- %s +++ <missed_value> === %s 000")
                .addValueProducers(first, second, third)
                .build();

        // When
        List<DeviceMessage> messages = produceMessages(producer, 5);

        // Then
        // throw exception
    }

    private List<DeviceMessage> produceMessages(MessageProducer producer, int count) {
        return IntStream.range(0, count).mapToObj(_unused_ -> producer.nextMessage()).collect(toList());
    }
}
