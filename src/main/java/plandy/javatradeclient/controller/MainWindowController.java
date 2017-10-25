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
import plandy.javatradeclient.DataRequest;
import plandy.javatradeclient.MarketDataService;
import plandy.javatradeclient.RequestType;
import plandy.javatradeclient.Ticker;

import java.net.URL;
import java.util.ArrayList;
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
        dataService.requestData( new DataRequestTickers( RequestType.DATA_CHART, this ) );

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

}
