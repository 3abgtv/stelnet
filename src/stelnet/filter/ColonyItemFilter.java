package stelnet.filter;

import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import stelnet.util.StelnetHelper;

public abstract class ColonyItemFilter extends MarketFilter {

    @Override
    public boolean accept(Object object) {
        if (object instanceof CargoStackAPI) {
            return acceptCargoStack((CargoStackAPI) object);
        }
        return super.accept(object);
    }

    @Override
    protected boolean acceptMarket(MarketAPI market) {
        for (CargoStackAPI cargoStack : StelnetHelper.getColonyItems(market).getStacksCopy()) {
            if (acceptCargoStack(cargoStack)) {
                return true;
            }
        }
        return false;
    }

    protected abstract boolean acceptCargoStack(CargoStackAPI cargoStack);
}
