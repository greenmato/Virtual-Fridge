package michaelgreen.virtual_fridge;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Suggestions extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);
    }
}
