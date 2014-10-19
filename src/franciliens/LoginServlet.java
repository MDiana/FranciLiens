package franciliens;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.googlecode.objectify.ObjectifyService;
import static franciliens.OfyService.ofy;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {
	
	private Document squelette;
	private boolean firstGetDone; // a-t-on déjà fait un get ?
	private boolean isErrorMessageDisplayed; // le message d'erreur est-il présent ?
	private User u;
	
	public void init() {
		try {
			squelette = Jsoup.connect("http://localhost:8888/index.html").get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		firstGetDone=false;
		isErrorMessageDisplayed=false; 
	}
	
	public void destroy() {
		
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.service(req, resp);
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		/*
		 * TODO Vérifier que l'utilisateur n'est pas déjà loggé
		 */
		
		if (!firstGetDone) {
			
			/*
			 * Construction de la page login
			 */
			Element contentElem = squelette.getElementById("content");
			Document login = Jsoup.connect("http://localhost:8888/login.html").get();
			Element loginElem = login.getElementById("login");
			contentElem.appendChild(loginElem);
			firstGetDone=true;
		}
		
		/*
		 * Envoyer le r�sultat
		 */
		resp.setContentType("text/html; charset=UTF-8");
		resp.setStatus(400);
		PrintWriter out = resp.getWriter();
		out.println(squelette.html());
		
		out.flush();
		out.close();
		
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		/*
		 * Envoyer le r�sultat
		 */
		resp.setContentType("text/html; charset=UTF-8");
		resp.setStatus(501);
		
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		/*
		 * Envoyer le r�sultat
		 */
		resp.setContentType("text/html; charset=UTF-8");
		resp.setStatus(400);
		PrintWriter out = resp.getWriter();
		out.println(squelette.head());
		
		out.flush();
		out.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		/*
		 * R�cup�rer le mail et le mot de passe
		 */
		
		String mail = req.getParameter("mail");
		String pass = req.getParameter("password");
		
//		resp.setContentType("text/plain; charset=UTF-8");
//		resp.setStatus(400);
//		PrintWriter out = resp.getWriter();
//		out.println(mail+" / "+pass);
//		
//		out.flush();
//		out.close();
		
		/*
		 * TODO Vérifier si l'utilisateur existe dans la BDD
		 */
		// cr�er un user bidon juste pour testser
		User u1= new User("fiori","fiori","fiori@hotmail.com", "lule");
		ofy().save().entity(u1).now();
		
		
		u = ofy().load().type(User.class).id(mail).now();
		//if (u==null){
			// L'utilisateur n'existe pas
		//}
		/*
		 * TODO L'utilisateur existe : Vérifier si le mot de passe est correct
		 */
		
		/*
		 * TODO Le mot de passe est correct : Logger l'utilisateur
		 * et le rediriger vers l'accueil
		 */
		//if (u.password.equals(pass)){
			
		//}
		// /!\ Retirer le message d'erreur s'il est présent
		if (isErrorMessageDisplayed) {
			Element errorMessage = squelette.getElementById("errorMessage");
			if (errorMessage!=null) {
				errorMessage.remove();
//				Element form = squelette.getElementById("login");
				Element form = squelette.select("div.loginform").first();
				form.attr("height", "14em");
				form.attr("padding-top", "1em");
			}
//			isErrorMessageDisplayed=false;
			doGet(req, resp);
		}
		
		
		/*
		 * TODO Le mot de passe est incorrect ou l'utilisateur n'existe pas : Rediriger vers la fenêtre de login avec
		 * le message "Adresse mail / Mot de passe incorrect(s)"
		 */
		
		// /!\ Ajouter le message à la partie login s'il n'est pas déjà présent
		
		if (!isErrorMessageDisplayed) {
			Element form = squelette.getElementById("login");
			form.children().first().before(""
					+ "<p id=\"errorMessage\" class=\"errorMessage\">Adresse mail / Mot de passe incorrect(s)</p>");
			isErrorMessageDisplayed=true;

			form = squelette.select("div.loginform").first();
			form.attr("height", "14em");
			form.attr("padding-top", "1em");
			doGet(req, resp);
		}
		
		
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		
		/*
		 * Envoyer le résultat
		 */
		resp.setContentType("text/html; charset=UTF-8");
		resp.setStatus(501);
	}
	
	
}
