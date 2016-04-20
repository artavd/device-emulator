package artavd;

import artavd.shells.demu.SmartListTableModel;
import org.fusesource.jansi.Ansi;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class TableTest {

    private final static String EXPECTED_TABLE =
            "\u001B[39;0mPORT      STATE        BOUND" + System.lineSeparator() +
            "TCP3001   \u001B[32mopened\u001B[m   \u001B[33mfalse\u001B[m" + System.lineSeparator() +
            "TCP3002   \u001B[31mclosed\u001B[m   \u001B[36mtrue\u001B[m" + System.lineSeparator() +
            "TCP3003   \u001B[31mclosed\u001B[m   \u001B[33mfalse\u001B[m" + System.lineSeparator() +
            "TCP3004   connecting   \u001B[36mtrue\u001B[m" + System.lineSeparator();

    @Test
    public void tableShouldBeRenderCorrectly() {
        // Given
        List<TestObject> ports = Arrays.asList(
                new TestObject("TCP3001", "opened", false),
                new TestObject("TCP3002", "closed", true),
                new TestObject("TCP3003", "closed", false),
                new TestObject("TCP3004", "connecting", true));

        // When
        String table = new SmartListTableModel<>(ports)
                .withHeaders("PORT", "STATE", "BOUND")
                .withAccessor(TestObject::getName)
                .withAccessor(TestObject::getState)
                .withAccessor(TestObject::isBound)
                .withColorizer(1, port ->
                        "opened".equals(port.state) ? Ansi.Color.GREEN :
                        "closed".equals(port.state) ? Ansi.Color.RED : null)
                .withColorizer(2, port -> port.isBound() ? Ansi.Color.CYAN : Ansi.Color.YELLOW)
                .render();

        // Then
        Assert.assertEquals(EXPECTED_TABLE, table);
    }

    private static class TestObject {
        private String name;
        private String state;
        private boolean bound;

        private TestObject(String name, String state, boolean bound) {
            this.name = name;
            this.state = state;
            this.bound = bound;
        }

        String getName() {
            return name;
        }

        String getState() {
            return state;
        }

        boolean isBound() {
            return bound;
        }
    }
}
