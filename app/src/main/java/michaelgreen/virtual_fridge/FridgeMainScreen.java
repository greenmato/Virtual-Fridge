package michaelgreen.virtual_fridge;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FridgeMainScreen extends AppCompatActivity {

    /* Called when a button is pressed - decides what to do */
    public void buttonListener(View view) {
        Intent intent = new Intent();
        switch(view.getId()) {
            case R.id.use_item:
                intent = new Intent(this, Suggestions.class);
                break;
            case R.id.add_to_fridge:
                intent = new Intent(this, AddItem.class);
                break;
//            case R.id.options:
//                intent = new Intent(this, JoinHunt.class);
//                break;
            case R.id.view_items:
                intent = new Intent(this, ViewItems.class);
                break;
        }
        startActivity(intent);
    }

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fridge_main_screen);
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            final EditText answer = new EditText(this);
            final FridgeItem item = fridgeList.get(position);
            answer.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
            adb.setTitle("Add Another");
            adb.setMessage("Please enter the amount you wish to add...");
            adb.setView(answer);
            adb.setPositiveButton("Add", new Dialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String amount = answer.getText().toString();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    new AddAnother().execute(item.getName(), item.getUnit(), amount, sdf.format(item.getExpiryDate()));
                    dialog.cancel();
                }
            });
            adb.setNegativeButton("Cancel", new Dialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            adb.show();
    }


    /*
     * Background asyncronous task to retrive all fridge items.
     */
    class GetAllFridgeItems extends AsyncTask<String, String, String> {

        private ProgressDialog pDialog;
        private AlertDialog alertDialog;

        JSONParser jsonParser = new JSONParser();

        // URL for PHP script to add an item
        private final String URL_GET_ALL_FRIDGE_ITEMS = "http://82.35.221.203/virtual-fridge/get_all_fridge_items.php";

        // JSON Node names
        private final String TAG_SUCCESS = "success";
        private final String TAG_MESSAGE = "message";
        private final String TAG_NO_OF_FRIDGE_ITEMS = "no_of_fridge_items";

        // Stores JSON response
        private int success;
        private String message;

        /* Brings up a pop-up dialog saying 'Adding Item...' for the duration of the task */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FridgeMainScreen.this);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        /* Assemble and send an HTTP request to try_add_item.php to add an item */
        protected String doInBackground(String... args) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String username = sp.getString("username", null);
            Log.d("username", username);
            // Build HTTP request parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username));

            // Get JSON Object
            JSONObject json = jsonParser.makeHttpRequest(URL_GET_ALL_FRIDGE_ITEMS,
                    "POST", params);

            Log.d("Create Response", json.toString());

            try {
                //Retrieve the JSON result
                success = json.getInt(TAG_SUCCESS);
                message = json.getString(TAG_MESSAGE);
                noOfFridgeItems = json.getInt(TAG_NO_OF_FRIDGE_ITEMS);
                fridgeList.clear();
                for(int i = 0; i < noOfFridgeItems; i++) {
                    String name = json.getString("name" + i);
                    String unit = json.getString("unit" + i);
                    String expiryDateStr = json.getString("expiry_date" + i);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date expiryDate = null;
                    try {
                        expiryDate = sdf.parse(expiryDateStr);
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                    double amount = json.getDouble("amount" + i);
                    FridgeItem item = new FridgeItem(name, unit, username, amount, expiryDate);
                    fridgeList.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_fridge_main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_log_out:
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("isSignedIn", false);
                editor.commit();
                Intent i = new Intent(this, StartPageN.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
