package franciliens.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
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


// pour le parsing utilisation de l'Api SAX car ne nécessite pas d'enregistrer l'aborescence en mémoire
// "If the data structures have already been determined, and you are writing a server application or an XML filter that needs to do fast processing, see Simple API for XML." ORACLE
// http://www.javablog.fr/javaxml-parsing-xml-with-jaxp-sax-dom-standard-apis.html 
// http://java.developpez.com/faq/xml/?page=SAX#Comment-parser-un-XML-avec-SAX

// Finalement utilisation de JAXP DOM --> Faire un tableau contenant tous les numéros de gares et faire une boucle qui fait pour tab[i] les 30 requêtes??
@SuppressWarnings("serial")
public class BackendServlet extends HttpServlet {

	private static final Logger _logger = Logger.getLogger(BackendServlet.class.getName());
	private ArrayList<Train> listeTrain;
	private GaresSelectionnees []lesFameuses30Gares={GaresSelectionnees.ARG, GaresSelectionnees.AUL,
			GaresSelectionnees.AUS, GaresSelectionnees.BEL, GaresSelectionnees.BER, GaresSelectionnees.BIB,
			GaresSelectionnees.BON, GaresSelectionnees.CDG1, GaresSelectionnees.CDG2, GaresSelectionnees.CHA,
			GaresSelectionnees.CHANT, GaresSelectionnees.CHAT, GaresSelectionnees.COL, GaresSelectionnees.CYR,
			GaresSelectionnees.DEF, GaresSelectionnees.EST, GaresSelectionnees.EXP, GaresSelectionnees.GEN,
			GaresSelectionnees.GER, GaresSelectionnees.LAZ, GaresSelectionnees.LYON, GaresSelectionnees.MON,
			GaresSelectionnees.MUR, GaresSelectionnees.NAN, GaresSelectionnees.NORD, GaresSelectionnees.PUT,
			GaresSelectionnees.QUEN, GaresSelectionnees.STDF, GaresSelectionnees.VIL, GaresSelectionnees.VIT};

	@Override
	public void init() throws ServletException {

		// connexion à l'API lors de l'initialisation?
		// Non car il faut faire une connexion pour chaque requêtes, on en a 30 
		super.init();
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// On fait les choses dans le doGet ou directement dans le Service?
		doGet(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//TODO récupérer les données de l'API SNCF. Ensuite les parser puis créer les données dans le datastore
		// Attention: trouver un algo pour savoir ce qu'on doit remplacer dans le datstore/ajouter/supprimer
		// EN effet, il va falloir vérifier les trains supprimés, notifier les gens qui ont un trajet enregistrés! Ne 
		// pas supprimer un train du datastore si une personne possède un trajet enregistré dans ce train 
		
		for(int k=0; k<lesFameuses30Gares.length; k++){
			try{
				_logger.setLevel(Level.INFO);
				_logger.info("Nous allons accéder à l'api SNCF");
				URL url = new URL("http://api.transilien.com/gare/"+lesFameuses30Gares[k].getCode()+"/depart/");
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
				 *  Parc des Expositions 87271486
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


				 * 
				 */

				_logger.info("Nous avons accedé à l'api SNCF pour les trains de "+ lesFameuses30Gares[k].getNom());
				// Avec l'API JAXP DOM
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = factory.newDocumentBuilder();


				// XML Stream			
				InputStream xmlStream = con.getInputStream();

				Document doc = docBuilder.parse(xmlStream);

				//			// Parse the given XML document using the callback handler
				//			saxParser.parse(xmlStream, new XMLTrainHandler());


				// element racine
				Element rootElement = doc.getDocumentElement();

				// Récupérer tous les trains
				NodeList listTrain = rootElement.getElementsByTagName("train");

				listeTrain = new ArrayList<Train>();
				// Récupérer le premier train
				//Node train = listTrain.item(0);

				// boucle for pour récupérer tous les trains en questions on va traiter pour chaque train
				for(int i =0; i<listTrain.getLength();i++){
					Node train = listTrain.item(i);
					Train tchouttchout= new Train("", "", "", lesFameuses30Gares[k].getCode(), 0); // on peut mettre direct dans le new le code gare départ
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
								tchouttchout.setDateHeure(node.getFirstChild().getTextContent());
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

					listeTrain.add(tchouttchout);

				}
				//			 // récupérer tous les noeuds:
				//			 NodeList listTrain = doc.getElementsByTagName("*");
				//			 listeTrain= new ArrayList<Train>();
				//			 
				//			 for(int i=0; i<listTrain.getLength();i++){
				//				 Element passages = (Element) listTrain.item(i);
				//				 String noeud = passages.getNodeName();
				//				 Train tchouttchout= new Train("", "", "", ' ', 0, 0);
				//				 
				//				 if(noeud.equals("train")){
				//					 _logger.info("On a trouvé un train:");
				//				 }
				//				 if(noeud.equals("date")){
				//					 tchouttchout.setDateHeure(passages.getChildNodes().item(0).getNodeValue());
				//					 String mode= passages.getAttribute("mode");
				//					 tchouttchout.setEtat(mode.charAt(0));
				//					 
				//				 }else if(noeud.equals("num")){
				//					 tchouttchout.setNum(passages.getChildNodes().item(0).getNodeValue());
				//				 }
				//				 
				//				listeTrain.add(tchouttchout);
				//			 }


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
		}

	}


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO ?
		super.doPost(req, resp);
	}


}


