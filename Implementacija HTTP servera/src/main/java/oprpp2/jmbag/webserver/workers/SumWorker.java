package oprpp2.jmbag.webserver.workers;

import oprpp2.jmbag.webserver.IWebWorker;
import oprpp2.jmbag.webserver.RequestContext;

public class SumWorker implements IWebWorker {
    @Override
    public void processRequest(RequestContext context) throws Exception {
        int a = 1;
        int b = 2;

        try {
            a = Integer.parseInt(context.getParameter("a"));
        } catch (NumberFormatException e) {
        }
        try {
            b = Integer.parseInt(context.getParameter("b"));
        } catch (NumberFormatException e) {
        }

        int ab = a + b;
        context.setMimeType("text/html");
        context.setTemporaryParameter("zbroj", String.valueOf(ab));
        context.setTemporaryParameter("varA", String.valueOf(a));
        context.setTemporaryParameter("varB", String.valueOf(b));
        context.setTemporaryParameter("imgName", ab % 2 == 0 ? "images/img1.jpg" : "images/img2.png");
        context.getDispatcher().dispatchRequest("/private/pages/calc.smscr");
    }
}
