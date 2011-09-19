import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServlet;

public class MainServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final boolean LOG_VERBOSE = false;

	public static final String LEARNING_ENGINE = "LearningEngine";

	public static final boolean LOG_ERROR = true;

	public static final String dbUrl = "jdbc:mysql://chess.neumont.edu:3306/learningchess";
	public static final String dbClass = "com.mysql.jdbc.Driver";

	public static String MD5(String str) {
		String s = null;
		try {
			byte[] bytesOfMessage = str.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] thedigest = md.digest(bytesOfMessage);
			s = new String(thedigest, "UTF-8");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s;
	}

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
