package oprpp2.jmbag.webserver;

import hr.fer.oprpp1.custom.scripting.exec.SmartScriptEngine;
import hr.fer.oprpp1.custom.scripting.parser.SmartScriptParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SmartHttpServer {
    private String address;
    private String domainName;
    private int port;
    private int workerThreads;
    private int sessionTimeout;
    private Map<String,String> mimeTypes = new HashMap<String, String>();
    private ServerThread serverThread;
    private ExecutorService threadPool;
    private Path documentRoot;
    private RequestContext rc = null;
    private Map<String,IWebWorker> workersMap = new HashMap<String, IWebWorker>();
    private Map<String, SessionMapEntry> sessions =
            new HashMap<String, SmartHttpServer.SessionMapEntry>();
    private Random sessionRandom = new Random();


    public SmartHttpServer(String configFileName) {
        Properties serverProperties = new Properties();
        Properties mimeProperties = new Properties();
        Properties workersProperties = new Properties();

        try(InputStream stream = Files.newInputStream(Paths.get(configFileName))){
            serverProperties.load(stream);
            mimeProperties.load(Files.newInputStream(Paths.get(serverProperties.getProperty("server.mimeConfig"))));
            workersProperties.load(Files.newInputStream(Paths.get(serverProperties.getProperty("server.workers"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        address = serverProperties.getProperty("server.address");
        domainName = serverProperties.getProperty("server.domainName");
        port = Integer.parseInt(serverProperties.getProperty("server.port"));
        workerThreads = Integer.parseInt(serverProperties.getProperty("server.workerThreads"));
        sessionTimeout = Integer.parseInt(serverProperties.getProperty("session.timeout"));
        documentRoot = Paths.get(serverProperties.getProperty("server.documentRoot"));

        for (String s : mimeProperties.stringPropertyNames()) {   //double check
            mimeTypes.put(s, mimeProperties.getProperty(s));
        }

        for (String path : workersProperties.stringPropertyNames()) {
            String fqcn = workersProperties.getProperty(path);
            try {
                Class<?> referenceToClass = this.getClass().getClassLoader().loadClass(fqcn);
                Object newObject = referenceToClass.getDeclaredConstructor().newInstance();
                IWebWorker iww = (IWebWorker) newObject;
                workersMap.put(path, iww);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

    }

    protected synchronized void start() {
// … start server thread if not already running …
// … init threadpool by Executors.newFixedThreadPool(...); …
        threadPool = Executors.newFixedThreadPool(workerThreads);
        serverThread = new ServerThread();
        serverThread.start();
    }
    protected synchronized void stop() {
// … signal server thread to stop running …
// … shutdown threadpool …
        serverThread.interrupt();
        threadPool.shutdown();
    }
    protected class ServerThread extends Thread {
        @Override
        public void run() {
// given in pesudo-code:
// open serverSocket on specified port
// while(true) {
// Socket client = serverSocket.accept();
// ClientWorker cw = new ClientWorker(client);
// submit cw to threadpool for execution
// }
            ServerSocket serverSocket = null;
            try{
                serverSocket = new ServerSocket();
                serverSocket.bind(new InetSocketAddress(address, port));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println("Server started" + serverSocket.getInetAddress() + ":" + serverSocket.getLocalPort());

            while(true) {
                Socket client = null;
                try {
                    client = serverSocket.accept();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                threadPool.submit(new ClientWorker(client));
            }
        }
    }
    private class ClientWorker implements Runnable,IDispatcher {
        private Socket csocket;
        private InputStream istream;
        private OutputStream ostream;
        private String version;
        private String method;
        private String host;
        private Map<String,String> params = new HashMap<String, String>();
        private Map<String,String> tempParams = new HashMap<String, String>();
        private Map<String,String> permPrams = new HashMap<String, String>();
        private List<RequestContext.RCCookie> outputCookies =
                new ArrayList<RequestContext.RCCookie>();
        private String SID;
        private
        RequestContext context = null;
        public ClientWorker(Socket csocket) {
            super();
            this.csocket = csocket;
        }
        @Override
        public void run() {
            try{
                // obtain input stream from socket
                istream = csocket.getInputStream();
                // obtain output stream from socket
                ostream = csocket.getOutputStream();
                
                // Then read complete request header from your client in separate method...
                List<String> request = extractHeaders(istream);

                // If header is invalid (less then a line at least) return response status 400
                if(request.size() < 1) {
                    response(400, "Bad request", new byte[0], "text/plain;charset=UTF-8");
                    return;
                }
                String firstLine = request.get(0);
                // Extract (method, requestedPath, version) from firstLine
                // if method not GET or version not HTTP/1.0 or HTTP/1.1 return response status 400
                String parts[] = firstLine.split(" ");
                if (parts.length != 3) {
                    response(400, "Bad request", new byte[0], "text/plain;charset=UTF-8");
                    return;
                }
                method = parts[0].toUpperCase();
                if(!method.equals("GET")) {
                    response(400, "Bad request", new byte[0], "text/plain;charset=UTF-8");
                    return;
                }
                version = parts[2].toUpperCase();
                if(!version.equals("HTTP/1.0") && !version.equals("HTTP/1.1")) {
                    response(400, "Bad request", new byte[0], "text/plain;charset=UTF-8");
                    return;
                }

                // Go through headers, and if there is header “Host: xxx”, assign host property
                // to trimmed value after “Host:”; else, set it to server’s domainName
                // If xxx is of form some-name:number, just remember “some-name”-part
                for(String s : request) {
                    if(s.toUpperCase().startsWith("HOST:")) {
                        host = s.substring(6).trim();
                        if(host.contains(":")) {
                            host = host.substring(0, host.indexOf(":"));
                        }
                    }
                }
                if (host == null) {
                    host = domainName;
                }

                //session management
                for(String s : request) {
                    if(s.toUpperCase().startsWith("COOKIE:")) {
                        String cookie = s.substring(8).trim();
                        String[] parts2 = cookie.split(";");
                        for(String s2 : parts2) {
                            String[] parts3 = s2.split("=");
                            if(parts3[0].toUpperCase().trim().equals("SID")) {
                                SID = parts3[1].trim();
                                SID = SID.replace("\"", "");
                            }
                        }
                    }
                }
                SessionMapEntry entry2 = sessions.get(SID);
                // if session with SID does not exist, create new sessionć
                /**
                System.out.println("before SID: " + SID);
                System.out.println("Entry2: " + entry2);
                System.out.println("sessions: ");
                sessions.forEach((k,v) -> System.out.println(k + " " + v.validUntil));**/
                if(entry2 == null) {
                    generateSession();
                    System.out.println("Heeey, I don't know you... here take your session");
                }else if (entry2.validUntil < Instant.now().getEpochSecond()) {
                    // if session with SID exists, but is not valid, create new session
                    sessions.remove(SID);
                    generateSession();
                    System.out.println("Heeey, your session is not valid... here take your new session");
                }else {
                    // if session with SID exists and is valid, update its validUntil
                    System.out.println("Heeey, I know you... here refresh your session");
                    entry2.validUntil = Instant.now().getEpochSecond() + sessionTimeout;
                    permPrams = entry2.map;
                }
                //System.out.println("afer SID: " + SID);

                outputCookies.add(new RequestContext.RCCookie("SID", SID, null, host, "/", true));


                String path; String paramString;
                // (path, paramString) = split requestedPath to path and parameterString
                // parseParameters(paramString); ==> your method to fill map parameters

                String[] pathParts = parts[1].split("\\?");
                path = pathParts[0];
                if (pathParts.length > 1) {
                    paramString = pathParts[1];

                    String[] entries = paramString.split("&");
                    for (String entry : entries) {
                        String[] parts2 = entry.split("=");
                        if (parts2.length == 2) {
                            params.put(parts2[0], parts2[1]);
                        }
                        else {
                            params.put(parts2[0], null);
                        }
                    }
                }

                // requestedPath = resolve path with respect to documentRoot
                // if requestedPath is not below documentRoot, return response status 403 forbidden
                // check if requestedPath exists, is file and is readable; if not, return status 404
                // else extract file extension
                // find in mimeTypes map appropriate mimeType for current file extension
                // (you filled that map during the construction of SmartHttpServer from mime.properties)
                // if no mime type found, assume application/octet-stream
                // create a rc = new RequestContext(...); set mime-type; set status to 200
                // If you want, you can modify RequestContext to allow you to add additional headers
                // so that you can add “Content-Length: 12345” if you know that file has 12345 bytes
                // open file, read its content and write it to rc (that will generate header and send
                // file bytes to client)
                internalDispatchRequest(path, true);

            }catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }finally {
                try {
                    csocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void generateSession() {
            SessionMapEntry entry2;
            SID = generateSID();
            entry2 = new SessionMapEntry();
            entry2.sid = SID;
            entry2.validUntil = Instant.now().getEpochSecond() + sessionTimeout;
            entry2.host = host;
            permPrams = entry2.map;
            sessions.put(SID, entry2);
        }

        private String generateSID() {
            String sid = "";
            for(int i = 0; i < 20; i++) {
                sid += (char)('A' + sessionRandom.nextInt(26));
            }
            return sid;
        }

        // Jednostavan automat koji ÄŤita zaglavlje HTTP zahtjeva...
        private static Optional<byte[]> readRequest(InputStream is) throws IOException {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int state = 0;
            l:		while(true) {
                int b = is.read();
                if(b==-1) {
                    if(bos.size()!=0) {
                        throw new IOException("Incomplete header received.");
                    }
                    return Optional.empty();
                }
                if(b!=13) {
                    bos.write(b);
                }
                switch(state) {
                    case 0:
                        if(b==13) { state=1; } else if(b==10) state=4;
                        break;
                    case 1:
                        if(b==10) { state=2; } else state=0;
                        break;
                    case 2:
                        if(b==13) { state=3; } else state=0;
                        break;
                    case 3:
                        if(b==10) { break l; } else state=0;
                        break;
                    case 4:
                        if(b==10) { break l; } else state=0;
                        break;
                }
            }
            return Optional.of(bos.toByteArray());
        }

        private static List<String> extractHeaders(InputStream istream) throws IOException {
            List<String> headers = new ArrayList<String>();
            Optional<byte[]> request = readRequest(istream);

            if(!request.isEmpty()) {
                String requestHeader = new String(request.get(), StandardCharsets.US_ASCII);

                String currentLine = null;
                for(String s : requestHeader.split("\n")) {
                    if(s.isEmpty()) break;
                    char c = s.charAt(0);
                    if(c==9 || c==32) {
                        currentLine += s;
                    } else {
                        if(currentLine != null) {
                            headers.add(currentLine);
                        }
                        currentLine = s;
                    }
                }
                if(!currentLine.isEmpty()) {
                    headers.add(currentLine);
                }
            }

            return headers;
        }

        public void response(int statusCode, String statusText, byte[] data, String mimeType) throws IOException {
            if (context == null){
                context = new RequestContext(ostream, params, permPrams, outputCookies);
            }
            context.setStatusCode(statusCode);
            context.setStatusText(statusText);
            context.setMimeType(mimeType);
            context.setContentLength(data.length);
            context.write(data);
        }

        @Override
        public void dispatchRequest(String urlPath) throws Exception {
            internalDispatchRequest(urlPath, false);
        }

        private void internalDispatchRequest(String urlPath, boolean b) throws Exception {
            // requestedPath = resolve path with respect to documentRoot
            // if requestedPath is not below documentRoot, return response status 403 forbidden

            Path requestedPath = documentRoot.resolve(urlPath.substring(1)).toAbsolutePath().normalize();
            if (!requestedPath.startsWith(documentRoot)) {
                response(403, "Forbidden", new byte[0], "text/plain;charset=UTF-8");
                return;
            }

            if (b && urlPath.startsWith("/private")){
                response(404, "Not Found", new byte[0], "text/plain;charset=UTF-8");
                return;
            }

            if(urlPath.startsWith("/ext/")){
                String fqcn = "oprpp2.jmbag.webserver.workers." + urlPath.substring(5);
                Class<?> referenceToClass = this.getClass().getClassLoader().loadClass(fqcn);
                Object newObject = referenceToClass.getDeclaredConstructor().newInstance();
                IWebWorker iww = (IWebWorker) newObject;
                if (context == null)
                    context = new RequestContext(ostream, params, permPrams, outputCookies,this, tempParams);
                context.setStatusCode(200);
                context.setStatusText("OK");
                iww.processRequest(context);
                return;
            }

            IWebWorker worker = workersMap.get(urlPath);
            if (worker != null) {
                if (context == null)
                    context = new RequestContext(ostream, params, permPrams, outputCookies,this, tempParams);
                context.setStatusCode(200);
                context.setStatusText("OK");
                worker.processRequest(context);
                return;
            }

            // check if requestedPath exists, is file and is readable; if not, return status 404
            if (!Files.exists(requestedPath) || !Files.isReadable(requestedPath) || !Files.isRegularFile(requestedPath)) {
                response(404, "Not Found", new byte[0], "text/plain;charset=UTF-8");
                return;
            }
            // else extract file extension
            // find in mimeTypes map appropriate mimeType for current file extension
            // (you filled that map during the construction of SmartHttpServer from mime.properties)
            // if no mime type found, assume application/octet-stream
            String extension = requestedPath.getFileName().toString();
            extension = extension.substring(extension.lastIndexOf(".") + 1).toLowerCase();

            String mimeType = mimeTypes.get(extension);

            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }



            // create a rc = new RequestContext(...); set mime-type; set status to 200
            // If you want, you can modify RequestContext to allow you to add additional headers
            // so that you can add “Content-Length: 12345” if you know that file has 12345 bytes
            // open file, read its content and write it to rc (that will generate header and send
            // file bytes to client)
            byte[] data = Files.readAllBytes(requestedPath);

            if(extension.equals("smscr")) {
                if (context == null){
                    context = new RequestContext(ostream, params, permPrams, outputCookies, this, tempParams);
                }
                context.setStatusCode(200);
                context.setStatusText("OK");
                //context.setMimeType(mimeType);

                String documentBody = new String(data, StandardCharsets.UTF_8);
                new SmartScriptEngine(new SmartScriptParser(documentBody).getDocumentNode(), context).execute();
                return;


            }

            response(200, "OK", data, mimeType);



        }
    }


    public static void main(String[] args) throws Exception {
        /**
        if (args.length != 1) {
            System.out.println("Invalid number of arguments");
            return;
        }**/

        SmartHttpServer server = new SmartHttpServer("./config/server.properties");
        server.start();
    }

    private static class SessionMapEntry {
        String sid;
        String host;
        long validUntil;
        Map<String,String> map = new ConcurrentHashMap<>();
    }
}

