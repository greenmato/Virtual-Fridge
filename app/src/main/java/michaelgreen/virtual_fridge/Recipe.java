package michaelgreen.virtual_fridge;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by John on 13/05/2016.
 */
public class Recipe {

    private String name;
    private ArrayList<Ingredient> items;
    private ArrayList<Integer> amounts;
    private String method;

    public Recipe(String name, ArrayList<Ingredient> items, ArrayList<Integer> amounts, String method) {
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

    public ArrayList<Integer> getAmounts() {
        return amounts;
    }

    public void setAmounts(ArrayList<Integer> amounts) {
        this.amounts = amounts;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
