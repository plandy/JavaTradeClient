package plandy.javatradeclient.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import org.zeromq.ZMsg;
import plandy.javatradeclient.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable{

    @FXML
    private ListView<Ticker> tickerListview;

    @FXML
    private LineChart priceChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //ObservableList<Ticker> listTickers = FXCollections.observableArrayList();
        //listTickers.add( new Ticker("AAPL", "Apple Inc.") );
        //listTickers.add( new Ticker("MSFT", "Microsoft Corporation") );

        DataRequestTickers listTickersDataRequest = new DataRequestTickers( RequestType.LIST_TICKERS, this ) ;

        MarketDataService dataService = new MarketDataService();
        dataService.start();
        //dataService.requestData( new DataRequestTickers( RequestType.LIST_TICKERS, this ) );
        dataService.requestData( new ListTickersDataRequest(), new ListTickersResultCallback(this) );

        //tickerListview.getItems().addAll( listTickers );
        tickerListview.setCellFactory( listcell -> new ListCell<Ticker>() {

            @Override
            protected void updateItem( Ticker p_item, boolean p_isEmpty ) {

                super.updateItem( p_item, p_isEmpty );

                if (p_isEmpty || p_item == null || p_item.getTicker() == null) {
                    setText(null);
                } else {
                    setText( p_item.getTicker() );
                }
            }
        }  );
    }

    public void populateListTickers( ObservableList<Ticker> p_istTickers ) {
        tickerListview.getItems().addAll( p_istTickers );
    }

    public class DataRequestTickers {
        private final RequestType requestType;
        private final MainWindowController controller;

        public DataRequestTickers( RequestType p_requestType, MainWindowController p_controller ) {
            requestType = p_requestType;
            controller = p_controller;
        }

        public String getRequestType() {
            return requestType.name();
        }

        public void executeCallback( DataResultTickers p_dataResult ) {
            Platform.runLater( () -> {
                controller.populateListTickers( p_dataResult.getListTickers() );
            });
        }
    }

    public class DataResultTickers {

        private ObservableList<Ticker> listTickers;

        public DataResultTickers(  ) {

        }

        public ObservableList<Ticker> getListTickers() {
            return listTickers;
        }

    }

    private static class ListTickersResultCallback implements IResultCallback {

        private final MainWindowController controller;

        public ListTickersResultCallback( MainWindowController p_controller ) {
            controller = p_controller;
        }

        @Override
        public void executeCallback( String p_listTickers ) {

            ObservableList<Ticker> priceHistory = FXCollections.observableArrayList();

            StringReader stringReader = new StringReader(p_listTickers);
            BufferedReader buff = new BufferedReader( stringReader );
            String line;

            try {
                line = buff.readLine();

                String[] columnPositions = line.split(",");

                int tickerIndex = -1;
                int fullnameIndex = -1;

                for ( int i = 0; i < columnPositions.length; i++ ) {
                    switch( columnPositions[i] ) {
                        case "ticker": tickerIndex = i;
                        case "fullname": fullnameIndex = i;
                    }

                }

                while ( (line = buff.readLine()) != null ) {
                    System.out.println(line);

                    String[] values = line.split(",");

                    Ticker dataObject = new Ticker( values[tickerIndex], values[fullnameIndex] );

                    priceHistory.add( dataObject );

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Platform.runLater( () -> {
                controller.populateListTickers( priceHistory );
            });
        }
    }

    private static class PriceHistoryResultCallback implements IResultCallback {

       private final MainWindowController controller;

        public PriceHistoryResultCallback( MainWindowController p_controller ) {
            controller = p_controller;
        }

        @Override
        public void executeCallback( String p_priceHistory ) {

            List< HashMap<String, Object>> l_priceHistory = new ArrayList<HashMap<String, Object>>();

            StringReader stringReader = new StringReader(p_priceHistory);
            BufferedReader l_buff = new BufferedReader( stringReader );
            String line;

            try {
                line = l_buff.readLine();

                String[] columnPositions = line.split(",");

                int dateIndex = -1;
                int openIndex = -1;
                int highIndex = -1;
                int lowIndex = -1;
                int closeIndex = -1;
                int volumeIndex = -1;

                for ( int i = 0; i < columnPositions.length; i++ ) {
                    switch( columnPositions[i] ) {
                        case "date": dateIndex = i;
                        case "open": openIndex = i;
                        case "high": highIndex = i;
                        case "low": lowIndex = i;
                        case "close": closeIndex = i;
                        case "volume": volumeIndex = i;
                    }

                }

                while ( (line = l_buff.readLine()) != null ) {
                    System.out.println(line);

                    String[] values = line.split(",");

                    HashMap<String, Object> dataObject = new HashMap<String, Object>();

                    dataObject.put( "date", values[dateIndex] );
                    dataObject.put( "open", new Double(values[openIndex]) );
                    dataObject.put( "high", new Double(values[highIndex]) );
                    dataObject.put( "low", new Double(values[lowIndex]) );
                    dataObject.put( "close", new Double(values[closeIndex]) );
                    dataObject.put( "volume", Integer.parseInt(values[volumeIndex]) );

                    l_priceHistory.add( dataObject );

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Platform.runLater( () -> {
                //controller.populateListTickers( p_dataResult.getListTickers() );
            //});
        }
    }

}
