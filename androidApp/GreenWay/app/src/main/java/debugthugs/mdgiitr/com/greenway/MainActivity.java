package debugthugs.mdgiitr.com.greenway;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener {

    private Button startRecording;
    private Button stopRecording;
    private Button showVariance;
    private Button showRoadCondition;
    private Button clearBtn;
    private Button mapsBtn;
    private SensorManager sensorManagerAcc;
    private Sensor accelerometer;
    private SensorManager sensorManagerGyro;
    private Sensor gyrosensor;

    private SensorManager sensorManager;

    public long newTime, startTime, prevTime;
    private ArrayList<Float> aZ;
    private ArrayList<Float> angleArrayList;
    public static ArrayList<Float> angleVariation;
    public static ArrayList<Float> variance;
    public static ArrayList<Byte> roadConditionData;
    private Float aZ_prev;
    private Float w2Y, w1Y, Ly, w2X, w1X, Lx;

    public static float SENSOR_SAMPLING_PERIOD = 10F; //in milliseconds

    private LineGraphSeries<DataPoint> lineGraphSeries;

    private Sensor accelerometer_g, gyro;
    LocationManager locationManager;
    String mprovider;

    private String TAG = "so47492459";

    boolean rekam = false;
    public static float ax[] = new float[10];
    public static float ay[] = new float[10];
    public static float az[] = new float[10];
    public static float gx[] = new float[10];
    public static float gy[] = new float[10];
    public static float gz[] = new float[10];
    public static float tm[] = new float[10];
    int c = 0;
    String t1;

    private static final int PERMISSION_REQUEST_CODE = 1;
    ArrayList<String> value;

    Location location;
    public static int count = 0;
    private String mJSONURLString = "https://debug-thugs.herokuapp.com/data";

    private LineGraphSeries<DataPoint> lineGraphSeries_forZ;
    private LineGraphSeries<DataPoint> lineGraphSeries_for_angles;

    Switch s1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkPermission()) {
            requestPermission();
        }

        value = new ArrayList<>();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer_g = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        s1 = (Switch) findViewById(R.id.data_switch);

        mprovider = locationManager.getBestProvider(criteria, false);

        if (mprovider != null && !mprovider.equals("")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("haha", "not done");
                return;
            }
            location = locationManager.getLastKnownLocation(mprovider);
            locationManager.requestLocationUpdates(mprovider, 50, (float) 0.01, this);

            if (location != null)
                onLocationChanged(location);
            else {
                Toast.makeText(getBaseContext(), "Turn your GPS ON", Toast.LENGTH_LONG).show();

            }
        }


        //switch code
        s1.setChecked(false);
        s1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getBaseContext(), "Data sending ON", Toast.LENGTH_SHORT).show();
                    rekam = true;
                } else {
                    Toast.makeText(getBaseContext(), "Data sending OFF", Toast.LENGTH_SHORT).show();
                    rekam = false;
                }
            }
        });

        lineGraphSeries = new LineGraphSeries<>();
        final GraphView graph = (GraphView) findViewById(R.id.id_graphZ);

        lineGraphSeries_forZ = new LineGraphSeries<>();
        lineGraphSeries_for_angles = new LineGraphSeries<>();
        final GraphView graphZ = (GraphView) findViewById(R.id.id_graphZ);
        final GraphView graphLy = (GraphView) findViewById(R.id.id_graphLy);

        startTime = newTime = prevTime = System.currentTimeMillis();
        aZ = new ArrayList<Float>();
        angleArrayList = new ArrayList<>();
        angleVariation = new ArrayList<>();
        roadConditionData = new ArrayList<>();

        aZ_prev = w2Y = w1Y = Ly = w2X = w1X = Lx = new Float(0);

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
                graphLy.getViewport().setMinY(-3);
                graphLy.getViewport().setMaxY(3);

                graphLy.getViewport().setXAxisBoundsManual(false);
                graphLy.getViewport().setMinX(1);


                // enable scaling and scrolling
                graphLy.getViewport().setScalable(true);
                graphLy.getViewport().setScalableY(true);

                graphLy.addSeries(lineGraphSeries_for_angles);
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
                startActivity(new Intent(MainActivity.this, VarianceGraphs.class));
            }
        });

        showRoadCondition = (Button) findViewById(R.id.id_showRoadCondition);
        showRoadCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createVarianceData();

                Float temp1, temp2;
                Iterator i1, i2;
                i1 = variance.iterator();
                i2 = angleVariation.iterator();
                while (i1.hasNext() && i2.hasNext()) {
                    temp1 = (Float) i1.next();
                    temp2 = (Float) i2.next();
                    if (temp1>120){
                        roadConditionData.add((byte)1);
                    }
                    else{
                        if(temp2>0.4){
                            roadConditionData.add((byte)2);
                        }
                        else {
                            roadConditionData.add((byte)0);
                        }
                    }
                }

                startActivity(new Intent(MainActivity.this,RoadCondition.class));
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

        mapsBtn = (Button) findViewById(R.id.id_maps);
        mapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });

    }

    private boolean checkPermission() {

        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {

            return false;

        }

    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            Toast.makeText(getApplicationContext(), "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getBaseContext(), "Permission Granted, Please refresh", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "Permission Denied, You cannot access location data", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //onResume() register the accelerometer_g for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (c == 0) {
                    value = new ArrayList<>();
                }

                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    if (rekam == true) {


                        float curx = 0, cury = 0, curz = 0, curt = 0;
                        if (count < 10) {
                            ax[count] = event.values[0];
                            ay[count] = event.values[1];
                            az[count] = event.values[2];
                            tm[count] = System.currentTimeMillis() / 10;
                            //count++;
                        } else if (count == 10) {
                            for (int j = 0; j < ax.length; j++) {
                                curx += ax[j];
                                cury += ay[j];
                                curz += az[j];
                                curt = tm[j];

                            }
                            curx /= 10;
                            cury /= 10;
                            curz /= 10;
                            curt /= 10;
                            if (c < 50) {
                                t1 = System.currentTimeMillis() / 10 + "," + Float.toString(curx) + "," + Float.toString(cury) + "," + Float.toString(curz);
                                Log.d("sensor", t1);
                                Log.d("c", c + "");
//                                value.add(t1);
//                                c++;
                            }

                            //count = 0;


                        }

                        //out.append((System.currentTimeMillis()/10)+",Accl|" + Float.toString(event.values[0]) + "\t" + Float.toString(event.values[1]) + "\t" + Float.toString(event.values[2]) + "\n");


                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        }, accelerometer_g, SensorManager.SENSOR_DELAY_GAME);


        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (c == 0) {
                    value = new ArrayList<>();
                }

                if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                    if (rekam == true) {

                        float curx = 0, cury = 0, curz = 0, curt = 0;
                        if (count < 10) {
                            gx[count] = event.values[0];
                            gy[count] = event.values[1];
                            gz[count] = event.values[2];
                            tm[count] = System.currentTimeMillis() / 10;
                            count++;
                        } else if (count == 10) {
                            for (int j = 0; j < ax.length; j++) {
                                curx += gx[j];
                                cury += gy[j];
                                curz += gz[j];
//                            curt = tm[j];

                            }
                            curx /= 10;
                            cury /= 10;
                            curz /= 10;
                            //curt/=10;
                            //out.append(Float.toString(curx) + "," + Float.toString(cury) + "," + Float.toString(curz) + ","+ gps2.getText()+"," + gps1.getText() +","+speed.getText()+ "\n");
                            if (c < 50) {
                                t1 = t1 + "," + Float.toString(curx) + "," + Float.toString(cury) + "," + Float.toString(curz) + "," + location.getLongitude() + "," + location.getLatitude() + "," + location.getSpeed();
                                Log.d("sensor", t1);
//                            Log.d("c", c + "");
                                value.add(t1);
                                if (c < 49){
                                    t1 = "";
                                }
                                c++;
                            } else if (c == 50) {
                                c = 0;
                                sendData();
                                value = null;
                            }
                            count = 0;

                        }


                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, gyro, SensorManager.SENSOR_DELAY_GAME);
    }

    //function to send data
    public void sendData() {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = "https://debug-thugs.herokuapp.com/data";
        JSONObject jsonBody = new JSONObject();
        try {

            for (int i = 0; i < value.size(); i++) {
                jsonBody.put("data" + i, value.get(i));
//                Log.d("sdata", value.get(i) + "|||");
            }
            Log.d("jsonbody_len", " " + jsonBody.toString());
            Log.d("value_size", " " + value.size());

            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("LOG_VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("LOG_VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {

                        responseString = String.valueOf(response.statusCode);

                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            queue.add(stringRequest);

        } catch (JSONException e) {
            e.printStackTrace();

        }
        // Request a string response from the provided URL.

        // Add the request to the RequestQueue.
        //queue.add(stringRequest);
    }

    //onPause() unregister the accelerometer_g for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        sensorManager.unregisterListener(this, gyro);
    }

    public void createVarianceData() {
        float[] x = new float[30];
        Iterator<Float> iterator = aZ.iterator();
        for (int i = x.length / 2; i < x.length; i++) {
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


        x = new float[50];
        iterator = angleArrayList.iterator();
        for (int i = x.length / 2; i < x.length; i++) {
            x[i] = iterator.next();
        }
        angleVariation = new ArrayList<>();
        while (iterator.hasNext()) {
            for (int i = 0; i <= x.length - 2; i++) {
                x[i] = x[i + 1];
            }
            x[x.length - 1] = iterator.next();
            angleVariation.add(variation(x));
        }
    }

    public float variation(float[] x) {
        float max = x[0], min = x[0];
        for (int i = 0; i < x.length; i++) {
            if (x[i] > max) {
                max = x[i];
            }
            if (x[i] < min) {
                min = x[i];
            }
        }
        return max - min;
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
                w2Y = event.values[1];
                Ly = Ly + ((w1Y + w2Y) / 2) * (SENSOR_SAMPLING_PERIOD / 1000);
                w1Y = w2Y;

                w2X = event.values[0];
                Lx = Lx + ((w1X + w2X) / 2) * (SENSOR_SAMPLING_PERIOD / 1000);
                w1X = w2X;

                angleArrayList.add((float) (Math.pow(Ly, 2) + Math.pow(Lx, 2)));

                lineGraphSeries_for_angles.appendData(new DataPoint((newTime - startTime) / 10, (float) (Math.pow(Ly, 2) + Math.pow(Lx, 2))), true, 10000000, false);
            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
