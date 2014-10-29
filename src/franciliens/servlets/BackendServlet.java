package franciliens.servlets;

import static franciliens.data.OfyService.ofy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import franciliens.data.GaresSelectionnees;
import franciliens.data.Train;

// Finalement utilisation de JAXP DOM --> Faire un tableau contenant tous les numéros de gares et faire une boucle qui fait pour tab[i] les 30 requêtes??
@SuppressWarnings("serial")
public class BackendServlet extends HttpServlet {


	private static final Logger _logger = Logger.getLogger(BackendServlet.class.getName());
	private ArrayList<Train> listeTrain;
	private List<Train> listeDesAnciensDeparts;
	private Document apiResult;
	private GaresSelectionnees []lesFameuses30Gares = GaresSelectionnees.values();

	@Override
	public void init() throws ServletException {
		super.init();
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//Récupérer les données de l'API SNCF. Ensuite les parser puis créer les données dans le datastore

		// Pour des raisons de quotas imposé par GAE pour les écritures dans le datastore,
		// on peut tenter de faire un cron toutes les 30 minutes et 
		//qui ne prend en compte que les trains partant dans plus de 30 mn pour chaque gare
		// et supprimant dans la dataStore tous les trains dont l'heure est dépassée 

		_logger.setLevel(Level.INFO);

		TimeZone pdt = TimeZone.getTimeZone("Europe/Paris");
		TimeZone.setDefault(pdt);
		Date dateActuelle = new Date();

		_logger.info("La date du système est : " + dateActuelle.toString());

		

		//for (GaresSelectionnees gare : lesFameuses30Gares) {
		GaresSelectionnees gare = GaresSelectionnees.ARG;
			try {
				_logger.info("Nous allons accéder à l'api SNCF");
				URL url = new URL("http://api.transilien.com/gare/"+gare.getCode()+"/depart/");
				URLConnection con = url.openConnection();
				String login = "upmc107:xMcQ2p38";
				String encodedLogin =new String(Base64.encodeBase64(login.getBytes()));
				con.setRequestProperty("Authorization", "Basic " + encodedLogin);
				con.setDoInput(true);

				_logger.info("Nous avons accedé à l'api SNCF pour les trains de "+gare.getNom());
				// Avec l'API JAXP DOM
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = factory.newDocumentBuilder();


				// XML Stream			
				InputStream xmlStream = con.getInputStream();

				this.apiResult = docBuilder.parse(xmlStream);

				// element racine
				Element rootElement = apiResult.getDocumentElement();

				// Récupérer tous les trains
				NodeList listTrain = rootElement.getElementsByTagName("train");

				listeTrain = new ArrayList<Train>();

				// boucle for pour récupérer tous les trains en questions on va traiter pour chaque train
				int listTrainCount = listTrain.getLength();
				for (int i =0; i<listTrainCount; i++) {
					Node train = listTrain.item(i);

					Train tchouttchout= new Train("", new Date(), "", gare.getCode(), 0); // on peut mettre direct dans le new le code gare départ
					// vu qu'on va faire un for pour parcourir la liste des numéros de gare et faire les 30 connexions à l'API

					// récupérer les enfants de ce train, c'est-à dire les balises qu'il contient
					NodeList traindonnees = train.getChildNodes();
					int length = traindonnees.getLength();
					for (int j = 0; j < length; j++) {
						Node node = traindonnees.item(j);
						// node type 1 is text
						if (1 == node.getNodeType()) {
							if ("date".equals(node.getNodeName())){
								// the text element is the child node
								String dateTrain= node.getFirstChild().getTextContent();
								Date dateT= stringToDate(dateTrain);
								
								// on rajoute une heure car dans le datastore il reformate en UTC
								Date traindate= new Date(dateT.getTime()+3600000);
								tchouttchout.setDateHeure(traindate);
								// Récupérer si l'horaire est un horaire réel ou s'il s'agit d'un horaire théorique.
								// Peut-être que ça peut servir??? 
								NamedNodeMap mode= node.getAttributes();
								char modeReeloutheoriue = mode.item(0).getTextContent().charAt(0);

							} else if("num".equals(node.getNodeName())){
								tchouttchout.setNum(node.getFirstChild().getTextContent());
							} else if("term".equals(node.getNodeName())){
								tchouttchout.setCodeUICTerminus(Integer.parseInt(node.getFirstChild().getTextContent()));
							} else if("etat".equals(node.getNodeName())){
								tchouttchout.setEtat(node.getFirstChild().getTextContent());
							}else if("miss".equals(node.getNodeName())){
								tchouttchout.setMission(node.getFirstChild().getTextContent());
							}

						}

					}

					//					listeTrain.add(tchouttchout);
					// on va ajouter le train à la datastore si dans plus de 15 minutes et moins de 45mn
					Date date15m = new Date(dateActuelle.getTime()+0*60000);
					Date date45m = new Date(dateActuelle.getTime()+45*60000);
					//					_logger.info("La date +15mn est : " + date15m.toString());
					//					_logger.info("La date +45mn est : " + date45m.toString());

					if((tchouttchout.getDateHeure().getTime()-3600000<date45m.getTime()) && (tchouttchout.getDateHeure().getTime()-3600000>date15m.getTime())){
						_logger.info("On enregistre dans le datastore le petit train");
						ofy().save().entity(tchouttchout).now();
						listeTrain.add(tchouttchout);
					}


				}



			} catch (Exception e) {
				e.printStackTrace();
			}



			//pour les premiers tests j'essaye dans une liste de trains, pour voir ce que ça donne 
			for(int i=0; i<listeTrain.size();i++){
				_logger.info("Train -> Terminus" + (listeTrain.get(i)).getCodeUICTerminus()
						+ " num: " + (listeTrain.get(i)).getNum()
						+ " date: " + (listeTrain.get(i)).getDateHeure() 
						+ " Mission: " + (listeTrain.get(i)).getMission()
						+ " Etat: " + (listeTrain.get(i)).getEtat()
						);

			}
//		}
		
		// Juste avant de faire les ajouts, on va vider le Datastore pour les Trains dont l'heure est dépassée!
			 	dateActuelle=new Date();
				listeDesAnciensDeparts = ofy().load().type(Train.class).list(); // à peu près 300 lectures (*48= 14 400)
				if(!listeDesAnciensDeparts.isEmpty()){
					for (Train t :listeDesAnciensDeparts) {
						if (t.getDateHeure().getTime()<dateActuelle.getTime()) {
							_logger.info("On supprime le train " +t.getNum());
							ofy().delete().type(Train.class).id(t.getNum()).now(); // une centaine d'écriture? (48*100 =4800)
						}
					}
				}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//TODO ?
		super.doGet(req, resp);

	}


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO ?
		super.doPost(req, resp);
	}

	public static Date stringToDate(String sDate) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return sdf.parse(sDate);
	} 


}


