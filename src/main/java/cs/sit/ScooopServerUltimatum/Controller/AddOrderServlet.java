package cs.sit.ScooopServerUltimatum.Controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.org.apache.xpath.internal.operations.Or;
import cs.sit.ScooopServerUltimatum.Model.Current;
import cs.sit.ScooopServerUltimatum.Model.Order;
import cs.sit.ScooopServerUltimatum.Model.OrderOperation;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/***
 * {
 *      "table": "a3",
 *      "items": [
 *          {
 *              "id": 1,
 *              "count": 3
 *          },{
 *              "id": 2,
 *              "count": 1
 *          },{
 *              "id": 4,
 *              "count": 2
 *          }
 *      ]
 *  }
 */

@WebServlet(name = "AddOrderServlet", value = "/api/order/add")
public class AddOrderServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Middleware.setCORS(response);
        try(PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            if(session.getAttribute("uid") == null || (Integer) session.getAttribute("uid") == -1)
            {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            Gson g = new Gson();
            Order or;
            try {
                or = g.fromJson(request.getParameter("data"), Order.class);
            } catch(JsonSyntaxException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            // Validate table
            ServletContext context = request.getServletContext();
            String currentOrderJson = OrderOperation.getCurrentOrderJson(context);
            Current currentOrder = g.fromJson(currentOrderJson, Current.class);
            if(currentOrder.getOrder().getOrDefault(or.getTable(), 1) != -1)
            {
                // that table doesnt exist or already has order
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Insert
            or.setEmployeeId((Integer) session.getAttribute("uid"));
            OrderOperation oo = new OrderOperation();
            int orderId = oo.addOrder(or);
            out.print("{\"order_id\":" + orderId + "}");

            // update current.json
            OrderOperation.updateCurrentOrderJson(context, or.getTable(), orderId);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
