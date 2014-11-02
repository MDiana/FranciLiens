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
			Long idPassage = (Long) req.getAttribute("idPassage");
			System.out.println(req.getAttribute("idPassage"));

			if (idPassage != null) {

				/*
				 * Récupérer le trajet éventuellement enregistré par 
				 * l'utilisateur
				 */
				
				HttpSession session = req.getSession();
				
				Long idTrajet = (Long) session.getAttribute("trajetEnregistre");
				
				Trajet trajet;
				
				if (idTrajet == null) {

					/*
					 * Si aucun trajet n'existe : en créer un
					 */
					
					String pseudo = (String) session.getAttribute("login");
					trajet = new Trajet(idPassage, pseudo);
					session.setAttribute("trajetEnregistre", trajet.getId());
					
				} else {
					
					/*
					 * Sinon, écraser le trajet existant
					 */
					
					trajet = ofy().load().type(Trajet.class).id(idTrajet).now();
					trajet.setIdPassage(idPassage);
				}
				
				ofy().save().entity(trajet).now();
				System.out.println("enregistrement fait, retour accueil");
				
				req.removeAttribute("idPassage");
				resp.sendRedirect("/accueil");

			} else {

				// Affichage normal de la page
				System.out.println("affichage normal");

				HttpSession session = req.getSession();
				String pseudo = (String)session.getAttribute("login");
				Document accueil = Jsoup.connect(url+"enregistrementTrajet.html").get();
				Element profilElem = accueil.getElementById("encartProfil");

				if (!firstGetDone) {

					/*
					 * Construction de la page d'accueil
					 */
					Element contentElem = squelette.getElementById("content");

					/*
					 * Remplir l'encart de profil
					 */

					User currentUser = ofy().load().type(User.class).filter("login ==", pseudo).list().get(0);

					profilElem.getElementById("avatar").attr("src", currentUser.getAvatarURL());
					profilElem.getElementById("pseudo").html(pseudo);					

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
				 * Afficher le voyage enregistré s'il existe, un lien pour en 
				 * enregistrer un sinon.
				 */

				List<Trajet> trajetEnregistre = ofy().load().type(Trajet.class).filter("pseudoUsager ==", pseudo).list();
				idPassage=(long) 0;

				if (trajetEnregistre.size()<1) {

					// Aucun trajet enregistré
					profilElem.getElementById("trajetUser").html("<a class=\"lien\" "
							+ "href=\"/enregistrerTrajet\">Enregistrer un trajet</a><br //>");


				} else {

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
				}

				req.getSession().setAttribute("prevurl", "/enregistrertrajet");

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
