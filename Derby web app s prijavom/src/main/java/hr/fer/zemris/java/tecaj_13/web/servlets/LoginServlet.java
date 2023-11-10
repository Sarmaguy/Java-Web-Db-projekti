package hr.fer.zemris.java.tecaj_13.web.servlets;

import hr.fer.zemris.java.tecaj_13.dao.DAOProvider;
import hr.fer.zemris.java.tecaj_13.model.BlogUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@WebServlet("/servleti/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nick = req.getParameter("nick");
        String password = req.getParameter("password");

        BlogUser user = DAOProvider.getDAO().getUser(nick);
        String newpassword="";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA256");
            digest.update(password.getBytes());
            newpassword = new String(digest.digest(), "UTF-8");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        if(user == null || !user.getPasswordHash().equals(newpassword)) {
            req.setAttribute("error", "Invalid username or password!");
            List<BlogUser> users = DAOProvider.getDAO().getAllUsers();

            req.setAttribute("users", users);
            req.getRequestDispatcher("/WEB-INF/pages/home.jsp").forward(req, resp);
            return;
        }

        req.getSession().setAttribute("current.user.id", user.getId());
        req.getSession().setAttribute("current.user.fn", user.getFirstName());
        req.getSession().setAttribute("current.user.ln", user.getLastName());
        req.getSession().setAttribute("current.user.nick", user.getNick());

        resp.sendRedirect(req.getContextPath() + "/servleti/main");

    }
}
