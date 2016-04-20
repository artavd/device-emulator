package artavd.shells.demu;

import org.springframework.shell.plugin.BannerProvider;
import org.springframework.shell.plugin.PromptProvider;
import org.springframework.stereotype.Component;

@Component
final class EmulatorShellView implements BannerProvider, PromptProvider {

    @Override
    public String getBanner() {
        return
                "  _____             _            ______                 _       _             \n" +
                " |  __ \\           (_)          |  ____|               | |     | |            \n" +
                " | |  | | _____   ___  ___ ___  | |__   _ __ ___  _   _| | __ _| |_ ___  _ __ \n" +
                " | |  | |/ _ \\ \\ / / |/ __/ _ \\ |  __| | '_ ` _ \\| | | | |/ _` | __/ _ \\| '__|\n" +
                " | |__| |  __/\\ V /| | (_|  __/ | |____| | | | | | |_| | | (_| | || (_) | |   \n" +
                " |_____/ \\___| \\_/ |_|\\___\\___| |______|_| |_| |_|\\__,_|_|\\__,_|\\__\\___/|_|   \n" +
                "                                                               " + getVersion() + "\n";
    }

    @Override
    public String getVersion() {
        String version = getClass().getPackage().getImplementationVersion();
        return version == null ? "" : version;
    }

    @Override
    public String getWelcomeMessage() {
        return String.format(
                "Welcome to %s! Enter 'help' command to get help about available commands or " +
                "'quit'/'exit' when you finish.", getProviderName());
    }

    @Override
    public String getProviderName() {
        return "Device Emulator Shell";
    }

    @Override
    public String getPrompt() {
        return "emulator-shell>";
    }
}
