package franciliens.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import franciliens.data.Trajet;
import static franciliens.data.OfyService.ofy;

@SuppressWarnings("serial")
public class RemoveTrajetServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		boolean isSessionNew = VerifSession.isSessionNew(req);
		if (isSessionNew) {
			
			resp.sendRedirect("/login");
			
		} else {
			
			HttpSession session = req.getSession();
			String pseudo = (String) session.getAttribute("login");
			Long idTrajetEnregistre = (Long) session.getAttribute("trajetEnregistre");
			
			if (idTrajetEnregistre !=null) {

				Trajet trajet = ofy().load().type(Trajet.class).id(idTrajetEnregistre).now();
				
				if (trajet != null && trajet.getPseudoUsager().compareTo(pseudo)==0) {
					
					ofy().delete().entity(trajet).now();
					
				}
				
			}
			
			session.removeAttribute("trajetEnregistre");
			resp.sendRedirect("/accueil");
			
		}
	}
	
}
