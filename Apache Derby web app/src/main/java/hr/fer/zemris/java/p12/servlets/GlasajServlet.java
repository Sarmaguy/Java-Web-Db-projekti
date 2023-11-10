package hr.fer.zemris.java.p12.servlets;

import hr.fer.zemris.java.p12.dao.DAOProvider;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/servlets/glasanje-glasaj")
public class GlasajServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = Long.parseLong(req.getParameter("id"));

        DAOProvider.getDao().incrementVote(id);

        long pollID = DAOProvider.getDao().getPollOption(id).getPollId();

        resp.sendRedirect(resp.encodeRedirectURL(req.getContextPath() + "/servlets/glasanje-rezultati?pollId=" + pollID));
    }
}
