package debugthugs.mdgiitr.com.greenway;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Button startRecording;
    private Button stopRecording;
    private Button showVariance;
    private Button clearBtn;
    private SensorManager sensorManagerAcc;
    private Sensor accelerometer;
    private SensorManager sensorManagerGyro;
    private Sensor gyrosensor;

    public long newTime, startTime, prevTime;
    private ArrayList<Float> aZ;
    private ArrayList<Float> LyArrayList;
    public static ArrayList<Float> variance;
    private Float aZ_prev;
    private Float w2, w1, Ly;

    public static float SENSOR_SAMPLING_PERIOD = 10F; //in milliseconds

    private LineGraphSeries<DataPoint> lineGraphSeries_forZ;
    private LineGraphSeries<DataPoint> lineGraphSeries_forLy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lineGraphSeries_forZ = new LineGraphSeries<>();
        lineGraphSeries_forLy = new LineGraphSeries<>();
        final GraphView graphZ = (GraphView) findViewById(R.id.id_graphZ);
        final GraphView graphLy = (GraphView) findViewById(R.id.id_graphLy);


        startTime = newTime = prevTime = System.currentTimeMillis();
        aZ = new ArrayList<Float>();
        LyArrayList = new ArrayList<>();

        aZ_prev = w2 = w1 = Ly = new Float(0);

        sensorManagerAcc = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManagerAcc.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        sensorManagerGyro = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyrosensor = sensorManagerAcc.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        startRecording = (Button) findViewById(R.id.id_startRecording);
        startRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManagerAcc.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
                sensorManagerAcc.registerListener(MainActivity.this, gyrosensor, SensorManager.SENSOR_DELAY_FASTEST);

                graphZ.getViewport().setYAxisBoundsManual(true);
                graphZ.getViewport().setMinY(-50);
                graphZ.getViewport().setMaxY(50);

                graphZ.getViewport().setXAxisBoundsManual(false);
                graphZ.getViewport().setMinX(1);


                // enable scaling and scrolling
                graphZ.getViewport().setScalable(true);
                graphZ.getViewport().setScalableY(true);

                graphZ.addSeries(lineGraphSeries_forZ);


                graphLy.getViewport().setYAxisBoundsManual(true);
                graphLy.getViewport().setMinY(-4);
                graphLy.getViewport().setMaxY(4);

                graphLy.getViewport().setXAxisBoundsManual(false);
                graphLy.getViewport().setMinX(1);


                // enable scaling and scrolling
                graphLy.getViewport().setScalable(true);
                graphLy.getViewport().setScalableY(true);

                graphLy.addSeries(lineGraphSeries_forLy);
            }
        });

        stopRecording = (Button) findViewById(R.id.id_stopRecording);
        stopRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManagerAcc.unregisterListener(MainActivity.this);
            }
        });

        showVariance = (Button) findViewById(R.id.id_showVariance);
        showVariance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createVarianceData();
                startActivity(new Intent(MainActivity.this, VarianceGraph.class));
            }
        });

        clearBtn = (Button) findViewById(R.id.id_clearButton);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
            }
        });
    }

    public void createVarianceData() {
        float[] x = new float[30];
        Iterator<Float> iterator = aZ.iterator();
        for (int i = 15; i < x.length; i++) {
            x[i] = iterator.next();
        }
        variance = new ArrayList<Float>();
        while (iterator.hasNext()) {
            for (int i = 0; i <= x.length - 2; i++) {
                x[i] = x[i + 1];
            }
            x[x.length - 1] = iterator.next();
            variance.add(variance(x));
        }
    }

    public float variance(float[] x) {
        float variance = 0f;
        float average = 0F;

        //calculating avg
        for (int i = 0; i < x.length; i++) {
            average = average + x[i];
        }
        average = average / x.length;

        //calc variance
        for (int i = 0; i < x.length; i++) {
            variance = variance + (float) Math.pow((double) (x[i] - average), 2);
        }
        variance = variance / (x.length - 1);
        return variance;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        newTime = System.currentTimeMillis();
        if (newTime - prevTime >= SENSOR_SAMPLING_PERIOD) {
            if (event.sensor.equals(accelerometer)) {
                if (Math.abs(event.values[2]) > 0.5) {
                    aZ.add(event.values[2]);
                    aZ_prev = event.values[2];
                } else {
                    aZ_prev = 0f;
                    aZ.add(aZ_prev);
                }
                lineGraphSeries_forZ.appendData(new DataPoint((newTime - startTime) / 10, aZ_prev), true, 10000, false);

            } else { //i.e. gyrosensor
                w2 = event.values[1];
                w2 = event.values[1];
                Ly = Ly + ((w1 + w2) / 2) * (SENSOR_SAMPLING_PERIOD/1000);
                w1 = w2;
                LyArrayList.add(Ly);

                lineGraphSeries_forLy.appendData(new DataPoint((newTime-startTime)/10,Ly),true,10000000,false);
            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
