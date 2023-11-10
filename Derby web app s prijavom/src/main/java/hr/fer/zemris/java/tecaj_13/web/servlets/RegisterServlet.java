package hr.fer.zemris.java.tecaj_13.web.servlets;

import hr.fer.zemris.java.tecaj_13.dao.DAO;
import hr.fer.zemris.java.tecaj_13.dao.DAOProvider;
import hr.fer.zemris.java.tecaj_13.model.BlogUser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/servleti/register")
public class RegisterServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nick = req.getParameter("nick");
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if(Objects.equals(nick, "") || Objects.equals(firstName, "") || Objects.equals(lastName, "") || Objects.equals(email, "") || Objects.equals(password, "")) {
        	req.setAttribute("error", "All fields are required!");
            req.setAttribute("nick", nick);
            req.setAttribute("firstName", firstName);
            req.setAttribute("lastName", lastName);
            req.setAttribute("email", email);
        	req.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(req, resp);
        	return;
        }

        DAO dao = DAOProvider.getDAO();
        BlogUser user = dao.getUser(nick);

        if(user != null) {
        	req.setAttribute("error", "User with this nick already exists!");
            req.setAttribute("firstName", firstName);
            req.setAttribute("lastName", lastName);
            req.setAttribute("email", email);
        	req.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(req, resp);
        	return;
        }

        user = new BlogUser();
        user.setNick(nick);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);


        try {
            MessageDigest digest = MessageDigest.getInstance("SHA256");
            digest.update(password.getBytes());
            String passwordHash = new String(digest.digest(), StandardCharsets.UTF_8);
            user.setPasswordHash(passwordHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        dao.createUser(user);


        req.getSession().setAttribute("current.user.id", user.getId());
        req.getSession().setAttribute("current.user.fn", user.getFirstName());
        req.getSession().setAttribute("current.user.ln", user.getLastName());
        req.getSession().setAttribute("current.user.nick", user.getNick());

        resp.sendRedirect(req.getContextPath() + "/servleti/main");
    }

}
