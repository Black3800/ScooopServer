package cs.sit.ScooopServerUltimatum.Controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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
import java.io.IOException;

/***
 * {
 *      "orderId": 1,
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
 *  Notice: only update current orders, return 400 for checked orders
 */

@WebServlet(name = "UpdateOrderServlet", value = "/api/order/update")
public class UpdateOrderServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Middleware.setCORS(response);
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
        int orderId = currentOrder.getOrder().getOrDefault(or.getTable(), -1);
        if(orderId == -1 || orderId != or.getOrderId())
        {
            // that table doesnt exist or an order doesnt exist
            // (i.e. you cant update order that doesnt exist)
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Update
        OrderOperation oo = new OrderOperation();
        if(!oo.updateOrder(or))
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
    }
}
