package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

@WebServlet(urlPatterns = "/glasanje-rezultati")
public class GlasanjeRezultatiServlet extends HttpServlet {

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

        votes.sort((o1, o2) -> Integer.parseInt(o2[1]) - Integer.parseInt(o1[1]));

        List<String[]> winners = new ArrayList<>();

        //add copies of winners, biggest value and all with same value
        String[] winner =  new String[2];
        winner[0] = votes.get(0)[0];
        winner[1] = votes.get(0)[1];
        winners.add(winner);
        for(int i = 1; i < votes.size(); i++) {
            if(Integer.parseInt(votes.get(i)[1]) == Integer.parseInt(votes.get(i-1)[1])) {
                winner =  new String[2];
                winner[0] = votes.get(i)[0];
                winner[1] = votes.get(i)[1];
                winners.add(winner);
            } else {
                break;
            }
        }
        //find every winner in data and swap
        for(String[] win : winners) {
            for(String[] dat : data) {
                if(win[0].equals(dat[0])) {
                    win[0] = dat[1];
                    win[1] = dat[2];
                }
            }
        }
        //replace ids with names in votes
        for(String[] vote : votes) {
            for(String[] dat : data) {
                if(vote[0].equals(dat[0])) {
                    vote[0] = dat[1];
                }
            }
        }


        request.setAttribute("votes", votes);
        request.setAttribute("winners", winners);
        request.getRequestDispatcher("/pages/glasanjeRez.jsp").forward(request, response);
    }

}
