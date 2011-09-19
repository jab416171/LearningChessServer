import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neumont.learningChess.json.Jsonizer;
import edu.neumont.learningChess.model.User;

public class LoginServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletContext context = request.getSession().getServletContext();
		String responseString = null;
		try {
			responseString = login(context, request) + "";
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

	private String login(ServletContext context, HttpServletRequest request) throws Exception{
		String jsonString = MainServlet.getPostBody(request.getReader());
		if (MainServlet.LOG_VERBOSE) {
			context.log("Request: " + jsonString);
		}
		User user = Jsonizer.dejsonize(jsonString, User.class);
		Class.forName(MainServlet.dbClass);
		Connection con = DriverManager.getConnection(MainServlet.dbUrl,"root","Ch3ssCh3ss");
		
		PreparedStatement stmt = con.prepareStatement("select * from user where username = ? and password = ?");
		stmt.setString(1, user.getUsername());
		stmt.setString(2, user.getPassword());
		ResultSet resultSet = stmt.executeQuery();
		boolean userExists = resultSet.next();
		return Jsonizer.jsonize(userExists);
	}
}
