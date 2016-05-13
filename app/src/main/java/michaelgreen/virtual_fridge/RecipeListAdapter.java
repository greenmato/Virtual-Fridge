package michaelgreen.virtual_fridge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by John on 13/05/2016.
 */

public class RecipeListAdapter extends ArrayAdapter<Recipe> {

    Context c;
    ArrayList<Recipe> items;
    LayoutInflater inflater;

    public RecipeListAdapter(Context context, ArrayList<Recipe> items) {
        super(context, R.layout.ingredient_list_item, items);
        this.c = context;
        this.items = items;
    }

    public class ViewHolder
    {
        TextView name;
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

        Recipe item = getItem(position);
        holder.name.setText(item.getName());


        return convertView;

    }

}

