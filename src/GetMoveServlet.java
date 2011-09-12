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
import edu.neumont.learningChess.engine.LearningEngine;
import edu.neumont.learningChess.json.Jsonizer;
import edu.neumont.learningChess.model.Move;

public class GetMoveServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		ServletContext context = request.getSession().getServletContext();
		String responseString = null;
		try {
			responseString = getMove(context, request) + "";
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

	private String getMove(ServletContext context, HttpServletRequest request) throws IOException {
		String jsonString = MainServlet.getPostBody(request.getReader());

		if (MainServlet.LOG_VERBOSE) {
			context.log("getmove request: " + jsonString);
		}
		ChessGameState gameState = Jsonizer.dejsonize(jsonString, ChessGameState.class);
		if (MainServlet.LOG_VERBOSE) {
			context.log("state created");
			context.log("got game state");
		}
		LearningEngine learningEngine = (LearningEngine) context.getAttribute(MainServlet.LEARNING_ENGINE);
		if (MainServlet.LOG_VERBOSE) {
			context.log("got engine");
		}
		Move gameMove = learningEngine.getMove(gameState);
		if (MainServlet.LOG_VERBOSE) {
			context.log("got move");
		}

		return Jsonizer.jsonize(gameMove);
	}
}
