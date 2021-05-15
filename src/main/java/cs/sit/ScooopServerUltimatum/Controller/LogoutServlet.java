package cs.sit.ScooopServerUltimatum.Controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LogoutServlet", value = "/api/auth/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Middleware.setCORS(response);
        if(request.getParameter("logout") != null)
        {
            HttpSession session = request.getSession();
            session.invalidate();
        }
        else
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
