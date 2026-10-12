package stelnet.widget.heading;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.CutStyle;
import com.fs.starfarer.api.ui.IntelUIAPI;
import stelnet.util.CommonL10n;
import stelnet.util.L10n;
import uilib.Button;
import uilib.EventHandler;
import uilib.UiConstants;
import uilib.property.Size;

public class GoButton extends Button {

    private final MarketAPI market;

    public GoButton(final MarketAPI market) {
        this(market, L10n.get(CommonL10n.GO));
    }

    public GoButton(final MarketAPI market, String title) {
        super(new Size(UiConstants.AUTO_WIDTH, UiConstants.DEFAULT_ROW_HEIGHT), title, true);
        this.market = market;
        setPadding(0);
        setCutStyle(CutStyle.C2_MENU);
        setHandler(
            new EventHandler() {
                @Override
                public void onConfirm(IntelUIAPI ui) {
                    layInCourse();
                }
            }
        );
    }

    /**
     * Lays in a course to this button's market, if it has one to navigate to.
     */
    public void layInCourse() {
        if (market == null) {
            return;
        }
        if (market.getPrimaryEntity() == null) {
            return;
        }
        Global.getSector().layInCourseFor(market.getPrimaryEntity());
    }
}
