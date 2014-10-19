package franciliens;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.googlecode.objectify.ObjectifyService;

@SuppressWarnings("serial")
public class RegisterServlet extends HttpServlet {
	
	private Document squelette;
	private boolean firstGetDone; // a-t-on déjà fait un get ?
	
	static {
        ObjectifyService.register(User.class); // Fait connaître votre classe-entité à Objectify
    }

	@Override
	public void init() {
		try {
			squelette = Jsoup.connect("http://localhost:8888/index.html").get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		firstGetDone=false;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		/*
		 * TODO Vérifier que l'utilisateur n'est pas déjà loggé
		 */
//		if ("userloggé") {
//		resp.sendRedirect("/accueil");
//	}
		
		/*
		 * Ne créer la page que si on accède à la page pour la première fois
		 */
		if (!firstGetDone) {
			
			/*
			 * Construction de la page register
			 */
			Element contentElem = squelette.getElementById("content");
			Document register = Jsoup.connect("http://localhost:8888/register.html").get();
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

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		/*
		 * Envoyer le résultat
		 */
		resp.setContentType("text/html; charset=UTF-8");
		resp.setStatus(400);
		PrintWriter out = resp.getWriter();
		out.println(squelette.head());
		
		out.flush();
		out.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		/*
		 * TODO Récupérer les différentes infos
		 */
		
		/*
		 * TODO Vérifier si l'email choisi est déjà utilisé
		 */
		
		/*
		 * TODO Mail déjà utilisé : Message d'erreur "Un compte existe
		 * déjà à cette adresse"
		 */
		
		/*
		 * TODO Mail non utilisé : Vérifier si le pseudo est déjà utilisé
		 * (indépendamment de la casse)
		 */
		
		/*
		 * TODO Pseudo existant : Message d'erreur "Pseudo déjà utilisé"
		 */
		
		/*
		 * TODO Tout va bien : Stocker dans le Datastore et
		 * rediriger vers l'accueil en loggant automatiquement
		 * l'utilisateur
		 */
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPut(req, resp);
	}

}
