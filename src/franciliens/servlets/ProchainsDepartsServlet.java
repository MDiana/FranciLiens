package franciliens.servlets;

import static franciliens.data.OfyService.ofy;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
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

import franciliens.data.GaresSelectionnees;
import franciliens.data.PassageEnGare;

@SuppressWarnings("serial")
public class ProchainsDepartsServlet extends HttpServlet {

	private List<PassageEnGare> listPassages;

//	@Override
//	public void init() throws ServletException {
//		super.init();
//		PassageEnGare p1 = new PassageEnGare("12345", new Date(), "PACA", 87271460, 87758011);
//		ofy().save().entity(p1).now();
////		PassageEnGare p2 = new PassageEnGare("12346", new Date(), "PACA", 87758607, 87381848);
////		ofy().save().entity(p2).now();
////		PassageEnGare p3 = new PassageEnGare("12347", new Date(), "PACA", 87758607, 87382002);
////		ofy().save().entity(p3).now();
//	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		JSONArray listDespassagesJson= new JSONArray();
		JSONObject envoi = new JSONObject();
		JSONObject passage= new JSONObject();
		TimeZone pdt = TimeZone.getTimeZone("Europe/Paris");
		TimeZone.setDefault(pdt);
		Date d= new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy 'à' HH:mm");
		
		// récupérer les données grâce au paramètre du code UIC dans la requête et renvoyer en format JSON les données de la gare
		
		int codeGare= Integer.parseInt(req.getParameter("gare"));
		listPassages = ofy().load().type(PassageEnGare.class).filter("codeUICGareDepart", codeGare).list();
		try {
			for(PassageEnGare t : listPassages){

				// Avant de faire tout ça, on pourrait vérifier si l'heure n'est pas passé pour certains trains de la liste, pour ne renvoyer que 
				// ceux que nous voulons afficher (car y a un intervalle de 15 minutes où des trains déjà passés sont encore dans le datastore)
				passage.put("num", t.getNum());
				passage.put("idPassage", t.getId());
				d=new Date(t.getDateHeure().getTime()-3600000);
				String date =sdf.format(d);
				passage.put("date",  date);
				passage.put("mission", t.getMission());
				passage.put("term", t.getCodeUICTerminus());
				
				listDespassagesJson.put(passage);
				passage = new JSONObject();

			}	

			envoi.put("Prochains Departs", listDespassagesJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");
		PrintWriter out= resp.getWriter();
		out.print(envoi);
	}



}
