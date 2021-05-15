package cs.sit.ScooopServerUltimatum.Controller;

import com.google.gson.Gson;
import cs.sit.ScooopServerUltimatum.Model.Current;
import cs.sit.ScooopServerUltimatum.Model.Order;
import cs.sit.ScooopServerUltimatum.Model.OrderOperation;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/***
 * This remove is used in the meaning of 'cancel an order'
 * Not to be confused with 'check an order'
 */
@WebServlet(name = "RemoveOrderServlet", value = "/api/order/remove")
public class RemoveOrderServlet extends HttpServlet {
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

        // if order is current, must be removed from json too
        OrderOperation oo = new OrderOperation();
        int id = Integer.parseInt(request.getParameter("id"));
        String table = oo.getOrder(id).getTable();
        Current c = OrderOperation.getCurrentOrder(request.getServletContext());
        if(c.isOrderExist(table))
        {
            OrderOperation.updateCurrentOrderJson(request.getServletContext(), table, -1);
        }
        if(!oo.removeOrder(id))
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
