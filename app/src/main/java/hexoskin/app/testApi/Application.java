package hexoskin.app.testApi;



import com.appspot.helloendpoints.helloworld.model.HelloGreeting;

import com.google.api.client.util.Lists;
import java.util.ArrayList;

/**
 * Created by Vincent on 27.05.2014.
 */
public class Application extends android.app.Application  {

    ArrayList<HelloGreeting> greetings = Lists.newArrayList();


}
