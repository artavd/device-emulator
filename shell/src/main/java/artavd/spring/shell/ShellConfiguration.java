package artavd.spring.shell;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.CommandLine;
import org.springframework.shell.commands.ExitCommands;
import org.springframework.shell.commands.HelpCommands;
import org.springframework.shell.core.JLineShellComponent;

@Configuration
@ComponentScan(basePackages = {
        "org.springframework.shell.converters",
        "org.springframework.shell.plugin.support" })
class ShellConfiguration {

    @Bean
    public JLineShellComponent shell() {
        return new JLineShellComponent();
    }

    @Bean
    public CommandLine commandLine() {
        return new CommandLine(null, 3000, null);
    }

    @Bean
    public ShellRunner shellRunner() {
        return new ShellRunner(shell());
    }

    @Bean
    public ExitCommands exitCommands() {
        return new ExitCommands();
    }

    @Bean
    public HelpCommands helpCommands() {
        return new HelpCommands();
    }
}
