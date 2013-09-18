package grouplogic.citywatch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

public class Report extends Activity {

    private ProgressDialog pDialog;

    JSONParser jParser = new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.report, menu);
        return true;
    }*/

}
