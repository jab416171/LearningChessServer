import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neumont.learningChess.api.ChessGameState;
import edu.neumont.learningChess.engine.GameStateInfo;
import edu.neumont.learningChess.engine.LearningEngine;
import edu.neumont.learningChess.json.Jsonizer;

public class GameStateInfoServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletContext context = request.getSession().getServletContext();
		String responseString = null;
		try {
			responseString = getGameStateInfo(context, request) + "";
		} catch (Throwable t) {
			if (MainServlet.LOG_ERROR) {
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

	private String getGameStateInfo(ServletContext context, HttpServletRequest request) throws IOException {
		String jsonString = MainServlet.getPostBody(request.getReader());
		ChessGameState chessGameState = Jsonizer.dejsonize(jsonString, ChessGameState.class);
		LearningEngine learningEngine = (LearningEngine) context.getAttribute(MainServlet.LEARNING_ENGINE);

		GameStateInfo gameStateInfo = learningEngine.getGameStateInfo(chessGameState);

		return Jsonizer.jsonize(gameStateInfo);
	}
}
