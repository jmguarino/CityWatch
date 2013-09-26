package grouplogic.citywatch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void openReport(View view) {
        Intent intent = new Intent(this, Report.class);
        startActivity(intent);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public void openKudos(View view) {
        Intent intent = new Intent(this, Kudos.class);
        startActivity(intent);
    }

    public void openVolunteer(View view) {
        Intent intent = new Intent(this, Volunteer.class);
        startActivity(intent);
    }

    public void openAllAgencies(View view) {
        Intent intent = new Intent(this, ListAllAgencies.class);
        startActivity(intent);
    }
    
    public void openLocation(View view) {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }

}
