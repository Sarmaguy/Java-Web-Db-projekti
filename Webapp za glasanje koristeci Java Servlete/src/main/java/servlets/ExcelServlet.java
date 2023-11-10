package servlets;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/powers"})
public class ExcelServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int a,b,n;

        a = Integer.parseInt(request.getParameter("a"));
        b = Integer.parseInt(request.getParameter("b"));
        n = Integer.parseInt(request.getParameter("n"));

        if (a < -100 || a > 100 || b < -100 || b > 100 || n < 1 || n > 6) {

            request.getRequestDispatcher("/pages/error.jsp").forward(request, response);

        }

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=table.xls");

        HSSFWorkbook workbook = new HSSFWorkbook();
        for (int i = 0; i < n; i++) {
            HSSFSheet sheet =  workbook.createSheet("Sheet" + i);

            short index = 1;
            for (int j = a; j <= b; j++) {
                HSSFRow row = sheet.createRow(index++);
                row.createCell(0).setCellValue(j);
                row.createCell(1).setCellValue(Math.pow(j, i + 1));
            }
        }
        workbook.write(response.getOutputStream());



    }
}
