package cs.sit.ScooopServerUltimatum.Controller;

import javax.servlet.http.HttpServletResponse;

public class Middleware {
    public static void setCORS(HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Origin, Accept, Cache-Control, Pragma");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Max-Age", "65536");
    }
}
