package hexoskin.app.seance;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.hexoskin.app.R;
import hexoskin.app.info.InfosUserActivity;
import hexoskin.app.maps.MapsActivity;


/**
 * Created by Vincent Pont
 * Last Modification 21.07.2014
 */

public class NewSeanceActivity extends Activity {

    private ImageButton buttonEnableGPS;
    private Button buttonStart;
    private Intent intentInfos;
    private Intent intentMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newseance);

        intentInfos = new Intent(this, InfosUserActivity.class);
        intentMaps = new Intent(this, MapsActivity.class);

        buttonEnableGPS = (ImageButton) findViewById(R.id.buttonEnableGPS);
        buttonStart = (Button) findViewById(R.id.buttonStart);

        // Check if GPS is already enable if not we ask the user to turn it on
        if (testGPSEnable() == false) {
            Toast.makeText(getApplicationContext(), "GPS OFF, Svp activez le GPS.", Toast.LENGTH_LONG).show();
            Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(i);
        }

        // Listener enable GPS button
        buttonEnableGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        });

        // Listener start button
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Only if GPS is enable
                if (testGPSEnable() == true) {
                    Toast.makeText(getApplicationContext(), "Nouvel entraînement...", Toast.LENGTH_SHORT).show();

                    // Get extra from intentInfo
                    Bundle extras = getIntent().getExtras();
                    String sexe = extras.getString("Sexe");
                    String age = extras.getString("Age").substring(0, 2);
                    String poid = extras.getString("Poids").substring(0, 2);

                    intentMaps.putExtra("Sexe", sexe);
                    intentMaps.putExtra("Age", age);
                    intentMaps.putExtra("Poids", poid);
                    startActivity(intentMaps);
                } else {
                    Toast.makeText(getApplicationContext(), "Le GPS n'est pas activé!", Toast.LENGTH_SHORT).show();
                }
            }
        });

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_generalsanscarte, menu);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
