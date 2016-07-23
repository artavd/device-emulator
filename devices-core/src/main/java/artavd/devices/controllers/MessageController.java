package artavd.devices.controllers;

import java.util.List;

public interface MessageController {

    List<String> getValues();

    void setValue(String valueName, String value);

    void resetValue(String valueName);
}
