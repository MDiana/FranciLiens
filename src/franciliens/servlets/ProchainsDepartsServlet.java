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

@SuppressWarnings("serial")
public class ProchainsDepartsServlet extends HttpServlet {

	private List<PassageEnGare> listTrainGareSelect;
	

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		JSONArray listDespassagesJson= new JSONArray();
		JSONObject envoi = new JSONObject();
		JSONObject passage;
		TimeZone pdt = TimeZone.getTimeZone("Europe/Paris");
		TimeZone.setDefault(pdt);
		Date d= new Date();
		
		// récupérer les données grâce au paramètre du code UIC dans la requête et renvoyer en format JSON les données de la gare
		int codeGare= Integer.parseInt(req.getParameter("gare"));
		listTrainGareSelect = ofy().load().type(PassageEnGare.class).filter("codeUICGareDepart ==", codeGare).list();
		try {
		for(PassageEnGare t : listTrainGareSelect){
			passage = new JSONObject();
			
				passage.put("num", t.getNum());
				d=new Date(t.getDateHeure().getTime()-3600000);
				passage.put("date",  d.toString());
				passage.put("mission", t.getMission());
				passage.put("term", t.getCodeUICTerminus());
				
				listDespassagesJson.put(passage);
		}	
		
			envoi.put("Prochains Departs", listDespassagesJson);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		resp.setContentType("application/json");
		PrintWriter out= resp.getWriter();
		out.print(envoi);
	}
	

		
}
