package hexoskin.app.info;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.hexoskin.app.R;
import com.google.android.gms.plus.PlusClient;

import java.util.ArrayList;
import java.util.List;

import hexoskin.app.login.LoginActivity;
import hexoskin.app.login.PlusBaseActivity;
import hexoskin.app.seance.NewSeanceActivity;

/**
 * Created by Vincent Pont
 * Last Modification 17.06.2014
 *
 */

public class InfosUserActivity extends Activity {

    private ImageButton buttonSaveInfo;
    private Spinner spinnerPoids;
    private Spinner spinnerAge;
    private Spinner spinnerSexe;
    private Intent intentNewSeance;
    private PlusClient mplusClient;
    private Intent intentLogin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos_user);

        buttonSaveInfo = (ImageButton) findViewById(R.id.buttonSaveInfo);
        spinnerPoids = (Spinner) findViewById(R.id.spinnerPoids);
        spinnerAge = (Spinner) findViewById(R.id.spinnerAge);
        spinnerSexe = (Spinner) findViewById(R.id.spinnerSexe);
        PlusBaseActivity.ClassIntern ca = new PlusBaseActivity.ClassIntern();


        intentLogin = new Intent(this, LoginActivity.class);
        intentNewSeance = new Intent(this, NewSeanceActivity.class);

        // Add values and layout to Spinners
        addValuesToSpinner();

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

    /**
     * Add values into the 3 spinners. (Sexe, Age, Poids)
     */
    public void addValuesToSpinner(){

        List<String> listSexe = new ArrayList<String>();
        listSexe.add("Homme");
        listSexe.add("Femme");

        ArrayAdapter<String> dataAdapterSexe = new ArrayAdapter<String>(this,
                R.layout.spinner_layout, listSexe);
        dataAdapterSexe.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSexe.setAdapter(dataAdapterSexe);

        // Add values to the spinnerPoids
        List<String> listPoids = new ArrayList<String>();
        for(int i = 30 ; i < 151 ; i++){
            listPoids.add(Integer.toString(i)+" kgs");
        }
        ArrayAdapter<String> dataAdapterPoids = new ArrayAdapter<String>(this,
                R.layout.spinner_layout, listPoids);
        dataAdapterPoids.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPoids.setAdapter(dataAdapterPoids);


        // Add values to the spinnerAge
        List<String> listAge = new ArrayList<String>();
        for(int i = 12 ; i < 91 ; i++){
            listAge.add(Integer.toString(i)+" ans");
        }
        ArrayAdapter<String> dataAdapterAge = new ArrayAdapter<String>(this,
                R.layout.spinner_layout, listAge);
        dataAdapterAge.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAge.setAdapter(dataAdapterAge);
    }



}
