package hr.fer.zemris.java.p12.servlets;

import hr.fer.zemris.java.p12.dao.DAOProvider;
import hr.fer.zemris.java.p12.model.PollOption;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@WebServlet("/servlets/glasanje-rezultati")
public class RezultatiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = Long.parseLong(req.getParameter("pollId"));

        req.setAttribute("pollId", id);

        List<PollOption> pollOptions = DAOProvider.getDao().getPollOptions(id);
        Collections.sort(pollOptions, (o1, o2) -> Long.compare(o2.getVotesCount(), o1.getVotesCount()));
        req.setAttribute("pollOptions", pollOptions);

        long maxVotes = pollOptions.get(0).getVotesCount();
        List<PollOption> winners = new ArrayList<>();

        for (PollOption pollOption : pollOptions) {
            if (pollOption.getVotesCount() == maxVotes) {
                winners.add(pollOption);
            }
        }
        req.setAttribute("winners", winners);

        req.getRequestDispatcher("/WEB-INF/pages/rezultati.jsp").forward(req, resp);

    }
}
