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

@WebServlet(name = "LoginServlet", value = "/api/auth/login")
public class LoginServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try(PrintWriter out = response.getWriter()) {
            Middleware.setCORS(response);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            UserOperation userOperation = new UserOperation();
            /***
             * LoginServlet response code is returned regarding to the following conditions
             * 200 -> successful login
             * 400 -> empty username or password submitted
             * 400 -> already logged in
             * 401 -> incorrect username or password
             */
            HttpSession session = request.getSession();
            String inputUsr = request.getParameter("username");
            String inputPwd = request.getParameter("password");
            if(inputUsr == null || inputPwd == null ||
                    inputUsr.isEmpty() || inputPwd.isEmpty() ||
                    session.getAttribute("uid") != null )
            {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            Login userDetails = userOperation.checkLogin(inputUsr, inputPwd);
            if(userDetails.getUid() == -1)
            {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            else
            {
                session.setAttribute("uid", userDetails.getUid());
                session.setAttribute("usr", userDetails.getUsr());
                session.setAttribute("privilege", userDetails.getPrivilege());
            }
            Gson g = new Gson();
            String json = g.toJson(userDetails);
            out.print(json);
            out.flush();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
