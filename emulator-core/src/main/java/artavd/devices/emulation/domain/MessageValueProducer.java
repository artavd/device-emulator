package artavd.devices.emulation.domain;

import artavd.devices.controllers.ValueController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class MessageValueProducer implements ValueController {

    private final String name;
    private final String[] values;
    private int currentValueIndex = 0;
    private String manualValue;

    private MessageValueProducer(String name, String[] values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public String nextValue() {
        if (manualValue != null) {
            return manualValue;
        }

        String result = values[currentValueIndex];
        currentValueIndex = (currentValueIndex == values.length - 1) ? 0 : currentValueIndex + 1;

        return result;
    }

    @Override
    public void setValue(String value) {
        manualValue = value;
    }

    @Override
    public void reset() {
        manualValue = null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        public String name;
        public List<String> values = new ArrayList<>();

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder addValues(String... values) {
            Arrays.stream(values).forEach(this.values::add);
            return this;
        }

        public Builder addValues(Collection<String> values) {
            this.values.addAll(values);
            return this;
        }

        public Builder addValue(String value) {
            this.values.add(value);
            return this;
        }

        public MessageValueProducer build() {
            if (name == null) {
                throw new IllegalStateException("Value name should be specified to build MessageValueProducer");
            }

            if (values.isEmpty()) {
                throw new IllegalStateException("At least one cyclic value should be specified to build MessageValueProducer");
            }

            return new MessageValueProducer(name, values.stream().toArray(String[]::new));
        }
    }
}
