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

import hexoskin.app.info.InfosUserActivity;
import hexoskin.app.maps.MapsActivity;

public class ResumeSeanceActivity extends Activity {


    private String durationExtras;
    private TextView durationView;
    private TextView caloriesView;
    private Intent intentInfos;
    private Intent intentMaps;
    private Intent intentNewSeance;
    private String calorieBurnedExtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_seance);

        intentInfos = new Intent(this, InfosUserActivity.class);
        intentMaps = new Intent(this, MapsActivity.class);
        intentNewSeance = new Intent(this, NewSeanceActivity.class);

        Bundle extras = getIntent().getExtras();
        durationExtras = extras.getString("Duration");
        calorieBurnedExtras = extras.getString("CaloriesBurned");

        durationView = (TextView) findViewById(R.id.textViewDurationResume);
        caloriesView = (TextView) findViewById(R.id.textViewCaloriesBurnedResume);

        if(calorieBurnedExtras != null) {
            caloriesView.setText(calorieBurnedExtras + " calories");
        }
        durationView.setText(durationExtras);

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
