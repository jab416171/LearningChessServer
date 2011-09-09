import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neumont.learningChess.api.ChessGameState;
import edu.neumont.learningChess.api.MoveHistory;
import edu.neumont.learningChess.engine.GameStateInfo;
import edu.neumont.learningChess.engine.LearningEngine;
import edu.neumont.learningChess.json.Jsonizer;
import edu.neumont.learningChess.model.Move;

/**
 * Servlet implementation class Test
 */
public class TestServlet extends HttpServlet {

	private static final boolean LOG_VERBOSE = false;
	private static final boolean LOG_ERROR = true;

	private static final String LEARNING_ENGINE = "LearningEngine";
	private static final long serialVersionUID = 1L;

	private enum Paths {
		analyzehistory, getmove, /*
								 * postdatatofacebook, getusestats,
								 * gettopscores,
								 */ping, getgamestateinfo, noValue;

		public static Paths toPath(String path) {
			try {
				return valueOf(path);
			} catch (Exception e) {
				return noValue;
			}
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String realPath = getServletContext().getRealPath("index.jsp");
		Scanner fileScanner = new Scanner(new FileInputStream(new File(realPath)));
		StringBuilder stringBuilder = new StringBuilder();
		if (LOG_VERBOSE) {
			request.getSession().getServletContext().log("In do get");
		}
		while (fileScanner.hasNextLine()) {
			stringBuilder.append(fileScanner.nextLine() + "\r\n");
		}
		response.getWriter().println(stringBuilder.toString());

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// need to get the url out of the request
		StringBuffer requestURLBuffer = request.getRequestURL();
		String requestURL = requestURLBuffer.toString();
		String urlString = requestURL.substring(requestURL.lastIndexOf("/") + 1);
		ServletContext context = request.getSession().getServletContext();
		String responseString = null;
		if (LOG_VERBOSE) {
			context.log("Method: " + urlString);
		}
		try {
			// switch on the url
			switch (Paths.toPath(urlString)) {
				case getmove :
					responseString = getMoveFromLearningEngine(context, request);
					break;

				case analyzehistory :
					responseString = "" + analyzeGameHistory(context, request);
					break;

				case getgamestateinfo :
					responseString = getGameStateInfo(context, request);
					break;

				case ping :
					responseString = "you have pinged the server's servlet";
					break;

				case noValue :
					responseString = "Unrecognized Action";
					break;

				default :
					responseString = "Unrecognized Action";
					break;
			}
		} catch (Throwable t) {
			if (LOG_ERROR) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				PrintStream printStream = new PrintStream(bos);
				t.printStackTrace(printStream);
				context.log(bos.toString());
			}
		}
		if (LOG_VERBOSE) {
			context.log("ResponseString: " + responseString);
			context.log("Server Activity: Finished DoPost");
		}
		PrintWriter writer = response.getWriter();
		writer.println(responseString);
		writer.flush();

	}

	private String getGameStateInfo(ServletContext context, HttpServletRequest request) throws IOException {
		String jsonString = getPostBody(request.getReader());
		ChessGameState chessGameState = Jsonizer.dejsonize(jsonString, ChessGameState.class);
		LearningEngine learningEngine = (LearningEngine) context.getAttribute(LEARNING_ENGINE);

		GameStateInfo gameStateInfo = learningEngine.getGameStateInfo(chessGameState);

		return Jsonizer.jsonize(gameStateInfo);
	}

	private int analyzeGameHistory(ServletContext context, HttpServletRequest request) throws IOException {
		if (LOG_VERBOSE) {
			context.log("Analyzing history...");
		}
		String jsonString = getPostBody(request.getReader());
		if (LOG_VERBOSE) {
			context.log("Request: " + jsonString);
		}
		MoveHistory gameStateHistory = Jsonizer.dejsonize(jsonString, MoveHistory.class);

		LearningEngine learningEngine = (LearningEngine) context.getAttribute(LEARNING_ENGINE);
		return learningEngine.analyzeGameHistory(gameStateHistory);
	}

	private String getMoveFromLearningEngine(ServletContext context, HttpServletRequest request) throws IOException {
		String jsonString = getPostBody(request.getReader());

		if (LOG_VERBOSE) {
			context.log("getmove request: " + jsonString);
		}
		ChessGameState gameState = Jsonizer.dejsonize(jsonString, ChessGameState.class);
		if (LOG_VERBOSE) {
			context.log("state created");
			context.log("got game state");
		}
		LearningEngine learningEngine = (LearningEngine) context.getAttribute(LEARNING_ENGINE);
		if (LOG_VERBOSE) {
			context.log("got engine");
		}
		Move gameMove = learningEngine.getMove(gameState);
		if (LOG_VERBOSE) {
			context.log("got move");
		}

		return Jsonizer.jsonize(gameMove);
	}

	private String getPostBody(BufferedReader reader) throws IOException {

		StringBuilder builder = new StringBuilder();
		for (;;) {
			String line = reader.readLine();
			if (line == null)
				break;
			builder.append(line);
		}
		return builder.toString();
	}

}
