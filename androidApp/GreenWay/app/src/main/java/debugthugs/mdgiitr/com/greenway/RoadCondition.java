package debugthugs.mdgiitr.com.greenway;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Iterator;

public class RoadCondition extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road_condition);

        GraphView graph_roadCond = (GraphView) findViewById(R.id.id_graphRoadCondition);

        graph_roadCond.getViewport().setYAxisBoundsManual(true);
        graph_roadCond.getViewport().setMinY(-1);
        graph_roadCond.getViewport().setMaxY(3);

        graph_roadCond.getViewport().setXAxisBoundsManual(false);
        graph_roadCond.getViewport().setMinX(1);

        // enable scaling and scrolling
        graph_roadCond.getViewport().setScalable(false);
        graph_roadCond.getViewport().setScalableY(false);

        LineGraphSeries lineGraphSeries = new LineGraphSeries();
        long time = 0; //millisec
        float temp;
        Iterator iterator = MainActivity.roadConditionData.iterator();
        while (iterator.hasNext()){
            temp = (byte) iterator.next();
            lineGraphSeries.appendData(new DataPoint(time*100,temp),true,1000000000,false);
            time+=MainActivity.SENSOR_SAMPLING_PERIOD;
        }
        graph_roadCond.addSeries(lineGraphSeries);

    }
}
