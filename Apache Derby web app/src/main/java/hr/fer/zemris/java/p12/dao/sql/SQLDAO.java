package hr.fer.zemris.java.p12.dao.sql;

import hr.fer.zemris.java.p12.dao.DAO;
import hr.fer.zemris.java.p12.dao.DAOException;
import hr.fer.zemris.java.p12.model.Poll;
import hr.fer.zemris.java.p12.model.PollOption;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Ovo je implementacija podsustava DAO uporabom tehnologije SQL. Ova
 * konkretna implementacija očekuje da joj veza stoji na raspolaganju
 * preko {@link SQLConnectionProvider} razreda, što znači da bi netko
 * prije no što izvođenje dođe do ove točke to trebao tamo postaviti.
 * U web-aplikacijama tipično rješenje je konfigurirati jedan filter 
 * koji će presresti pozive servleta i prije toga ovdje ubaciti jednu
 * vezu iz connection-poola, a po zavrsetku obrade je maknuti.
 *  
 * @author marcupic
 */
public class SQLDAO implements DAO {

    @Override
    public List<Poll> getPolls() throws DAOException {
        Connection con = SQLConnectionProvider.getConnection();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM Polls");
            ResultSet rs = statement.executeQuery();
            List<Poll> polls = new ArrayList<>();

            while (rs.next()){
                polls.add(new Poll(rs.getLong(1), rs.getString(2), rs.getString(3)));
            }

            if (polls.size() == 0)
                throw new DAOException("No polls found.");

            return polls;
        } catch (SQLException e) {
            throw new DAOException("Error while getting polls.", e);
        }
    }

    @Override
    public List<PollOption> getPollOptions(long pollID) throws DAOException {
        Connection con = SQLConnectionProvider.getConnection();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM PollsOptions WHERE pollID = ?");
            statement.setLong(1, pollID);
            ResultSet rs = statement.executeQuery();
            List<PollOption> pollOptions = new ArrayList<>();

            while (rs.next()){
                pollOptions.add(new PollOption(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getLong(4), rs.getLong(5)));
            }

            if (pollOptions.size() == 0)
                throw new DAOException("No poll options found.");

            return pollOptions;
        } catch (SQLException e) {
            throw new DAOException("Error while getting poll options.", e);
        }
    }

    @Override
    public PollOption getPollOption(long id) throws DAOException {
        Connection con = SQLConnectionProvider.getConnection();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM PollsOptions WHERE id = ?");
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();

            if (!rs.next())
                throw new DAOException("No poll option found.");

            return new PollOption(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getLong(4), rs.getLong(5));
        } catch (SQLException e) {
            throw new DAOException("Error while getting poll option.", e);
        }
    }

    @Override
    public void incrementVote(long id) throws DAOException {
        Connection con = SQLConnectionProvider.getConnection();
        try {
            PreparedStatement statement = con.prepareStatement("UPDATE PollsOptions SET votesCount = votesCount + 1 WHERE id = ?");
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error while incrementing vote.", e);
        }
    }

    @Override
    public Poll getPoll(long id) throws DAOException {
        Connection con = SQLConnectionProvider.getConnection();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM Polls WHERE id = ?");
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();

            if (!rs.next())
                throw new DAOException("No poll found.");

            return new Poll(rs.getLong(1), rs.getString(2), rs.getString(3));
        } catch (SQLException e) {
            throw new DAOException("Error while getting poll.", e);
        }
    }
}