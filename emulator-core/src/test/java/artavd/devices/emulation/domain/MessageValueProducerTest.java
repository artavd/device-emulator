package artavd.devices.emulation.domain;

import org.junit.Test;

import java.util.stream.IntStream;

import static artavd.devices.utils.CommonUtils.asArray;
import static org.junit.Assert.assertArrayEquals;

public class MessageValueProducerTest {

    @Test
    public void shouldProduceNextValueCyclic() {
        // Given
        MessageValueProducer producer = MessageValueProducer.builder()
                .withName("test")
                .addValues("1", "2", "3")
                .build();

        // When
        String[] result = produceValues(producer, 10);

        // Then
        assertArrayEquals(asArray("1", "2", "3", "1", "2", "3", "1", "2", "3", "1"), result);
    }

    @Test
    public void shouldProduceManualValueIfSpecified() {
        // Given
        MessageValueProducer producer = MessageValueProducer.builder()
                .withName("test")
                .addValues("1", "2", "3")
                .build();

        // When
        String[] producedBefore = produceValues(producer, 2);
        producer.setValue("test manual");
        String[] producedManual = produceValues(producer, 2);
        producer.reset();
        String[] producedAfter = produceValues(producer, 2);

        // Then
        assertArrayEquals(asArray("1", "2"), producedBefore);
        assertArrayEquals(asArray("test manual", "test manual"), producedManual);
        assertArrayEquals(asArray("3", "1"), producedAfter);
    }

    private String[] produceValues(MessageValueProducer producer, int count) {
        return IntStream.range(0, count)
                .mapToObj(_unused_ -> producer.nextValue())
                .toArray(String[]::new);
    }
}
