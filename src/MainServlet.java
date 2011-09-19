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
		MessageDigest md;
		byte[] md5hash = new byte[32];
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes("iso-8859-1"), 0, str.length());
			md5hash = md.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return convertToHex(md5hash);

	}

	private static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
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
