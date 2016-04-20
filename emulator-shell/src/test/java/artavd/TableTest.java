package artavd;

import artavd.devices.io.Port;
import artavd.devices.io.PortsRepository;
import artavd.devices.io.PortsRepositoryImpl;
import artavd.devices.shell.SmartListTableModel;
import org.fusesource.jansi.Ansi;
import org.junit.Test;
import org.springframework.shell.support.table.Table;
import org.springframework.shell.support.table.TableHeader;
import org.springframework.shell.support.table.TableRenderer;
import org.springframework.shell.support.util.AnsiEscapeCode;

public class TableTest {

    @Test
    public void test() {
        PortsRepository ports = new PortsRepositoryImpl();

        System.out.println(new SmartListTableModel<>(ports.getPorts())
                .withHeaders("PORT", "STATE", "BOUND")
                .withAccessor(Port::getName)
                .withAccessor(port -> port.getCurrentState().getName())
                .withAccessor(port -> true)
                .withColorizer(1, port -> port.getCurrentState().isError() ? Ansi.Color.RED : Ansi.Color.DEFAULT)
                .render());
    }

    @Test
    public void test2() {
        Table table = new Table()
                .addHeader(1, new TableHeader("PORT"))
                .addHeader(2, new TableHeader("STATE"))
                .addHeader(3, new TableHeader("BOUND"))
                .addRow(AnsiEscapeCode.decorate("Port Name", AnsiEscapeCode.FG_BLUE), "current state", "true");
        System.out.println(TableRenderer.renderTextTable(table));
    }
}
