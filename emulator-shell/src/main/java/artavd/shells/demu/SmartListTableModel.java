package artavd.shells.demu;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiString;
import org.springframework.shell.support.util.StringUtils;
import org.springframework.shell.table.TableModel;

import java.util.*;
import java.util.function.Function;

public final class SmartListTableModel<T> extends TableModel {

    private final static String COLUMN_DELIMITER = "   ";

    private List<T> items;
    private List<Function<T, Object>> accessors;
    private Map<Integer, Function<T, Ansi.Color>> colorizers;
    private String[] headers;

    public SmartListTableModel(List<T> items) {
        this.items = items;
        this.accessors = new ArrayList<>();
        this.colorizers = new HashMap<>();
    }

    public SmartListTableModel<T> withAccessor(Function<T, Object> accessor) {
        accessors.add(accessor);
        return this;
    }

    public SmartListTableModel<T> withColorizer(int column, Function<T, Ansi.Color> colorizer) {
        this.colorizers.put(column, colorizer);
        return this;
    }

    public SmartListTableModel<T> withHeaders(String... headers) {
        this.headers = headers;
        return this;
    }


    @Override
    public int getRowCount() {
        return headers == null ? items.size() : items.size() + 1;
    }

    @Override
    public int getColumnCount() {
        return accessors.isEmpty() ? 1 : accessors.size();
    }

    @Override
    public Object getValue(int row, int column) {
        if (headers != null) {
            assert headers.length == accessors.size();
            if (row == 0) {
                return headers[column];
            }

            row -= 1;
        }

        T item = items.get(row);
        Object result = accessors.isEmpty() ? item : accessors.get(column).apply(item);

        if (colorizers.containsKey(column)) {
            Ansi.Color color = colorizers.get(column).apply(item);
            return color == null ? result : Ansi.ansi().fg(color).a(result).reset().toString();
        }

        return result;
    }

    public String render() {
        String[][] cells = new String[getRowCount()][getColumnCount()];
        int[] widths = new int[getColumnCount()];
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                cells[i][j] = getValue(i, j).toString();
                widths[j] = Math.max(widths[j], new AnsiString(cells[i][j]).length());
            }
        }

        StringBuilder sb = new StringBuilder();
        for (String[] row : cells) {
            for (int i = 0; i < row.length; i++) {
                String cell = i == row.length - 1 ? row[i] : StringUtils.padRight(row[i], widths[i]) + COLUMN_DELIMITER;
                sb.append(cell);
            }

            sb.append(System.lineSeparator());
        }

        return Ansi.ansi().fg(Ansi.Color.DEFAULT).reset().a(sb.toString()).toString();
    }
}
