package franciliens.servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

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

import franciliens.data.Trajet;
import franciliens.data.User;
import static franciliens.data.OfyService.ofy;

@SuppressWarnings("serial")
public class InviteServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		boolean isSessionNew = VerifSession.isSessionNew(req);

		if (isSessionNew) {
			resp.sendRedirect("/login");
		} else {

			boolean ok=true; //TODO enlever le true après les tests

			// TODO Récupérer le destinataire du mail
			String recipient = (String) req.getAttribute("recipient");

			// TODO Vérifier que le destinataire existe

			List<User> resU = ofy().load().type(User.class).filter("login ==", recipient).list();
			if (resU.size() > 0) {

				// TODO Si oui, vérifier que cet utilisateur a un voyage enregistré
				// encore valide

				User recipientU = resU.get(0);
				List<Trajet> resT = ofy().load().type(Trajet.class).filter("pseudoUsager ==", recipientU.getLogin()).list();

				if (resT.size() > 0) {

					// TODO tout va bien : créer et envoyer le mail
					try {
						envoyerMail((String) req.getSession().getAttribute("login"), recipientU, resT.get(0));
					} catch (Exception e) {
						resp.sendRedirect("/accueil");
					}

				} else {

					ok = false;

				}

			} else {

				ok = false;
			}

			if (!ok) {

				// TODO Il y a eu un problème : renvoyer vers l'accueil
				resp.sendRedirect("/accueil");

			}

		}
	}

	private void envoyerMail(String sender, User recipientU, Trajet trajet)
			throws MessagingException, UnsupportedEncodingException {
		String url = "http://localhost:8888/";

		String msgBody = "Bonjour, "+recipientU.getLogin()
				+" ! Vous avez reçu une invitation de la part de "+sender
				+".\n\n"+getProfile(sender)
				+"\n\nSouhaitez-vous <a href=\""+ url + "accept?sender=" + sender
				+ "\">accepter</a> ou <a href=\""+ url + "refuse?sender=" + sender
				+ "\">refuser</a> ce rendez-vous ?";

		try {
			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);

			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress("noreply@franci-liens.appspot.com", "Franci'Liens"));
			msg.addRecipient(MimeMessage.RecipientType.TO,
					new InternetAddress(recipientU.getEmail(), recipientU.getLogin()));
			msg.setSubject("Vous avez reçu une nouvelle invitation !");
			msg.setText(msgBody);
			Transport.send(msg);

		} catch (AddressException e) {
			System.err.println("Problème avec l'adresse");
			throw e;
		} catch (MessagingException e) {
			System.err.println("Problème lors de l'envoi du mail");
			throw e;
		} catch (UnsupportedEncodingException e) {
			System.err.println("Problème d'encodage");
			throw e;
		}
	}

	private String getProfile(String sender) {

		// TODO Récupérer l'utilisateur
		User user = ofy().load().type(User.class).filter("login ==", sender).list().get(0);
		// Il y aura forcément un résultat car sender vient de la variable de session
		String profile = sender+" est ";
		if (user.getSexe()=='f') {
			profile += "une femme agée de ";
		} else {
			profile += "un homme agé de ";
		}
		profile += (user.getAge()+" ans. ");
		if (user.getDescription().toString().length()>0) {
			if (user.getSexe()=='f') {
				profile += ("Voici comment elle se décrit :\n"+user.getDescription());
			} else {
				profile += ("Voici comment il se décrit :\n"+user.getDescription());
			}
		}
		
		return profile;
	}

}
