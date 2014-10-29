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
import franciliens.data.Train;
import franciliens.data.Trajet;
import franciliens.data.User;
import static franciliens.data.OfyService.ofy;

@SuppressWarnings("serial")
public class AccueilServlet extends HttpServlet {
	
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
			
			// TODO L'utilisateur est loggé : afficher la page 
			// d'accueil
			
			if (!firstGetDone) {

				/*
				 * Construction de la page d'accueil
				 */
				Element contentElem = squelette.getElementById("content");
				Document accueil = Jsoup.connect(url+"accueil.html").get();
				
				/*
				 * TODO Remplir l'encart de profil
				 */
				Element profilElem = accueil.getElementById("encartProfil");
				
				HttpSession session = req.getSession();
				String pseudo = (String)session.getAttribute("login");
				User currentUser = ofy().load().type(User.class).filter("login ==", pseudo).list().get(0);
				
				profilElem.getElementById("avatar").attr("src", currentUser.getAvatarURL());
				
				//TODO TEEEEEEEEEEEEEEEEEEEEEEEEEEST
				profilElem.append("<a href=\"/invite?recipient=" + pseudo
								+ "\"><img src=\"images/invite32.png\"></a>");
				
				/*
				 * TODO Afficher le voyage enregistré s'il existe, un lien pour en 
				 * enregistrer un sinon.
				 */
				
				List<Trajet> trajetEnregistre = ofy().load().type(Trajet.class).filter("pseudoUsager ==", pseudo).list();
				int codeTrain=0;
				
				if (trajetEnregistre.size()<1) {
					
					// Aucun trajet enregistré
					
					
				} else {
					
					// Il existe un trajet enregistré
					Trajet trajet = trajetEnregistre.get(0);
					codeTrain=trajet.getNumTrain();
					
					// Chercher le train correspondans au code
					Train train = ofy().load().type(Train.class).id(codeTrain).now();
					
					profilElem.getElementById("trajetUser").html("Votre Trajet : "
							+ "<div class=\"infosTrajet\">"
							+ "Gare de départ : <br />"
							+ GaresSelectionnees.getNom(train.getCodeUICGareDepart())+"<br />"
							+ "Terminus : <br />"
							+ GaresSelectionnees.getNom(train.getCodeUICTerminus())+"<br />"
							+ "Heure : <br />"
							+ train.getDateHeure()+"</div>");
				}
				
				contentElem.appendChild(profilElem);
				
				/*
				 * TODO Remplir la liste des trajets suivant la gare :
				 * - Du trajet enregistré s'il existe
				 * - De la gare sélectionnée par défaut sinon.
				 */
				
				Element trajetsElem = accueil.getElementById("encartTrajetsEnregistres");
				
				if (trajetEnregistre.size()<1) {
					
					// Pas de trajet enregistré : utiliser la gare par défaut
					// => Pas possible. Attendre le POST (si pas de JS), ou laisser
					// JS faire
					
				} else {
					
					// Trajet existant : utiliser la gare choisie
					// (codeTrajet !=0)					
					List<Trajet> trajetsEnregistres = ofy().load().type(Trajet.class).
							filter("numTrain ==", codeTrain).list();

					Element trajetsEnrElem = trajetsElem.getElementById("trajetsEnregistres");
					
					for (Trajet trajet : trajetsEnregistres) {
						
						// Récupérer le profil usager correspondant
						User user = ofy().load().type(User.class).filter("login ==", trajet.getPseudoUsager())
								.list().get(0);
						
						// Construire la ligne du tableau avec les infos
						Element newEntry = trajetsEnrElem.appendElement("tr");
						newEntry.html("<td><img class=\"miniavatar\" src="+ user.getAvatarURL()
								+"></td><td>" + user.getLogin()
								+ "</td><td>" + user.getAge()
								+ "</td><td>" + GaresSelectionnees.getNom(codeTrain)
								+ "</td><td><p class=\"description\">" + user.getDescription()
								+ "</p></td><td class=\"invitation\">"
								+ "<a href=\"/invite?recipient=" + user.getLogin()
								+ "\"><img src=\"images/invite32.png\"></a></td>");
					}
					
				}
				
				contentElem.appendChild(trajetsElem);
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
		
		if (isSessionNew) {
			resp.sendRedirect("/login");
		} else {
			super.doPost(req, resp);
		}
	}
}
