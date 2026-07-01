package stelnet.filter;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import stelnet.board.query.Result;
import stelnet.board.query.ResultSet;

@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public class ResultMarketFilter extends ResultFilter {

    private final Filter filter;

    @Override
    protected boolean acceptResultSet(ResultSet resultSet) {
        return acceptResultSetCopy(resultSet);
    }

    @Override
    protected boolean acceptResult(Result result) {
        return filter.accept(result.getMarket());
    }
}
