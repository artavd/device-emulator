package artavd.devices.shell;

import org.apache.commons.lang3.StringUtils;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiString;
import org.springframework.shell.table.*;
import org.springframework.shell.table.Formatter;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

public final class SmartListTableModel<T> extends TableModel {

    private static final int DEFAULT_AVAILABLE_WIDTH = 80;
    private static final BorderStyle DEFAULT_BORDER_STYLE = BorderStyle.air;

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
            return Ansi.ansi().fg(colorizers.get(column).apply(item)).a(result).reset().toString();
        }

        return result;
    }

    public String render() {
        TableBuilder builder = new TableBuilder(this)
                .addHeaderAndVerticalsBorders(DEFAULT_BORDER_STYLE);

        tryToRemoveHeaderBorder(builder);
        tryToRemoveDefaultAligner(builder);
        tryToRemoveDefaultSizer(builder);
        tryToRemoveDefaultWrapper(builder);
        tryToRemoveDefaultFormatter(builder);

        return builder
                .on(CellMatchers.table())
                .addAligner(SmartAligner.instance)
                .addSizer(SmartSizer.instance)
                .addWrapper(SmartWrapper.instance)
                .addFormatter(SmartFormatter.instance)
                .build()
                .render(DEFAULT_AVAILABLE_WIDTH);
    }

    // Due to poor API of TableBuilder there is no ability to create table with
    // only vertical borders. So table with header and vertical borders is created
    // and then header border specification removed.
    private void tryToRemoveHeaderBorder(TableBuilder builder) {
        final String privateFieldName = "borderSpecifications";
        try {
            Field field = builder.getClass().getDeclaredField(privateFieldName);
            field.setAccessible(true);
            List borders = (List)field.get(builder);
            borders.remove(0);
        }
        catch (Exception ex) {
            // Removing header border failed. Ignore exception.
        }
    }

    // Due to poor API of TableBuilder there is no ability to colorize specific cells.
    // Default aligner trims special symbols. So it removed from table builder and new
    // custom one added instead.
    private void tryToRemoveDefaultAligner(TableBuilder builder) {
        final String privateFieldName = "aligners";
        try {
            Field field = builder.getClass().getDeclaredField(privateFieldName);
            field.setAccessible(true);
            Map aligners = (Map)field.get(builder);
            aligners.clear();
        }
        catch (Exception ex) {
            // Removing header border failed. Ignore exception.
        }
    }

    // Due to poor API of TableBuilder there is no ability to colorize specific cells.
    // Default sizer measure cells width without handling special characters. So it removed
    // from table builder and new custom one added instead.
    private void tryToRemoveDefaultSizer(TableBuilder builder) {
        final String privateFieldName = "sizeConstraints";
        try {
            Field field = builder.getClass().getDeclaredField(privateFieldName);
            field.setAccessible(true);
            Map sizers = (Map)field.get(builder);
            sizers.clear();
        }
        catch (Exception ex) {
            // Removing header border failed. Ignore exception.
        }
    }

    private void tryToRemoveDefaultWrapper(TableBuilder builder) {
        final String privateFieldName = "wrappers";
        try {
            Field field = builder.getClass().getDeclaredField(privateFieldName);
            field.setAccessible(true);
            Map wrappers = (Map)field.get(builder);
            wrappers.clear();
        }
        catch (Exception ex) {
            // Removing header border failed. Ignore exception.
        }
    }

    private void tryToRemoveDefaultFormatter(TableBuilder builder) {
        final String privateFieldName = "formatters";
        try {
            Field field = builder.getClass().getDeclaredField(privateFieldName);
            field.setAccessible(true);
            Map formatters = (Map)field.get(builder);
            formatters.clear();
        }
        catch (Exception ex) {
            // Removing header border failed. Ignore exception.
        }
    }

    private enum SmartAligner implements Aligner {
        instance;

        @Override
        public String[] align(String[] text, int cellWidth, int cellHeight) {
            return Arrays.stream(text).map(line -> padRight(line, cellWidth)).toArray(String[]::new);
        }

        private String padRight(String text, int width) {
            int adding = width - new AnsiString(text).length();
            if (width > 0) {
                return String.format("%s%s", text, StringUtils.repeat(' ', adding));
            }

            return text;
        }
    }

    private enum SmartSizer implements SizeConstraints {
        instance;

        @Override
        public Extent width(String[] raw, int tableWidth, int nbColumns) {
            OptionalInt width = Arrays.stream(raw)
                    .mapToInt(line -> new AnsiString(line).length())
                    .max();

            assert width.isPresent();
            return new Extent(width.getAsInt(), width.getAsInt());
        }
    }

    private enum SmartWrapper implements TextWrapper {
        instance;

        @Override
        public String[] wrap(String[] original, int columnWidth) {
            return original;
        }
    }

    private enum SmartFormatter implements Formatter {
        instance;

        @Override
        public String[] format(Object value) {
            return new String[] { value == null ? "" : String.format(" %s ", value) };
        }
    }
}
