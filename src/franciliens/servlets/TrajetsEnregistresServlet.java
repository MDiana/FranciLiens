package franciliens.servlets;

import static franciliens.data.OfyService.ofy;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import franciliens.data.PassageEnGare;
import franciliens.data.Trajet;
import franciliens.data.User;

@SuppressWarnings("serial")
public class TrajetsEnregistresServlet extends HttpServlet {

	private List<PassageEnGare> listpassageGare;
	private List<Trajet> trajetsEnregistresPourGare;
	

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		JSONArray listDesTrajetsEnregistres= new JSONArray();
		JSONObject envoi = new JSONObject();
		JSONObject trajet= new JSONObject();
		TimeZone pdt = TimeZone.getTimeZone("Europe/Paris");
		TimeZone.setDefault(pdt);
		Date d= new Date();

		// récupérer les départs d'une gare et regarder si un des num de trains appartient au num 
		// de la liste des trajets enregistrés
		int codeGare= Integer.parseInt(req.getParameter("gare"));
		listpassageGare = ofy().load().type(PassageEnGare.class).filter("codeUICGareDepart", codeGare).list();
		int count;
		
		// récupérer la liste des trajets enregistrés pour un train donné dans la gare
		for(PassageEnGare peg : listpassageGare){
			List <Trajet> tr = ofy().load().type(Trajet.class).filter("idPassage", peg.getId()).list();
				
		}
		
		// récupérer les infos de l'utilisateur associé, du passageEnGare associé et créer l'objet JSON
		if(!trajetsEnregistresPourGare.isEmpty()){
		try {
			for(Trajet tra : trajetsEnregistresPourGare){
			User userTrajet= ofy().load().type(User.class).filter("login", tra.getPseudoUsager()).list().get(0);
				
				trajet.put("num", tra.getNumTrain());


				listDesTrajetsEnregistres.put(trajet);
				trajet = new JSONObject();	
			}
			envoi.put("Prochains Departs", listDesTrajetsEnregistres);
		} catch (JSONException e) {
			e.printStackTrace();
		}
			
		}
		
		
		resp.setContentType("application/json");
		PrintWriter out= resp.getWriter();
		out.print(envoi);
	}



}
