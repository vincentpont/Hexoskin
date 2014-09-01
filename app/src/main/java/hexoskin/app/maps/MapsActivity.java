package hexoskin.app.maps;

import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.hexoskin.app.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import hexoskin.app.seance.ResumeSeanceActivity;

/**
 * Created by Vincent Pont
 * Activity maps : Show the maps and information to the users
 * Last Modification 25.07.2014
 */

public class MapsActivity extends FragmentActivity implements View.OnClickListener {

    private GoogleMap mMap;
    private String caloriesBurned;
    private String bestProvider;
    private String sexe;
    private String totalCalories;
    private String totalDistance;
    private double latitude;
    private double longitude;
    private double altitude;
    private float distance;
    private float distanceMeterMin;
    private long timeWhenStopped = 0;
    private int poids;
    private int age;
    private int cTextSize;
    private DecimalFormat decimalformatTwo = new DecimalFormat();
    private LatLng actualPosition;
    private Location locations;
    private LocationManager locationManager;
    private List<Double> listLat = new ArrayList<Double>();
    private List<String> listStringLat = new ArrayList<String>();
    private List<Double> listLong = new ArrayList<Double>();
    private List<String> listStringLong = new ArrayList<String>();
    private List<Double> listAltitude = new ArrayList<Double>();
    private List<String> listStringAlti = new ArrayList<String>();
    private List<Double> listSpeed = new ArrayList<Double>();
    private List<String> listStringSpeed = new ArrayList<String>();
    private PolylineOptions rectOptions = new PolylineOptions().width(10).color(Color.MAGENTA);
    private ImageButton buttonPlay;
    private ImageButton buttonPause;
    private ImageButton buttonStop;
    private Chronometer chronometer;
    private Intent resumeSeance;
    private TextView caloriesBurnedView;
    private TextView distanceView;
    private TextView avgMeterMinView;
    private TextView speedView;
    private TextView textViewSearch;
    private ProgressBar progressBar;
    private TableLayout tableLayoutMaps;
    private Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Set decimal 2 for distance
        decimalformatTwo.setMaximumFractionDigits(2);


