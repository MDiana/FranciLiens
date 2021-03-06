package franciliens.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import franciliens.data.ToutesGares;
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
		
		resp.setCharacterEncoding("UTF-8");

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

				List<Trajet> t = ofy().load().type(Trajet.class).filter("pseudoUsager", pseudo).list();


				Trajet trajet;

				if (t.size() < 1) {

					/*
					 * Si aucun trajet n'existe : en créer un
					 */

					trajet = new Trajet(idPassage, pseudo);

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

				if (!firstGetDone) {

					/*
					 * Construction de la page
					 */

					Document accueil = Jsoup.connect(url+"enregistrementTrajet.html").get();
					Element profilElem = accueil.getElementById("encartProfil");
					Element contentElem = squelette.getElementById("content");

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

				squelette.getElementById("avatar").attr("src", currentUser.getAvatarURL());
				squelette.getElementById("pseudo").html(pseudo);

				/*
				 * Afficher le voyage enregistré s'il existe, un lien pour en 
				 * enregistrer un sinon.
				 */

				List<Trajet> trajetEnregistre = ofy().load().type(Trajet.class).filter("pseudoUsager ==", pseudo).list();
				Long idPassage=(long) 0;

				if (trajetEnregistre.size()<1) {

					// Aucun trajet enregistré
					squelette.getElementById("trajetUser").html("<a class=\"lien\" "
							+ "href=\"/enregistrertrajet\">Enregistrer un trajet</a><br //>");


				} else {

					System.out.println("il existe un trajet enregistré");

					// Il existe un trajet enregistré
					Trajet trajet = trajetEnregistre.get(0);
					idPassage=trajet.getIdPassage();

					// Chercher le train correspondant au code
					PassageEnGare passage = ofy().load().type(PassageEnGare.class).id(idPassage).now();
					Date d= new Date(passage.getDateHeure().getTime()-3600000);
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy 'à' HH:mm");
					String date =sdf.format(d);
					
					squelette.getElementById("trajetUser").html("Votre Trajet "
							+ "<a href=\"/removetrajet\">"
							+ "<img src=\"/images/cross24.png\"></a> "
							+ "<a href=\"/enregistrertrajet\">"
							+ "<img src=\"/images/edit24.png\"></a>"
							+ "<div class=\"infosTrajet\">"
							+ "Gare de départ : <br //>"
							+ GaresSelectionnees.getNom(passage.getCodeUICGareDepart())+"<br //>"
							+ "Terminus : <br //>"
							+ ToutesGares.getNom(passage.getCodeUICTerminus())+"<br //>"
							+ "Heure : <br //>"
							+ date+"</div>");
					System.out.println(squelette.getElementById("trajetUser").html());
				}

				req.getSession().setAttribute("prevurl", "/enregistrertrajet");

				/*
				 * Envoyer le résultat
				 */

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
