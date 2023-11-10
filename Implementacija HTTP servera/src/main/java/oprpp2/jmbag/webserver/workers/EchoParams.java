package oprpp2.jmbag.webserver.workers;

import oprpp2.jmbag.webserver.IWebWorker;
import oprpp2.jmbag.webserver.RequestContext;

public class EchoParams implements IWebWorker {
    @Override
    public void processRequest(RequestContext context) throws Exception {
        context.setMimeType("text/html");
        context.write("<html><body><table>");
        context.write("<tr><th>Parameter name</th><th>Parameter value</th></tr>");
        for (String name : context.getParameterNames()) {
            context.write("<tr><td>" + name + "</td><td>" + context.getParameter(name) + "</td></tr>");
        }
        context.write("</table></body></html>");
    }
}
