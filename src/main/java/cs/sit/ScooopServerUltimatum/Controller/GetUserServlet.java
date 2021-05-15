package cs.sit.ScooopServerUltimatum.Controller;

import com.google.gson.Gson;
import cs.sit.ScooopServerUltimatum.Model.Login;
import cs.sit.ScooopServerUltimatum.Utils.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

@WebServlet(name = "GetUserervlet", value = "/api/users")
public class GetUserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try(PrintWriter out = response.getWriter()) {
            Middleware.setCORS(response);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            HttpSession session = request.getSession();
            if(session.getAttribute("uid") == null || (Integer) session.getAttribute("uid") == -1)
            {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            connection = DBConnection.getMySQLConnection();
            preparedStatement = connection.prepareStatement("SELECT uid, usr, privilege FROM account");
            rs = preparedStatement.executeQuery();
            ArrayList<Login> users = new ArrayList<Login>();
            while(rs.next()) {
                users.add( new Login(rs) );
            }
            Gson g = new Gson();
            String json = g.toJson(users);
            out.print(json);
            out.flush();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            DBConnection.close(connection, preparedStatement, rs);
        }
    }
}
