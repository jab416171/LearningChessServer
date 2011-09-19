import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neumont.learningChess.api.MoveHistory;
import edu.neumont.learningChess.engine.LearningEngine;
import edu.neumont.learningChess.json.Jsonizer;

public class AnalyzeGameHistoryServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String dbUrl = "jdbc:mysql://chess.neumont.edu/learningchess";
	private static final String dbClass = "com.mysql.jdbc.Driver";

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		ServletContext context = request.getSession().getServletContext();
		String responseString = null;
		try {
			responseString = analyzeGameHistory(context, request) + "";
		} catch (Throwable t) {
			if (MainServlet.LOG_ERROR) {
				context.log("Exception in analyze history!");
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				PrintStream printStream = new PrintStream(bos);
				t.printStackTrace(printStream);
				context.log(bos.toString());
			}
		}
		if (MainServlet.LOG_VERBOSE) {
			context.log("ResponseString: " + responseString);
			context.log("Server Activity: Finished DoPost");
		}
		PrintWriter writer = response.getWriter();
		writer.println(responseString);
		writer.flush();
	}

	private int analyzeGameHistory(ServletContext context, HttpServletRequest request) throws IOException {
		if (MainServlet.LOG_VERBOSE) {
			context.log("Analyzing history...");
		}
		String jsonString = MainServlet.getPostBody(request.getReader());
		if (MainServlet.LOG_VERBOSE) {
			context.log("Request: " + jsonString);
		}
		MoveHistory gameStateHistory = Jsonizer.dejsonize(jsonString, MoveHistory.class);

		LearningEngine learningEngine = (LearningEngine) context.getAttribute(MainServlet.LEARNING_ENGINE);
		return learningEngine.analyzeGameHistory(gameStateHistory);
	}

	private void writeToDb(Calendar datePlayed, String whiteName, String blackName, int winnerType, int moveCount) {
		try {
			Class.forName(dbClass);
			Connection con = DriverManager.getConnection(dbUrl);
			PreparedStatement stmt = con.prepareStatement("insert into History values(?, ?, ?, ?, ?");
			stmt.setDate(1, new java.sql.Date(datePlayed.getTimeInMillis()));
			stmt.setString(2, whiteName);
			stmt.setString(3, blackName);
			stmt.setInt(4, winnerType);
			stmt.setInt(5, moveCount);
			stmt.executeUpdate();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
