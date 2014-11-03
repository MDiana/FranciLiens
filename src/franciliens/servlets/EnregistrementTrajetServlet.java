package franciliens.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import franciliens.data.GaresSelectionnees;
import franciliens.data.PassageEnGare;
import franciliens.data.Trajet;
import franciliens.data.User;
import static franciliens.data.OfyService.ofy;

@SuppressWarnings("serial")
public class EnregistrementTrajetServlet extends HttpServlet {

	private Document squelette;
	private boolean firstGetDone; // a-t-on déjà fait un get ?
	private String url= "http://franci-liens.appspot.com/";

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
		 * Vérifier que l'utilisateur est connecté
		 */
		boolean isSessionNew = VerifSession.isSessionNew(req);
		if (isSessionNew) {
			// L'utilisateur n'était pas loggé :
			// Le rediriger vers la page de login
			resp.sendRedirect("/login");
		} else {

			/*
			 * TODO Enregistrement d'un passage sélectionné
			 */

			// Tester si l'argument idPassage est présent
			String param = req.getParameter("idPassage");

			if (param != null) {

				Long idPassage = Long.parseLong(param);
				/*
				 * Récupérer le trajet éventuellement enregistré par 
				 * l'utilisateur
				 */
				
				HttpSession session = req.getSession();
				String pseudo = (String) session.getAttribute("login");
				
//				Long idTrajet = (Long) session.getAttribute("trajetEnregistre");
				List<Trajet> t = ofy().load().type(Trajet.class).filter("pseudoUsager", pseudo).list();
				
				
				Trajet trajet;
				
				if (t.size() < 1) {

					/*
					 * Si aucun trajet n'existe : en créer un
					 */
					
					trajet = new Trajet(idPassage, pseudo);
//					session.setAttribute("trajetEnregistre", trajet.getId());
					
				} else {
					
					/*
					 * Sinon, écraser le trajet existant
					 */
					System.out.println("on écrase");
					
					trajet = t.get(0);
					trajet.setIdPassage(idPassage);
				}
				
				ofy().save().entity(trajet).now();
				
				req.removeAttribute("idPassage");
				resp.sendRedirect("/accueil");

			} else {

				// Affichage normal de la page

				HttpSession session = req.getSession();
				String pseudo = (String)session.getAttribute("login");
				Document accueil = Jsoup.connect(url+"enregistrementTrajet.html").get();
				Element profilElem = accueil.getElementById("encartProfil");

				/*
				 * Construction de la page
				 */
				Element contentElem = squelette.getElementById("content");

				if (!firstGetDone) {

					contentElem.appendChild(profilElem);

					Element trajetsElem = accueil.getElementById("encartTrajets");

					/*
					 * Générer les options du select
					 */
					trajetsElem.getElementById("gareSelect").html(GaresSelectionnees.genererOptionsSelect());

					contentElem.appendChild(trajetsElem);
					firstGetDone=true;
				}

				/*
				 * Remplir l'encart de profil
				 */

				User currentUser = ofy().load().type(User.class).filter("login ==", pseudo).list().get(0);

				profilElem.getElementById("avatar").attr("src", currentUser.getAvatarURL());
				profilElem.getElementById("pseudo").html(pseudo);
				
				/*
				 * Afficher le voyage enregistré s'il existe, un lien pour en 
				 * enregistrer un sinon.
				 */

				List<Trajet> trajetEnregistre = ofy().load().type(Trajet.class).filter("pseudoUsager ==", pseudo).list();
				Long idPassage=(long) 0;

				if (trajetEnregistre.size()<1) {

					// Aucun trajet enregistré
					profilElem.getElementById("trajetUser").html("<a class=\"lien\" "
							+ "href=\"/enregistrerTrajet\">Enregistrer un trajet</a><br //>");


				} else {

					System.out.println("il existe un trajet enregistré");
					
					// Il existe un trajet enregistré
					Trajet trajet = trajetEnregistre.get(0);
					idPassage=trajet.getIdPassage();

					// Chercher le train correspondant au code
					PassageEnGare passage = ofy().load().type(PassageEnGare.class).id(idPassage).now();

					profilElem.getElementById("trajetUser").html("Votre Trajet "
							+ "<a href=\"/removeTrajet\">"
							+ "<img src=\"/images/cross24.png\"></a> "
							+ "<a href=\"/enregistrertrajet\">"
							+ "<img src=\"/images/edit24.png\"></a>"
							+ "<div class=\"infosTrajet\">"
							+ "Gare de départ : <br //>"
							+ GaresSelectionnees.getNom(passage.getCodeUICGareDepart())+"<br //>"
							+ "Terminus : <br //>"
							+ GaresSelectionnees.getNom(passage.getCodeUICTerminus())+"<br //>"
							+ "Heure : <br //>"
							+ passage.getDateHeure()+"</div>");
					System.out.println(profilElem.getElementById("trajetUser").html());
				}

				req.getSession().setAttribute("prevurl", "/enregistrertrajet");

				/*
				 * Envoyer le résultat
				 */
				
				resp.setContentType("text/html; charset=UTF-8");
				resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
				resp.setHeader("Pragma", "no-cache"); // HTTP 1.0.
				resp.setDateHeader("Expires", 0); // Proxies.
				resp.setStatus(400);
				PrintWriter out = resp.getWriter();
				out.println(squelette.html());

				out.flush();
				out.close();
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		boolean isSessionNew = VerifSession.isSessionNew(req);

		if (isSessionNew) {
			resp.sendRedirect("/login");
		} else {
			super.doPost(req, resp);
		}
	}
}
