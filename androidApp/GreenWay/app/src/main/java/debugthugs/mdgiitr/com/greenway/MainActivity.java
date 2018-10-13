package debugthugs.mdgiitr.com.greenway;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Button startRecording;
    private Button stopRecording;
    private Button showVariance;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private long newTime, startTime, prevTime;
    private ArrayList<Float> aZ;
    private ArrayList<Float> variance;
    private Float aZ_prev;

    private int SENSOR_SAMPLING_PERIOD = 10; //in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startTime = newTime = prevTime = System.currentTimeMillis();
        aZ = new ArrayList<Float>();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        startRecording = (Button) findViewById(R.id.id_startRecording);
        startRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.registerListener(MainActivity.this, accelerometer, SENSOR_SAMPLING_PERIOD * 1000);
            }
        });

        stopRecording = (Button) findViewById(R.id.id_stopRecording);
        stopRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.unregisterListener(MainActivity.this);
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
    }

    public void createVarianceData() {
        float[] x = new float[20];

        variance = new ArrayList<Float>();
        Iterator<Float> iterator = aZ.iterator();
        while (iterator.hasNext()) {
            for (int i = 0; i < 19; i++) {
                x[i] = x[i + 1];
            }
            x[20] = iterator.next();
            variance.add(variance(x));
        }
    }

    public float variance(float[] x) {
        float variance = 0f;
        float average = 0F;
        for (int i = 0; i < x.length; i++) {
            average = average + x[i];
        }
        average = average / x.length;
        for (int i = 0; i < x.length; i++) {
            variance = (float) Math.pow((double) (x[i] - average), 2);
        }
        variance = variance / (x.length - 1);
        return variance;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        newTime = System.currentTimeMillis();
        if (newTime - prevTime >= SENSOR_SAMPLING_PERIOD) {
            if (Math.abs(event.values[2]) > 0.5) {
                aZ.add(event.values[2]);
                aZ_prev = event.values[2];
            } else {
                aZ.add(aZ_prev);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
