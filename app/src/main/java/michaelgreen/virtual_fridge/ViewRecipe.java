package michaelgreen.virtual_fridge;

import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ViewRecipe extends AppCompatActivity {

    Recipe recipe;
    TextView title;
    TextView ingredientList;
    TextView method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        recipe = getIntent().getParcelableExtra("recipe");
        Log.d("recipe", recipe.getName());
        title = (TextView) findViewById(R.id.recipe_name);
        title.setText(recipe.getName());
        ingredientList = (TextView) findViewById(R.id.ingredient_list);
        String ingredientListStr = "Ingredients: \n";
        for(int i = 0; i < recipe.getItems().size(); i++) {
            ingredientListStr = ingredientListStr + recipe.getItems().get(i) + "\n";
        }
        ingredientList.setText(ingredientListStr);
        method = (TextView) findViewById(R.id.method);
        method.setText(recipe.getMethod());
    }
}
