package plandy.javatradeclient;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;

import java.util.Date;

public class PriceChart extends LineChart<Date,Number> {

    @FXML
    final private DateAxis xDateAxis;

    @FXML
    final private NumberAxis yPriceAxis;

    public PriceChart() {
        super( new DateAxis(), new NumberAxis() );

        xDateAxis = (DateAxis) super.getXAxis();
        yPriceAxis = (NumberAxis) super.getYAxis();

        setDefaultStyle();
    }

    public void setDefaultStyle() {
        //yPriceAxis.setForceZeroInRange(false);

        this.setAnimated(false);
        this.setLegendVisible(false);
        this.setCreateSymbols(false);
        this.setHorizontalGridLinesVisible(true);
        this.setVerticalGridLinesVisible(true);
    }
}
