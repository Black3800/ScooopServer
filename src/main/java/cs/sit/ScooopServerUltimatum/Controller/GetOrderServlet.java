package cs.sit.ScooopServerUltimatum.Controller;

import com.google.gson.Gson;
import cs.sit.ScooopServerUltimatum.Model.Order;
import cs.sit.ScooopServerUltimatum.Model.OrderOperation;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

@WebServlet(name = "GetOrderServlet", value = "/api/order/get")
public class GetOrderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Middleware.setCORS(response);
        try(PrintWriter out = response.getWriter()) {
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

            OrderOperation oo = new OrderOperation();
            Order o = oo.getOrder(Integer.parseInt(request.getParameter("id")));
            Gson g = new Gson();
            out.println(g.toJson(o));
            out.flush();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
