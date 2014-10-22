package franciliens;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.commons.lang3.StringEscapeUtils;
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
	private String url= "http://localhost:8888/";

	static {
		ObjectifyService.register(User.class); // Fait connaître votre classe-entité à Objectify
	}

	@Override
	public void init() {

		try {
			squelette = Jsoup.connect(url+"index.html").get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		firstGetDone=false;
		isErrorMessageDisplayed=false; 

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		/*
		 * Vérifier que l'utilisateur n'est pas déjà loggé
		 */
		boolean isSessionNew = VerifSession.isSessionNew(req);
		if (!isSessionNew) {
			resp.sendRedirect("/accueil");
		} else {
			/*
			 * Ne créer la page que si on accède à la page pour la première fois
			 */
			if (!firstGetDone) {

				/*
				 * Construction de la page login
				 */
				Element contentElem = squelette.getElementById("content");
				Document login = Jsoup.connect(url+"login.html").get();
				Element loginElem = login.getElementById("login");
				contentElem.appendChild(loginElem);
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
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		/*
		 * Envoyer le résultat
		 */
		resp.setContentType("text/html; charset=UTF-8");
		resp.setStatus(501);

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		/*
		 * Vérifier que l'utilisateur n'est pas déjà loggé
		 */

		boolean isSessionNew = VerifSession.isSessionNew(req);
		if (!isSessionNew) {
			resp.sendRedirect("/accueil");
		} else {
			/*
			 * Récupérer le mail et le mot de passe
			 */

			String mail = StringEscapeUtils.escapeHtml4(req.getParameter("mail"));
			String pass = StringEscapeUtils.escapeHtml4(req.getParameter("password"));

			/*
			 * Vérifier si l'utilisateur existe dans la BDD
			 * et si le mdp saisi est correct
			 */


			User u = ofy().load().type(User.class).id(mail).now();
			if (u==null || !u.password.equals(pass)){

				/*
				 * Le mot de passe est incorrect ou l'utilisateur n'existe pas : 
				 * Rediriger vers la fenêtre de login avec
				 * le message "Adresse mail / Mot de passe incorrect(s)"
				 */

				// /!\ Ajouter le message à la partie login s'il n'est pas déjà présent

				if (!isErrorMessageDisplayed) {
					Element form = squelette.getElementById("login");
					form.children().first().before(""
							+ "<p id=\"errorMessage\" class=\"errorMessage\">"
							+ "Adresse mail / Mot de passe incorrect(s)</p>");
					isErrorMessageDisplayed=true;
				}

				doGet(req, resp);

			} else {
				/*
				 * L'utilisateur existe et le mot de passe est correct :
				 * Logger l'utilisateur et le rediriger vers l'accueil
				 */
				
				if (isErrorMessageDisplayed) {
					squelette.getElementById("errorMessage").remove();
					isErrorMessageDisplayed=false;
				}
				req.getSession(true); // créer une session
				req.setAttribute("login", u.login);
				resp.sendRedirect("/accueil");
			}
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
