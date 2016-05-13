package michaelgreen.virtual_fridge;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ViewItems extends ListActivity {

    private ListView lv;
    private ArrayList<FridgeItem> fridgeList = new ArrayList<FridgeItem>();
    private int noOfFridgeItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_items);

        //Populate items from current fridge
        new GetAllFridgeItems().execute();
    }


    /* Called when a button is pressed - decides what to do */
    public void buttonListener(View view) {
        switch(view.getId()) {
//            case R.id.add_another:
//                tryAddAnother();
//                break;
            case R.id.sort_by:
                break;
//            case R.id.remove_item:
//                AlertDialog.Builder builder = new AlertDialog.Builder(ViewItems.this);
//                final String[] optionArray = new String[4];
//                builder.setTitle("Select Number");
//                break;
        }
    }

    public void tryAddAnother(int position) {
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

    public void tryRemoveSome(int position) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        final EditText answer = new EditText(this);
        final FridgeItem item = fridgeList.get(position);
        answer.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        adb.setTitle("Remove Some");
        adb.setMessage("Please enter the amount you wish to remove...");
        adb.setView(answer);
        adb.setPositiveButton("Remove", new Dialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String amount = "-" + answer.getText().toString();
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

    public void initialiseList() {
        lv = (ListView) findViewById(android.R.id.list);
        FridgeItemListAdapter fridgeItemListAdapter = new FridgeItemListAdapter(this, fridgeList);
        lv.setAdapter(fridgeItemListAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(ViewItems.this);
                final int index = position;
                adb.setTitle("Select Action");
                adb.setPositiveButton("Add More", new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        tryAddAnother(index);
                        dialog.cancel();
                    }
                });
                adb.setNegativeButton("Remove Some", new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        tryRemoveSome(index);
                        dialog.cancel();
                    }
                });
                adb.show();
            }
        });
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
            pDialog = new ProgressDialog(ViewItems.this);
            pDialog.setMessage("Retrieving Items...");
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
                            if(success == 1) {
                                initialiseList();
                            }
                            dialog.cancel();
                        }
                    });
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

    }

    /*
     * Background asyncronous task to retrive all fridge items.
     */
    class AddAnother extends AsyncTask<String, String, String> {

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

        /* Brings up a pop-up dialog saying 'Adding Another...' for the duration of the task */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewItems.this);
            pDialog.setMessage("Working...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        /* Assemble and send an HTTP request to try_add_item.php to add an item */
        protected String doInBackground(String... args) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String username = sp.getString("username", null);
            String name = args[0];
            String units = args[1];
            String amount = args[2];
            String expiryDate = args[3];

            Log.d("stuff", name + " " + units + " " + username + " " + amount + " " + expiryDate);

            // Build HTTP request parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("item_name", name));
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
                            if(success == 1) {
                                new GetAllFridgeItems().execute();
                            }
                            dialog.cancel();
                        }
                    });
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

    }
}
