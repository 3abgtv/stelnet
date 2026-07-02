package stelnet.board.query.provider;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;

import java.util.*;

import lombok.experimental.ExtensionMethod;
import stelnet.board.query.QueryL10n;
import stelnet.board.query.ResultSet;
import stelnet.board.query.grouping.GroupingStrategy;
import stelnet.filter.*;
import stelnet.util.*;
import uilib.RenderableShowComponent;
import uilib.ShowCargo;
import uilib.property.Size;

@ExtensionMethod({ CargoStackExtension.class })
public class ColonyItemQueryProvider extends QueryProvider {

    private static transient List<CargoStackAPI> allColonyItems;

    public static void resetCache() {
        allColonyItems = null;
    }

    @Override
    public List<CargoStackAPI> getMatching(Set<Filter> filters) {
        if (allColonyItems == null) {
            allColonyItems = new LinkedList<>();
            addAsCargoStacks(allColonyItems, Includer.getAiCoreIds());
            addAsCargoStacks(allColonyItems, getAllColonyItems());
        }
        List<CargoStackAPI> cargoStacksCopy = new LinkedList<CargoStackAPI>(allColonyItems);
        CollectionUtils.reduce(cargoStacksCopy, filters);
        return cargoStacksCopy;
    }

    @Override
    public RenderableShowComponent getPreview(Set<Filter> filters, Size size) {
        CargoAPI cargo = StelnetHelper.makeCargoFromStacks(getMatching(filters));
        return new ShowCargo(
            cargo,
            L10n.get(QueryL10n.MATCHING_COLONY_ITEMS),
            L10n.get(QueryL10n.NO_MATCHING_COLONY_ITEMS),
            size
        );
    }

    @Override
    protected void processMarkets(
            List<ResultSet> resultSets,
            List<MarketAPI> markets,
            Set<Filter> filters,
            final GroupingStrategy groupingStrategy
    ) {
        // Additional filter equivalent to a whitelist over all getMatching().
        // Removes unknown colony items from the "find matching" case, and doesn't affect "find selected".
        Filter baseFilter = new CargoStackIsColonyItem();
        filters.add(baseFilter);
        for (MarketAPI market : markets) {
            List<CargoStackAPI> cargoStacks = StelnetHelper.getColonyItems(market).getStacksCopy();
            CollectionUtils.reduce(cargoStacks, filters);
            ResultSet resultSet = new ResultSet(groupingStrategy, market);
            resultSet.addCargoStacks(market, null, cargoStacks);
            addToResultSets(resultSets, resultSet);
        }
        filters.remove(baseFilter);
    }

    private List<SpecialItemSpecAPI> getAllColonyItems() {
        List<SpecialItemSpecAPI> items = Global.getSettings().getAllSpecialItemSpecs();
        CollectionUtils.reduce(items, new SpecialSpecIsColonyItem());
        return items;
    }

    private <T> void addAsCargoStacks(List<CargoStackAPI> cargoStacks, List<T> elements) {
        for (T element : elements) {
            cargoStacks.add(element.asCargoStack());
        }
    }
}
