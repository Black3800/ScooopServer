package cs.sit.ScooopServerUltimatum.Model;

import com.google.gson.Gson;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Locale;

public class Order {
    private int orderId;
    private int employeeId;
    private String table;
    private String timeIn;
    private String timeOut;
    private ArrayList<Item> items = new ArrayList<>();

    public Order(ResultSet rs) {
        if(rs == null)
        {
            this.orderId = -1;
        }
    }

    public void addItem(Item it) {
        items.add(it);
    }

    public void setTable(ResultSet rs) {
        try {
            this.orderId = rs.getInt("order_id");
            this.employeeId = rs.getInt("emp");
            this.table = rs.getString("table_name").toLowerCase();
            this.timeIn = rs.getString("time_in");
            this.timeOut = rs.getString("time_out");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public void setOrderId(int id) {
        this.orderId = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getTable() {
        return table;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", employeeId=" + employeeId +
                ", table='" + table + '\'' +
                ", timeIn='" + timeIn + '\'' +
                ", timeOut='" + timeOut + '\'' +
                ", items=" + items +
                '}';
    }
}
