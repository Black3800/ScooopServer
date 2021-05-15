package cs.sit.ScooopServerUltimatum.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class Current {
    private ArrayList<String> x;
    private ArrayList<String> y;
    private HashMap<String, Integer> order;

    public ArrayList<String> getX() {
        return x;
    }

    public ArrayList<String> getY() {
        return y;
    }

    public HashMap<String, Integer> getOrder() {
        return order;
    }

    public void setOrder(String key, int value) {
        this.order.put(key, value);
    }

    public boolean isOrderExist(String key) {
        return this.order.getOrDefault(key, 1) != -1;
    }

    @Override
    public String toString() {
        return "Current{" +
                "x=" + x +
                ", y=" + y +
                ", order=" + order +
                '}';
    }
}
