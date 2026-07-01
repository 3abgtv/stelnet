package stelnet.filter;

import com.fs.starfarer.api.campaign.FactionAPI;

public class FactionIsPlayer extends FactionFilter {

    @Override
    protected boolean acceptFaction(FactionAPI faction) {
        return faction.isPlayerFaction();
    }
}
