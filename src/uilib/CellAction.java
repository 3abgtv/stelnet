package uilib;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * An interactive element drawn over one column of a {@link Table} row.
 *
 * Table cells are text only (the engine casts anything else in a cell to its label
 * class), so a button has to be laid over the table rather than placed inside a cell.
 * {@link Table} reserves no space for it - the column it names is expected to be left
 * empty by the row's cells.
 */
@Getter
@RequiredArgsConstructor
public class CellAction {

    /**
     * Zero-based index into the header columns passed to
     * {@link TableContent#getHeaders(float)}.
     */
    private final int column;

    /**
     * Rendered with {@link Renderable#render(com.fs.starfarer.api.ui.TooltipMakerAPI)}
     * and positioned over the column. Its size decides where it lands, so a
     * {@link Button} sized from its title works.
     */
    private final Renderable renderable;
}
