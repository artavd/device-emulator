package artavd.spring.shell;

import org.springframework.boot.CommandLineRunner;
import org.springframework.shell.core.ExitShellRequest;
import org.springframework.shell.core.JLineShellComponent;

public final class ShellRunner implements CommandLineRunner {

    // TODO: Order to lower precedence

    private JLineShellComponent shell;

    public ShellRunner(JLineShellComponent shell) {
        this.shell = shell;
    }

    @Override
    public void run(String[] args) {
        shell.start();
        shell.promptLoop();
        ExitShellRequest exitShellRequest = shell.getExitShellRequest();
        shell.waitForComplete();

        // TODO: handle exit shell request
        System.out.println("FINISHED! " + exitShellRequest);
    }
}
