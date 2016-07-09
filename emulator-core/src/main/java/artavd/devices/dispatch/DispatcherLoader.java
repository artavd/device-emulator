package artavd.devices.dispatch;

import java.util.Map;

public interface DispatcherLoader {

    Dispatcher load(Map<String, String> parameters);
}
