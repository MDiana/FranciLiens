package franciliens;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;

@SuppressWarnings("serial")
public class BackendServlet extends HttpServlet {
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
//	protected void service(HttpServletRequest arg0, HttpServletResponse arg1)
//			throws ServletException, IOException {
//		// TODO Auto-generated method stub
//		super.service(arg0, arg1);
//	}
//	
//	@Override
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
//			throws ServletException, IOException {
//		// TODO Auto-generated method stub
//		super.doGet(req, resp);
//	}
//	
//	@Override
//	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
//			throws ServletException, IOException {
//		// TODO Auto-generated method stub
//		super.doPost(req, resp);
//	}

//}
