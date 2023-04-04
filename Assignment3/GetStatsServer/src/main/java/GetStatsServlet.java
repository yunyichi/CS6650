import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "GetStatsServlet", value = "/GetStatsServlet")
public class GetStatsServlet extends HttpServlet {
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
        GetStatsDao getStatsDao = new GetStatsDao();
        List<Integer> statsList = getStatsDao.getStats(Integer.parseInt(urlParts[2]));
        if (statsList.size() == 0) {
            res.setStatus((HttpServletResponse.SC_CREATED));
            res.getWriter().write("Found no record for this user");
        } else {
            res.setStatus((HttpServletResponse.SC_CREATED));
            Gson gson = new Gson();
            String matchListString = gson.toJson(statsList);
            res.getWriter().write(matchListString);
        }
    }

    private boolean isUrlValid(String[] urlParts) {
        if (    urlParts[1].equals("stats") &&
                (Integer.parseInt(urlParts[2]) >= 1 && Integer.parseInt(urlParts[2]) <= 5000)) {
            return true;
        }
        return false;
    }
}
