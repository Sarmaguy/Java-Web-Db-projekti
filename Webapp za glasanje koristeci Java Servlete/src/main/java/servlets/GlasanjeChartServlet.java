package servlets;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@WebServlet(urlPatterns = "/glasanjeChart")
public class GlasanjeChartServlet  extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileNameRezultati = request.getServletContext().getRealPath("/WEB-INF/glasanje-rezultati.txt");
        String fileNameDefinicija = request.getServletContext().getRealPath("/WEB-INF/glasanje-definicija.txt");

        List<String[]> data = new ArrayList<>();
        try(Scanner sc = new Scanner(new File(fileNameDefinicija))) {
            while(sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] parts = line.split("\t");
                data.add(parts);
            }
        }

        List<String[]> votes = new ArrayList<>();
        try(Scanner sc = new Scanner(new File(fileNameRezultati))) {
            while(sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] parts = line.split("\t");
                votes.add(parts);
            }
        }

        //replace id with name
        for(String[] vote : votes) {
            for(String[] band : data) {
                if(vote[0].equals(band[0])) {
                    vote[0] = band[1];
                    break;
                }
            }
        }

        DefaultPieDataset dataset = new DefaultPieDataset();
        for(String[] vote : votes) {
            dataset.setValue(vote[0], Integer.parseInt(vote[1]));
        }
        JFreeChart chart = org.jfree.chart.ChartFactory.createPieChart3D("Glasanje", dataset, true, true, false);


        response.setContentType("image/png");
        ChartUtilities.writeChartAsPNG(response.getOutputStream(), chart, 500, 500);
    }
}
