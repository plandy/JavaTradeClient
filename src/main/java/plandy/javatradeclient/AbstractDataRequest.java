package plandy.javatradeclient;

public abstract class AbstractDataRequest implements IDataRequest {

    protected final RequestType requestType;

    protected AbstractDataRequest(RequestType requestType) {
        this.requestType = requestType;
    }
}
