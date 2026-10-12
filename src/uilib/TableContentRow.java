package uilib;

import java.util.Collections;
import java.util.List;

public interface TableContentRow {
    public Object[] buildObjectArray();

    /**
     * Buttons and other interactive elements to lay over this row's columns. A cell
     * can only render text, so this is the only way to give a row a clickable target.
     */
    default List<CellAction> getCellActions() {
        return Collections.emptyList();
    }
}
