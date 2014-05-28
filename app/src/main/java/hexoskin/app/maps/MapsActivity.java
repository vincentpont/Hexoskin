package hexoskin.app.maps;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hexoskin.app.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import hexoskin.app.seance.ResumeSeanceActivity;


public class MapsActivity extends FragmentActivity implements View.OnClickListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private double latitude;
    private double longitude;
    private int poids;
    private int age;
    private DecimalFormat decimalformat = new DecimalFormat();
    private LatLng actualPosition;
    private Location locations;
    private LocationManager locationManager;
    private List<Double> listLat = new ArrayList<Double>();
    private List<Double> listLong = new ArrayList<Double>();
    private PolylineOptions rectOptions = new PolylineOptions().width(3).color(Color.BLUE);
    private Button buttonPause;
    private Button buttonStop;
    private Chronometer chronometer;
    private Intent resumeSeance;
    private TextView caloriesBurnedView;
    private TextView distanceView;
    private TextView moyMinKmView;
    private String caloriesBurned ;
    private String provider;
    private String sexe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Set decimal 2
        decimalformat.setMaximumFractionDigits(2);


        // Get extra from intentInfo
        Bundle extras = getIntent().getExtras();
        sexe = extras.getString("Sexe");
        poids = Integer.parseInt(extras.getString("Poids"));
        age = Integer.parseInt(extras.getString("Age"));

        resumeSeance = new Intent(this, ResumeSeanceActivity.class);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        caloriesBurnedView = (TextView) findViewById(R.id.textViewCaloriesBurnedMaps);
        distanceView = (TextView) findViewById(R.id.textViewDistance);
        moyMinKmView = (TextView) findViewById(R.id.textViewMoyMinKm);
        buttonPause = (Button) findViewById(R.id.buttonPause);
        buttonStop = (Button) findViewById(R.id.buttonStop);

        buttonPause.setOnClickListener(this);
        buttonStop.setOnClickListener(this);

        // Set strings or hours and minutes
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            public void onChronometerTick(Chronometer c) {
                int cTextSize = c.getText().length();
                if (cTextSize == 5) {
                    chronometer.setText("00:"+c.getText().toString());
                } else if (cTextSize == 7) {
                    chronometer.setText("0"+c.getText().toString());
                }
            }
        });


        // Test if GPS is enable or not
        if(testGPSEnable() == true){
            //Toast.makeText(getApplicationContext(), "GPS ON", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "GPS OFF, Please turn on GPS.", Toast.LENGTH_LONG).show();
            Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(i);
        }

        // Get infos form GPS
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Get the best provider based on Accuracy, power consumption, response, bearing and monetary cost
        Criteria c = new Criteria();
        provider = locationManager.getBestProvider(c, false);

        locations = locationManager.getLastKnownLocation(provider);
        if(locations != null) {
            longitude = locations.getLongitude();
            latitude = locations.getLatitude();
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

        // Create map
        setUpMapIfNeeded();
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.buttonPause:
                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());
                break;

            case R.id.buttonStop:

                chronometer.stop();
                // Close GPS activity
                locationManager.removeUpdates(locationListener);
                locationManager = null;
                locations = null;
                Toast.makeText(getApplicationContext(), "Workout finish.", Toast.LENGTH_LONG).show();
                resumeSeance.putExtra("Duration", chronometer.getText().toString());
                resumeSeance.putExtra("CaloriesBurned", caloriesBurned );
                startActivity(resumeSeance);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    // Method that test is GPS is enable or not
    private Boolean testGPSEnable(){

        Boolean test ;
        LocationManager mlocManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        test = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return test;
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        actualPosition = new LatLng(latitude,longitude);

        // Start chronometer when the map appears
        chronometer.start();
        //Toast.makeText(getApplicationContext(), "lat :" +latitude +" long: " +longitude, Toast.LENGTH_LONG).show();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(actualPosition, 16));

        // Modifier pour ajouter début fin
        //mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Départ"));

    }

    //Calculate distance between two points
    private double calculateDistance(){

        double distance = 0.0;

        for(int i = 0 ; i<listLat.size(); i++) {

            if(listLat.size() > 1 && listLong.size() > 2) {
                Location locationA = new Location("Point1");
                locationA.setLatitude(listLat.get(i));
                locationA.setLongitude(listLong.get(i));

                Location locationB = new Location("Point2");
                locationB.setLatitude(listLat.get(i + 1));
                locationB.setLongitude(listLong.get(i + 1));

                distance = locationA.distanceTo(locationB);
                distance = distance / 1000 ;
            }
        }
        return Double.parseDouble(decimalformat.format(distance));
    }

    //Calculate calories for men
    private double calculateCaloriesMen(){
        double elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        elapsedMillis = (elapsedMillis/1000.00)/60.00;
        double calorieBurnedMen = ((age * 0.2017) + (poids * 0.09036) +
                ((200-age) * 0.6309) - 55.0969) * Double.parseDouble(decimalformat.format(elapsedMillis))   / 4.184 ;

        return Double.parseDouble(decimalformat.format(calorieBurnedMen));
    }

    //Calculate calories for women
    private double calculateCaloriesWomen(){
        double elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        elapsedMillis = (elapsedMillis/1000.00)/60.00;
        double calorieBurnedWomen = ((age * 0.074) + (poids * 0.05741) +
                ((200-age) * 0.4472) - 20.4022) * Double.parseDouble(decimalformat.format(elapsedMillis))  / 4.184 ;

        return Double.parseDouble(decimalformat.format(calorieBurnedWomen));
    }


    public LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            locations = location;
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            double elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
            elapsedMillis = (elapsedMillis/1000.00)/60.00;

            Toast.makeText(getApplicationContext(), "NEWS! lat :" +latitude +" long: " +longitude + "Minutes :" +decimalformat.format(elapsedMillis), Toast.LENGTH_LONG).show();

            // Add to the list the new Lat & Long
            listLat.add(latitude);
            listLong.add(longitude);

            // Add a points(location) and draw the polyline when 2 points are inserted
            rectOptions.add(new LatLng(latitude, longitude));
            mMap.addPolyline(rectOptions);

            // UPDATE textViews CALORIES, if women or men calcule are differents
            if(sexe.toString().equals("femme")){
                caloriesBurned = String.valueOf(calculateCaloriesWomen());
                caloriesBurnedView.setText(caloriesBurned + " calories");
            }
            else{
                caloriesBurned = String.valueOf(calculateCaloriesMen());
                caloriesBurnedView.setText(caloriesBurned + " calories");
            }

            // UPDATE textViews DISTANCE bugg
            //distanceView.setText(String.valueOf(calculateDistance() +" km"));

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };
}
