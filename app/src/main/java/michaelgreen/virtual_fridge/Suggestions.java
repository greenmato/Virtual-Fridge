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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Suggestions extends AppCompatActivity {

    private ListView lv;
    private ArrayList<Recipe> suggestionList = new ArrayList<Recipe>();
    private int noOfSuggestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);

        new GetSuggestions().execute();
    }

    /* Called when a button is pressed - decides what to do */
    public void buttonListener(View view) {
        Intent intent;
        switch(view.getId()) {
            case R.id.add_recipe:
                intent = new Intent(this, AddRecipe.class);
                startActivity(intent);
                break;
            case R.id.search_recipes:
//                intent = new Intent(this, CreateAccount.class);
//                startActivity(intent);
                break;
        }
    }

    public void initialiseList() {
        Log.d("al size", String.valueOf(suggestionList.size()));
        lv = (ListView) findViewById(android.R.id.list);
        RecipeListAdapter recipeListAdapter = new RecipeListAdapter(this, suggestionList);
        lv.setAdapter(recipeListAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Suggestions.this, ViewRecipe.class);
                intent.putExtra("recipe", suggestionList.get(position));
                startActivity(intent);
            }
        });
    }

    /*
     * Background asyncronous task to retrive suggestions.
     */
    class GetSuggestions extends AsyncTask<String, String, String> {

        private ProgressDialog pDialog;
        private AlertDialog alertDialog;

        JSONParser jsonParser = new JSONParser();

        // URL for PHP script to add an item
        private final String URL_GET_SUGGESTIONS = "http://82.35.221.203/virtual-fridge/get_suggestions.php";

        // JSON Node names
        private final String TAG_SUCCESS = "success";
        private final String TAG_MESSAGE = "message";
        private final String TAG_NAME = "name";
        private final String TAG_METHOD = "method";
        private final String TAG_AMOUNT = "amount";
        private final String TAG_INGREDIENT_NAME = "ingredient_name";
        private final String TAG_INGREDIENT_UNIT= "ingredient_unit";
        private final String TAG_NO_OF_SUGGESTIONS = "no_of_suggestions";
        private final String TAG_NO_OF_INGREDIENTS = "no_of_ingredients";
        private final String TAG_SCORE = "score";

        // Stores JSON response
        private int success;
        private String message;

        private int noOfIngredients;

        /* Brings up a pop-up dialog saying 'Adding Item...' for the duration of the task */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Suggestions.this);
            pDialog.setMessage("Retrieving Suggestions...");
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
            JSONObject json = jsonParser.makeHttpRequest(URL_GET_SUGGESTIONS,
                    "POST", params);

            Log.d("Create Response", json.toString());

            try {
                success = json.getInt(TAG_SUCCESS);
                message = json.getString(TAG_MESSAGE);
                noOfSuggestions = json.getInt(TAG_NO_OF_SUGGESTIONS);
                Log.d("noOfSuggestions", String.valueOf(noOfSuggestions));
                for(int x = 0; x < noOfSuggestions; x++) {
                    //Retrieve the JSON result
                    noOfIngredients = json.getInt(TAG_NO_OF_INGREDIENTS + x);
                    String name = json.getString(TAG_NAME + x);
                    String method = json.getString(TAG_METHOD + x);
                    ArrayList<Ingredient> items = new ArrayList<Ingredient>();
                    ArrayList<Double> amounts = new ArrayList<Double>();
                    for (int i = 0; i < noOfIngredients; i++) {
                        String ingredientName = json.getString(TAG_INGREDIENT_NAME + x + i);
                        String unitName = json.getString(TAG_INGREDIENT_UNIT + x + i);
                        double amount = json.getDouble(TAG_AMOUNT + x + i);
                        Ingredient ingredient = new Ingredient(ingredientName, unitName);
                        items.add(ingredient);
                        amounts.add(amount);
                    }
                    int score = json.getInt(TAG_SCORE + x);
                    Recipe recipe = new Recipe(name, items, amounts, method);
                    recipe.setScore(score);
                    suggestionList.add(recipe);
                }
                Collections.sort(suggestionList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /* Inform the user in an alert dialog whether the async task was successful */
        protected void onPostExecute(String file_url) {
            // Dismiss the process dialog
            pDialog.dismiss();

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Suggestions.this);
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


}
