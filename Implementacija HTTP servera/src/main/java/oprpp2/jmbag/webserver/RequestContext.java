package oprpp2.jmbag.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class RequestContext {
    private OutputStream outputStream;
    private Charset charset;
    public String encoding = "UTF-8";
    public int statusCode = 200;
    public String statusText = "OK";
    public String mimeType = "text/html";
    public Long contentLength = null;
    private Map<String, String> parameters;
    private Map<String, String> temporaryParameters;
    private Map<String, String> persistantParameters;
    private List<RCCookie> outputCookies;
    private boolean headerGenerated = false;
    private IDispatcher dispatcher;

    public void setContentLength(int length) {
        this.contentLength = (long) length;
    }

    public static class RCCookie{
        private String name;
        private String value;
        private String domain;
        private String path;
        private Integer maxAge;
        private boolean httpOnlyCookie = false;


        public static void main(String[] args) throws IOException {
            demo1("primjer1.txt", "ISO-8859-2");
            demo1("primjer2.txt", "UTF-8");
            demo2("primjer3.txt", "UTF-8");
        }
        private static void demo1(String filePath, String encoding) throws IOException {
            OutputStream os = Files.newOutputStream(Paths.get(filePath));
            RequestContext rc = new RequestContext(os, new HashMap<String, String>(),
                    new HashMap<String, String>(),
                    new ArrayList<RequestContext.RCCookie>());
            rc.setEncoding(encoding);
            rc.setMimeType("text/plain");
            rc.setStatusCode(205);
            rc.setStatusText("Idemo dalje");
// Only at this point will header be created and written...
            rc.write("Čevapčići i Šiščevapčići.");
            os.close();
        }
        private static void demo2(String filePath, String encoding) throws IOException {
            OutputStream os = Files.newOutputStream(Paths.get(filePath));
            RequestContext rc = new RequestContext(os, new HashMap<String, String>(),
                    new HashMap<String, String>(),
                    new ArrayList<RequestContext.RCCookie>());
            rc.setEncoding(encoding);
            rc.setMimeType("text/plain");
            rc.setStatusCode(205);
            rc.setStatusText("Idemo dalje");
            rc.addRCCookie(
                    new RCCookie("korisnik", "perica", 3600, "127.0.0.1", "/"));
            rc.addRCCookie(new RCCookie("zgrada", "B4", null, null, "/"));
// Only at this point will header be created and written...
            rc.write("Čevapčići i Šiščevapčići.");
            os.close();
        }

        public RCCookie(String name, String value, Integer maxAge, String domain, String path){
            this.name = name;
            this.value = value;
            this.maxAge = maxAge;
            this.domain = domain;
            this.path = path;
        }

        public RCCookie(String name, String value, Integer maxAge, String domain, String path, boolean httpOnlyCookie){
            this(name, value, maxAge, domain, path);
            this.httpOnlyCookie = httpOnlyCookie;
        }


        public String getName(){
            return name;
        }

        public String getValue(){
            return value;
        }

        public String getDomain(){
            return domain;
        }

        public String getPath(){
            return path;
        }

        public Integer getMaxAge(){
            return maxAge;
        }


    }

    private void addRCCookie(RCCookie rcCookie) {
        outputCookies.add(rcCookie);
    }

    void setStatusText(String s) {
        this.statusText = s;
    }

    void setStatusCode(int i) {
        this.statusCode = i;
    }

    public IDispatcher getDispatcher() {
        return dispatcher;
    }

    public void setMimeType(String s) {
        this.mimeType = s;
    }

    private void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public RequestContext(OutputStream outputStream, Map<String, String> parameters,
                          Map<String,String> persistantParameters, List<RCCookie> outputCookies){
        if (outputStream == null) throw new IllegalArgumentException("OutputStream mustn't be null!");
        this.outputStream = outputStream;

        if (parameters == null) this.parameters = new HashMap<>();
        else this.parameters = parameters;

        if (persistantParameters == null) this.persistantParameters = new HashMap<>();
        else this.persistantParameters = persistantParameters;

        if (outputCookies == null) this.outputCookies = new ArrayList<>();
        else this.outputCookies = outputCookies;
    }

    public RequestContext(OutputStream outputStream, Map<String, String> parameters,
                          Map<String,String> persistantParameters, List<RCCookie> outputCookies, IDispatcher dispatcher, Map<String, String> temporaryParameters){
        this(outputStream, parameters, persistantParameters, outputCookies);
        this.dispatcher = dispatcher;
        this.temporaryParameters = temporaryParameters;
    }

    public String getParameter(String name){
        return parameters.get(name);
    }

    public Set<String> getParameterNames(){
        return parameters.keySet();
    }

    public String getPersistentParameter(String name){
        return persistantParameters.get(name);
    }

    public Set<String> getPersistentParameterNames(){
        return persistantParameters.keySet();
    }

    public void setPersistentParameter(String name, String value){
        persistantParameters.put(name, value);
    }

    public void removePersistentParameter(String name){
        persistantParameters.remove(name);
    }

    public String getTemporaryParameter(String name){
        return temporaryParameters.get(name);
    }

    public Set<String> getTemporaryParameterNames(){
        return temporaryParameters.keySet();
    }

    public String getSessionID(){
        return "";
    }

    public void setTemporaryParameter(String name, String value){
        temporaryParameters.put(name, value);
    }

    public void removeTemporaryParameter(String name){
        temporaryParameters.remove(name);
    }


    public RequestContext write(byte[] data, int offset, int len) throws IOException{
        if (!headerGenerated){
            String s = "HTTP/1.1 "+statusCode+" "+statusText+"\r\n";

            if (mimeType.startsWith("text/")) s += "Content-Type: " + mimeType + "; charset=" + encoding + "\r\n";
            else s += "Content-Type: "+mimeType+"\r\n";

            if (contentLength != null) s += "Content-Length: " + contentLength + "\r\n";

            for(RCCookie cookie : outputCookies){
                s += "Set-Cookie: " + cookie.getName() + "=\"" + cookie.getValue() + "\"";

                if (cookie.getDomain() != null) s += "; Domain=" + cookie.getDomain();
                if (cookie.getPath() != null) s += "; Path=" + cookie.getPath();
                if (cookie.getMaxAge() != null) s += "; Max-Age=" + cookie.getMaxAge();

                s += "\r\n";
            }

            s += "\r\n";

            outputStream.write(s.getBytes(StandardCharsets.ISO_8859_1));
            headerGenerated = true;
        }

        outputStream.write(data, offset, len);
        return this;

    }
    public RequestContext write(byte[] data) throws IOException {
        return write(data, 0, data.length);
    }

    public RequestContext write(String text) throws IOException{
        charset = Charset.forName(encoding);
        return write(text.getBytes(charset));
    }





}
