package servlets;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

@WebServlet(urlPatterns = "/appInfo")
public class AppInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ServletContext context = request.getServletContext();
        Long time = (Long) context.getAttribute("time");
        Long currentTime = System.currentTimeMillis();
        //formats date and writes it in seconds,minutes, hours, days but days on first place
        String timeString = String.format("%d days, %d hours, %d minutes, %d seconds",
                (currentTime - time) / (1000 * 60 * 60 * 24),
                (currentTime - time) / (1000 * 60 * 60) % 24,
                (currentTime - time) / (1000 * 60) % 60,
                (currentTime - time) / 1000 % 60);

        HttpSession session = request.getSession();
        String sessionTime = "Application started " + new Date(time) + " (" + timeString + " ago)";

        session.setAttribute("time", sessionTime);
        request.getRequestDispatcher("/pages/appInfo.jsp").forward(request, response);

    }
}
