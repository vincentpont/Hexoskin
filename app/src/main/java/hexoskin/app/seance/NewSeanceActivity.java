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


public class NewSeanceActivity extends Activity {

    private ImageButton buttonEnableGPS ;
    private ImageButton buttonStart ;
    private Boolean isGPSEnable;
    private Intent intentInfos;
    private Intent intentMaps;
    private Intent intentNewSeance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newseance);

        intentInfos = new Intent(this, InfosUserActivity.class);
        intentMaps = new Intent(this, MapsActivity.class);
        intentNewSeance = new Intent(this, NewSeanceActivity.class);

        buttonEnableGPS = (ImageButton) findViewById(R.id.buttonEnableGPS);
        buttonStart = (ImageButton) findViewById(R.id.buttonStart);

        // If GPS is enable we hide the button to ask user ot enable GPS
        if(testGPSEnable() == true){
            buttonEnableGPS.setEnabled(false);
        }
        else{
            // Ask enable gps
            Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(i);
            Toast.makeText(getApplicationContext(), "GPS OFF, Please turn on GPS.", Toast.LENGTH_LONG).show();
        }

        // Listener enable GPS
        buttonEnableGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Ask enable gps
                Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        });

        // Listener start
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Only if GPS is enable
                if(testGPSEnable() == true) {
                    Toast.makeText(getApplicationContext(), "New workout in progress...", Toast.LENGTH_SHORT).show();

                    // Get extra from intentInfo
                    Bundle extras = getIntent().getExtras();
                    String sexe = extras.getString("Sexe");
                    String age = extras.getString("Age").substring(0,2);
                    String poid = extras.getString("Poids").substring(0,2);

                    intentMaps.putExtra("Sexe", sexe);
                    intentMaps.putExtra("Age", age);
                    intentMaps.putExtra("Poids", poid);
                    startActivity(intentMaps);
                }
                else{
                    buttonEnableGPS.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "The GPS is not enable!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // Method that test is GPS is enable or not
    private Boolean testGPSEnable(){

        Boolean test ;
        LocationManager mlocManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        test = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return test;
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
