import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.neumont.learningChess.engine.LearningEngine;
import edu.neumont.learningChess.engine.SerializedChessGameState;

public class LearningEngineServletContextListener implements ServletContextListener {

	private static final String LEARNING_ENGINE = "LearningEngine";
	public static LearningEngine learningEngine = null;
	private static final long RECORD_SIZE = SerializedChessGameState.getRecordSize();

	@Override
	public void contextDestroyed(ServletContextEvent contextEvent) {

		ServletContext context = contextEvent.getServletContext();
		// LearningEngine learningEngine = (LearningEngine) context.getAttribute(LEARNING_ENGINE);
		// learningEngine.close();
		context.setAttribute(LEARNING_ENGINE, null);
	}

	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {
		ServletContext context = contextEvent.getServletContext();
		context.log("Initializing the context");
		if (learningEngine == null) {
			String fileName = context.getInitParameter("FILE_NAME");
			String cacheSize = context.getInitParameter("CACHE_SIZE");
			String performInit = context.getInitParameter("performInitialization");
			
			context.log("fileName: " + fileName);
			context.log("cacheSize: " + cacheSize);
			context.log("LearningEngine: " + LEARNING_ENGINE);
			context.log("record size: " + RECORD_SIZE);

			try {
				learningEngine = LearningEngine.open(fileName);

			} catch (Throwable e) {
				LearningEngine.create(fileName, Integer.parseInt(cacheSize));
				learningEngine = LearningEngine.open(fileName);
			}
			if(Integer.parseInt(performInit) != 0) {
				learningEngine.initializeFiles();
			}
			
			context.log("Learning engine is set up!");

			context.setAttribute(LEARNING_ENGINE, learningEngine);
		}
	}

}
