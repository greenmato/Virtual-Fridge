package michaelgreen.virtual_fridge;

import java.sql.Date;

/*
 * Michael Green, 2016
 *
 * Class to hold entries to the fridge_items table.
 */
public class FridgeItem {

    private String name;
    private String unit;
    private String username;
    private double amount;
    private Date expiryDate;

    public FridgeItem(String name, String unit, String username, double amount, Date expiryDate) {
        this.name = name;
        this.unit = unit;
        this.username = username;
        this.amount = amount;
        this.expiryDate = expiryDate;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
