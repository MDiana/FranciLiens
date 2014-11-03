package franciliens.servlets;

import static franciliens.data.OfyService.ofy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import franciliens.data.PassageEnGare;
import franciliens.data.Trajet;
import franciliens.data.User;

@SuppressWarnings("serial")
public class ResponseServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		boolean isSessionNew = VerifSession.isSessionNew(req);

		if (isSessionNew) {
			resp.sendRedirect("/login");
		} else {

			// Récupérer le destinataire du mail
			String recipient = (String) req.getParameter("recipient");
			// Récupérer le sender
			String sender = (String) req.getParameter("sender");
			// Récupérer l'action
			String action = (String) req.getParameter("action");

			if (recipient!=null && sender!=null && action!=null) {

				// Vérifier que le destinataire existe, que le sender existe,
				// et que le sender avait bien enregistré un trajet

				List<User> resR = ofy().load().type(User.class).filter("login ==", recipient).list();
				List<User> resS = ofy().load().type(User.class).filter("login ==", sender).list();
				List<Trajet> resT = ofy().load().type(Trajet.class).filter("pseudoUsager ==", sender).list();
				if (resR.size() > 0 && resS.size() > 0 && resT.size() > 0) {

					// Tout va bien : créer et envoyer le mail
					try {
						envoyerMail(resS.get(0), resR.get(0), action, resT.get(0));
						
						// TODO Supprimer le trajet enregistré par le sender une fois le mail envoyé, 
						// si l'invitation a été acceptée.
						if (action.compareTo("accept")==0) {
							ofy().delete().entity(resT.get(0)).now();
						}
					} catch (Exception e) {
						resp.sendRedirect("/accueil");
					}
				} 
			}
			
			resp.sendRedirect("/accueil");
		}
	}

	private void envoyerMail(User sender, User recipient, String action, Trajet trajet)
			throws MessagingException, UnsupportedEncodingException {

		String msgBody; 
		
		if (action.compareTo("accept")==0) {
			PassageEnGare p = ofy().load().type(PassageEnGare.class).id(trajet.getIdPassage()).now();
			msgBody = "Bonjour, "+recipient.getLogin()
					+" ! L'invitation que vous avez envoyée à "+sender
					+"a été acceptée.\n\nRendez-vous à l'accueil de votre gare 5 minutes avant votre départ"
					+ " qui est à "+ p.getDateHeure()+ ".";
		} else {
			msgBody = "Bonjour, "+recipient.getLogin()
					+" . Nous sommes au regret de vous informer que "+sender
					+" a décliné votre invitation. N'hésitez pas à rententer votre chance sur Franci'Liens !";
		}

		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		try {


			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("franciliensDAR@gmail.com", "Franci'Liens"));

			message.addRecipient(
					Message.RecipientType.TO,
					new InternetAddress(recipient.getEmail()));


			message.setSubject(""+recipient.getLogin()+"a répondu à votre invitation !");
			message.setText(msgBody);


			Transport.send(message);


		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
