package hexoskin.app.seance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hexoskin.app.R;

import org.w3c.dom.Text;

import hexoskin.app.info.InfosUserActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_seance);

        intentInfos = new Intent(this, InfosUserActivity.class);
        intentNewSeance = new Intent(this, NewSeanceActivity.class);

        Bundle extras = getIntent().getExtras();
        durationExtras = extras.getString("Duration");
        calorieBurnedExtras = extras.getString("CaloriesBurned");
        distanceExtras = extras.getString("Distance");
        avgMeterMinExtras = extras.getString("AvgMeterKm");

        durationView = (TextView) findViewById(R.id.textViewDurationResume);
        caloriesView = (TextView) findViewById(R.id.textViewCaloriesBurnedResume);
        distanceView = (TextView) findViewById(R.id.textViewDistanceResume);
        avgMeterMinView = (TextView) findViewById(R.id.textAvgMeterMinResume);

        // Set TextViews
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
            case R.id.menu_start:
                startActivity(intentInfos);
                return true;
            case R.id.menu_training:
                startActivity(intentNewSeance);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