        // Get extras from intentInfo
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sexe = extras.getString("Sexe");
            poids = Integer.parseInt(extras.getString("Poids"));
            age = Integer.parseInt(extras.getString("Age"));
        }

        // Set variables
        resumeSeance = new Intent(this, ResumeSeanceActivity.class);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        caloriesBurnedView = (TextView) findViewById(R.id.textViewCaloriesBurnedMaps);
        distanceView = (TextView) findViewById(R.id.textViewDistance);
        avgMeterMinView = (TextView) findViewById(R.id.textViewMoyMinKm);
        speedView = (TextView) findViewById(R.id.textViewSpeed);
        buttonPlay = (ImageButton) findViewById(R.id.buttonPlay);
        buttonPause = (ImageButton) findViewById(R.id.buttonPause);
        buttonStop = (ImageButton) findViewById(R.id.buttonStop);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        textViewSearch = (TextView) findViewById(R.id.textViewSearch);
        tableLayoutMaps = (TableLayout) findViewById(R.id.tableLayoutMaps);

        // Add listener
        buttonPlay.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        buttonPause.setOnClickListener(this);

        // Test if GPS is already enable
        if (testGPSEnable() == false) {
            Toast.makeText(getApplicationContext(), "GPS OFF, Svp activez le GPS.", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(i);
        }

        // Get the location manager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        bestProvider = locationManager.getBestProvider(criteria, false);
        locations = locationManager.getLastKnownLocation(bestProvider);


        // If we have an older location
        if (locations != null) {
            longitude = locations.getLongitude();
            latitude = locations.getLatitude();
            actualPosition = new LatLng(latitude, longitude);
        }


        // Create map
        setUpMapIfNeeded();

        // Move camera if we had already an location
        if (actualPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(actualPosition, 16));
        }


        Toast.makeText(getApplicationContext(), "Attendez pendant que le GPS se calibre.", Toast.LENGTH_SHORT).show();

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
                // IF chrono is 15 second we can calculate average for one min, etc..
                if (chronometer.getText().toString().equals("0:15")) {
                    distanceMeterMin = distance * 4;
                    String stringResult = decimalformatTwo.format(distanceMeterMin) + " m/min";
                    avgMeterMinView.setText(stringResult);
                } else if (chronometer.getText().toString().equals("0:30")) {
                    distanceMeterMin = distance * 2;
                    String stringResult = decimalformatTwo.format(distanceMeterMin) + " m/min";
                    avgMeterMinView.setText(stringResult);
                } else if (chronometer.getText().toString().equals("1:00")) {
                    distanceMeterMin = distance;
                    String stringResult = decimalformatTwo.format(distanceMeterMin) + " m/min";
                    avgMeterMinView.setText(stringResult);
                } else if (chronometer.getText().toString().equals("10:00")) {
                    distanceMeterMin = distance / 10;
                    String stringResult = decimalformatTwo.format(distanceMeterMin) + " m/min";
                    avgMeterMinView.setText(stringResult);
                } else if (chronometer.getText().toString().equals("30:00")) {
                    distanceMeterMin = distance / 30;
                    String stringResult = decimalformatTwo.format(distanceMeterMin) + " m/min";
                    avgMeterMinView.setText(stringResult);
                } else if (chronometer.getText().toString().equals("1:00:00")) {
                    distanceMeterMin = distance / 60;
                    String stringResult = decimalformatTwo.format(distanceMeterMin) + " m/min";
                    avgMeterMinView.setText(stringResult);
                }
            }
        });

        // Call method to wait until GPS is calibrate
        setProgressBar();

    }

    // Button during workout play/pause stop
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonPlay:
                buttonPlay.setVisibility(View.GONE);
                buttonPause.setEnabled(true);

                // Launch listener GPS, 2000 = time until update in second, 2 = meters until update
                locationManager.requestLocationUpdates(bestProvider, 2000, 2, locationListener);

                chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                chronometer.start();

                break;

            case R.id.buttonPause:
                buttonPause.setEnabled(false);
                buttonPlay.setVisibility(View.VISIBLE);

                // Save the chronometer
                timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
                chronometer.stop();

                // Save last location
                locations = locationManager.getLastKnownLocation(bestProvider);

                // Stop GPS listener
                locationManager.removeUpdates(locationListener);

                break;

            case R.id.buttonStop:
                buttonStop.setEnabled(false);
                buttonPlay.setEnabled(false);
                buttonPause.setEnabled(false);

                // Close GPS activity and chronometer
                chronometer.stop();
                locationManager.removeUpdates(locationListener);
                locationManager = null;
                locations = null;
                mMap.stopAnimation();

                Toast.makeText(getApplicationContext(), "Entraînement terminé.", Toast.LENGTH_SHORT).show();

                // Add finish marker
                if (listLat.size() > 0) {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(listLat.get(listLat.size() - 1), listLong.get(listLong.size() - 1))).title("Finish"));
                }

                // Transformation de la liste en double -> pour passer dans l'intent + datastore
                for (double d : listLat) {
                    listStringLat.add(String.valueOf(d));
                }
                for (double d : listLong) {
                    listStringLong.add(String.valueOf(d));
                }
                for (double d : listSpeed) {
                    listStringSpeed.add(String.valueOf(d));
                }
                for (double d : listAltitude) {
                    listStringAlti.add(String.valueOf(d));
                }

                // Pass the values for activity resume seance
                resumeSeance.putStringArrayListExtra("listStringLat", (ArrayList<String>) listStringLat);
                resumeSeance.putStringArrayListExtra("listStringLong", (ArrayList<String>) listStringLong);
                resumeSeance.putStringArrayListExtra("listStringSpeed", (ArrayList<String>) listStringSpeed);
                resumeSeance.putStringArrayListExtra("listStringAlti", (ArrayList<String>) listStringAlti);
                resumeSeance.putExtra("Duration", chronometer.getText().toString());
                resumeSeance.putExtra("CaloriesBurned", totalCalories);
                resumeSeance.putExtra("Distance", totalDistance);
                resumeSeance.putExtra("Speed", getSpeedAverage(listSpeed));

                // Put infos of users for saving
                resumeSeance.putExtra("Sexe", sexe);
                resumeSeance.putExtra("Poids", String.valueOf(poids));
                resumeSeance.putExtra("Age", String.valueOf(age));

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

    /**
     * Method to test if the GPS is enable
     *
     * @return boolean true or false
     */
    private Boolean testGPSEnable() {
        Boolean test;
        LocationManager mlocManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        test = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return test;
    }

    /**
     * Create a timer for wainting to calibrate the GPS to be more accurate.
     */
    public void setProgressBar() {

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                long t = Calendar.getInstance().getTimeInMillis();

                // Wait 15 seconds
                while (Calendar.getInstance().getTimeInMillis() - t < 15000) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Modify the UI element
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tableLayoutMaps.setVisibility(View.VISIBLE);
                        buttonPlay.setVisibility(View.VISIBLE);
                        buttonPause.setVisibility(View.VISIBLE);
                        buttonStop.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        textViewSearch.setVisibility(View.GONE);
                    }
                });
            }
        }, 0, 999999999); // Set max time

    }

    /**
     * Set up the Google maps
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
     * Add attributes to the maps (location, markers)
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        actualPosition = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(actualPosition, 16));
    }

    /**
     * Calculate distance between two points
     *
     * @return distance in String
     */
    private String calculateDistance() {

        // We must reset the variable each time
        distance = 0;

        for (int i = 0; i < listLat.size() - 1; i++) {

            if (listLat.size() > 1 && listLong.size() > 1) {
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

    /**
     * Calculate burned calories for men
     * We take an average for heart rate beacause we don't have this information
     * So : 220 - age.
     *
     * @return double calories
     */
    private double calculateCaloriesMen() {
        double elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        elapsedMillis = (elapsedMillis / 1000.00) / 60.00;
        double calorieBurnedMen = ((age * 0.2017) + (poids * 0.09036) +
                ((220 - age) * 0.6309) - 55.0969) * Double.parseDouble(decimalformatTwo.format(elapsedMillis)) / 4.184;

        return Double.parseDouble(decimalformatTwo.format(calorieBurnedMen));
    }

    /**
     * Calculate burned calories for women
     * We take an average for heart rate because we don't have this information
     * So : 220 - age.
     *
     * @return double calories
     */
    private double calculateCaloriesWomen() {
        double elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        elapsedMillis = (elapsedMillis / 1000.00) / 60.00;
        double calorieBurnedWomen = ((age * 0.074) + (poids * 0.05741) +
                ((220 - age) * 0.4472) - 20.4022) * Double.parseDouble(decimalformatTwo.format(elapsedMillis)) / 4.184;

        return Double.parseDouble(decimalformatTwo.format(calorieBurnedWomen));
    }

    /**
     * Method that calculate the speed average
     *
     * @param list speed
     * @return String with two decimals
     */
    private String getSpeedAverage(List<Double> list) {

        Double value = 0.0;
        int count = 1;

        Iterator<Double> iterator = listSpeed.iterator();
        while (iterator.hasNext()) {
            value += iterator.next();
            count++;
        }
        value = value / count;
        return decimalformatTwo.format(value);

    }


    /**
     * Location listener
     * Here we know when the GPS location changed and we do all the stuff
     */
    public LocationListener locationListener = new LocationListener() {

        public void onLocationChanged(Location location) {

            locations = location;
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            // Add marker start, only one time
            if (listLat.size() == 1) {
                mMap.addMarker(new MarkerOptions().position
                        (new LatLng(listLat.get(0), listLong.get(0))).title("Start"));
            }

            // Draw path
            // Add a point(location) and draw the polyline when 2 points are inserted
            rectOptions.add(new LatLng(latitude, longitude));
            mMap.addPolyline(rectOptions);

            // Add to the list the new Lat & Long
            listLat.add(latitude);
            listLong.add(longitude);

            // Get altitude and add to list
            altitude = locations.getAltitude();
            listAltitude.add(altitude);

            // Add speed to the list
            listSpeed.add(Double.parseDouble(decimalformatTwo.format(locations.getSpeed() * 3.6)));

            // Update speed view
            speedView.setText(decimalformatTwo.format(locations.getSpeed() * 3.6) + " km/h");


            // Update textViews distance
            distanceView.setText(calculateDistance() + " m");
            totalDistance = decimalformatTwo.format(distance);

            // Update textViews calories, and check if women or men
            if (sexe.toString().equals("femme")) {
                caloriesBurned = String.valueOf(calculateCaloriesWomen());
                caloriesBurnedView.setText(caloriesBurned + " ca");
                totalCalories = caloriesBurned;
            } else {
                caloriesBurned = String.valueOf(calculateCaloriesMen());
                caloriesBurnedView.setText(caloriesBurned + " ca");
                totalCalories = caloriesBurned;
            }
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
