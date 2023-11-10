package oprpp2.jmbag.webserver.workers;

import oprpp2.jmbag.webserver.IWebWorker;
import oprpp2.jmbag.webserver.RequestContext;

public class HomeWorker implements IWebWorker {
    @Override
    public void processRequest(RequestContext context) throws Exception {
        String color = context.getPersistentParameter("bgcolor");
        if (color == null) {
            color = "7F7F7F";
            context.setTemporaryParameter("background", color);
        }
        else {
            context.setTemporaryParameter("background", color);
        }

        context.getDispatcher().dispatchRequest("/private/pages/home.smscr");
    }
}
