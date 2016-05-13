package michaelgreen.virtual_fridge;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by John on 13/05/2016.
 */
public class Recipe implements Parcelable {

    private String name;
    private ArrayList<Ingredient> items;
    private ArrayList<Double> amounts;
    private String method;

    public Recipe(String name, ArrayList<Ingredient> items, ArrayList<Double> amounts, String method) {
        this.name = name;
        this.items = items;
        this.amounts = amounts;
        this.method = method;
    }

    public Recipe() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Ingredient> getItems() {
        return items;
    }

    public void setItems(ArrayList<Ingredient> items) {
        this.items = items;
    }

    public ArrayList<Double> getAmounts() {
        return amounts;
    }

    public void setAmounts(ArrayList<Double> amounts) {
        this.amounts = amounts;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    protected Recipe(Parcel in) {
        name = in.readString();
        if (in.readByte() == 0x01) {
            items = new ArrayList<Ingredient>();
            in.readList(items, Ingredient.class.getClassLoader());
        } else {
            items = null;
        }
        if (in.readByte() == 0x01) {
            amounts = new ArrayList<Double>();
            in.readList(amounts, Double.class.getClassLoader());
        } else {
            amounts = null;
        }
        method = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        if (items == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(items);
        }
        if (amounts == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(amounts);
        }
        dest.writeString(method);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}