package plandy.javatradeclient.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import plandy.javatradeclient.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.time.Instant;
import java.util.*;

public class MainWindowController implements Initializable{

    private MarketDataService marketDataService;

    @FXML
    private ListView<Stock> tickerListview;

    @FXML
    private PriceChart priceChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        marketDataService = new MarketDataService();
        marketDataService.start();

        //dataService.requestData( new PriceHistoryDataRequest("aaaa"), new PriceHistoryResultCallback(this) );
        marketDataService.requestData( new ListTickersDataRequest(), new ListTickersResultCallback(this) );

        tickerListview.setCellFactory( listcell -> new ListCell<Stock>() {

            @Override
            protected void updateItem(Stock p_item, boolean p_isEmpty ) {

                super.updateItem( p_item, p_isEmpty );

                if (p_isEmpty || p_item == null || p_item.getTicker() == null) {
                    setText(null);
                } else {
                    setText( p_item.getTicker() );
                }
            }
        }  );
        tickerListview.getSelectionModel().selectedItemProperty().addListener( new StockSelectionListener() );
    }

    private class StockSelectionListener implements ChangeListener<Stock> {
        @Override
        public void changed(ObservableValue<? extends Stock> observable, Stock oldValue, Stock newValue) {
            if ( newValue != null ) {
                selectStock( newValue.getTicker() );
            }
        }
    }

    private void selectStock( String p_ticker ) {
        PriceHistoryDataRequest priceHistoryDataRequest = new PriceHistoryDataRequest( p_ticker );
        marketDataService.requestData( priceHistoryDataRequest, new PriceHistoryResultCallback(this) );
    }

    public void populateListTickers( ObservableList<Stock> p_istStocks) {
        tickerListview.getItems().addAll(p_istStocks);
    }

    public void populatePriceHistoryChart( XYChart.Series<Date, Number> p_priceSeries ) {
        priceChart.getData().clear();
        priceChart.getData().add( p_priceSeries );
    }

    private static class ListTickersResultCallback implements IResultCallback {

        private final MainWindowController controller;

        public ListTickersResultCallback( MainWindowController p_controller ) {
            controller = p_controller;
        }

        @Override
        public void executeCallback( String p_listStocks ) {

            ObservableList<Stock> priceHistory = FXCollections.observableArrayList();

            StringReader stringReader = new StringReader(p_listStocks);
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

                    Stock dataObject = new Stock( values[tickerIndex], values[fullnameIndex] );

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

            XYChart.Series<Date, Number> priceSeries = new XYChart.Series<>();

            List< HashMap<String, Object>> priceHistory = new ArrayList<HashMap<String, Object>>();

            StringReader stringReader = new StringReader(p_priceHistory);
            BufferedReader buff = new BufferedReader( stringReader );
            String line;

            try {
                line = buff.readLine();

                String[] columnPositions = line.split(",");

                int dateIndex = -1;
                int openIndex = -1;
                int highIndex = -1;
                int lowIndex = -1;
                int closeIndex = -1;
                int volumeIndex = -1;

                for ( int i = 0; i < columnPositions.length; i++ ) {
                    switch( columnPositions[i] ) {
                        case "timestamp": dateIndex = i;
                        case "open": openIndex = i;
                        case "high": highIndex = i;
                        case "low": lowIndex = i;
                        case "close": closeIndex = i;
                        case "volume": volumeIndex = i;
                    }

                }
                Instant startInstant = Calendar.getInstance().toInstant();

                while ( (line = buff.readLine()) != null ) {
                    System.out.println(line);

                    String[] values = line.split(",");

                    HashMap<String, Object> dataObject = new HashMap<String, Object>();

                    dataObject.put( "timestamp", values[dateIndex] );
                    dataObject.put( "open", new Double(values[openIndex]) );
                    dataObject.put( "high", new Double(values[highIndex]) );
                    dataObject.put( "low", new Double(values[lowIndex]) );
                    dataObject.put( "close", new Double(values[closeIndex]) );
                    dataObject.put( "volume", Long.parseLong(values[volumeIndex]) );

                    priceHistory.add( dataObject );

                    XYChart.Data<Date, Number> priceData = new XYChart.Data<Date, Number>( DateUtility.parseStringToDate(values[dateIndex]), new Double(values[closeIndex]) );
                    priceSeries.getData().add(0, priceData );

                }
                System.out.println( "Starting parse: " + startInstant.toString() );
                System.out.println( "Finish parse: " + Calendar.getInstance().toInstant().toString() );
            } catch (IOException e) {
                throw new RuntimeException( e );
            }


            Platform.runLater( () -> {
                controller.populatePriceHistoryChart( priceSeries );
            });
        }
    }

//    public void injectMarketDataService( MarketDataService p_marketDataService ) {
//        marketDataService = p_marketDataService;
//        marketDataService.requestData( new ListTickersDataRequest(), new ListTickersResultCallback(this) );
//    }

}
