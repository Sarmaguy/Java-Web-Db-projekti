package oprpp2.jmbag.webserver;

public interface IWebWorker {
    public void processRequest(RequestContext context) throws Exception;
}
