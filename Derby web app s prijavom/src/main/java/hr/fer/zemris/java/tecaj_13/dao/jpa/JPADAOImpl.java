package hr.fer.zemris.java.tecaj_13.dao.jpa;

import hr.fer.zemris.java.tecaj_13.dao.DAO;
import hr.fer.zemris.java.tecaj_13.dao.DAOException;
import hr.fer.zemris.java.tecaj_13.model.BlogComment;
import hr.fer.zemris.java.tecaj_13.model.BlogEntry;
import hr.fer.zemris.java.tecaj_13.model.BlogUser;

import java.util.List;

public class JPADAOImpl implements DAO {

	@Override
	public BlogEntry getBlogEntry(Long id) throws DAOException {
		BlogEntry blogEntry = JPAEMProvider.getEntityManager().find(BlogEntry.class, id);
		return blogEntry;
	}

	@Override
	public List<BlogUser> getAllUsers() {
		return JPAEMProvider.getEntityManager()
				.createNamedQuery("BlogUser.all", BlogUser.class)
				.getResultList();
	}

	@Override
	public BlogUser getUser(String nick) {
		List<BlogUser> users = JPAEMProvider.getEntityManager()
				.createNamedQuery("BlogUser.getUser", BlogUser.class)
				.setParameter("nick", nick)
				.getResultList();



		return users == null || users.isEmpty() ? null : users.get(0);
	}

	@Override
	public void createUser(BlogUser user) {
		JPAEMProvider.getEntityManager().persist(user);
	}

	@Override
	public void createEntry(BlogEntry entry) {
		JPAEMProvider.getEntityManager().persist(entry);
	}

	@Override
	public void createComment(BlogComment comment) {
		JPAEMProvider.getEntityManager().persist(comment);
	}

	@Override
	public void updateEntry(BlogEntry entry) {
		JPAEMProvider.getEntityManager().merge(entry);
	}

}