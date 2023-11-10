package servlets;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/chart")
public class ChartGeneratingServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("image/png");

        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Windows", 36);
        dataset.setValue("MacOs", 27);
        dataset.setValue("MS-DOS", 12);
        dataset.setValue("Linux", 18);
        dataset.setValue("Other", 7);

        JFreeChart chart = org.jfree.chart.ChartFactory.createPieChart("OS usage", dataset, true, true, false);

        ChartUtilities.writeChartAsPNG(response.getOutputStream(), chart, 400, 400);
    }
}
