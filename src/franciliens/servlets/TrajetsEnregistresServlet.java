package franciliens.servlets;

import static franciliens.data.OfyService.ofy;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import franciliens.data.GaresSelectionnees;
import franciliens.data.PassageEnGare;
import franciliens.data.ToutesGares;
import franciliens.data.Trajet;
import franciliens.data.User;

@SuppressWarnings("serial")
public class TrajetsEnregistresServlet extends HttpServlet {

	private List<PassageEnGare> listpassageGare;




	@Override
	public void init() throws ServletException {
		Trajet tr1= new Trajet(new Long("4925812092436480") ,"DieNah");
		ofy().save().entity(tr1).now();
		Trajet tr2= new Trajet(new Long("6051711999279104") ,"Juju");
		ofy().save().entity(tr2).now();
	}




	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		JSONArray listDesTrajetsEnregistres= new JSONArray();
		JSONObject envoi = new JSONObject();
		JSONObject trajet=new JSONObject();

		// récupérer les départs d'une gare et regarder si un des num de trains appartient au num 
		// de la liste des trajets enregistrés
		String nomGarePasse =req.getParameter("gare");
		int codeGare= GaresSelectionnees.getCode(nomGarePasse);
		listpassageGare = ofy().load().type(PassageEnGare.class).filter("codeUICGareDepart", codeGare).list();

		try {
			// récupérer la liste des trajets enregistrés dans cette gare
			for(PassageEnGare peg : listpassageGare){
				List <Trajet> tra = ofy().load().type(Trajet.class).filter("idPassage", peg.getId()).list();
				if(!tra.isEmpty()){
				for(Trajet tr : tra){
					// faire ici la récupération des users etc... et on crée l'objet json que l'on ajoute au tableau json
					User userTrajet= ofy().load().type(User.class).filter("login", tr.getPseudoUsager()).list().get(0);
					trajet.put("avatarmini", userTrajet.getAvatarURL());
					trajet.put("pseudo", userTrajet.getLogin());
					trajet.put("age", userTrajet.getAge());
					String terminus = ToutesGares.getNom(peg.getCodeUICTerminus());
					trajet.put("term",terminus);
					trajet.put("description", userTrajet.getDescription());
					trajet.put("idPassage", tr.getIdPassage());

					listDesTrajetsEnregistres.put(trajet);
					trajet=new JSONObject();
				}
				}
			}
			
				envoi.put("Trajets Enregistres", listDesTrajetsEnregistres);
			} catch (JSONException e) {
				e.printStackTrace();
			}


			resp.setContentType("application/json");
			PrintWriter out= resp.getWriter();
			out.print(envoi);
		}



	}
