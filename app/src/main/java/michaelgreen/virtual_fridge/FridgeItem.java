package michaelgreen.virtual_fridge;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Michael Green, 2016
 *
 * Class to hold entries to the fridge_items table.
 */
public class FridgeItem implements Parcelable {

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

    public String getDescription() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return "" + amount + " " + unit + ". Best Before: " + sdf.format(expiryDate) + ".";
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

    protected FridgeItem(Parcel in) {
        name = in.readString();
        unit = in.readString();
        username = in.readString();
        amount = in.readDouble();
        long tmpExpiryDate = in.readLong();
        expiryDate = tmpExpiryDate != -1 ? new Date(tmpExpiryDate) : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(unit);
        dest.writeString(username);
        dest.writeDouble(amount);
        dest.writeLong(expiryDate != null ? expiryDate.getTime() : -1L);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FridgeItem> CREATOR = new Parcelable.Creator<FridgeItem>() {
        @Override
        public FridgeItem createFromParcel(Parcel in) {
            return new FridgeItem(in);
        }

        @Override
        public FridgeItem[] newArray(int size) {
            return new FridgeItem[size];
        }
    };
}