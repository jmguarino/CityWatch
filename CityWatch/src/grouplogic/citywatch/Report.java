package grouplogic.citywatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Report extends ListActivity{
    TextView deptInfo;

    private ProgressDialog pDialog;

    JSONParser jParser = new JSONParser();

    private static String url_sr_agencies  =
            "http://jgnetworks.net/citywatch/php/db_read_all_sr.php";
    private static String url_rp_agencies =
            "http://jgnetworks.net/citywatch/php/db_read_all_rp.php";
    private static String url_cot_agencies =
            "http://jgnetworks.net/citywatch/php/db_read_all_cot.php";
    private static String url_all_agencies = url_sr_agencies;

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

    JSONArray agencies = null;
    ArrayList<HashMap<String, String>> agenciesList;

    private Spinner setLoc;

    int issueNum = 0;
    String pid = "1";
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        initList();
        addItemsOnSpinner();
        ListView lv = (ListView) findViewById(R.id.issueView);

        SimpleAdapter simpleAdpt = new SimpleAdapter(this, issueList,
                R.layout.listview_item, new String[]{"issue"},
                new int[] {R.id.issue});

        lv.setAdapter(simpleAdpt);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentAdapter, View view,
                    int position, long id) {
                //LinearLayout ll = (LinearLayout) view;
                //TextView clickedView = (TextView) ll.findViewById(R.id.issue);
                issueNum=position;
                pid=Integer.toString(issueNum);
                new LoadAllAgencies().execute();
            }
        });

        setLoc.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int pos, long id) {
                if(pos==0){
                    url_all_agencies = url_sr_agencies;
                }
                if(pos==1){
                    url_all_agencies = url_cot_agencies;
                }
                if(pos==2){
                    url_all_agencies = url_rp_agencies;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }});

    }

    public void callNum(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + agenciesList.get(0).get(TAG_TN)));
        startActivity(intent);
    }

    public void editEmail(View view) {
        Intent intent = new Intent(this, EmailEditor.class);
        intent.putExtra("emailAddress", agenciesList.get(0).get(TAG_EMAIL));
        intent.putExtra("sendTo", agenciesList.get(0).get(TAG_DEPT));
        intent.putExtra("issue",  agenciesList.get(0).get(TAG_ISSUES));
        intent.putExtra("typeOfReport",  "report");
        
        startActivity(intent);
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
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllAgencies extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Report.this);
            pDialog.setMessage("Loading agencies. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All agencies from url
         * */
        @Override
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_agencies, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Agencies: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // agencies found
                    // Getting Array of Agencies
                    agencies = json.getJSONArray(TAG_AGENCIES);

                    // get
                    JSONObject c = agencies.getJSONObject(issueNum);

                    // Storing each json item in variable
                    String id = c.getString(TAG_PID);
                    String dept = c.getString(TAG_DEPT);
                    String TN = c.getString(TAG_TN);
                    String Email = c.getString(TAG_EMAIL);
                    String Issues = c.getString(TAG_ISSUES);
                    String Location = c.getString(TAG_LOCATION);

                    // creating new HashMap
                    HashMap<String, String> map = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    map.put(TAG_PID, id);
                    map.put(TAG_DEPT, dept);
                    map.put(TAG_TN, TN);
                    map.put(TAG_EMAIL, Email);
                    map.put(TAG_ISSUES, Issues);
                    map.put(TAG_LOCATION, Location);

                    // adding HashList to ArrayList
                    agenciesList = new ArrayList<HashMap<String, String>>();
                    agenciesList.add(map);
                    // }
                } else {
                    // no agencies found
                    // Launch Add New product Activity
                    Toast.makeText(getApplicationContext(),
                            "No Agencies found.", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all agencies
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            Report.this, agenciesList,
                            R.layout.list_item, new String[] { TAG_PID,
                                    TAG_DEPT, TAG_TN, TAG_EMAIL},
                                    new int[] { R.id.pid, R.id.dept, R.id.TN,
                                    R.id.Email});
                    // updating listview
                    setListAdapter(adapter);
                    Button EmailButton = (Button) findViewById(R.id.email);
                    Button CallButton = (Button) findViewById(R.id.call);
                    EmailButton.setVisibility(View.VISIBLE);
                    CallButton.setVisibility(View.VISIBLE);
                }
            });

        }

    }

}

