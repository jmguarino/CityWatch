package grouplogic.citywatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ListAllAgencies extends ListActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> agenciesList;

    // url to get all agencies list
    private static String url_all_agencies = "http://jgnetworks.net/citywatch/php/db_read_all.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_AGENCIES = "agency";
    private static final String TAG_PID = "pid";
    private static final String TAG_DEPT = "Dept";
    private static final String TAG_TN = "TN";
    private static final String TAG_EMAIL = "Email";
    private static final String TAG_ISSUES = "Issues";
    private static final String TAG_LOCATION = "Location";

    // agencies JSONArray
    JSONArray agencies = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_all_agencies);

        // Hashmap for ListView
        agenciesList = new ArrayList<HashMap<String, String>>();

        // Loading agencies in Background Thread
        new LoadAllAgencies().execute();

        // Get listview
        ListView lv = getListView();

        /*
        // on seleting single product
        // launching Edit Product Screen
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.pid)).getText()
                        .toString();

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        EditProductActivity.class);
                // sending pid to next activity
                in.putExtra(TAG_PID, pid);

                // starting new activity and expecting some response back
                startActivityForResult(in, 100);
            }
        });*/


    }
    /*
    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    } */

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
            pDialog = new ProgressDialog(ListAllAgencies.this);
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

                    // looping through All agencies
                    for (int i = 0; i < agencies.length(); i++) {
                        JSONObject c = agencies.getJSONObject(i);

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
                        agenciesList.add(map);
                    }
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
                            ListAllAgencies.this, agenciesList,
                            R.layout.list_item, new String[] { TAG_PID,
                                    TAG_DEPT, TAG_TN, TAG_EMAIL},
                                    new int[] { R.id.pid, R.id.dept, R.id.TN,
                                    R.id.Email});
                    // updating listview
                    setListAdapter(adapter);
                }
            });

        }

    }
}
