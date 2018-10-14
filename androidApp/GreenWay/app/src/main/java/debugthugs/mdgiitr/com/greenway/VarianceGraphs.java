package debugthugs.mdgiitr.com.greenway;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Iterator;

public class VarianceGraphs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_variance_graph);
        GraphView graph_ofZvar = (GraphView) findViewById(R.id.id_graphVariance);
        GraphView graph_of_AngleVar = (GraphView) findViewById(R.id.id_graphVariation_in_angle);

        graph_ofZvar.getViewport().setYAxisBoundsManual(true);
        graph_ofZvar.getViewport().setMinY(0);
        graph_ofZvar.getViewport().setMaxY(150);

        graph_ofZvar.getViewport().setXAxisBoundsManual(false);
        graph_ofZvar.getViewport().setMinX(1);

        // enable scaling and scrolling
        graph_ofZvar.getViewport().setScalable(false);
        graph_ofZvar.getViewport().setScalableY(false);

        LineGraphSeries lineGraphSeries = new LineGraphSeries();
        long time = 0; //millisec
        float temp;
        Iterator iterator = MainActivity.variance.iterator();
        while (iterator.hasNext()){
            temp = (float) iterator.next();
            lineGraphSeries.appendData(new DataPoint(time*100,temp),true,1000000000,false);
            time+=MainActivity.SENSOR_SAMPLING_PERIOD;
        }
        graph_ofZvar.addSeries(lineGraphSeries);




        graph_of_AngleVar.getViewport().setYAxisBoundsManual(true);
        graph_of_AngleVar.getViewport().setMinY(-2);
        graph_of_AngleVar.getViewport().setMaxY(2);

        graph_of_AngleVar.getViewport().setXAxisBoundsManual(false);
        graph_of_AngleVar.getViewport().setMinX(1);

        // enable scaling and scrolling
        graph_of_AngleVar.getViewport().setScalable(false);
        graph_of_AngleVar.getViewport().setScalableY(false);

        LineGraphSeries lineGraphSeries_ofAngleVariation = new LineGraphSeries();
        time = 0; //millisec
        Iterator iterator2 = MainActivity.angleVariation.iterator();
        while (iterator2.hasNext()){
            temp = (float) iterator2.next();
            lineGraphSeries_ofAngleVariation.appendData(new DataPoint(time*100,temp),true,1000000000,false);
            time+=MainActivity.SENSOR_SAMPLING_PERIOD;
        }
        graph_of_AngleVar.addSeries(lineGraphSeries_ofAngleVariation);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
