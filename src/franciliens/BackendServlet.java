package franciliens;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import javax.servlet.ServletException;

@SuppressWarnings("serial")
public class BackendServlet extends HttpServlet {
	//	Exemple à laisser pour l'instant :)
	private static final Logger _logger = Logger.getLogger(BackendServlet.class.getName());
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		try {
			_logger.info("Cronhas been executed");

			//Put your logic here
			//BEGIN
			//END
		}
		catch (Exception ex) {
			//Log any exceptions in your Cron Job
		}
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
}

//	@Override
//	public void init() throws ServletException {
//		// TODO Auto-generated method stub
//		super.init();
//	}
//
//	@Override
//	protected void service(HttpServletRequest req, HttpServletResponse resp)
//			throws ServletException, IOException {
//		doGet(req, resp);
//		
//	}
//	
//	@Override
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
//			throws ServletException, IOException {
// TODO Auto-generated method stub
//		resp.setContentType("application/vnd.sncf.transilien.od.depart+xml;vers=1.0");
//		req.setAttribute("Accept", "application/vnd.sncf.transilien.od.depart+xml;vers=1");
//		try{
//		URL url = new URL("http://api.transilien.com");
//		URLConnection con = url.openConnection();
//		//on utilise l'url comme un input non?
//		con.setDoInput(true);
//		//HttpURLConnection httpConn = (HttpURLConnection)con;
//		BufferedReader in = new BufferedReader(new InputStreamReader(
//                con.getInputStream()));
//        String inputLine;
//        while ((inputLine = in.readLine()) != null)
//            System.out.println(inputLine);
//        in.close();
//        
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	
//	}
//	
//	@Override
//	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
//			throws ServletException, IOException {
//		// TODO Auto-generated method stub
//		super.doPost(req, resp);
//	}

//
//}
