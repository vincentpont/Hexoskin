package hexoskin.app.testApi;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import com.appspot.helloendpoints.helloworld.Helloworld;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;


import javax.annotation.Nullable;


/**
 * Created by Vincent on 02.06.2014.
 */
public class AppConstants {
    /**
     * Class instance of the JSON factory.
     */
    public static final JsonFactory JSON_FACTORY = new AndroidJsonFactory();

    /**
     * Class instance of the HTTP transport.
     */
    public static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();


    /**
     * Retrieve a Helloworld api service handle to access the API.
     */
    public static Helloworld getApiServiceHandle() {
        // Use a builder to help formulate the API request.
        Helloworld.Builder helloWorld = new Helloworld.Builder(AppConstants.HTTP_TRANSPORT,
                AppConstants.JSON_FACTORY,null);

        return helloWorld.build();
    }

}
