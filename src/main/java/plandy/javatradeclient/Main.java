package plandy.javatradeclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import plandy.javatradeclient.controller.MainWindowController;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/plandy/javatradeclient/fxml/MainWindow.fxml"));
        primaryStage.setTitle("Java Trade Client");
        primaryStage.setScene(new Scene(root, 1600, 900));
        primaryStage.show();

//        MarketDataService marketDataService = new MarketDataService();
//        marketDataService.start();
//
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/plandy/javatradeclient/fxml/MainWindow.fxml"));
//        MainWindowController mainWindowController = fxmlLoader.<MainWindowController>getController();
//        mainWindowController.injectMarketDataService( marketDataService );
    }
}
