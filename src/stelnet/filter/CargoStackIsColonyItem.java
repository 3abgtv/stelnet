package stelnet.filter;

import com.fs.starfarer.api.campaign.CargoStackAPI;
import stelnet.util.Includer;
import java.util.List;

public class CargoStackIsColonyItem extends CargoStackFilter {
    private final List<String> aiCores = Includer.getAiCoreIds();
    private final SpecialSpecIsColonyItem specFilter = new SpecialSpecIsColonyItem();

    @Override
    protected boolean acceptCargoStack(CargoStackAPI cargoStack) {
        if (cargoStack.isSpecialStack()) {
            return specFilter.accept(cargoStack.getSpecialItemSpecIfSpecial());
        }
        if (cargoStack.isCommodityStack()) {
            return aiCores.contains(cargoStack.getCommodityId());
        }
        return false;
    }
}
