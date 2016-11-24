package artavd.devices.console;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.StringWriter;
import java.util.Optional;

public final class Options {

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

    @Option(name = "-s", aliases = { "--storage" }, metaVar = "<DIR>",
            usage = "Path to directory with device emulator configurations")
    private String storageDirectory = "devices";

    @Option(name = "--service", metaVar = "<PORT>",
            usage = "If specified then application will be started in server mode. It will listen specified TCP port " +
                    "and will provide REST API on this port.")
    private Integer restApiPort;

    private final CmdLineParser parser = new CmdLineParser(this);
    private String errorMessage = null;

    public boolean tryParseArguments(String[] args) {
        try {
            parser.parseArgument(args);
            if (configurationFile == null && deviceName == null && restApiPort == null) {
                errorMessage = "One of '-c' or '-d' options should be specified";
                return false;
            }
            return true;
        } catch (CmdLineException ex) {
            errorMessage = ex.getMessage();
            return false;
        }
    }

    public String getUsage() {
        StringWriter output = new StringWriter();
        parser.printUsage(output, null);
        String message = Optional.ofNullable(errorMessage)
                .map(em -> em + System.lineSeparator())
                .orElse("");

        return String.format("ERROR: %s%s", message, output.toString());
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

    public String getStorageDirectory() {
        assertNoErrors();
        return storageDirectory;
    }

    public Optional<Integer> getRestApiPort() {
        assertNoErrors();
        return Optional.ofNullable(restApiPort);
    }

    private void assertNoErrors() {
        assert errorMessage == null;
    }
}
