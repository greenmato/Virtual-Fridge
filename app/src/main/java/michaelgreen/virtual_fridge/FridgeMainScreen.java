package michaelgreen.virtual_fridge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
