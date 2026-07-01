package stelnet.filter;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public final class FactionIsHostile extends FactionFilter {

    @Override
    protected boolean acceptFaction(FactionAPI faction) {
        return faction.getRelToPlayer().isAtBest(RepLevel.HOSTILE);
    }
}
