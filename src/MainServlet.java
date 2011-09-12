import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServlet;

public class MainServlet extends HttpServlet {

	public static final boolean LOG_VERBOSE = false;

	public static final String LEARNING_ENGINE = "LearningEngine";

	public static final boolean LOG_ERROR = true;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static String getPostBody(BufferedReader reader) throws IOException {

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
