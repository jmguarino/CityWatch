package grouplogic.citywatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class Report extends Activity {
    TextView deptInfo;

    private ProgressDialog pDialog;

    JSONParser jParser = new JSONParser();

    private static final String url_get_agency =
            "http://jgnetworks.net/citywatch/php/db_read.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_AGENCIES = "agency";
    private static final String TAG_PID = "pid";
    private static final String TAG_DEPT = "Dept";
    private static final String TAG_TN = "TN";
    private static final String TAG_EMAIL = "Email";
    private static final String TAG_ISSUES = "Issues";
    private static final String TAG_LOCATION = "Location";

    List<Map<String, String>> issueList = new ArrayList<Map<String, String>>();

    private Spinner setLoc;

    int issueNum = 0;
    String pid = "1";
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        //Remove this at some point!
        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initList();
        addItemsOnSpinner();
        ListView lv = (ListView) findViewById(R.id.issueView);

        SimpleAdapter simpleAdpt = new SimpleAdapter(this, issueList,
                R.layout.listview_item, new String[]{"issue"},
                new int[] {R.id.issue});

        lv.setAdapter(simpleAdpt);

        // Loading agencies in Background Thread
        new GetAgencyDetails().execute();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentAdapter, View view,
                    int position, long id) {
                LinearLayout ll = (LinearLayout) view;
                TextView clickedView = (TextView) ll.findViewById(R.id.issue);
                issueNum=position;
                pid=Integer.toString(issueNum);
                new GetAgencyDetails().execute();
                /*Toast.makeText(Report.this, "Item with id ["+id+"] - Position [" +
                        position+"] - Issue ["
                        +clickedView.getText()+"]", Toast.LENGTH_SHORT).show();*/
            }
        });
    }

    public void addItemsOnSpinner() {
        setLoc = (Spinner) findViewById(R.id.setLoc);
        List<String> list = new ArrayList<String>();
        list.add("Santa Rosa");
        list.add("Cotati");
        list.add("Rohnert Park");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setLoc.setAdapter(dataAdapter);
    }



    private void initList() {
        issueList.add(createIssue("issue", "Pothole"));
        issueList.add(createIssue("issue", "Street Light Out"));
        issueList.add(createIssue("issue", "Traffic Light Out"));
    }

    private HashMap<String, String> createIssue(String key, String name) {
        HashMap<String, String> issue = new HashMap<String, String>();
        issue.put(key, name);

        return issue;
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.report, menu);
        return true;
    }*/

    /**
     * Background Async Task to Get complete agency details
     **/

    class GetAgencyDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         **/
        //    @Override
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Report.this);
            pDialog.setMessage("Loading agency details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Getting agency details in background thread
         * */
        @Override
        protected String doInBackground(String... params) {

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Check for success tag
                    int success;
                    try {
                        // Building Parameters
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        //Something is broken here params.add(new BasicNameValuePair("pid", pid));

                        // getting agency details by making HTTP request
                        // Note that agency details url will use GET request
                        JSONObject json = jParser.makeHttpRequest(
                                url_get_agency, "GET", params);

                        // check your log for json response
                        Log.d("Single agency Details", json.toString());

                        // json success tag
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // successfully received agency details
                            JSONArray agencyObj = json
                                    .getJSONArray(TAG_AGENCIES); // JSON Array

                            // get agency object from JSON Array
                            JSONObject agency = agencyObj.getJSONObject(0);

                            // agency with this id found
                            // Edit Text
                            deptInfo = (TextView) findViewById(R.id.deptInfo);
                            // display agency data in TextView
                            deptInfo.setText(agency.getString(TAG_DEPT));

                        }else{
                            // agency with id not found
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            pDialog.dismiss();
        }
    }

}

