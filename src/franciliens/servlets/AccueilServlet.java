package franciliens.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class AccueilServlet extends HttpServlet {
	
	public void init() {
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
			// Le redériger vers la page de login
			resp.sendRedirect("/login");
		} else {
			// L'utilisateur est loggé : afficher la page 
			// d'accueil
			
			resp.setStatus(400);
			resp.setContentType("text/plain ; charset=UTF-8");
			resp.getWriter().write("Vous êtes loggé !");
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

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		boolean isSessionNew = VerifSession.isSessionNew(req);
		
		if (isSessionNew) {
			resp.sendRedirect("/login");
		} else {
			super.doPut(req, resp);
		}
	}

}
