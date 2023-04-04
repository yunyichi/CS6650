import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "GetMatchServlet", value = "/GetMatchServlet")
public class GetMatchServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain");
        String urlPath = req.getPathInfo();

//        check if url is null or empty
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("not found url path");
            return;
        }

        String[] urlParts = urlPath.split("/");


        if (!isUrlValid(urlParts)) {
            res.setStatus((HttpServletResponse.SC_NOT_FOUND));
            res.getWriter().write("url is not valid");
        }
        GetMatchDao getMatchDao = new GetMatchDao();
        List<Integer> matchList = getMatchDao.getMatch(Integer.parseInt(urlParts[2]));
        if (matchList.size() == 0) {
            res.setStatus((HttpServletResponse.SC_CREATED));
            res.getWriter().write("Found no matched id");
        } else {
            res.setStatus((HttpServletResponse.SC_CREATED));
            Gson gson = new Gson();
            String matchListString = gson.toJson(matchList);
            res.getWriter().write(matchListString);
        }
    }

    private boolean isUrlValid(String[] urlParts) {
        if (    urlParts[1].equals("matches") &&
                (Integer.parseInt(urlParts[2]) >= 1 && Integer.parseInt(urlParts[2]) <= 5000)) {
            return true;
        }
        return false;
    }
}
