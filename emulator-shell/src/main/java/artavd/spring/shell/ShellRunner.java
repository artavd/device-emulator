package artavd.spring.shell;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.shell.core.ExitShellRequest;
import org.springframework.shell.core.JLineShellComponent;

final class ShellRunner implements CommandLineRunner {

    @Autowired
    private JLineShellComponent shell;

    @Override
    public void run(String[] args) throws Exception {
        shell.start();
        shell.promptLoop();
        ExitShellRequest exitShellRequest = shell.getExitShellRequest();
        shell.waitForComplete();

        // TODO: handle exit shell request
        System.out.println("FINISHED! " + exitShellRequest);
    }
}
