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
import com.appspot.logical_light_564.helloworld.Helloworld.Greetings.PutData;
import com.example.hexoskin.app.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import hexoskin.app.info.InfosUserActivity;
import hexoskin.app.apiGoogle.AppConstants;
import hexoskin.app.login.PlusBaseActivity;
import hexoskin.app.maps.MapsActivity;


public class ResumeSeanceActivity extends Activity {

    private String durationExtras;
    private TextView durationView;
    private TextView caloriesView;
    private TextView distanceView;
    private TextView avgMeterMinView;
    private Intent intentInfos;
    private Intent intentNewSeance;
    private String calorieBurnedExtras;
    private String distanceExtras;
    private String avgMeterMinExtras;
    private ImageButton imageButtonSave;
    private ImageButton imageButtonDelete;
    private AsyncTask<Void, Void, PutData> putData;
    private String emailUser;
    private Date date = new Date();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    private Intent intentMaps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_seance);

        intentInfos = new Intent(this, InfosUserActivity.class);
        intentNewSeance = new Intent(this, NewSeanceActivity.class);
        intentMaps = new Intent(this, MapsActivity.class);

        // Call class intern and get userEmail
        PlusBaseActivity.ClassIntern ca = new PlusBaseActivity.ClassIntern();
        emailUser = ca.getEmailUser();


        Bundle extras = getIntent().getExtras();
        durationExtras = extras.getString("Duration");
        calorieBurnedExtras = extras.getString("CaloriesBurned");
        distanceExtras = extras.getString("Distance");
        avgMeterMinExtras = extras.getString("AvgMeterKm");

        durationView = (TextView) findViewById(R.id.textViewDurationResume);
        caloriesView = (TextView) findViewById(R.id.textViewCaloriesBurnedResume);
        distanceView = (TextView) findViewById(R.id.textViewDistanceResume);
        avgMeterMinView = (TextView) findViewById(R.id.textAvgMeterMinResume);
        imageButtonSave = (ImageButton) findViewById(R.id.imageButtonSave);
        imageButtonDelete = (ImageButton) findViewById(R.id.imageButtonDelete);





        putData = new AsyncTask<Void, Void, Helloworld.Greetings.PutData> () {

            @Override
            protected Helloworld.Greetings.PutData doInBackground(Void... voids) {

                // Retrieve service handle.
                Helloworld apiServiceHandle = AppConstants.getApiServiceHandle();

                try {
                    // Call the api method and pass the values to save the data
                    Helloworld.Greetings.PutData putDataInStore = apiServiceHandle.greetings()
                            .putData(sdf.format(date),
                                    emailUser,
                                    distanceView.getText().toString(),
                                    durationView.getText().toString(),
                                    caloriesView.getText().toString(),
                                    avgMeterMinView.getText().toString()
                            );


                    putDataInStore.execute();

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
                putData.execute();
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



        // Set the TextViews
        if(calorieBurnedExtras != null) {
            caloriesView.setText(calorieBurnedExtras);
        }

        if(distanceExtras != null) {
            distanceView.setText(distanceExtras);
        }

        if(durationExtras != null) {
            durationView.setText(durationExtras);
        }

        if(avgMeterMinExtras != null){
            avgMeterMinView.setText(avgMeterMinExtras);
        }

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
