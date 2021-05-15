package cs.sit.ScooopServerUltimatum.Controller;

import com.google.gson.Gson;
import cs.sit.ScooopServerUltimatum.Model.Login;
import cs.sit.ScooopServerUltimatum.Model.UserOperation;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "WhoAmIServlet", value = "/api/auth/whoami")
public class WhoAmIServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try(PrintWriter out = response.getWriter()) {
            Middleware.setCORS(response);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            UserOperation userOperation = new UserOperation();
            HttpSession session = request.getSession();
            if(session.getAttribute("uid") == null || (Integer) session.getAttribute("uid") == -1)
            {
                out.print("{\"uid\":-1}");
            }
            else
            {
                Login userDetails = userOperation.getUserDetails((Integer) session.getAttribute("uid"));
                Gson g = new Gson();
                String json = g.toJson(userDetails);
                out.print(json);
            }
            out.flush();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
