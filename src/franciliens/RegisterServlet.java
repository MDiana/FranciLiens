package franciliens;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.googlecode.objectify.ObjectifyService;

import static franciliens.OfyService.ofy;


@SuppressWarnings("serial")
public class RegisterServlet extends HttpServlet {

	private Document squelette;
	private boolean firstGetDone; // a-t-on déjà fait un get ?
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
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		/*
		 * Vérifier que l'utilisateur n'est pas déjà loggé
		 */
		boolean isSessionNew = VerifSession.isSessionNew(req);
		if (! isSessionNew) {
			resp.sendRedirect("/accueil");
		} else {
			/*
			 * Ne créer la page que si on accède à la page pour la première fois
			 */
			if (!firstGetDone) {

				/*
				 * Construction de la page register
				 */
				Element contentElem = squelette.getElementById("content");
				Document register = Jsoup.connect(url+"register.html").get();
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
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		/*
		 * Récupérer les différentes infos
		 */

		String pseudo = StringEscapeUtils.escapeHtml4(req.getParameter("login"));
		String mail = StringEscapeUtils.escapeHtml4(req.getParameter("mail"));
		String pass = StringEscapeUtils.escapeHtml4(req.getParameter("password"));
		int age=-1;
		try {
			age= Integer.parseInt(StringEscapeUtils.escapeHtml4(req.getParameter("age")));
		} catch (Exception e) {
			// Ne rien faire : ça sera géré après
		}

		boolean infosOk = true;

		/*
		 * TODO Vérifier si l'email choisi est déjà utilisé
		 */

		if (ofy().load().type(User.class).id(mail).now() != null) {

			/*
			 * TODO Mail déjà utilisé : Message d'erreur "Un compte existe
			 * déjà à cette adresse"
			 */

			infosOk=false;

		}
		/*
		 * TODO Vérifier si le pseudo est déjà utilisé
		 * (indépendamment de la casse)
		 */

		if (ofy().load().type(User.class).filter("login ==", pseudo).list().size() > 0) {

			/*
			 * TODO Pseudo existant : Message d'erreur "Pseudo déjà utilisé"
			 */

			infosOk=false;

		} 

		/*
		 * TODO Vérifier si l'âge est valide
		 */

		if (age < 18 && age > 120) {

			/*
			 * TODO Âge invalide : Message d'erreur "Âge invalide"
			 */

			infosOk=false;

		}

		/*
		 * TODO Âge valide : Vérifier si le mot de passe a bien
		 * au moins 8 caractères
		 */

		if (pass.length() < 8) {

			/*
			 * TODO Mot de passe trop court : afficher un message
			 * d'erreur 
			 */

			infosOk=false;

		}

		if (infosOk) {

			/*
			 * TODO Tout va bien : Stocker dans le Datastore et
			 * rediriger vers l'accueil en loggant automatiquement
			 * l'utilisateur
			 */

			User newUser = new User(pseudo, mail, pass);
			ofy().save().entity(newUser).now();
			HttpSession session = req.getSession(true);
			session.setAttribute("login", pseudo);
			resp.sendRedirect("/accueil");

		} else {

			/*
			 * TODO Rester sur la page Register
			 * et afficher les messages d'erreur
			 */
			
			doGet(req, resp);
		}
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPut(req, resp);
	}

}
