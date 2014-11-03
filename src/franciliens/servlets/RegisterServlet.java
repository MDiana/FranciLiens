package franciliens.servlets;

import static franciliens.data.OfyService.ofy;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.Crypt;
import org.apache.commons.lang3.StringEscapeUtils;
//import org.apache.catalina.filters.CsrfPreventionFilter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import franciliens.data.User;

@SuppressWarnings("serial")
public class RegisterServlet extends HttpServlet {

	// localhost:8888
	//franci-liens.appspot.com
	private Document squelette;
	private boolean firstGetDone; // a-t-on déjà fait un get ?
	private String url= "http://localhost:8888/";
	private ArrayList<String> errorList;

	@Override
	public void init() {
		try {
			squelette = Jsoup.connect(url+"index.html").get();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		firstGetDone=false;
		errorList=new ArrayList<String>();
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

		boolean isSessionNew = VerifSession.isSessionNew(req);
		if (!isSessionNew) {

			resp.sendRedirect("/accueil");
			
		} else {

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

			if(pseudo == null){
				infosOk=false;
				String e = "<p id=\"errorMessage\" class=\"errorMessage\"> Veuillez entrer un pseudonyme </p>";
				errorList.add(e);
			}

			if(mail == null ){
				infosOk=false;
				String e = "<p id=\"errorMessage\" class=\"errorMessage\"> Veuillez remplir votre adresse mail. </p>";
				errorList.add(e);
			}

			/*
			 * Vérifier si l'email choisi est déjà utilisé
			 */

			if (mail != null && ofy().load().type(User.class).id(mail).now() != null) {

				/*
				 * Mail déjà utilisé : Message d'erreur "Un compte existe
				 * déjà à cette adresse"
				 */


				infosOk=false;
				String error = "<p id=\"errorMessage\" class=\"errorMessage\">Un compte existe déjà à cette adresse. </p>";
				errorList.add(error);
			}

			/*
			 * Vérifier si le pseudo est déjà utilisé
			 * (indépendamment de la casse)
			 */

			if (pseudo != null && ofy().load().type(User.class).filter("login ==", pseudo).list().size() > 0) {

				/*
				 * Pseudo existant : Message d'erreur "Pseudo déjà utilisé"
				 */
				infosOk=false;
				String e= "<p id=\"errorMessage\" class=\"errorMessage\">Le pseudonyme est utilisé par un autre utilisateur \" </p> ";
				errorList.add(e);
			} 

			/*
			 * Vérifier si l'âge est valide
			 */

			if (age < 18 && age > 120) {

				/*
				 * Âge invalide : Message d'erreur "Âge invalide"
				 */
				infosOk=false;
				String e= "<p id=\"errorMessage\" class=\"errorMessage\">Veuillez entrer votre âge réelle ! \" </p> ";
				errorList.add(e);
			}

			/*
			 * Âge valide : Vérifier si le mot de passe a bien
			 * au moins 8 caractères
			 */

			if (pass == null || pass.length() < 8) {

				/*
				 * Mot de passe trop court : afficher un message
				 * d'erreur 
				 */
				infosOk=false;
				String e= "<p id=\"errorMessage\" class=\"errorMessage\">Le mot de passe doit contenir au minimum 8 caracteres.  \" </p> ";
				errorList.add(e);
			}
			Element errorElem;
			while ((errorElem  = squelette.getElementById("errorMessage")) != null) {
				errorElem.remove();
			}
			if (infosOk) {

				/*
				 * Tout va bien : Stocker dans le Datastore et
				 * rediriger vers l'accueil en loggant automatiquement
				 * l'utilisateur
				 */
				String mdp= Crypt.crypt(pass, "xx");
				User newUser = new User(pseudo, mail, mdp);
				newUser.setAge(age);
				ofy().save().entity(newUser).now();
				if(!errorList.isEmpty()){					
					errorList.clear();
				}
				req.getSession(true).setAttribute("login", pseudo);
				resp.sendRedirect("/editionprofil");


			} else {

				/*
				 * Rester sur la page Register
				 * et afficher les messages d'erreur
				 */
				Element form = squelette.getElementById("register");
				for(int i=0; i<errorList.size(); i++){
					form.children().first().before(errorList.get(i));

				}
				errorList.clear();

				doGet(req, resp);

			}
		}
	}

}
