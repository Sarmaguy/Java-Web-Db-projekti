package hr.fer.zemris.java.p12.servlets;

import hr.fer.zemris.java.p12.dao.DAOProvider;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet ("/servlets/glasanje")
public class GlasanjeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = Long.parseLong(req.getParameter("pollID"));

        //req.getSession().setAttribute("PollID", id);

        req.setAttribute("Poll", DAOProvider.getDao().getPoll(id));
        req.setAttribute("PollOptions", DAOProvider.getDao().getPollOptions(id));

        req.getRequestDispatcher("/WEB-INF/pages/glasanje.jsp").forward(req, resp);
    }
}
