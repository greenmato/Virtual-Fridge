package michaelgreen.virtual_fridge;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CreateAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }

    /* Get the user's input and start the asynchronous task TryCreateAccount with this information */
    public void tryCreateAccount(View view) {
        String username = ((EditText) findViewById(R.id.username)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();
        String usernameConfirm = ((EditText) findViewById(R.id.confirm_username)).getText().toString();
        String passwordConfirm = ((EditText) findViewById(R.id.confirm_password)).getText().toString();
        new TryCreateAccount().execute(username, password, usernameConfirm, passwordConfirm);
    }

    /*
     * Background asyncronous task to create a new account.
     */
    class TryCreateAccount extends AsyncTask<String, String, String> {

        private ProgressDialog pDialog;
        private AlertDialog alertDialog;

        JSONParser jsonParser = new JSONParser();

        // URL for PHP script to create an account
        private final String URL_TRY_CREATE_ACCOUNT = "http://82.35.221.203/virtual-fridge/try_create_account.php";

        // JSON Node names
        private final String TAG_SUCCESS = "success";
        private final String TAG_MESSAGE = "message";

        // Stores JSON response
        private int success;
        private String message;

        /* Brings up a pop-up dialog saying 'Creating Account...' for the duration of the task */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CreateAccount.this);
            pDialog.setMessage("Creating Account...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        /* Assemble and send an HTTP request to try_create_account.php to create an account */
        protected String doInBackground(String... args) {
            String username = args[0];
            String password = args[1];
            String confirmUsername = args[2];
            String confirmPassword = args[3];
            Log.d("details", username + " " + password + " " + confirmUsername + " " + confirmPassword);

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("confirm_username", confirmUsername));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("confirm_password", confirmPassword));

            // Get JSON Object
            JSONObject json = jsonParser.makeHttpRequest(URL_TRY_CREATE_ACCOUNT,
                    "POST", params);

            Log.d("Create Response", json.toString());

            try {
                success = json.getInt(TAG_SUCCESS);
                message = json.getString(TAG_MESSAGE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /* Inform the user in an alert dialog whether Creating an account was successful */
        protected void onPostExecute(String file_url) {
            // Dismiss the process dialog
            pDialog.dismiss();

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateAccount.this);
            if(success == 1) {
                alertDialogBuilder.setTitle("Sign In Success");
            }
            else {
                alertDialogBuilder.setTitle("Sign In Failed");
            }
            alertDialogBuilder
                    .setMessage(message)
                    .setCancelable(false)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if(success == 1) {
                                finish();
                            }
                            dialog.cancel();
                        }
                    });
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
