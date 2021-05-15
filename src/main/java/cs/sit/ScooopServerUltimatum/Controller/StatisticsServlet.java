package cs.sit.ScooopServerUltimatum.Controller;

import com.google.gson.Gson;
import cs.sit.ScooopServerUltimatum.Model.DateIncomePair;
import cs.sit.ScooopServerUltimatum.Utils.DBConnection;
import javafx.util.Pair;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

@WebServlet(name = "StatisticsServlet", value = "/api/stats")
public class StatisticsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Middleware.setCORS(response);
        HttpSession session = request.getSession();
        if(session.getAttribute("uid") == null || (Integer) session.getAttribute("uid") == -1)
        {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        // validate dates
        if(request.getParameter("from") == null
                || request.getParameter("from").isEmpty()
                || request.getParameter("to") == null
                || request.getParameter("to").isEmpty())
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String from_date = request.getParameter("from");
        String to_date = request.getParameter("to");
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            PrintWriter out = response.getWriter();
            connection = DBConnection.getMySQLConnection();
            String sql = "SELECT SUM(items.item_price*orders.item_count) as total, SUBSTRING(orders_table.time_in, 1, 10) as date\n" +
                    "FROM orders INNER JOIN (orders_table, items)\n" +
                    "ON (orders.order_id=orders_table.order_id AND orders.item_id=items.item_id)\n" +
                    "WHERE orders_table.time_in BETWEEN ? AND ?\n" +
                    "GROUP BY SUBSTRING(orders_table.time_in, 1, 10)";
            System.out.println(sql);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, from_date);
            preparedStatement.setString(2, to_date);
            rs = preparedStatement.executeQuery();
            ArrayList<DateIncomePair> income = new ArrayList<>();
            while(rs.next())
            {
                income.add(new DateIncomePair(rs.getString("date"), rs.getFloat("total")));
            }
            Gson g = new Gson();
            out.print(g.toJson(income));
            out.flush();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, preparedStatement, rs);
        }
    }
}
