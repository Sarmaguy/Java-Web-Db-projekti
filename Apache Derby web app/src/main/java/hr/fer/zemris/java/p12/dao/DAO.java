package hr.fer.zemris.java.p12.dao;

import hr.fer.zemris.java.p12.model.Poll;
import hr.fer.zemris.java.p12.model.PollOption;

import java.util.List;

/**
 * Sučelje prema podsustavu za perzistenciju podataka.
 * 
 * @author marcupic
 *
 */
public interface DAO {

        public List<Poll> getPolls() throws DAOException;

        public List<PollOption> getPollOptions(long pollID) throws DAOException;
        public PollOption getPollOption(long id) throws DAOException;
        public void incrementVote(long id) throws DAOException;
        public Poll getPoll(long id) throws DAOException;

}