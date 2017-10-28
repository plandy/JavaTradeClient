package plandy.javatradeclient;

import org.zeromq.ZMsg;

public interface IDataRequest {

    ZMsg toMsg();

}
