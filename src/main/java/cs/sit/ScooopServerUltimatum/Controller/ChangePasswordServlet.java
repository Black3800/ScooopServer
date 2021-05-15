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
 * POST /api/auth/change-pwd
 * {uid, current_pwd, new_pwd, confirm_pwd}
 */

@WebServlet(name = "ChangePasswordServlet", value = "/api/auth/change-pwd")
public class ChangePasswordServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Middleware.setCORS(response);
        try(PrintWriter out = response.getWriter())
        {
            if(
                    request.getParameter("uid") == null
                            || request.getParameter("uid").isEmpty()
                            || request.getParameter("current_pwd") == null
                            || request.getParameter("current_pwd").isEmpty()
                            || request.getParameter("new_pwd") == null
                            || request.getParameter("new_pwd").isEmpty()
                            || request.getParameter("confirm_pwd") == null
                            || request.getParameter("confirm_pwd").isEmpty()
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
            UserOperation uo = new UserOperation();
            Login target = uo.getUserDetails(Integer.parseInt(request.getParameter("uid")));
            if((Integer) session.getAttribute("uid") != target.getUid()
                    && (Integer) session.getAttribute("privilege") >= target.getPrivilege())
            {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            // Check new password confirm
            if(!request.getParameter("new_pwd").equals(request.getParameter("confirm_pwd")))
            {
//                out.println("confirm wrong");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            // Check current password
            if(!uo.validateSecret(String.valueOf(session.getAttribute("usr")), request.getParameter("current_pwd")))
            {
//                out.println("pwd wrong");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            String[] newHash = UserOperation.generateHash(request.getParameter("new_pwd"));
            uo.updateHash(target.getUid(), newHash[0], newHash[1]);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
