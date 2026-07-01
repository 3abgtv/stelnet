package stelnet.board.query.view.add;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import stelnet.board.query.provider.ColonyItemQueryProvider;
import stelnet.board.query.view.dialog.ItemPickerDialog;
import stelnet.board.query.view.dialog.PickerDialog;
import stelnet.filter.*;
import stelnet.util.CommonL10n;
import stelnet.util.L10n;
import stelnet.util.StelnetHelper;
import uilib.Button;
import uilib.Renderable;

public class ColonyItemQueryFactory extends QueryFactory {

    @Getter
    private transient ColonyItemQueryProvider provider = new ColonyItemQueryProvider();

    public Object readResolve() {
        provider = new ColonyItemQueryProvider();
        return this;
    }

    @Override
    public Set<Filter> getFilters() {
        return new LinkedHashSet<>();
    }

    @Override
    protected Button[] getFinalComponents() {
        Set<Filter> filters = getFilters();
        PickerDialog picker = new ItemPickerDialog(
            StelnetHelper.makeCargoFromStacks(provider.getMatching(filters)),
            this,
            L10n.get(CommonL10n.COLONY_ITEMS)
        );
        return new Button[] {
            new FindMatchingButton(this, L10n.get(CommonL10n.COLONY_ITEMS)),
            new FindSelectedButton(picker),
        };
    }

    @Override
    protected List<Renderable> getQueryBuildingComponents() {
        return new LinkedList<>();
    }
}
