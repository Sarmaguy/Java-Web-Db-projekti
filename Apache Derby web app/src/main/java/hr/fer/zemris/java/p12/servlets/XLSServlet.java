package hr.fer.zemris.java.p12.servlets;

import hr.fer.zemris.java.p12.dao.DAOProvider;
import hr.fer.zemris.java.p12.model.PollOption;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/servlets/XLS")
public class XLSServlet extends HttpServlet{
    @Override
    protected void  doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long id = Long.parseLong(request.getParameter("pollID"));
        List<PollOption> pollOptions = DAOProvider.getDao().getPollOptions(id);

        HSSFWorkbook hwb = new HSSFWorkbook();
        HSSFSheet sheet = hwb.createSheet("Rezultati glasanja");
        int rownum = 0;
        for(PollOption pollOption : pollOptions) {
            HSSFRow row = sheet.createRow(rownum++);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(pollOption.getOptionTitle());
            cell = row.createCell(1);
            cell.setCellValue(pollOption.getVotesCount());
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=\"rezultati.xls\"");
        hwb.write(response.getOutputStream());
    }
}
