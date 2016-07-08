package artavd.devices.controllers;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface MessageController {

    void setPeriod(long period, TimeUnit unit);

    Set<String> getValues();

    void setValue(String name, String value);

    void resetValue(String name);
}
