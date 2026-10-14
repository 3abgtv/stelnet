package stelnet.board.commodity.table;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import stelnet.board.commodity.CommodityL10n;
import stelnet.util.L10n;
import uilib.Button;
import uilib.TableContent;

@RequiredArgsConstructor
public class ProfitTableContent implements TableContent {

    public static int MAX_ROWS = 50;
    public static int MINIMUM_PROFIT_VALUE = 10000;

    private static final float BUTTON_COLUMN_MARGIN = 6f;

    /**
     * Floor for the whole data side of the table. The button columns are sized in pixels,
     * so on a narrow panel they are the ones that give way - without a floor the data
     * columns would keep shrinking with the panel until their width went negative.
     */
    private static final float MIN_DATA_WIDTH = 420f;

    /**
     * Proportions the original seven columns shared the table width in, still used for
     * whatever width is left once the two button columns have taken their pixels.
     */
    private static final float[] DATA_COLUMN_RATIOS = { .05f, .17f, .22f, .12f, .22f, .12f, .1f };

    private final List<MarketAPI> sellMarkets;
    private final List<MarketAPI> buyMarkets;
    private final String commodityId;

    /**
     * Two blank-titled columns are inserted after the market names to hold the "lay in a
     * course" buttons that {@link ProfitTableRow} draws over the table. They are sized in
     * pixels rather than fractions, because a button is as wide as its own label and
     * would otherwise spill over its neighbours; the remaining columns keep their
     * original proportions of whatever width is left.
     */
    @Override
    public Object[] getHeaders(float width) {
        float buttonColumn = buttonColumnWidth(width);
        float dataWidth = width - 2 * buttonColumn;
        return new Object[] {
            "#",
            DATA_COLUMN_RATIOS[0] * dataWidth,
            L10n.get(CommodityL10n.HEADER_PROFIT),
            DATA_COLUMN_RATIOS[1] * dataWidth,
            L10n.get(CommodityL10n.HEADER_BUY_LOCATION),
            DATA_COLUMN_RATIOS[2] * dataWidth,
            "",
            buttonColumn,
            L10n.get(CommodityL10n.HEADER_BUY_EXPENSE),
            DATA_COLUMN_RATIOS[3] * dataWidth,
            L10n.get(CommodityL10n.HEADER_SELL_LOCATION),
            DATA_COLUMN_RATIOS[4] * dataWidth,
            "",
            buttonColumn,
            L10n.get(CommodityL10n.HEADER_SELL_REVENUE),
            DATA_COLUMN_RATIOS[5] * dataWidth,
            L10n.get(CommodityL10n.HEADER_TRIP),
            DATA_COLUMN_RATIOS[6] * dataWidth,
        };
    }

    /**
     * Wide enough for the wider of the two labels, plus a little breathing room so the
     * button does not touch the neighbouring column - but never at the expense of
     * {@link #MIN_DATA_WIDTH}: on a panel too narrow for both, the buttons give up their
     * pixels rather than the columns they sit next to.
     */
    private float buttonColumnWidth(float tableWidth) {
        float natural = Math.max(
            Button.autoWidth(L10n.get(CommodityL10n.ROUTE_TO_BUY)),
            Button.autoWidth(L10n.get(CommodityL10n.ROUTE_TO_SELL))
        ) + BUTTON_COLUMN_MARGIN;
        return Math.max(0f, Math.min(natural, (tableWidth - MIN_DATA_WIDTH) / 2f));
    }

    @Override
    public List<ProfitTableRow> getRows() {
        List<ProfitTableRow> rows = createRows();
        Collections.sort(rows);
        injectNumbers(rows);
        int limit = Math.min(MAX_ROWS, rows.size());
        return rows.subList(0, limit);
    }

    private List<ProfitTableRow> createRows() {
        List<ProfitTableRow> rows = new LinkedList<>();
        for (MarketAPI buyMarket : buyMarkets) {
            List<ProfitTableRow> sellMarketTableRows = createSellMarketRows(buyMarket, sellMarkets);
            rows.addAll(sellMarketTableRows);
        }
        return rows;
    }

    private List<ProfitTableRow> createSellMarketRows(MarketAPI buyMarket, List<MarketAPI> sellMarkets) {
        List<ProfitTableRow> rows = new LinkedList<>();
        for (MarketAPI sellMarket : sellMarkets) {
            ProfitTableRow row = new ProfitTableRow(buyMarket, sellMarket, commodityId);
            if (row.getProfit() >= MINIMUM_PROFIT_VALUE) {
                rows.add(row);
            }
        }
        return rows;
    }

    private void injectNumbers(List<ProfitTableRow> rows) {
        int i = 1;
        for (ProfitTableRow row : rows) {
            row.addNumber(i++);
        }
    }
}
