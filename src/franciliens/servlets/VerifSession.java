package franciliens.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class VerifSession {
	
	public static boolean isSessionNew(HttpServletRequest req) {
		
		HttpSession session = req.getSession(true);
		
		if (session.isNew()) {
			// L'utilisateur n'était pas loggé :
			// Le redériger vers la page de login et 
			// supprimer la session qu'on vient de créer
			
			session.invalidate();
			return true;
		} else {
			return false;
		}
		
	}
	
}
