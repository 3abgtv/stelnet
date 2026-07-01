package stelnet.board.query;

import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import stelnet.util.L10n;
import stelnet.util.StelnetHelper;
import uilib.*;
import uilib.property.Size;

@RequiredArgsConstructor
public abstract class ResultView implements RenderableFactory {

    protected final ResultIntel intel;
    protected final ResultSet resultSet;
    protected final ResultOrganiser resultOrganiser = new ResultOrganiser();

    @Override
    public List<Renderable> create(Size size) {
        float width = size.getWidth();
        List<Renderable> elements = new LinkedList<>();
        addResults(elements, width);
        return elements;
    }

    protected void addPeople(List<Renderable> elements, MarketAPI market, float width) {
        List<PersonAPI> people = resultOrganiser.getPeople(resultSet, market);
        if (people.isEmpty()) {
            return;
        }
        elements.add(new Spacer(UiConstants.DEFAULT_SPACER));
        ShowPeople showPeople = new ShowPeople(people, L10n.get(QueryL10n.NO_MATCHING_PEOPLE), new Size(width, 0));
        showPeople.setGroupColor(getSupportColor(market.getFaction()));
        elements.add(showPeople);
    }

    protected void addColonyItems(List<Renderable> elements, MarketAPI market, float width) {
        List<CargoStackAPI> items = resultOrganiser.getColonyItems(resultSet, market);
        if (items.isEmpty()) {
            return;
        }
        elements.add(new Spacer(UiConstants.DEFAULT_SPACER));
        CargoAPI cargo = StelnetHelper.makeCargoFromStacks(items);
        ShowCargo showCargo = new ShowCargo(
            cargo,
            L10n.get(QueryL10n.MATCHING_COLONY_ITEMS),
            L10n.get(QueryL10n.NO_MATCHING_COLONY_ITEMS),
            new Size(width, 0)
        );
        showCargo.setTitleColor(getSupportColor(market.getFaction()));
        elements.add(showCargo);
    }

    protected abstract void addResults(List<Renderable> elements, float width);

    protected Color getSupportColor(FactionAPI faction) {
        return Misc.scaleAlpha(faction.getBaseUIColor(), 0.5f);
    }
}
