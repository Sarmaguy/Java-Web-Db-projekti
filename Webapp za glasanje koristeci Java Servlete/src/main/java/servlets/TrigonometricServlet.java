package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/trigonometric")
public class TrigonometricServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int a = 0;
        int b = 360;

        a = Integer.parseInt(request.getParameter("a"));
        b = Integer.parseInt(request.getParameter("b"));

        if (a > b) {
            int temp = a;
            a = b;
            b = temp;
        }

        if (b > a + 720)
            b = a + 720;

        request.setAttribute("a", a);
        request.setAttribute("b", b);

        request.getRequestDispatcher("/pages/trigonometric.jsp").forward(request, response);
    }
}
