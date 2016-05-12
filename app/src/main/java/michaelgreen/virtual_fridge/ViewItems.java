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
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewItems extends AppCompatActivity {

    private List<FridgeItem> fridgeList = new ArrayList<FridgeItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_items);

        //Populate items from current fridge
        new GetAllFridgeItems().execute();
    }


    /* Called when a button is pressed - decides what to do */
    public void buttonListener(View view) {
        Intent intent;
        switch(view.getId()) {
            case R.id.add_another:
                tryAddAnother(view);
                break;
            case R.id.sort_by:
                break;
            case R.id.remove_item:
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewItems.this);
                final String[] optionArray = new String[4];
                builder.setTitle("Select Number")
                break;
        }

    }

    public void tryAddAnother(View view) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        final EditText answer = new EditText(this);
        answer.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        adb.setTitle("Add another");
        adb.setMessage("Please enter the amount you wish to add...");
        adb.setView(answer);
        adb.setPositiveButton("Add", new Dialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String userAnswer = answer.getText().toString();
                dialog.cancel();
            }
        });
        adb.show();
        new TryCreateAccount().execute(itemName, unit, amount, expiryDate);
    }


    /*
     * Background asyncronous task to add a new ingredient.
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

        // Stores JSON response
        private int success;
        private String message;

        /* Brings up a pop-up dialog saying 'Adding Item...' for the duration of the task */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewItems.this);
            pDialog.setMessage("Adding Item...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        /* Assemble and send an HTTP request to try_add_item.php to add an item */
        protected String doInBackground(String... args) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            String itemName = args[0];
            String units = args[1];
            String amount = args[2];
            String expiryDate = args[3];
            String username = sp.getString("username", "NULL");

            // Build HTTP request parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("item_name", itemName));
            params.add(new BasicNameValuePair("unit", units));
            params.add(new BasicNameValuePair("amount", amount));
            params.add(new BasicNameValuePair("expiry_date", expiryDate));
            params.add(new BasicNameValuePair("username", username));

            // Get JSON Object
            JSONObject json = jsonParser.makeHttpRequest(URL_TRY_ADD_ITEM,
                    "POST", params);

            Log.d("Create Response", json.toString());

            try {
                //Retrieve the JSON result
                success = json.getInt(TAG_SUCCESS);
                message = json.getString(TAG_MESSAGE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /* Inform the user in an alert dialog whether the async task was successful */
        protected void onPostExecute(String file_url) {
            // Dismiss the process dialog
            pDialog.dismiss();

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewItems.this);
            if(success == 1) {
                alertDialogBuilder.setTitle("Success");
            }
            else {
                alertDialogBuilder.setTitle("Fail");
            }
            alertDialogBuilder
                    .setMessage(message)
                    .setCancelable(false)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

    }
}
