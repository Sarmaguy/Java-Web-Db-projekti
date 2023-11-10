package hr.fer.zemris.java.p12.servlets;

import hr.fer.zemris.java.p12.dao.DAOProvider;
import hr.fer.zemris.java.p12.model.PollOption;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/servlets/chart")
public class ChartServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("image/png");
        long id = Long.parseLong(request.getParameter("pollID"));

        String title = DAOProvider.getDao().getPoll(id).getTitle();
        List<PollOption> pollOptions = DAOProvider.getDao().getPollOptions(id);


        DefaultPieDataset dataset = new DefaultPieDataset();

        for (PollOption pollOption : pollOptions)
            dataset.setValue(pollOption.getOptionTitle(), pollOption.getVotesCount());


        JFreeChart chart = org.jfree.chart.ChartFactory.createPieChart(title, dataset, true, true, false);

        ChartUtilities.writeChartAsPNG(response.getOutputStream(), chart, 400, 400);
    }
}
