package plandy.javatradeclient;

import org.zeromq.ZFrame;
import org.zeromq.ZMsg;

public class PriceHistoryDataRequest extends AbstractDataRequest{

    private final String ticker;

    public PriceHistoryDataRequest( String p_ticker ) {
        super( RequestType.PRICE_HISTORY );
        ticker = p_ticker;
    }

    @Override
    public ZMsg toMsg() {
        ZMsg zMsg = new ZMsg();
        zMsg.add( new ZFrame(super.requestType.name() + ";" + ticker) );
        //zMsg.add( new ZFrame(ticker) );

        return zMsg;
    }
}
