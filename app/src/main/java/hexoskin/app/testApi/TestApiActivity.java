package hexoskin.app.testApi;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.helloendpoints.helloworld.Helloworld;
import com.appspot.helloendpoints.helloworld.Helloworld.Greetings.PutData;

import com.example.hexoskin.app.R;

import java.io.IOException;


public class TestApiActivity extends ActionBarActivity {


    private Button buttonSaveData ;
    private TextView idEmployeeView;
    private TextView firstnameView;
    private TextView lastnameView;
    private AsyncTask<Void, Void, PutData> putData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_api);


        idEmployeeView = (TextView) findViewById(R.id.textViewIDEmployee);
        firstnameView = (TextView) findViewById(R.id.textViewFirstname);
        lastnameView = (TextView) findViewById(R.id.textViewLastname);
        buttonSaveData = (Button) findViewById(R.id.buttonSaveData);

        putData = new AsyncTask<Void, Void, PutData > () {

                    @Override
                    protected PutData doInBackground(Void... voids) {

                        // Retrieve service handle.
                        Helloworld apiServiceHandle = AppConstants.getApiServiceHandle();

                        try {

                            PutData putDataInStore = apiServiceHandle.greetings()
                                    .putData(idEmployeeView.getText().toString(),
                                            firstnameView.getText().toString(),
                                            lastnameView.getText().toString());

                            putDataInStore.execute();

                        } catch (IOException e) {
                        }
                        return null;
                    }

        };

        // Listener saveInfo
        buttonSaveData.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                putData.execute();
                Toast.makeText(getApplicationContext(), "Data saved.", Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test_api, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
