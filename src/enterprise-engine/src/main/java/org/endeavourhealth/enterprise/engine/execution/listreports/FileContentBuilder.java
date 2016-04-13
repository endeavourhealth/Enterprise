package org.endeavourhealth.enterprise.engine.execution.listreports;

public class FileContentBuilder {

    private final int oneMB = 1048576;
    private final StringBuilder stringBuilder = new StringBuilder(oneMB);
    private boolean isFirstRow = true;
    private boolean isFirstColumn = true;

    public void newRow() {
        if (isFirstRow)
            isFirstRow = false;
        else
            stringBuilder.append('\n');
    }

    public void addField(String value) {
        if (isFirstColumn)
            isFirstColumn = false;
        else
            addSeparator();

        stringBuilder.append(value);
    }

    private void addSeparator() {
        stringBuilder.append(',');
    }

    public void clear() {
        stringBuilder.setLength(0);
    }
}
