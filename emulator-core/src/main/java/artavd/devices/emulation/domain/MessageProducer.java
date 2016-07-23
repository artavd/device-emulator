package artavd.devices.emulation.domain;

import artavd.devices.controllers.MessageController;
import artavd.devices.core.DeviceMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

public class MessageProducer implements MessageController {

    private final String name;
    private final long periodInMilliseconds;
    private final String formatString;
    private final MessageValueProducer[] valueProducers;

    public MessageProducer(String name, long periodInMilliseconds, String formatString,
                           MessageValueProducer[] valueProducers) {
        this.name = name;
        this.periodInMilliseconds = periodInMilliseconds;
        this.formatString = formatString;
        this.valueProducers = valueProducers;
    }

    public DeviceMessage nextMessage() {
        String messageText = String.format(
                formatString,
                Arrays.stream(valueProducers).map(MessageValueProducer::nextValue).toArray());
        return new DeviceMessage(getName(), messageText);
    }

    public String getName() {
        return name;
    }

    public long getPeriodInMilliseconds() {
        return periodInMilliseconds;
    }

    @Override
    public List<String> getValues() {
        return Arrays.stream(valueProducers)
                .map(MessageValueProducer::getName)
                .collect(toList());
    }

    @Override
    public void setValue(String valueName, String value) {
        getValueProducer(valueName).setValue(value);
    }

    @Override
    public void resetValue(String valueName) {
        getValueProducer(valueName).reset();
    }

    private MessageValueProducer getValueProducer(String valueName) {
        return Arrays.stream(valueProducers)
                .filter(vp -> vp.getName().equals(valueName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "Unknown message value: '%s'", valueName)));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private static final String formatStringPlaceholderPatternString =
                "%(\\d+\\$)?([-#+ 0,(\\<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])";

        private static final Pattern placeholderPattern = Pattern.compile(formatStringPlaceholderPatternString);

        private String name;
        private String formatString;
        private long period = 15000;
        private List<MessageValueProducer> valueProducers = new ArrayList<>();

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withFormatString(String formatString) {
            this.formatString = formatString;
            return this;
        }

        public Builder withPeriod(long period, TimeUnit timeUnit) {
            this.period = TimeUnit.MILLISECONDS.convert(period, timeUnit);
            return this;
        }

        public Builder addValueProducers(MessageValueProducer... valueProducers) {
            Arrays.stream(valueProducers).forEach(this.valueProducers::add);
            return this;
        }

        public Builder addValueProducers(Collection<MessageValueProducer> valueProducers) {
            this.valueProducers.addAll(valueProducers);
            return this;
        }

        public Builder addValueProducer(MessageValueProducer valueProducer) {
            this.valueProducers.add(valueProducer);
            return this;
        }

        public MessageProducer build() {
            if (name == null || name.isEmpty()) {
                throw new IllegalStateException("Message name should be specified to build MessageProducer");
            }

            if (formatString == null || formatString.isEmpty()) {
                throw new IllegalStateException("Format string should be specified to build MessageProducer");
            }

            checkValueProducersMatchFormatString();

            return new MessageProducer(
                    name,
                    period,
                    formatString,
                    valueProducers.stream().toArray(MessageValueProducer[]::new));
        }

        private void checkValueProducersMatchFormatString() {
            Matcher matcher = placeholderPattern.matcher(formatString);
            int placeholdersCount = 0;
            while (matcher.find()) {
                placeholdersCount++;
            }

            if (placeholdersCount != valueProducers.size()) {
                throw new IllegalStateException(String.format(
                        "Message format string is not compatible with specified value producers. " +
                        "Format placeholders count = %s, value producers count = %s",
                        placeholdersCount, valueProducers.size()));
            }
        }
    }
}
