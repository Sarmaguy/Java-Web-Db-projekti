package servlets;

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

@WebServlet(urlPatterns = "/glasanje")
public class GlasanjeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String fileName = request.getServletContext().getRealPath("/WEB-INF/glasanje-definicija.txt");
        List<String[]> data = new ArrayList<>();

        try(Scanner sc = new Scanner(new File(fileName))) {
            while(sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] parts = line.split("\t");
                data.add(parts);
            }
        }

        request.setAttribute("data", data);
        request.getRequestDispatcher("/pages/glasanjeIndex.jsp").forward(request, response);

    }
}
