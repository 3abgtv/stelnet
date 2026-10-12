package stelnet.board.commodity.table;

import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import stelnet.board.commodity.CommodityL10n;
import stelnet.board.commodity.price.DemandPrice;
import stelnet.board.commodity.price.Price;
import stelnet.board.commodity.price.SupplyPrice;
import stelnet.util.L10n;
import stelnet.util.StelnetHelper;
import stelnet.widget.heading.GoButton;
import uilib.CellAction;
import uilib.TableContentRow;

@Getter
@RequiredArgsConstructor
public class ProfitTableRow implements Comparable<ProfitTableRow>, TableContentRow {

    public static int MINIMUM_QUANTITY = 100;
    public static boolean SHOW_ROUTE_BUTTONS = true;

    /**
     * Indices of the blank columns the route buttons are drawn over. They have to line
     * up with the header layout in {@link ProfitTableContent}.
     */
    private static final int BUY_BUTTON_COLUMN = 3;
    private static final int SELL_BUTTON_COLUMN = 6;

    private final List<Object> elements = new LinkedList<>();
    private final MarketAPI buyMarket;
    private final MarketAPI sellMarket;
    private float profit;
    private int quantity;

    public ProfitTableRow(MarketAPI buyMarket, MarketAPI sellMarket, String commodityId) {
        this(buyMarket, sellMarket);
        quantity = calculateQuantity(buyMarket, sellMarket, commodityId);
        profit = calculateProfit(buyMarket, sellMarket, commodityId);
        addAllCells(commodityId);
    }

    private void addAllCells(String commodityId) {
        float buyPrice = new SupplyPrice(commodityId).getTotalPrice(buyMarket, quantity);
        float sellPrice = new DemandPrice(commodityId).getTotalPrice(sellMarket, quantity);
        Color rowColor = getRowColor(Misc.getTextColor());
        addCell(rowColor, Misc.getDGSCredits(profit) + "  (" + Misc.getWithDGS(quantity) + ")");
        addCell(buyMarket.getTextColorForFactionOrPlanet(), StelnetHelper.getMarketWithFactionName(buyMarket));
        addCell(rowColor, "");
        addCell(rowColor, Misc.getDGSCredits(buyPrice));
        addCell(sellMarket.getTextColorForFactionOrPlanet(), StelnetHelper.getMarketWithFactionName(sellMarket));
        addCell(rowColor, "");
        addCell(rowColor, Misc.getDGSCredits(sellPrice));
        addCell(rowColor, String.format("%.1f", getDistance()));
    }

    private void addCell(Color color, Object element) {
        elements.add(Alignment.MID);
        elements.add(color);
        elements.add(element.toString());
    }

    public void addNumber(int number) {
        elements.add(0, number + ".");
        elements.add(0, getRowColor(Misc.getGrayColor()));
        elements.add(0, Alignment.MID);
    }

    @Override
    public Object[] buildObjectArray() {
        return elements.toArray();
    }

    /**
     * The two "lay in a course" buttons, drawn over the blank columns that follow each
     * market name. A market can be both the best place to buy from and the best place
     * to sell to, in which case a single button is enough.
     */
    @Override
    public List<CellAction> getCellActions() {
        if (!SHOW_ROUTE_BUTTONS) {
            return Collections.emptyList();
        }
        List<CellAction> actions = new LinkedList<>();
        actions.add(new CellAction(BUY_BUTTON_COLUMN, routeButton(buyMarket, CommodityL10n.ROUTE_TO_BUY)));
        if (!isSameMarket()) {
            actions.add(new CellAction(SELL_BUTTON_COLUMN, routeButton(sellMarket, CommodityL10n.ROUTE_TO_SELL)));
        }
        return actions;
    }

    private GoButton routeButton(MarketAPI market, CommodityL10n title) {
        return new GoButton(market, L10n.get(title));
    }

    private boolean isSameMarket() {
        if (buyMarket == sellMarket) {
            return true;
        }
        if (buyMarket == null || sellMarket == null || buyMarket.getId() == null) {
            return false;
        }
        return buyMarket.getId().equals(sellMarket.getId());
    }

    @Override
    public int compareTo(ProfitTableRow o) {
        return compare(profit, o.getProfit());
    }

    private int compare(float o1, float o2) {
        return (int) (o2 - o1);
    }

    private float calculateProfit(MarketAPI buyMarket, MarketAPI sellMarket, String commodityId) {
        if (quantity < MINIMUM_QUANTITY) {
            return 0;
        }

        Price supplyPrice = new SupplyPrice(commodityId);
        Price demandPrice = new DemandPrice(commodityId);
        float buyPrice = supplyPrice.getUnitPrice(buyMarket);
        float sellPrice = demandPrice.getUnitPrice(sellMarket);
        if (buyPrice >= sellPrice) {
            return 0;
        }

        float bought = supplyPrice.getTotalPrice(buyMarket, quantity);
        float sold = demandPrice.getTotalPrice(sellMarket, quantity);
        return sold - bought;
    }

    private int calculateQuantity(MarketAPI buyMarket, MarketAPI sellMarket, String commodityId) {
        CommodityOnMarketAPI buyFromCommodity = buyMarket.getCommodityData(commodityId);
        CommodityOnMarketAPI sellToMarketCommodity = sellMarket.getCommodityData(commodityId);
        int available = StelnetHelper.getCommodityAvailable(buyFromCommodity);
        int demand = StelnetHelper.getCommodityDemand(sellMarket, sellToMarketCommodity);
        return Math.min(available, demand);
    }

    private float getDistance() {
        float playerToBuy = Misc.getDistanceToPlayerLY(buyMarket.getPrimaryEntity());
        float buyToSell = Misc.getDistanceLY(buyMarket.getPrimaryEntity(), sellMarket.getPrimaryEntity());
        return playerToBuy + buyToSell;
    }

    private Color getRowColor(Color defaultColor) {
        String buySystemName = StelnetHelper.getStarSystemName(buyMarket.getStarSystem(), true);
        String sellSystemName = StelnetHelper.getStarSystemName(sellMarket.getStarSystem(), true);
        if (buySystemName.equals(sellSystemName)) {
            return Misc.getHighlightColor();
        }
        return defaultColor;
    }
}
