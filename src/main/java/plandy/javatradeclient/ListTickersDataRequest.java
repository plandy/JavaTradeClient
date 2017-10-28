package plandy.javatradeclient;

import org.zeromq.ZFrame;
import org.zeromq.ZMsg;

public class ListTickersDataRequest extends AbstractDataRequest {

    public ListTickersDataRequest() {
        super( RequestType.LIST_TICKERS );
    }

    @Override
    public ZMsg toMsg() {
        ZMsg zMsg = new ZMsg();
        zMsg.add( new ZFrame( super.requestType.name() ) );

        return zMsg;
    }
}
