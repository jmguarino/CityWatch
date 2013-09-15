package grouplogic.citywatch;

import grouplogic.citywatch.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    private Button report;
    private Button settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        report = (Button) findViewById(R.id.report);
        settings = (Button) findViewById(R.id.settings);

        //Loads the report activity
        report.setOnClickListener(new View.OnClickListener() {
            Intent myIntent = new Intent(MainActivity.this, Report.class);
            @Override
            public void onClick(View arg0) {
                MainActivity.this.startActivity(myIntent);
            }
        });

        //Loads settings activity
        settings.setOnClickListener(new View.OnClickListener() {
            Intent myIntent = new Intent(MainActivity.this, Settings.class);
            @Override
            public void onClick(View arg0) {
                MainActivity.this.startActivity(myIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
