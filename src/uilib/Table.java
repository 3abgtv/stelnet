package uilib;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import uilib.property.Size;

@RequiredArgsConstructor
@Setter
public class Table extends RenderableComponent {

    /**
     * The engine lays columns out with a small gap between them (its header builder does
     * cursor += width + 3), which is why callers hand {@link TableContent#getHeaders(float)}
     * a width smaller than the table's own. Positioning has to reproduce the same gap.
     */
    private static final float COLUMN_GAP = 3f;

    /**
     * Half the difference between a table row and the (shorter) button drawn in it, so a
     * button ends up centred rather than sitting on the row's bottom edge.
     */
    private static final float TABLE_BOTTOM_OFFSET = 1f;

    private final String title;
    private final float width;
    private final float maxHeight;
    private final TableContent tableContent;

    private FactionAPI faction = Global.getSector().getPlayerFaction();
    private String noRowsDescription = "No rows present.";

    @Override
    public Size getSize() {
        float tableHeight = UiConstants.TABLE_ROW_HEIGHT * (tableContent.getRows().size() + 1) + 8;
        float height = maxHeight == 0 ? tableHeight : Math.min(maxHeight, tableHeight);
        return new Size(width, height);
    }

    @Override
    public void render(CustomPanelAPI panel, float x, float y) {
        super.render(panel, x - 4, y);
    }

    @Override
    public void render(TooltipMakerAPI tooltip) {
        List<? extends TableContentRow> rows = tableContent.getRows();
        Object[] headers = tableContent.getHeaders(width - 24);

        tooltip.beginTable(faction, UiConstants.TABLE_ROW_HEIGHT, headers);
        for (TableContentRow row : rows) {
            tooltip.addRow(row.buildObjectArray());
        }
        if (rows.isEmpty()) {
            tooltip.addPara(noRowsDescription, 0);
            return;
        }
        tooltip.addTable(title, 0, 1);
        renderCellActions(tooltip, headers, rows, tooltip.getPrev());
    }

    /**
     * Draws each row's cell actions over the table. Buttons go through
     * {@link TooltipMakerAPI#addButton}, which adds their height to the tooltip's content -
     * left alone that would give the table a screenful of empty space to scroll into, so
     * the height is restored once everything is placed.
     *
     * Actions anchor to the table rather than to a row: the engine re-sorts a panel's
     * children whenever it recomputes positions and throws "only siblings may be anchored
     * to each other" for anything else, and rows live inside the table's own panel.
     */
    private void renderCellActions(
        TooltipMakerAPI tooltip,
        Object[] headers,
        List<? extends TableContentRow> rows,
        UIComponentAPI tableComponent
    ) {
        if (tableComponent == null) {
            return;
        }
        float heightBefore = tooltip.getHeightSoFar();
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            for (CellAction action : rows.get(rowIndex).getCellActions()) {
                renderCellAction(tooltip, headers, tableComponent, rowIndex, rows.size(), action);
            }
        }
        tooltip.setHeightSoFar(heightBefore);
    }

    private void renderCellAction(
        TooltipMakerAPI tooltip,
        Object[] headers,
        UIComponentAPI tableComponent,
        int rowIndex,
        int rowCount,
        CellAction action
    ) {
        Renderable renderable = action.getRenderable();
        renderable.render(tooltip);
        UIComponentAPI component = tooltip.getPrev();
        if (component == null) {
            return;
        }
        // The engine sizes a table as header + 3 + rows * rowHeight and puts the row list
        // right below the header, so the last row's bottom edge is the table's bottom edge
        // and every row above it is exactly one row height away. Measuring up from there
        // avoids needing the height of the title and header rows, which is not exposed.
        //
        // belowLeft(anchor, f) is relativeTo(anchor, 0, 0, 0, -1, 0, -f), so it sets
        //   self.bottom = anchor.bottom - self.height - f
        // - it also subtracts the component's own height, which has to be cancelled out
        // here or every action sits a whole button-height too high.
        PositionAPI position = component.getPosition();
        float rowsAboveBottom = (rowCount - 1 - rowIndex) * UiConstants.TABLE_ROW_HEIGHT;
        position
            .belowLeft(tableComponent, -position.getHeight() - rowsAboveBottom - TABLE_BOTTOM_OFFSET)
            .setXAlignOffset(columnOffset(headers, action.getColumn()));
    }

    /**
     * Distance from the left edge of the table to the left edge of a column. Headers
     * alternate between a title and a width, so column i's width sits at index 2i + 1.
     */
    private float columnOffset(Object[] headers, int column) {
        float offset = 0;
        for (int i = 0; i < column; i++) {
            int widthIndex = 2 * i + 1;
            if (widthIndex >= headers.length || !(headers[widthIndex] instanceof Number)) {
                break;
            }
            offset += ((Number) headers[widthIndex]).floatValue() + COLUMN_GAP;
        }
        return offset;
    }
}
