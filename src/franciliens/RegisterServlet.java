package franciliens;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@SuppressWarnings("serial")
public class RegisterServlet extends HttpServlet {
	
	private Document squelette;
	private boolean firstGetDone; // a-t-on déjà fait un get ?

	@Override
	public void init() {
		try {
			squelette = Jsoup.connect("http://localhost:8888/index.html").get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		firstGetDone=false;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		/*
		 * TODO Vérifier que l'utilisateur n'est pas déjà loggé
		 */
//		if ("userloggé") {
//		resp.sendRedirect("/accueil");
//	}
		
		/*
		 * Ne créer la page que si on accède à la page pour la première fois
		 */
		if (!firstGetDone) {
			
			/*
			 * Construction de la page register
			 */
			Element contentElem = squelette.getElementById("content");
			Document register = Jsoup.connect("http://localhost:8888/register.html").get();
			Element registerElem = register.getElementById("register");
			contentElem.appendChild(registerElem);
			firstGetDone=true;
		}
		
		/*
		 * Envoyer le résultat
		 */
		resp.setContentType("text/html; charset=UTF-8");
		resp.setStatus(400);
		PrintWriter out = resp.getWriter();
		out.println(squelette.html());
		
		out.flush();
		out.close();
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doHead(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPut(req, resp);
	}

}
