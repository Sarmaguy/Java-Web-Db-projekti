package servlets;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

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

@WebServlet("/glasanjeExcel")
public class GlasanjeExcelServlet extends HttpServlet{
    @Override
    protected void  doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

        HSSFWorkbook hwb = new HSSFWorkbook();
        HSSFSheet sheet = hwb.createSheet("Rezultati glasanja");
        int rownum = 0;
        for(String[] vote : votes) {
            HSSFRow row = sheet.createRow(rownum++);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(vote[0]);
            cell = row.createCell(1);
            cell.setCellValue(vote[1]);
        }
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=\"rezultati.xls\"");
        hwb.write(response.getOutputStream());
    }
}
