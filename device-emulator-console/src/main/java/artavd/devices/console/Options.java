package artavd.devices.console;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.StringWriter;
import java.util.Optional;

public class Options {

    @Option(name = "-c", aliases = { "--config" }, metaVar = "<FILE>",
            usage = "Path to configuration file.")
    private String configurationFile;

    @Option(name = "-d", aliases = { "--device" }, metaVar = "<NAME>",
            usage = "Name of emulated device (i.e. 'CL31', 'PTB220')",
            depends = "-p", forbids = "-c")
    private String deviceName;

    @Option(name = "-p", aliases = { "--port" }, metaVar = "<TYPE>",
            usage = "Type and number of port for transmitting messages from emulated device (i.e. 'TCP3001', 'COM2')",
            depends = "-d", forbids = "-c")
    private String portName;

    private CmdLineParser parser = new CmdLineParser(this);
    private CmdLineException exception = null;

    public boolean tryParseArguments(String[] args) {
        try {
            parser.parseArgument(args);
            return true;
        } catch (CmdLineException ex) {
            exception = ex;
            return false;
        }
    }

    public String getUsage() {
        StringWriter output = new StringWriter();
        parser.printUsage(output, null);
        String errorMessage = Optional.ofNullable(exception)
                .map(ex -> ex.getMessage() + System.lineSeparator())
                .orElse("");

        return String.format("%s%s", errorMessage, output.toString());
    }

    public String getConfigurationFile() {
        assertNoErrors();
        return configurationFile;
    }

    public String getDeviceName() {
        assertNoErrors();
        return deviceName;
    }

    public String getPortName() {
        assertNoErrors();
        return portName;
    }

    private void assertNoErrors() {
        assert exception == null;
    }
}
