package franciliens.servlets;

import static franciliens.data.OfyService.ofy;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.Crypt;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import franciliens.data.Trajet;
import franciliens.data.User;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {

	private Document squelette;
	private boolean firstGetDone; // a-t-on déjà fait un get ?
	private boolean isErrorMessageDisplayed; // le message d'erreur est-il présent ?
	private String url= "http://localhost:8888/";

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
			String mdp= Crypt.crypt(pass, "xx");
			if (u==null || !u.getPassword().equals(mdp)){

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
				
				req.getSession(true).setAttribute("login", u.getLogin());
				
				/*
				 * Charger l'id du trajet enregistré en session s'il existe
				 */
				List<Trajet> t = ofy().load().type(Trajet.class).filter("pseudoUsager", u.getLogin()).list();
				if (t.size()>0) {
					Trajet trajet = t.get(0);
					req.getSession().setAttribute("trajetEnregistre", trajet.getId());
				}
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
