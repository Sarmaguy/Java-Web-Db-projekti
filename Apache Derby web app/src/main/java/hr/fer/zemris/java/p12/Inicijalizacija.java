package hr.fer.zemris.java.p12;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import hr.fer.zemris.java.p12.model.Poll;
import hr.fer.zemris.java.p12.model.PollOption;

@WebListener
public class Inicijalizacija implements ServletContextListener {


	@Override
	public void contextInitialized(ServletContextEvent sce) {

		Path path = Path.of(sce.getServletContext().getRealPath("/WEB-INF/dbsettings.properties"));
		Properties properties = new Properties();
		try (InputStream is = Files.newInputStream(path)){
			properties.load(is);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String connectionURL = "jdbc:derby://"
				+ properties.getProperty("host") + ":"
				+ properties.getProperty("port") + "/"
				+ properties.getProperty("name") + ";user="
				+ properties.getProperty("user") + ";password="
				+ properties.getProperty("password");


		ComboPooledDataSource cpds = new ComboPooledDataSource();
		try {
			cpds.setDriverClass("org.apache.derby.client.ClientAutoloadedDriver");
		} catch (PropertyVetoException e1) {
			throw new RuntimeException("Pogreška prilikom inicijalizacije poola.", e1);
		}
		cpds.setJdbcUrl(connectionURL);

		sce.getServletContext().setAttribute("hr.fer.zemris.dbpool", cpds);

		checkDatabase(cpds);
	}



	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ComboPooledDataSource cpds = (ComboPooledDataSource)sce.getServletContext().getAttribute("hr.fer.zemris.dbpool");
		if(cpds!=null) {
			try {
				DataSources.destroy(cpds);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void checkDatabase(ComboPooledDataSource cpds) {
		try (Connection con = cpds.getConnection()) {
			DatabaseMetaData dbmd = con.getMetaData();
			ResultSet rs = dbmd.getTables(null, null, null, null);

			List<String> dataTables = new ArrayList<>();

			while (rs.next()) dataTables.add(rs.getString(3).toUpperCase());

			if (!dataTables.contains("POLLS")) {
				PreparedStatement statement = con.prepareStatement("CREATE TABLE Polls"
						+ "(id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
						+ " title VARCHAR(150) NOT NULL,"
						+ " message CLOB(2048) NOT NULL)"
				);

				statement.execute();
			}

			if (!dataTables.contains("POLLSOPTIONS")){
				PreparedStatement statement = con.prepareStatement("CREATE TABLE PollsOptions"
						+ "(id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
						+ " optionTitle VARCHAR(100) NOT NULL,"
						+ " optionLink VARCHAR(150) NOT NULL,"
						+ " pollID BIGINT,"
						+ " votesCount BIGINT,"
						+ " FOREIGN KEY (pollID) REFERENCES Polls(id))"
				);

				statement.execute();
			}

			PreparedStatement statement = con.prepareStatement("SELECT * FROM Polls");
			rs = statement.executeQuery();

			if (!rs.next()){
				for (Poll p : getStaticData()){
					PreparedStatement statement1 = con.prepareStatement("INSERT INTO Polls (title, message) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
					statement1.setString(1, p.getTitle());
					statement1.setString(2, p.getMessage());
					statement1.executeUpdate();

					ResultSet rset = statement1.getGeneratedKeys();

					rset.next();
					long pollID = rset.getLong(1);

					for (PollOption option : p.getPollOptions()){
						PreparedStatement statement2 = con.prepareStatement("INSERT INTO PollsOptions (optionTitle, optionLink, pollID, votesCount) VALUES (?, ?, ?, ?)");
						statement2.setString(1, option.getOptionTitle());
						statement2.setString(2, option.getOptionLink());
						statement2.setLong(3, pollID);
						statement2.setLong(4, option.getVotesCount());
						statement2.executeUpdate();
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<Poll> getStaticData(){
		List<Poll> polls = new ArrayList<>();

		polls.add(new Poll(1, "Glasanje za omiljeni bend", "Od sljedećih bendova, koji Vam je bend najdraži? Kliknite na link kako biste glasali!"));
		polls.add(new Poll(2, "Glasanje za omiljeni bend", "Od sljedećih bendova, koji Vam je bend najdraži? Kliknite na link kako biste glasali!"));

		List<PollOption> pollOptions = new ArrayList<>();

		pollOptions.add(new PollOption(1, "The Beatles", "https://www.youtube.com/watch?v=z9ypq6_5bsg", 1, 0));
		pollOptions.add(new PollOption(2, "The Platters", "https://www.youtube.com/watch?v=H2di83WAOhU", 1, 0));
		pollOptions.add(new PollOption(3,"The Beach Boys", "https://www.youtube.com/watch?v=2s4slliAtQU", 1, 0));
		pollOptions.add(new PollOption(4,"The Four Seasons", "https://www.youtube.com/watch?v=y8yvnqHmFds", 1, 0));

		polls.get(0).setPollOptions(pollOptions);

		pollOptions = new ArrayList<>();
		pollOptions.add(new PollOption(5,"The Marcels", "https://www.youtube.com/watch?v=qoi3TH59ZEs", 2, 0));
		pollOptions.add(new PollOption(6,"The Everly Brothers", "https://www.youtube.com/watch?v=tbU3zdAgiX8", 2, 0));
		pollOptions.add(new PollOption(7,"The Mamas And The Papas", "https://www.youtube.com/watch?v=N-aK6JnyFmk", 2, 0));

		polls.get(1).setPollOptions(pollOptions);

		return polls;
	}

}
