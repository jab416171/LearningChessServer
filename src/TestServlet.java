import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neumont.learningChess.engine.GameStateHistory;
import edu.neumont.learningChess.engine.LearningEngine;

/**
 * Servlet implementation class Test
 */
public class TestServlet extends HttpServlet {
	
	private static final String LEARNING_ENGINE = "LearningEngine";
	private static final long serialVersionUID = 1L;
	
	private enum Paths
	{
		analyzehistory, getmove, postdatatofacebook, getusestats, gettopscores, ping, noValue;
		
		public static Paths toPath(String path) {
			try
			{
				return valueOf(path);
			} catch (Exception e)
			{
				return noValue;
			}
		}
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		writer.println("get repsonse");
		writer.flush();
		
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		
		// need to get the url our of the request
		String urlString = request.getServletPath().substring(1);
		ServletContext context = request.getSession().getServletContext();
		String responseString = null;
		
		try
		{
			// switch on the url
			switch (Paths.toPath(urlString))
			{
				case getmove:
					responseString = getMoveFromLearningCenter(context, request);
					break;
				
				case analyzehistory:
					//TODO get mehtod from Greg
					responseString = "done";
					evaluatePlayedGame(context, request);
					break;
				
				case gettopscores:
					double[] temp = getTopScores((Integer) context.getAttribute("Scores"));
					JSONArray jsonArray = new JSONArray();
					for (double d : temp)
					{
						jsonArray.put(d);
					}
					
					responseString = jsonArray.toString();
					break;
				
				case getusestats:
					responseString = getUserStats((Integer) context.getAttribute("UserId")).toString();
					break;
				
				case postdatatofacebook:
					responseString = "done";
					postDataToFacebook( request.getReader());
					break;
					
				case ping:
					responseString = "you have pinged the server's servlet";
					break;
					
				case noValue:
					responseString = "Unrecognized Action";
					break;
				
				default:
					responseString = "Unrecognized Action";
					break;
			}
		} catch (Exception e)
		{
			context.log("POST Exception: " + e.getMessage());
			context.log(e.getStackTrace().toString());
		}
		
		context.log("ResponseString: " + responseString);
		context.log("Server Activity: Finished Method");
		
		PrintWriter writer = response.getWriter();
		writer.println(responseString);
		writer.flush();
		
	}
	
	private void evaluatePlayedGame(ServletContext context, HttpServletRequest request) throws Exception {
		
		String jsonString;
		jsonString = getPostBody(request.getReader());
		context.log("history request: "+jsonString);
		
		//TODO need to get method from Greg
		List<Move> jsonState = (List<Move>) JSONFactory.createMoveHistoryFromJSON(jsonString);
		
		
		GameStateHistory gameStateHistory = new GameStateHistory(jsonState);// = json.getGameState();
		
		LearningEngine learningEngine = (LearningEngine) context.getAttribute(LEARNING_ENGINE);
		learningEngine.analyzeGameHistory(gameStateHistory);
		
		learningEngine.close();
		
	}
	
	private String getMoveFromLearningCenter(ServletContext context, HttpServletRequest request) throws IOException {
		String jsonString = getPostBody(request.getReader());
	
		context.log("getmove request: "+jsonString);
		JSONState jsonState = JSONFactory.createJSONStateFromJSON(jsonString);
		context.log("state created");
		
		GameState gameState = jsonState.getGameState();
		context.log("got game state");
		LearningEngine learningEngine = (LearningEngine) context.getAttribute(LEARNING_ENGINE);
		context.log("got engine");
		Move gameMove = learningEngine.getMove(gameState);
		context.log("got move");
		
		return JSONFactory.createJSONFromMove(gameMove);
	}
	
	private String getPostBody(BufferedReader reader) throws IOException {
		
		StringBuilder builder = new StringBuilder();
		for (;;)
		{
			String line = reader.readLine();
			if (line == null) break;
			builder.append(line);
		}
		return builder.toString();
	}
	
	private void postDataToFacebook(BufferedReader reader) throws JSONException, SQLException, IOException {
		
		String jsonString = getPostBody(reader);
		JSONObject json = new JSONObject(jsonString);
		UserDAO userDao = new UserDAO();
		userDao.SetScore(json);
	}
	
	private JSONObject getUserStats(int userId) throws SQLException, JSONException {
		UserDAO data = new UserDAO();
		UserInfo u = data.getUser(userId);
		JSONObject stats = new JSONObject();
		stats.put("Time", u.getTime());
		stats.put("TimesBeaten", u.getTimesBeaten());
		stats.put("LastBeaten", u.getLastBeaten());
		stats.put("UserId", u.getUserId());
		return stats;
		
	}
	
	private double[] getTopScores(int numberofscores) throws SQLException {
		UserDAO data = new UserDAO();
		UserInfo[] users = data.getTopUsers(numberofscores);
		double[] scores = new double[numberofscores];
		int count = 0;
		for (UserInfo userInfo : users)
		{
			scores[count] = userInfo.getTime();
			count++;
		}
		return scores;
	}
}
