package cs.sit.ScooopServerUltimatum.Controller;

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

@WebServlet(name = "GetCurrentOrderServlet", value = "/api/order/current")
public class GetCurrentOrderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Middleware.setCORS(response);
        try(PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            if(session.getAttribute("uid") == null || (Integer) session.getAttribute("uid") == -1)
            {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
            else
            {
                ServletContext context = request.getServletContext();
                String currentOrderJson = OrderOperation.getCurrentOrderJson(context);
                out.print(currentOrderJson);
                out.flush();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
