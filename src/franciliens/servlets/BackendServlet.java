package franciliens.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;

@SuppressWarnings("serial")
public class BackendServlet extends HttpServlet {
	
	private static final Logger _logger = Logger.getLogger(BackendServlet.class.getName());
	
	@Override
	public void init() throws ServletException {
		// TODO connexion à l'API lors de l'initialisation
		super.init();
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(req, resp);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//TODO récupérer les données de l'API SNCF. Ensuite les parser puis créer les données dans le datastore
		// Attention: trouver un algo pour savoir ce qu'on doit remplacer dans le datstore/ajouter/supprimer
		try{
			_logger.setLevel(Level.INFO);
			_logger.info("Nous allons accéder à l'api SNCF");
		URL url = new URL("http://api.transilien.com/gare/87393009/depart/)");
		URLConnection con = url.openConnection();
		String login = "upmc107:xMcQ2p38";
		String encodedLogin =new String(Base64.encodeBase64(login.getBytes()));
		con.setRequestProperty("Authorization", "Basic " + encodedLogin);
		//on utilise l'url comme un input non?
		con.setDoInput(true);
		//HttpURLConnection httpConn = (HttpURLConnection)con;
		// Récupérer les trois prochains départs pour chaque gare (pour l'instant suffisant)
		// Comme on ne peut faire que 30 requêtes par minutes, nous allons choisir 30 gares précises
		/* Choix:
		 * Aéroport Ch. de Gaulle 1 87271460
			Aéroport Ch. de Gaulle 2 TGV 87001479
			Argenteuil 87381848
			Aulnay sous Bois 87271411	
		 *  Bellevue 87393116
		 *  Bibliothèque François Mitterrand 87328328
		 *  Bondy 87113407
		 *  Châtelet les Halles 87758607
		 *  Colombes 87381087
		 *  Gennevilliers 87271205
		 *  La Défense Grande Arche 87758011
		 *  Les Mureaux 87386680
		 *  Nanterre Université 87386318
		 *  Paris Austerlitz 87547026
			Paris Bercy 87686667
			Paris Est 87113001
			Paris Gare de Lyon 87686006
			Paris Montparnasse 87391003
			Paris Nord 87271007
			Paris Saint-Lazare 87384008
			Puteaux 87382382
			Saint-Cyr 87393223
			Saint Germain en Laye GC 87382804
			Saint-Quentin en Y. Montigny le B. 87393843
			Stade de France Saint-Denis 87164780
			Versailles Chantiers 87393009
			Versailles Château Rive Gauche 87393157
			Vitry sur Seine 87545293
			Villiers sur Marne Le Plessis Trévise 87113795
			Parc des Expositions 87271486

		 * 
		 */
		
		_logger.info("Nous avons accedé à l'api SNCF");
		BufferedReader in = new BufferedReader(new InputStreamReader(
                con.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null)
        	_logger.info(""+inputLine);
        in.close();
        
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}
	
	
//	Exemple à laisser pour l'instant :)
//		private static final Logger _logger = Logger.getLogger(BackendServlet.class.getName());
//		
//		public void doGet(HttpServletRequest req, HttpServletResponse resp)
//		throws IOException {
//			
//		try {
//		_logger.setLevel(Level.INFO);
//		_logger.info("Cron has been executed");
//	
//		//Put your logic here
//		//BEGIN
//		//END
//		}
//		catch (Exception ex) {
//		//Log any exceptions in your Cron Job
//		}
//		}
//	
//		@Override
//		public void doPost(HttpServletRequest req, HttpServletResponse resp)
//		throws ServletException, IOException {
//		doGet(req, resp);
//		}

}


