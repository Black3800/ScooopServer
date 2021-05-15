package cs.sit.ScooopServerUltimatum.Controller;

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

/***
 * POST /api/auth/add
 * {usr, pwd, privilege}
 */

@WebServlet(name = "AddUserServlet", value = "/api/auth/add")
public class AddUserServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Middleware.setCORS(response);
        try(PrintWriter out = response.getWriter())
        {
            if(
                    request.getParameter("usr") == null
                            || request.getParameter("usr").isEmpty()
                            || request.getParameter("pwd") == null
                            || request.getParameter("pwd").isEmpty()
                            || request.getParameter("privilege") == null
                            || request.getParameter("privilege").isEmpty()
                            || !request.getParameter("privilege").matches("^[0-9]$")
                            || Integer.parseInt(request.getParameter("privilege")) < 0
                            || Integer.parseInt(request.getParameter("privilege")) > 2
            ) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                out.println("null");
                return;
            }
            // Check permission
            HttpSession session = request.getSession();
            if(session.getAttribute("uid") == null)
            {
//                out.println("not logged in");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            if((Integer) session.getAttribute("privilege") >= Integer.parseInt(request.getParameter("privilege")))
            {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            // Check usr exists
            UserOperation uo = new UserOperation();
            Login usrCheck = uo.getUserDetails(request.getParameter("usr"));
            if(usrCheck.getUid() != -1)
            {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                out.println("{\"err\":\"This username has already been used\"}");
                out.flush();
                return;
            }
            String[] newHash = UserOperation.generateHash(request.getParameter("pwd"));
            int uid = uo.addUser(
                    request.getParameter("usr"),
                    newHash[0],
                    newHash[1],
                    Integer.parseInt(request.getParameter("privilege"))
            );
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.println("{\"uid\":" + uid + "}");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
