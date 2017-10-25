package plandy.javatradeclient;

public class Ticker {

    private final String ticker;
    private final String fullName;

    public Ticker( String p_ticker, String p_fullName ) {
        ticker = p_ticker;
        fullName = p_fullName;
    }

    public String getTicker() {
        return ticker;
    }

    public String getFullName() {
        return fullName;
    }

}
