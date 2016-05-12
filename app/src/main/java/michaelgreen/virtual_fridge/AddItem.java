package michaelgreen.virtual_fridge;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddItem extends AppCompatActivity {

    //Used for displaying the expiry date chosen by the user
    //Must be global and static to communicate with the DatePicker fragment
    private static TextView dateChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
    }

    /* Called when a button is pressed - decides what to do */
    public void buttonListener(View view) {
        DatePickerFragment datePicker;
        switch(view.getId()) {
            case R.id.add_item_button:
                tryAddItem(view);
                break;
            case R.id.expiry_date_button:
                //Initialise the TextView that displays the chosen date
                dateChoice = (TextView) findViewById(R.id.expiry_date_text);
                //Initialise and call the DatePicker fragment
                datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "startDate");
                break;
        }

    }

    public void tryAddItem(View view) {
        String itemName = ((EditText) findViewById(R.id.enter_item_name)).getText().toString();
        String unit = ((EditText) findViewById(R.id.units)).getText().toString();
        String amount = ((EditText) findViewById(R.id.amount)).getText().toString();
        String expiryDate = ((TextView) findViewById(R.id.expiry_date_text)).getText().toString();
        new TryAddItem().execute(itemName, unit, amount, expiryDate);
    }


    /*
     * Background asyncronous task to add a new ingredient.
     */
    class TryAddItem extends AsyncTask<String, String, String> {

        private ProgressDialog pDialog;
        private AlertDialog alertDialog;

        JSONParser jsonParser = new JSONParser();

        // URL for PHP script to add an item
        private final String URL_TRY_ADD_ITEM = "http://82.35.221.203/virtual-fridge/try_add_item.php";

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
            pDialog = new ProgressDialog(AddItem.this);
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

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddItem.this);
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

    /* Displays a calender graphic that allows the user to press on their desired date, then press OK */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            //Convert the date into a readable (and SQL compatible) string, ensure leading zeros are added
            month = month + 1;
            String monthStr = Integer.toString(month);
            String dayStr = Integer.toString(day);
            if(month < 10) {
                monthStr = "0" + monthStr;
            }
            if(day < 10) {
                dayStr = "0" + dayStr;
            }
            String text = year + "-" + monthStr + "-" + dayStr;
            dateChoice.setText(text);
        }
    }

}
