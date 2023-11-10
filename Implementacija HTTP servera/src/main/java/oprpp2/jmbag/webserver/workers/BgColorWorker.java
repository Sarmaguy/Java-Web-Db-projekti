package oprpp2.jmbag.webserver.workers;

import oprpp2.jmbag.webserver.IWebWorker;
import oprpp2.jmbag.webserver.RequestContext;

public class BgColorWorker implements IWebWorker {
    @Override
    public void processRequest(RequestContext context) throws Exception {
        String color = context.getParameter("bgcolor");

        context.write("<html><body>");
        context.write("<a href=\"/index2.html\">Vrati me nazad</a></br>");

        //check if color is valid hex color
        if (color != null && color.matches("^([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
            context.setPersistentParameter("bgcolor", color);
            context.write("Color is successfully updated.");
        } else {
            context.write("Color not updated.");
        }
    }
}
