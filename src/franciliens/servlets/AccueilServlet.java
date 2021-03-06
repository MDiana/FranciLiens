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
			
			// L'utilisateur est loggé : afficher la page 
			// d'accueil
			User currentUser;
			HttpSession session = req.getSession();
			String pseudo = (String)session.getAttribute("login");
			currentUser = ofy().load().type(User.class).filter("login ==", pseudo).list().get(0);
			
			if (!firstGetDone) {

				/*
				 * Construction de la page d'accueil
				 */
				Element contentElem = squelette.getElementById("content");
				Document accueil = Jsoup.connect(url+"accueil.html").get();
				Element profilElem = accueil.getElementById("encartProfil");
				
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
			
			squelette.getElementById("avatar").attr("src", currentUser.getAvatarURL());
			squelette.getElementById("pseudo").html(pseudo);
			
			/*
			 * Afficher le voyage enregistré s'il existe, un lien pour en 
			 * enregistrer un sinon.
			 */
			
			List<Trajet> trajetEnregistre = ofy().load().type(Trajet.class).filter("pseudoUsager ==", pseudo).list();
			Long codePassage=new Long("0");
			
			if (trajetEnregistre.size()<1) {
				
				// Aucun trajet enregistré
				squelette.getElementById("trajetUser").html("<a class=\"lien\" "
						+ "href=\"/enregistrertrajet\">Enregistrer un trajet</a><br //>");
				
				
			} else {
				
				System.out.println("il existe un trajet enregistré");
				// Il existe un trajet enregistré
				Trajet trajet = trajetEnregistre.get(0);
				codePassage=trajet.getIdPassage();
				
				// Chercher le passage en gare correspondant à l'id
				PassageEnGare passage = ofy().load().type(PassageEnGare.class).id(codePassage).now();
				Date d = new Date(passage.getDateHeure().getTime()-3600000);
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
						+ date +"</div>");
				System.out.println(squelette.getElementById("trajetUser").html());
			}
			
			req.getSession().setAttribute("prevurl", "/accueil");
			squelette.getElementById("avatar").attr("src", currentUser.getAvatarURL());

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
