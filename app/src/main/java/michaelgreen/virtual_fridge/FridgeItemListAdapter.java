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
//public class Adapter extends ArrayAdapter<String>{

//    Context c;
//    String[] names;
//    String[] descriptions;
//    LayoutInflater inflater;
//
//    public Adapter(Context context, String[] names, String[] descriptions) {
//        super(context, R.layout.ingredient_list_item);
//        this.c = context;
//        this.names = names;
//        this.descriptions = descriptions;
//    }
//
//    public class ViewHolder {
//        ImageView image;
//        TextView name;
//        TextView description;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if(convertView == null) {
//            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = inflater.inflate(R.layout.ingredient_list_item, null);
//        }
//
//        ViewHolder viewHolder = new ViewHolder();
//        viewHolder.name = (TextView) convertView.findViewById(R.id.first_line);
//        viewHolder.description = (TextView) convertView.findViewById(R.id.second_line);
//
//        viewHolder.name.setText(names[position]);
//        viewHolder.description.setText(descriptions[position]);
//
//        return convertView;
//    }
public class FridgeItemListAdapter extends ArrayAdapter<FridgeItem> {

    Context c;
    ArrayList<FridgeItem> items;
    LayoutInflater inflater;

    public FridgeItemListAdapter(Context context, ArrayList<FridgeItem> items) {
        super(context, R.layout.ingredient_list_item, items);
        this.c = context;
        this.items = items;
    }

    public class ViewHolder
    {
        TextView name;
        TextView description;
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
        holder.description = (TextView) convertView.findViewById(R.id.second_line);

        FridgeItem item = getItem(position);
        holder.name.setText(item.getName());
        holder.description.setText(item.getDescription());


        return convertView;

    }

}

