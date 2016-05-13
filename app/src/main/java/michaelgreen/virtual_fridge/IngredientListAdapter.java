package michaelgreen.virtual_fridge;

import android.content.Context;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 *
 */
public class IngredientListAdapter extends ArrayAdapter<Ingredient> {

    Context c;
    ArrayList<Ingredient> items;
    LayoutInflater inflater;

    public IngredientListAdapter(Context context, ArrayList<Ingredient> items) {
        super(context, R.layout.ingredient_list_item, items);
        this.c = context;
        this.items = items;
    }

    public class ViewHolder
    {
        TextView name;
        TextView unit;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.ingredient_list_item, null);
        }
        ViewHolder holder = new ViewHolder();

        //Initialise views
        holder.name = (TextView) convertView.findViewById(R.id.first_line);
        holder.unit = (TextView) convertView.findViewById(R.id.second_line);

        Ingredient item = getItem(position);
        holder.name.setText(item.getName());
        holder.unit.setText(item.getUnit());


        return convertView;

    }

}

