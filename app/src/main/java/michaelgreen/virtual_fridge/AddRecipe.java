package michaelgreen.virtual_fridge;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AddRecipe extends AppCompatActivity {

    private ListView lv;
    private ArrayList<Ingredient> itemList = new ArrayList<Ingredient>();
    //private HashMap<String, Integer> recipeItems = new HashMap<String, Integer>();
    private int noOfItems;
    private Recipe recipe = new Recipe();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        recipe.setItems(new ArrayList<Ingredient>());
        recipe.setAmounts(new ArrayList<Integer>());

        new GetAllIngredients().execute();
    }

    /* Called when a button is pressed - decides what to do */
    public void buttonListener(View view) {
        switch(view.getId()) {
            case R.id.add_recipe_button:
                tryAddRecipe();
                break;
        }
    }

    public void initialiseList() {
        lv = (ListView) findViewById(android.R.id.list);
        IngredientListAdapter adapter = new IngredientListAdapter(this, itemList);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(AddRecipe.this);
                final EditText amount = new EditText(AddRecipe.this);
                final Ingredient item = itemList.get(position);
                amount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                adb.setTitle("Enter Amount");
                adb.setView(amount);
                adb.setPositiveButton("Add To Recipe", new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        recipe.getItems().add(item);
                        recipe.getAmounts().add(Integer.valueOf(amount.getText().toString()));
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
        });
    }

    public void tryAddRecipe() {
        EditText recipeName = (EditText) findViewById(R.id.recipe_name);
        recipe.setName(recipeName.getText().toString());
        EditText recipeMethod = (EditText) findViewById(R.id.enter_method);
        recipe.setMethod(recipeMethod.getText().toString());
        new TryAddRecipe().execute(recipe);
    }

    /*
     * Background asyncronous task to retrive all fridge items.
     */
    class TryAddRecipe extends AsyncTask<Recipe, String, String> {

        private ProgressDialog pDialog;
        private AlertDialog alertDialog;

        JSONParser jsonParser = new JSONParser();

        // URL for PHP script to add an item
        private final String URL_TRY_ADD_RECIPE = "http://82.35.221.203/virtual-fridge/try_add_recipe.php";

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
            pDialog = new ProgressDialog(AddRecipe.this);
            pDialog.setMessage("Working...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        /* Assemble and send an HTTP request to try_add_item.php to add an item */
        protected String doInBackground(Recipe... args) {
            Recipe recipe = args[0];

            //Log.d("stuff", name + " " + units + " " + username + " " + amount + " " + expiryDate);

            int noOfItems = recipe.getItems().size();

            // Build HTTP request parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("recipe_name", recipe.getName()));
            params.add(new BasicNameValuePair("method", recipe.getMethod()));
            params.add(new BasicNameValuePair("no_of_items", String.valueOf(noOfItems)));
            for(int i = 0; i < noOfItems; i++) {
                params.add(new BasicNameValuePair("item" + i, recipe.getItems().get(i).getName()));
                params.add(new BasicNameValuePair("unit" + i, recipe.getItems().get(i).getUnit()));
                params.add(new BasicNameValuePair("amount" + i, recipe.getAmounts().get(i).toString()));
            }

            // Get JSON Object
            JSONObject json = jsonParser.makeHttpRequest(URL_TRY_ADD_RECIPE,
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

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddRecipe.this);
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

    /*
     * Background asyncronous task to retrive all fridge items.
     */
    class GetAllIngredients extends AsyncTask<String, String, String> {

        private ProgressDialog pDialog;
        private AlertDialog alertDialog;

        JSONParser jsonParser = new JSONParser();

        // URL for PHP script to add an item
        private final String URL_GET_ALL_INGREDIENTS = "http://82.35.221.203/virtual-fridge/get_all_ingredients.php";

        // JSON Node names
        private final String TAG_SUCCESS = "success";
        private final String TAG_MESSAGE = "message";
        private final String TAG_NO_OF_INGREDIENTS = "no_of_ingredients";

        // Stores JSON response
        private int success;
        private String message;

        /* Brings up a pop-up dialog saying 'Retrieving Items...' for the duration of the task */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddRecipe.this);
            pDialog.setMessage("Retrieving Items...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        /* Assemble and send an HTTP request to try_add_item.php to add an item */
        protected String doInBackground(String... args) {
            // Build HTTP request parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            // Get JSON Object
            JSONObject json = jsonParser.makeHttpRequest(URL_GET_ALL_INGREDIENTS,
                    "POST", params);

            Log.d("Create Response", json.toString());

            try {
                //Retrieve the JSON result
                success = json.getInt(TAG_SUCCESS);
                message = json.getString(TAG_MESSAGE);
                noOfItems = json.getInt(TAG_NO_OF_INGREDIENTS);
                itemList.clear();
                for (int i = 0; i < noOfItems; i++) {
                    String name = json.getString("name" + i);
                    String unit = json.getString("unit" + i);
                    Ingredient item = new Ingredient(name, unit);
                    itemList.add(item);
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

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddRecipe.this);
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
                            initialiseList();
                            dialog.cancel();
                        }
                    });
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }
}
