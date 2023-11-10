package servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = "/setColor/*")
public class SetColorServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String color = request.getPathInfo().substring(1);

        session.setAttribute("pickedBgCol", color == null ? "FFFFFF" : color);
        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/colors"));
    }
}
