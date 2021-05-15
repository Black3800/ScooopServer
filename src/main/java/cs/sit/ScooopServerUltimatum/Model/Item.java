package cs.sit.ScooopServerUltimatum.Model;

import java.sql.ResultSet;

public class Item {
    private int id;
    private int count;
    private float price;
    private String name = null;
    private String img = null;

    public Item(int id, String name, float price, String img) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.img = img;
    }

    public Item(ResultSet rs) {
        if(rs == null)
        {
            this.id = -1;
        }
        else
        {
            try {
                this.id = rs.getInt("item_id");
                this.count = rs.getInt("item_count");
                this.price = rs.getFloat("item_price");
                this.name = rs.getString("item_name");
                this.img = rs.getString("img");
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public String getImg() {
        return img;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", count=" + count +
                ", price=" + price +
                ", name='" + name + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
