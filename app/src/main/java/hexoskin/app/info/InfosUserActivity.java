package hexoskin.app.info;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.hexoskin.app.R;

import java.util.ArrayList;
import java.util.List;

import hexoskin.app.maps.MapsActivity;
import hexoskin.app.seance.NewSeanceActivity;


public class InfosUserActivity extends Activity {

    private Button buttonSaveInfo;
    private Spinner spinnerPoids;
    private Spinner spinnerAge;
    private Spinner spinnerSexe;
    private Intent intentInfos;
    private Intent intentNewSeance;
    private Intent intentMaps;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos_user);

        buttonSaveInfo = (Button) findViewById(R.id.buttonSaveInfo);
        spinnerPoids = (Spinner) findViewById(R.id.spinnerPoids);
        spinnerAge = (Spinner) findViewById(R.id.spinnerAge);
        spinnerSexe = (Spinner) findViewById(R.id.spinnerSexe);

        intentInfos = new Intent(this, InfosUserActivity.class);
        intentNewSeance = new Intent(this, NewSeanceActivity.class);
        intentMaps = new Intent (this, MapsActivity.class);

        // Add values to the spinnerPoids
        List<String> listPoids = new ArrayList<String>();
        for(int i = 30 ; i < 151 ; i++){
            listPoids.add(Integer.toString(i)+" kgs");
        }
        ArrayAdapter<String> dataAdapterPoids = new ArrayAdapter<String>(this,
        android.R.layout.simple_spinner_item, listPoids);
        dataAdapterPoids.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPoids.setAdapter(dataAdapterPoids);

        // Add values to the spinnerAge
        List<String> listAge = new ArrayList<String>();
        for(int i = 12 ; i < 91 ; i++){
            listAge.add(Integer.toString(i)+" ans");
        }
        ArrayAdapter<String> dataAdapterAge = new ArrayAdapter<String>(this,
        android.R.layout.simple_spinner_item, listAge);
        dataAdapterAge.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAge.setAdapter(dataAdapterAge);


        // Listener saveInfo
        buttonSaveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentNewSeance.putExtra("Sexe", spinnerSexe.getSelectedItem().toString());
                intentNewSeance.putExtra("Age", spinnerAge.getSelectedItem().toString());
                intentNewSeance.putExtra("Poids", spinnerPoids.getSelectedItem().toString());
                startActivity(intentNewSeance);
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
