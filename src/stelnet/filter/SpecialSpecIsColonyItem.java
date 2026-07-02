package stelnet.filter;

import java.util.List;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import lombok.EqualsAndHashCode;
import stelnet.util.Includer;

@EqualsAndHashCode(callSuper = false)
public final class SpecialSpecIsColonyItem extends Filter {

    private final List<String> additionalColonyItemIds;

    public SpecialSpecIsColonyItem() {
        additionalColonyItemIds = Includer.getAdditionalColonyItemIds();
    }

    @Override
    public boolean accept(Object object) {
        if (object instanceof SpecialItemSpecAPI special) {
            return special.hasTag(Items.TAG_COLONY_ITEM) || additionalColonyItemIds.contains(special.getId());
        }
        return super.accept(object);
    }
}
