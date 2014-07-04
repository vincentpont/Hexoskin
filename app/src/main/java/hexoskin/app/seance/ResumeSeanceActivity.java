package hexoskin.app.seance;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.appspot.logical_light_564.helloworld.Helloworld;
import com.appspot.logical_light_564.helloworld.Helloworld.Greetings.PutDataSeance;
import com.appspot.logical_light_564.helloworld.Helloworld.Greetings.PutDataMap;
import com.example.hexoskin.app.R;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import hexoskin.app.info.InfosUserActivity;
import hexoskin.app.apiGoogle.AppConstants;
import hexoskin.app.login.PlusBaseActivity;
import hexoskin.app.maps.MapsActivity;
import com.google.appengine.api.datastore.Text;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Vincent Pont
 * Last Modification 17.06.2014
 *
 */

public class ResumeSeanceActivity extends Activity {

    private String durationExtras;
    private TextView durationView;
    private TextView caloriesView;
    private TextView distanceView;
    private TextView textViewDate;
    private TextView speedView;
    private Intent intentInfos;
    private String calorieBurnedExtras;
    private String distanceExtras;
    private String speedExtras;
    private String emailUser;
    private ImageButton imageButtonSave;
    private ImageButton imageButtonDelete;
    private AsyncTask<Void, Void, PutDataSeance> putDataSeance;
    private AsyncTask<Void, Void, PutDataMap> putDataMap;
    private List<String> listStringLat = new ArrayList<String>();
    private List<String> listStringLong = new ArrayList<String>();
    private List<String> listStringAlti = new ArrayList<String>();
    private List<String> listStringSpeed = new ArrayList<String>();
    private Date date = new Date();
    private SimpleDateFormat sdfDataStore = new SimpleDateFormat("yyyy.MM.dd.HH:mm");
    private SimpleDateFormat sdfView = new SimpleDateFormat("yyyy.MM.dd");
    private Intent intentMaps;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_seance);

        // Set intents
        intentInfos = new Intent(this, InfosUserActivity.class);
        intentMaps = new Intent(this, MapsActivity.class);

        // Call class intern and get userEmail
        PlusBaseActivity.ClassIntern ca = new PlusBaseActivity.ClassIntern();
        emailUser = ca.getEmailUser();

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            durationExtras = extras.getString("Duration");
            calorieBurnedExtras = extras.getString("CaloriesBurned");
            distanceExtras = extras.getString("Distance");
            speedExtras = extras.getString("Speed");
            listStringLat = extras.getStringArrayList("listStringLat");
            listStringLong = extras.getStringArrayList("listStringLong");
            listStringAlti = extras.getStringArrayList("listStringAlti");
            listStringSpeed = extras.getStringArrayList("listStringSpeed");
        }

        // Set variables
        durationView = (TextView) findViewById(R.id.textViewDurationResume);
        caloriesView = (TextView) findViewById(R.id.textViewCaloriesBurnedResume);
        distanceView = (TextView) findViewById(R.id.textViewDistanceResume);
        imageButtonSave = (ImageButton) findViewById(R.id.imageButtonSave);
        imageButtonDelete = (ImageButton) findViewById(R.id.imageButtonDelete);
        textViewDate = (TextView) findViewById(R.id.textViewDate);
        speedView = (TextView) findViewById((R.id.textViewSpeedResume));
        textViewDate.setText(sdfView.format(date));

        // Set the TextViews
        if(extras != null) {
            caloriesView.setText(calorieBurnedExtras);
            distanceView.setText(distanceExtras);
            durationView.setText(durationExtras);
            speedView.setText(speedExtras);
        }


        // Put data in Datastore
        putDataSeance = new AsyncTask<Void, Void, PutDataSeance> () {

            @Override
            protected PutDataSeance doInBackground(Void... voids) {

                // Retrieve service handle.
                Helloworld apiServiceHandle = AppConstants.getApiServiceHandle();

                try {
                    // Call the api method and pass the values to save the data
                    PutDataSeance putDataInStore = apiServiceHandle.greetings()
                            .putDataSeance(sdfDataStore.format(date),
                                    emailUser,
                                    distanceView.getText().toString(),
                                    durationView.getText().toString(),
                                    caloriesView.getText().toString(),
                                    speedView.getText().toString()
                            );
                    putDataInStore.execute();

                } catch (IOException e) {
                    Log.e("Error", e.toString());

                }
                return null;
            }
        };

        // Put data map in Datastore
        putDataMap = new AsyncTask<Void, Void, PutDataMap> () {

            @Override
            protected PutDataMap doInBackground(Void... voids) {

                // Retrieve service handle.
                Helloworld apiServiceHandle = AppConstants.getApiServiceHandle();

                try {
                    // Call the api method and pass the values to save the data
                    PutDataMap putlistLat = apiServiceHandle.greetings()
                            .putDataMap(sdfDataStore.format(date),
                                    emailUser, listStringLat, listStringLong,
                                    listStringSpeed, listStringAlti);

                    putlistLat.execute();

                } catch (IOException e) {
                    Log.e("Error", e.toString());
                }
                return null;
            }
        };

        // Listener save data in DataStore
        imageButtonSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Put data in datastore
                putDataSeance.execute();
                putDataMap.execute();
                Toast.makeText(getApplicationContext(), "Seance saved.", Toast.LENGTH_SHORT).show();
                startActivity(intentInfos);
            }
        });

        // Listener save data in DataStore
        imageButtonDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Seance deleted.", Toast.LENGTH_SHORT).show();
                startActivity(intentInfos);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_general, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.menu_newtraining:
                startActivity(intentInfos);
                return true;
            case R.id.menu_carte:
                // Resume the activity and not start a new one
                intentMaps.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intentMaps);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
