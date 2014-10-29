package franciliens.servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Message;
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
		
		try {
			envoyerMail("DieNah", ofy().load().type(User.class).id("mrshickenbottom@hotmail.fr").now());
		} catch (MessagingException e1) {
			System.err.println("Echec du test");
			e1.printStackTrace();
		}

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
						envoyerMail((String) req.getSession().getAttribute("login"), recipientU);
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

	private void envoyerMail(String sender, User recipientU)
			throws MessagingException, UnsupportedEncodingException {
		String url = "http://franci-liens.appspot.com/";

		String msgBody = "Bonjour, "+recipientU.getLogin()
				+" ! Vous avez reçu une invitation de la part de "+sender
				+".\n\n"+getProfile(sender)
				+"\n\nSouhaitez-vous accepter ("+ url + "accept?sender=" + sender
				+ ") ou refuser ("+ url + "refuse?sender=" + sender
				+ ") ce rendez-vous ?</body></html>";

		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        try {

            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("diana.malabard@gmail.com", "Franci'Liens"));
            
            message.addRecipient(
                    Message.RecipientType.TO,
                    new InternetAddress(recipientU.getEmail()));

            
            message.setSubject("Vous avez reçu une nouvelle invitation !");
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

	private String getProfile(String sender) {

		// TODO Récupérer l'utilisateur
		User user = ofy().load().type(User.class).filter("login ==", sender).list().get(0);
		// Il y aura forcément un résultat car sender vient de la variable de session
		String profile = sender+" est ";
		if (user.getSexe()==null) {
			profile+= "agé(e) de ";
		} else {
			if (user.getSexe()=='f') {
				profile += "une femme agée de ";
			} else {
				profile += "un homme agé de ";
			}
		}
		profile += (user.getAge()+" ans. ");
		if (user.getDescription()!=null && user.getDescription().toString().length()>0) {
			profile += ("Voici comment ce membre se décrit :\n"+user.getDescription());
		}
		
		return profile;
	}

}
