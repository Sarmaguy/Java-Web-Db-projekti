package hr.fer.zemris.java.tecaj_13.web.servlets;

import hr.fer.zemris.java.tecaj_13.dao.DAO;
import hr.fer.zemris.java.tecaj_13.dao.DAOProvider;
import hr.fer.zemris.java.tecaj_13.model.BlogComment;
import hr.fer.zemris.java.tecaj_13.model.BlogEntry;
import hr.fer.zemris.java.tecaj_13.model.BlogUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@WebServlet("/servleti/author/*")
public class AuthorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        req.setCharacterEncoding("UTF-8");
        if (pathInfo.startsWith("/")) {
            pathInfo = pathInfo.substring(1);
        }

        String[] pathParts = pathInfo.split("/");
        DAO dao = DAOProvider.getDAO();

        boolean isAuthor = pathParts[0].equals((String) req.getSession().getAttribute("current.user.nick"));
        req.setAttribute("isAuthor", isAuthor);

        if (pathParts.length == 1){
            String nick = pathParts[0];
            BlogUser user = dao.getUser(nick);
            List<BlogEntry> entries = user.getEntries();

            String loggedInNick = (String) req.getSession().getAttribute("current.user.nick");
            req.setAttribute("user", user);
            req.setAttribute("entries", entries);
            req.setAttribute(("isAuthor"), isAuthor);
            req.getRequestDispatcher("/WEB-INF/pages/author.jsp").forward(req, resp);
        }

        if(pathParts.length == 2 && pathParts[1].equals("new")){
            if(!isAuthor){
                forbiddenAccess(req, resp);
                return;
            }
            String nick = pathParts[0];
            req.setAttribute("nick", nick);
            req.getRequestDispatcher("/WEB-INF/pages/newEntry.jsp").forward(req, resp);
            return;
        }
        if (pathParts.length == 2){
            String nick = pathParts[0];
            Long id = Long.parseLong(pathParts[1]);
            BlogEntry entry = dao.getBlogEntry(id);
            req.setAttribute("entry", entry);
            req.setAttribute("nick", nick);
            req.setAttribute("comments", entry.getComments());
            req.getRequestDispatcher("/WEB-INF/pages/entry.jsp").forward(req, resp);
            return;
        }
        if (pathParts.length == 3 && pathParts[2].equals("edit")){
            if(!isAuthor){
                forbiddenAccess(req, resp);
                return;
            }
            String nick = pathParts[0];
            Long id = Long.parseLong(pathParts[1]);
            BlogEntry entry = dao.getBlogEntry(id);
            req.setAttribute("entry", entry);
            req.setAttribute("nick", nick);
            req.getRequestDispatcher("/WEB-INF/pages/editEntry.jsp").forward(req, resp);
            return;
        }

    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        req.setCharacterEncoding("UTF-8");
        String pathInfo = req.getPathInfo();
        if (pathInfo.startsWith("/")) {
            pathInfo = pathInfo.substring(1);
        }

        String[] pathParts = pathInfo.split("/");
        DAO dao = DAOProvider.getDAO();
        boolean isAuthor = pathParts[0].equals(req.getSession().getAttribute("current.user.nick"));
        req.setAttribute("isAuthor", isAuthor);

        if(pathParts.length == 2 && pathParts[1].equals("create")){
            String nick = pathParts[0];
            String title = req.getParameter("title");
            String text = req.getParameter("text");

            if (!isAuthor){
                forbiddenAccess(req, resp);
                return;
            }

            if(title.equals("") || text.equals("")){
                req.setAttribute("error", "Title and text must not be empty!");
                req.setAttribute("nick", nick);
                req.setAttribute("title", title);
                req.setAttribute("text", text);
                req.getRequestDispatcher("/WEB-INF/pages/newEntry.jsp").forward(req, resp);
                return;
            }


            BlogEntry entry = new BlogEntry();
            entry.setCreator(DAOProvider.getDAO().getUser(nick));
            entry.setTitle(title);
            entry.setText(text);
            entry.setCreatedAt(new Date());
            entry.setLastModifiedAt(new Date());
            dao.createEntry(entry);
            resp.sendRedirect(resp.encodeRedirectURL(req.getContextPath() + "/servleti/author/" + nick));
            return;
        }

        if (pathParts.length == 3 && pathParts[2].equals("addComment")){
            String nick = pathParts[0];
            Long id = Long.parseLong(pathParts[1]);
            String email = req.getParameter("email");
            String message = req.getParameter("message");
            BlogEntry entry = dao.getBlogEntry(id);


            if(email.equals("") || message.equals("")){
                req.setAttribute("error", "Email and message must not be empty!");
                req.setAttribute("nick", nick);
                req.setAttribute("entry", entry);
                req.setAttribute("comments", entry.getComments());
                req.getRequestDispatcher("/WEB-INF/pages/entry.jsp").forward(req, resp);
                return;
            }

            BlogComment comment = new BlogComment();
            comment.setBlogEntry(entry);
            comment.setMessage(message);
            comment.setUsersEMail(email);
            comment.setPostedOn(new Date());
            dao.createComment(comment);
            resp.sendRedirect(resp.encodeRedirectURL(req.getContextPath() + "/servleti/author/" + nick + "/" + id));
            return;
        }
        if(pathParts.length == 3 && pathParts[2].equals("edit")){
            String nick = pathParts[0];
            Long id = Long.parseLong(pathParts[1]);
            String title = req.getParameter("title");
            String text = req.getParameter("text");

            if (!isAuthor){
                forbiddenAccess(req, resp);
                return;
            }

            if(title.equals("") || text.equals("")){
                req.setAttribute("error", "Title and text must not be empty!");
                req.setAttribute("nick", nick);
                req.setAttribute("title", title);
                req.setAttribute("text", text);
                req.getRequestDispatcher("/WEB-INF/pages/editEntry.jsp").forward(req, resp);
                return;
            }

            BlogEntry entry = dao.getBlogEntry(id);
            entry.setTitle(title);
            entry.setText(text);
            entry.setLastModifiedAt(new Date());
            dao.updateEntry(entry);
            resp.sendRedirect(resp.encodeRedirectURL(req.getContextPath() + "/servleti/author/" + nick + "/" + id));
        }
    }

    private void forbiddenAccess(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        req.getRequestDispatcher("/WEB-INF/pages/err.jsp").forward(req, resp);
    }

}

