package hexoskin.app.testApi;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.appspot.helloendpoints.helloworld.model.HelloGreeting;
import com.example.hexoskin.app.R;
import com.google.common.base.Strings;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class TestApiActivity extends ActionBarActivity {
    private static final String LOG_TAG = "MainActivity";
    private GreetingsDataAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_api);

        // Prevent the keyboard from being visible upon startup.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ListView listView = (ListView) findViewById(R.id.greetings_list_view);
        mListAdapter = new GreetingsDataAdapter((Application) getApplication());
        listView.setAdapter(mListAdapter);
    }

    /**
     * Simple use of an ArrayAdapter but we're using a static class to ensure no references to the
     * Activity exists.
     */
    static class GreetingsDataAdapter extends ArrayAdapter {
        GreetingsDataAdapter(Application application) {
            super(application.getApplicationContext(), android.R.layout.simple_list_item_1,
                    application.greetings);
        }

        void replaceData(HelloGreeting[] greetings) {
            clear();
            for (HelloGreeting greeting : greetings) {
                add(greeting);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);

            HelloGreeting greeting = (HelloGreeting)this.getItem(position);

            StringBuilder sb = new StringBuilder();

            Set<String> fields = greeting.keySet();
            boolean firstLoop = true;
            for (String fieldName : fields) {
                // Append next line chars to 2.. loop runs.
                if (firstLoop) {
                    firstLoop = false;
                } else {
                    sb.append("\n");
                }

                sb.append(fieldName)
                        .append(": ")
                        .append(greeting.get(fieldName));
            }

            view.setText(sb.toString());
            return view;
        }
    }

}
