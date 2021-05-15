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
 * POST /api/auth/remove
 * {usr, pwd, privilege}
 */

@WebServlet(name = "RemoveUserServlet", value = "/api/auth/remove")
public class RemoveUserServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Middleware.setCORS(response);
        try(PrintWriter out = response.getWriter())
        {
            if(
                    request.getParameter("uid") == null
                            || request.getParameter("uid").isEmpty()
                            || !request.getParameter("uid").matches("^[0-9]+$")
            ) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("null");
                return;
            }
            // Check permission
            HttpSession session = request.getSession();
            if(session.getAttribute("uid") == null || (Integer) session.getAttribute("uid") == -1)
            {
                out.println("not logged in");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            // Check usr exists
            UserOperation uo = new UserOperation();
            Login usrCheck = uo.getUserDetails(Integer.parseInt(request.getParameter("uid")));
            if(usrCheck.getUid() == -1)
            {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            if((Integer) session.getAttribute("privilege") >= usrCheck.getPrivilege())
            {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            uo.removeUser(Integer.parseInt(request.getParameter("uid")));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
