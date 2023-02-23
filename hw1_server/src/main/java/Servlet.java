/**
 * HTTP Servlet
 * By Yunyi Chi
 * */

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(name = "Servlet", value = "/Servlet")
public class Servlet extends HttpServlet {
// handle Post request
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain");
        String urlPath = req.getPathInfo();

//        check if url is null or empty
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("not found url path");
            return;
        }

        String[] urlParts = urlPath.split("/");

        BufferedReader content = req.getReader();
        String returnString = ReadBigStringIn(content);

        if (!isUrlValid(urlParts)) {
            res.setStatus((HttpServletResponse.SC_NOT_FOUND));
            res.getWriter().write("url not valid");
        } else {
            res.setStatus((HttpServletResponse.SC_CREATED));
            res.getWriter().write("It works" + returnString);
            System.out.println("success");
        }
    }
//    check if url is valid
    private boolean isUrlValid(String[] urlPath) {
        if (    urlPath[1].equals("swipe") &&
                (urlPath[2].equals("left") || urlPath[2].equals("right"))) {
            return true;
        }
        return false;
    }
//    read Buffer data
    public String ReadBigStringIn(BufferedReader buffIn) throws IOException {
        StringBuilder everything = new StringBuilder();
        String line;
        while( (line = buffIn.readLine()) != null) {
            everything.append(line);
        }
        return everything.toString();
    }
}
