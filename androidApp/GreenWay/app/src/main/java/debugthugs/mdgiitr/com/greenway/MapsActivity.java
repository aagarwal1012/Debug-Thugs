package debugthugs.mdgiitr.com.greenway;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toolbar;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nonnull;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<HashMap<String, String>> googlelist;

    private String TAG = "MapsActivity";

    private ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Maps");
        setActionBar(toolbar);

        googlelist = new ArrayList<>();

        progressDialog = new ProgressDialog(MapsActivity.this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    void getPoints() {
        progressDialog.setMessage("Fetching The Data...");
        progressDialog.show();
        GraphQLCLient.getApolloClient().query(
                FetchQuery.builder().build()).enqueue(new ApolloCall.Callback<FetchQuery.Data>() {
            @Override
            public void onResponse(@Nonnull com.apollographql.apollo.api.Response<FetchQuery.Data> response) {
                Log.d("Response from GraphQL", "onResponse: " + response.toString());

                googlelist = new ArrayList();

                for (int i = 0; i < response.data().pothole().size(); i++){
                    HashMap<String, String> plocation = new HashMap<>();
                    plocation.put("latitude", response.data().pothole().get(i).lattitude());
                    plocation.put("longitude", response.data().pothole().get(i).longitude());
                    googlelist.add(plocation);
                }

                MapsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        markOnMap();
                    }
                });

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                progressDialog.dismiss();
            }
        });
    }

    private void markOnMap() {

        Log.d("mark", ""+googlelist.size());
        for (int i = 0; i < googlelist.size(); i++) {
            // Log.d("i", ""+i);
            Log.d("mark", googlelist.get(i).get("longitude"));
            LatLng sydney = new LatLng(Double.parseDouble(googlelist.get(i).get("latitude")), Double.parseDouble(googlelist.get(i).get("longitude")));
            MarkerOptions marker = new MarkerOptions();
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            mMap.addMarker(marker.position(sydney).title("pothole"));

            //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
//        if (googlelist.size() >= 2){
//            PolylineOptions polyOptions = new PolylineOptions();
//            polyOptions.color(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
//            polyOptions.width(10);
//            LatLng startLatLng = new LatLng(Double.parseDouble(googlelist.get(0).get("latitude")), Double.parseDouble(googlelist.get(0).get("longitude")));
//            LatLng latLngDestination = new LatLng(Double.parseDouble(googlelist.get(1).get("latitude")), Double.parseDouble(googlelist.get(1).get("longitude")));
//            polyOptions.add(startLatLng, latLngDestination);
//            mMap.addPolyline(polyOptions);
//        }

    }
}
