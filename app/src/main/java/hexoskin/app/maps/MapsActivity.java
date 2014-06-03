package hexoskin.app.maps;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hexoskin.app.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
    private float distance ;
    private float distanceMeterMin;
    private long timeWhenStopped = 0;
    private int poids;
    private int age;
    private int cTextSize;
    private DecimalFormat decimalformatTwo = new DecimalFormat();
    private LatLng actualPosition;
    private Location locations;
    private Location locationWhenStopped;
    private LocationManager locationManager;
    private List<Double> listLat = new ArrayList<Double>();
    private List<Double> listLong = new ArrayList<Double>();
    private PolylineOptions rectOptions = new PolylineOptions().width(10).color(Color.MAGENTA);
    private ImageButton buttonPlay;
    private ImageButton buttonPause;
    private ImageButton buttonStop;
    private Chronometer chronometer;
    private Intent resumeSeance;
    private TextView caloriesBurnedView;
    private TextView distanceView;
    private TextView avgMeterMinView;
    private String caloriesBurned ;
    private String provider;
    private String sexe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Set decimal 2 for distance
        decimalformatTwo.setMaximumFractionDigits(2);

        // Get extras from intentInfo
        Bundle extras = getIntent().getExtras();
        sexe = extras.getString("Sexe");
        poids = Integer.parseInt(extras.getString("Poids"));
        age = Integer.parseInt(extras.getString("Age"));

        resumeSeance = new Intent(this, ResumeSeanceActivity.class);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        caloriesBurnedView = (TextView) findViewById(R.id.textViewCaloriesBurnedMaps);
        distanceView = (TextView) findViewById(R.id.textViewDistance);
        avgMeterMinView = (TextView) findViewById(R.id.textViewMoyMinKm);
        buttonPlay = (ImageButton) findViewById(R.id.buttonPlay);
        buttonPause = (ImageButton) findViewById(R.id.buttonPause);
        buttonStop = (ImageButton) findViewById(R.id.buttonStop);


        // Add listener
        buttonPlay.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        buttonPause.setOnClickListener(this);

        // Test if GPS is enable or not
        if(testGPSEnable() == true){
            //Toast.makeText(getApplicationContext(), "GPS ON", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), "GPS OFF, Please turn on GPS.", Toast.LENGTH_LONG).show();
            Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(i);
        }



            // Chronometer listener
            chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                    public void onChronometerTick(Chronometer c) {
                        cTextSize = c.getText().length();
                        // Set strings or hours and minutes
                        if (cTextSize == 5) {
                            chronometer.setText("00:" + c.getText().toString());
                        } else if (cTextSize == 7) {
                            chronometer.setText("0" + c.getText().toString());
                        }
                        // Calculate meter/min
                        //IF chrono is 15 second we can calculate average for one min, etc..
                        if(chronometer.getText().toString().equals("0:15")){
                            distanceMeterMin = distance*4;
                            String stringResult = decimalformatTwo.format(distanceMeterMin) +" m/min";
                            avgMeterMinView.setText(stringResult);
                        }
                        else if(chronometer.getText().toString().equals("0:30")){
                            distanceMeterMin = distance*2;
                            String stringResult = decimalformatTwo.format(distanceMeterMin) +" m/min";
                            avgMeterMinView.setText(stringResult);
                        }
                        else if(chronometer.getText().toString().equals("1:00")){
                            distanceMeterMin = distance;
                            String stringResult = decimalformatTwo.format(distanceMeterMin) +" m/min";
                            avgMeterMinView.setText(stringResult);
                        }
                        else if(chronometer.getText().toString().equals("10:00")){
                            distanceMeterMin = distance/10;
                            String stringResult = decimalformatTwo.format(distanceMeterMin) +" m/min";
                            avgMeterMinView.setText(stringResult);
                        }
                        else if(chronometer.getText().toString().equals("30:00")){
                            distanceMeterMin = distance/30;
                            String stringResult = decimalformatTwo.format(distanceMeterMin) +" m/min";
                            avgMeterMinView.setText(stringResult);
                        }
                        else if(chronometer.getText().toString().equals("1:00:00")){
                            distanceMeterMin = distance/60;
                            String stringResult = decimalformatTwo.format(distanceMeterMin) +" m/min";
                            avgMeterMinView.setText(stringResult);
                        }
                    }
             });

            // Get info from GPS
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            //Get the best provider based on Accuracy, power consumption, response, bearing and monetary cost
            Criteria c = new Criteria();
            provider = locationManager.getBestProvider(c, false);

            locations = locationManager.getLastKnownLocation(provider);

            // If we have an older location
            if (locations != null) {
                longitude = locations.getLongitude();
                latitude = locations.getLatitude();
            }

            // Create map
            setUpMapIfNeeded();

            Toast.makeText(getApplicationContext(), "Before launch, wait until your location is good.", Toast.LENGTH_LONG).show();

    }

    // Button during workout play/pause stop
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.buttonPlay:
                buttonPlay.setEnabled(false);

                // Launch listener GPS, 2000 = time until update, 2 = meter until update
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 2, locationListener);

                chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                chronometer.start();

                break;

            case R.id.buttonPause:
                buttonPlay.setEnabled(true);

                // Save the chronometer
                timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
                chronometer.stop();

                // Save last location
                locationWhenStopped = locationManager.getLastKnownLocation(provider);
                locations = locationWhenStopped;

                // Stop GPS listener
                locationManager.removeUpdates(locationListener);
                break;

            case R.id.buttonStop:
                // Close GPS activity and chronometer
                chronometer.stop();
                locationManager.removeUpdates(locationListener);
                locationManager = null;
                locations = null;
                mMap.stopAnimation();
                Toast.makeText(getApplicationContext(), "Workout finish.", Toast.LENGTH_LONG).show();

                // Add finish marker
                mMap.addMarker(new MarkerOptions().position(new LatLng(listLat.get(listLat.size()-1), listLong.get(listLong.size()-1))).title("Finish"));

                // Pass the values for resume seance
                resumeSeance.putExtra("Duration", chronometer.getText().toString());
                resumeSeance.putExtra("CaloriesBurned", caloriesBurnedView.getText());
                resumeSeance.putExtra("Distance", distanceView.getText());
                resumeSeance.putExtra("AvgMeterKm",avgMeterMinView.getText());
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

    // Set up the Google maps
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


    // Add attributes to the maps (location, markers)
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        actualPosition = new LatLng(latitude,longitude);

        //Toast.makeText(getApplicationContext(), "lat :" +latitude +" long: " +longitude, Toast.LENGTH_LONG).show();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(actualPosition, 16));

    }

    //Calculate distance between two points
    private String calculateDistance(){

        // We must reset the variable
        distance = 0 ;

        for(int i = 0 ; i < listLat.size()-1; i++) {

            if(listLat.size() > 1 && listLong.size() > 1) {
                Location locationA = new Location("PointA");
                locationA.setLatitude(listLat.get(i));
                locationA.setLongitude(listLong.get(i));

                Location locationB = new Location("PointB");
                locationB.setLatitude(listLat.get(i + 1));
                locationB.setLongitude(listLong.get(i + 1));

                distance += locationA.distanceTo(locationB);

            }
        }
        return decimalformatTwo.format(distance);
    }

    //Calculate calories for men
    private double calculateCaloriesMen(){
        double elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        elapsedMillis = (elapsedMillis/1000.00)/60.00;
        double calorieBurnedMen = ((age * 0.2017) + (poids * 0.09036) +
                ((220-age) * 0.6309) - 55.0969) * Double.parseDouble(decimalformatTwo.format(elapsedMillis))   / 4.184 ;

        return Double.parseDouble(decimalformatTwo.format(calorieBurnedMen));
    }

    //Calculate calories for women
    private double calculateCaloriesWomen(){
        double elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        elapsedMillis = (elapsedMillis/1000.00)/60.00;
        double calorieBurnedWomen = ((age * 0.074) + (poids * 0.05741) +
                ((220-age) * 0.4472) - 20.4022) * Double.parseDouble(decimalformatTwo.format(elapsedMillis))  / 4.184 ;

        return Double.parseDouble(decimalformatTwo.format(calorieBurnedWomen));
    }


    public LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            locations = location;
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            //Toast.makeText(getApplicationContext(), "NEWS! lat :" +latitude +" long: " +longitude + "Distance " +distance + "SizeList "+ listLat.size(), Toast.LENGTH_SHORT).show();

            // Add to the list the new Lat & Long
            listLat.add(latitude);
            listLong.add(longitude);

            // Add marker start, only one time
            if(listLat.size() == 1) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(listLat.get(0), listLong.get(0))).title("Start"));
            }

            // Add a point(location) and draw the polyline when 2 points are inserted
            rectOptions.add(new LatLng(latitude, longitude));
            mMap.addPolyline(rectOptions);

            // UPDATE textViews CALORIES, and check if women or men
            if(sexe.toString().equals("femme")){
                caloriesBurned = String.valueOf(calculateCaloriesWomen());
                caloriesBurnedView.setText(caloriesBurned + " calories");
            }
            else{
                caloriesBurned = String.valueOf(calculateCaloriesMen());
                caloriesBurnedView.setText(caloriesBurned + " calories");
            }

            // UPDATE textViews DISTANCE
            distanceView.setText(calculateDistance() +" m");



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
