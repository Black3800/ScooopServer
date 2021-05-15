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

@WebServlet(name = "CheckOrderServlet", value = "/api/order/check")
public class CheckOrderServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Middleware.setCORS(response);
        HttpSession session = request.getSession();
        if(session.getAttribute("uid") == null || (Integer) session.getAttribute("uid") == -1)
        {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if(request.getParameter("id") == null
                || request.getParameter("id").isEmpty()
                || !request.getParameter("id").matches("^[0-9]+$")
        ) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Validate table
        Gson g = new Gson();
        OrderOperation oo = new OrderOperation();
        ServletContext context = request.getServletContext();
        String currentOrderJson = OrderOperation.getCurrentOrderJson(context);
        Current currentOrder = g.fromJson(currentOrderJson, Current.class);
        Order or = oo.getOrder(Integer.parseInt(request.getParameter("id"))); // guanrantee order is in db
        int orderId = currentOrder.getOrder().getOrDefault(or.getTable(), -1); // error if id is invalid

        if(or.getOrderId() == -1 || orderId == -1 || orderId != or.getOrderId())
        {
            // the order doesnt exist or it is not one of current orders
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Update
        if(!oo.checkOrder(orderId))
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        // Update json (dont do this yet)
        OrderOperation.updateCurrentOrderJson(context, or.getTable(), -1);
    }
}
