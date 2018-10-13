package debugthugs.mdgiitr.com.greenway;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Iterator;

public class VarianceGraph extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_variance_graph);
        GraphView graph = (GraphView) findViewById(R.id.id_graphVariance);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-5);
        graph.getViewport().setMaxY(5);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);

        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        LineGraphSeries lineGraphSeries = new LineGraphSeries();
        long time = 0; //millisec
        float temp;
        Iterator iterator = MainActivity.variance.iterator();
        while (iterator.hasNext()){
            temp = (float) iterator.next();
            lineGraphSeries.appendData(new DataPoint(time*100,temp),true,1000000000,false);
            time+=MainActivity.SENSOR_SAMPLING_PERIOD;
        }
        graph.addSeries(lineGraphSeries);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
