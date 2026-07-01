package stelnet.filter;

import java.util.List;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import lombok.EqualsAndHashCode;
import stelnet.util.Includer;

@EqualsAndHashCode(callSuper = false)
public final class SpecialAdditionalColonyItem extends Filter {

    private final List<String> additionalColonyItemIds;

    public SpecialAdditionalColonyItem() {
        additionalColonyItemIds = Includer.getAdditionalColonyItemIds();
    }

    @Override
    public boolean accept(Object object) {
        if (object instanceof SpecialItemSpecAPI special) {
            return additionalColonyItemIds.contains(special.getId());
        }
        return super.accept(object);
    }
}
