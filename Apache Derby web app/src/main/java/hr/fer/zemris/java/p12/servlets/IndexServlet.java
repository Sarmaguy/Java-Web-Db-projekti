package hr.fer.zemris.java.p12.servlets;

import hr.fer.zemris.java.p12.dao.DAOProvider;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/servlets/index.html")
public class IndexServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("Polls", DAOProvider.getDao().getPolls());

        req.getRequestDispatcher("/WEB-INF/pages/Index.jsp").forward(req, resp);
    }
}
