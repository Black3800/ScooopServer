package cs.sit.ScooopServerUltimatum.Model;

import com.google.gson.Gson;
import cs.sit.ScooopServerUltimatum.Utils.DBConnection;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class OrderOperation {
    Connection connection;
    PreparedStatement preparedStatement;
    ResultSet resultSet;
    static final String CURRENT_JSON_PATH = "/WEB-INF/current.json";

    public Item getItem(int id) {
        Item i = new Item(null);
        try {
            connection = DBConnection.getMySQLConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM items WHERE item_id = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                i = new Item(resultSet);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, preparedStatement, resultSet);
        }
        return i;
    }

    public Order getOrder(int id) {
        Order or = new Order(null);
        try {
            connection = DBConnection.getMySQLConnection();
            String sql = "SELECT * FROM orders INNER JOIN items ON orders.item_id = items.item_id WHERE orders.order_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            Item it;
            while(resultSet.next()) {
                it = new Item(resultSet);
                or.addItem(it);
            }
            sql = "SELECT * FROM orders INNER JOIN orders_table ON orders.order_id = orders_table.order_id WHERE orders.order_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                or.setTable(resultSet);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, preparedStatement, resultSet);
        }
        return or;
    }

    public int addOrder(Order or) {
        int lastOrderId = -1;
        try {
            connection = DBConnection.getMySQLConnection();
            // Get latest order_id
            preparedStatement = connection.prepareStatement("SELECT MAX(order_id) as max_id FROM orders");
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                lastOrderId = resultSet.getInt("max_id");
            } else return -1;
            lastOrderId++;
            // Insert into orders
            int rowsAffected = 0;
            ArrayList<Item> items = or.getItems();
            for(Item it : items)
            {
                preparedStatement = connection.prepareStatement(
                        "INSERT INTO orders (order_id, item_id, item_count)\n" +
                        "SELECT ?, item_id, ? FROM items WHERE item_id=?");
                preparedStatement.setInt(1, lastOrderId);
                preparedStatement.setInt(2, it.getCount());
                preparedStatement.setInt(3, it.getId());
                rowsAffected += preparedStatement.executeUpdate();
            }
            if(rowsAffected == 0) return -1;
            // Insert into orders_table
            LocalDateTime myDateObj = LocalDateTime.now();
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = myDateObj.format(myFormatObj) + " GMT+0700";
            preparedStatement = connection.prepareStatement("INSERT INTO orders_table VALUES (?, ?, ?, NULL, ?)");
            preparedStatement.setInt(1, lastOrderId);
            preparedStatement.setString(2, or.getTable());
            preparedStatement.setString(3, formattedDate);
            preparedStatement.setInt(4, or.getEmployeeId());
            rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected == 0) return -1;
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, preparedStatement, resultSet);
        }
        or.setOrderId(lastOrderId);
        return lastOrderId;
    }

    public boolean updateOrder(Order or) {
        boolean success = true;
        try {
            // check if order exists in db
            int orderId = or.getOrderId();
            connection = DBConnection.getMySQLConnection();
            preparedStatement = connection.prepareStatement("SELECT order_id FROM orders_table WHERE order_id=?");
            preparedStatement.setInt(1, orderId);
            resultSet = preparedStatement.executeQuery();
            if(!resultSet.next()) return false;
            int rowsAffected = 0;
            ArrayList<Item> items = or.getItems();
            for(Item it : items)
            {
                if(it.getCount() == -1)
                {
                    preparedStatement = connection.prepareStatement("DELETE FROM orders WHERE order_id=? AND item_id=?");
                    preparedStatement.setInt(1, orderId);
                    preparedStatement.setInt(2, it.getId());
                }
                else
                {
                    preparedStatement = connection.prepareStatement(
                            "INSERT INTO orders (order_id, item_id, item_count)\n" +
                                    "SELECT ?, item_id, ? FROM items WHERE item_id=?\n" +
                                    "ON DUPLICATE KEY UPDATE item_count=?");
                    preparedStatement.setInt(1, orderId);
                    preparedStatement.setInt(2, it.getCount());
                    preparedStatement.setInt(3, it.getId());
                    preparedStatement.setInt(4, it.getCount());
                }
                rowsAffected += preparedStatement.executeUpdate();
            }
            if(rowsAffected == 0) return false;
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, preparedStatement, resultSet);
        }
        return success;
    }

    public boolean removeOrder(int id) {
        try {
            connection = DBConnection.getMySQLConnection();
            preparedStatement = connection.prepareStatement(
                    "DELETE orders , orders_table FROM orders\n" +
                    "INNER JOIN orders_table  \n" +
                    "WHERE orders.order_id=orders_table.order_id AND orders.order_id=?");
            preparedStatement.setInt(1, id);
            if(preparedStatement.executeUpdate() >= 1)
            {
                return true;
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, preparedStatement, resultSet);
        }
        return false;
    }

    public boolean checkOrder(int id) {
        try {
            connection = DBConnection.getMySQLConnection();
            preparedStatement = connection.prepareStatement("UPDATE orders_table SET time_out=? WHERE order_id=?");
            LocalDateTime myDateObj = LocalDateTime.now();
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = myDateObj.format(myFormatObj) + " GMT+0700";
            preparedStatement.setString(1, formattedDate);
            preparedStatement.setInt(2, id);
            if(preparedStatement.executeUpdate() < 1) return false;
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, preparedStatement, resultSet);
        }
        return true;
    }

    public static String getCurrentOrderJson(ServletContext context) {
        String json = "";
        try {
            String fullPath = context.getRealPath(CURRENT_JSON_PATH);
            File f = new File(fullPath);
            Scanner sc = new Scanner(f);
            while(sc.hasNextLine())
            {
                json += sc.nextLine();
            }
            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public static Current getCurrentOrder(ServletContext context) {
        Current c = null;
        try {
            String json = OrderOperation.getCurrentOrderJson(context);
            Gson g = new Gson();
            c = g.fromJson(json, Current.class);
            c.getOrder().remove("null");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public static void updateCurrentOrderJson(ServletContext context, Current c) {
        try {
            String fullPath = context.getRealPath(CURRENT_JSON_PATH);
            FileWriter fw = new FileWriter(fullPath);
            Gson g = new Gson();
            fw.write(g.toJson(c));
            fw.close();
//            System.out.println("Successfully wrote to the file.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateCurrentOrderJson(ServletContext context, String table, int orderId) {
        try {
            String json = OrderOperation.getCurrentOrderJson(context);
            Gson g = new Gson();
            Current c = g.fromJson(json, Current.class);
            c.setOrder(table, orderId);
            c.getOrder().remove("null");
            OrderOperation.updateCurrentOrderJson(context, c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
