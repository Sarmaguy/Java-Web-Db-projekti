package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@WebServlet(urlPatterns = "/glasanje-glasaj")
public class GlasanjeGlasajServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String fileName = request.getServletContext().getRealPath("/WEB-INF/glasanje-rezultati.txt");

        Map<Integer,Integer> votes = new HashMap<>();
        try(Scanner sc = new Scanner(new File(fileName))) {
            while(sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] parts = line.split("\t");
                votes.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            }
        }

        int id = Integer.parseInt(request.getParameter("id"));

        if(votes.containsKey(id)) {
            votes.put(id, votes.get(id) + 1);
        } else {
            votes.put(id, 1);
        }

        try(OutputStream os = new FileOutputStream(fileName)) {
            String s = "";
            for(Map.Entry<Integer,Integer> entry : votes.entrySet()) {
                s += entry.getKey() + "\t" + entry.getValue() + "\n";
            }
            os.write(s.getBytes());
        }catch (IOException e) {
            e.printStackTrace();
        }

        response.sendRedirect(request.getContextPath() + "/glasanje-rezultati");

    }
}
