package franciliens.servlets;

import static franciliens.data.OfyService.ofy;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.appengine.api.datastore.Text;

import franciliens.data.User;

@SuppressWarnings("serial")
public class EditionProfilServlet extends HttpServlet {

	// localhost
	private Document squelette;
	private boolean firstGetDone; // a-t-on déjà fait un get ?
	private String url= "http://localhost:8888/";

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
		if (isSessionNew) {
			
			resp.sendRedirect("/login");
			
		} else {
			/*
			 * Ne créer la page que si on accède à la page pour la première fois
			 */
			if (!firstGetDone) {

				/*
				 * Construction de la page register
				 */
				Element contentElem = squelette.getElementById("content");
				Document editProfile = Jsoup.connect(url+"editProfile.html").get();
				Element editProfileElem = editProfile.getElementById("editionprofil");
				contentElem.appendChild(editProfileElem);
				
				/*
				 * Récupérer la description courante si elle existe pour pré-remplir
				 * la description
				 */
				String pseudo = (String) req.getSession().getAttribute("login");
				User user = ofy().load().type(User.class).
						filter("login ==", pseudo).list().get(0); //existe obligatoirement
				Text description = user.getDescription();
				if (description!=null && description.getValue().compareTo("")!=0) {
					editProfileElem.getElementById("description").val(description.getValue());
				}
				
				/*
				 * Récupérer l'avatar courant s'il existe pour pré-remplir le champ
				 */
				String avatar = user.getAvatarURL();
				if (avatar!=null && avatar.length()>0 && avatar.compareTo("images/defaultAvatar.png")!=0) {
					editProfileElem.getElementById("avatar").val(avatar);
				}
				
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
			Character sexe = StringEscapeUtils.escapeHtml4(req.getParameter("sexe")).charAt(0);
			String avatar = StringEscapeUtils.escapeHtml4(req.getParameter("avatar"));
			Text description = new Text(StringEscapeUtils.escapeHtml4(req.getParameter("description")));
			
			/*
			 * Récupérer l'utilisateur courant
			 */

			String pseudo = (String) req.getSession().getAttribute("login");
			User user = ofy().load().type(User.class).filter("login ==", pseudo).list().get(0);
			user.setAvatarURL(avatar);
			user.setDescription(description);
			user.setSexe(sexe);
			ofy().save().entity(user).now();
			
			resp.sendRedirect(req.getHeader("referer"));
			
		}
	}
}
