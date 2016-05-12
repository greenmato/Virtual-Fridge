package michaelgreen.virtual_fridge;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Looper;
import android.preference.PreferenceManager;
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


public class StartPageN extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page_n);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isSignedIn = sp.getBoolean("isSignedIn", false);

        if(isSignedIn) {
            Intent intent = new Intent(this, FridgeMainScreen.class);
            startActivity(intent);
            finish();
        }
    }

    /* Called when a button is pressed - decides what to do */
    public void buttonListener(View view) {
        Intent intent;
        switch(view.getId()) {
            case R.id.sign_in:
                trySignIn(view);
                break;
            case R.id.create_account:
                intent = new Intent(this, CreateAccount.class);
                startActivity(intent);
                break;
        }
    }

    /* Creates an instance of the asynchronous task TrySignIn, passing the email and password to it */
    public void trySignIn(View view) {
        String username = ((EditText) findViewById(R.id.username)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();
        new TrySignIn().execute(username, password);
    }

    /*
     * Background task to sign in using the server
     */
    class TrySignIn extends AsyncTask<String, String, String> {

        private int success;
        private String message;

        private ProgressDialog pDialog;
        private AlertDialog alertDialog;

        JSONParser jsonParser = new JSONParser();

        // URL to the sign in PHP script
        private final String URL_TRY_SIGN_IN = "http://82.35.221.203/virtual-fridge/try_sign_in.php";

        // JSON Node names
        private final String TAG_SUCCESS = "success";
        private final String TAG_USERNAME = "username";
        private final String TAG_MESSAGE = "message";

        /*
         * Brings up a pop-up dialog saying 'Signing In...' for the duration of the task
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(StartPageN.this);
            pDialog.setMessage("Signing In..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /*
         * Attempts signing in by checking credentials
         */
        protected String doInBackground(String... args) {
            String username = args[0];
            String password = args[1];

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));

            // Getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(URL_TRY_SIGN_IN,
                    "POST", params);

            // Check log cat for response
            Log.d("Create Response", json.toString());

            // Check for success tag
            try {
                success = json.getInt(TAG_SUCCESS);
                message = json.getString(TAG_MESSAGE);

                if (success == 1) {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("isSignedIn", true);
                    editor.putString("username", username);
                    editor.commit();

                    Intent i = new Intent(getApplicationContext(), FridgeMainScreen.class);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /* Inform the user in an alert dialog whether Creating an account was successful */
        protected void onPostExecute(String file_url) {
            // Dismiss the process dialog
            pDialog.dismiss();
            if(success == 0) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StartPageN.this);
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start_page_n, menu);
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
