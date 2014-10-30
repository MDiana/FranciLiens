package franciliens.servlets;

import static franciliens.data.OfyService.ofy;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import franciliens.data.Train;

@SuppressWarnings("serial")
public class ProchainsDepartsServlet extends HttpServlet {

	private List<Train> listTrainGareSelect;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// récupérer les données grâce au paramètre du code UIC dans la requête et renvoyer en format JSON les données de la gare
		int codeGare= Integer.parseInt(req.getParameter("gare"));
		listTrainGareSelect = ofy().load().type(Train.class).filter("codeUICGareDepart ==", codeGare).list();
		for(Train t : listTrainGareSelect){
			
		}	
	}

		
}
