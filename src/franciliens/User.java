package franciliens;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.images.Image;
import com.googlecode.objectify.annotation.*;

@Entity
@Index
public class User {
	String id;
	String login;
	String password;
	@Id String email;
	@Unindex Character sexe;
	@Unindex Image avatar;
	@Unindex Text description;
	 int age;
	
	public User(String id, String login, String password, String email){
		this.id=id;
		this.login=login;
		this.password=password;
		this.email=email;
	}
	
}
