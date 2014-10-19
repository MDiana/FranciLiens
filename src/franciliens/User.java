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
	
	public User(String id, String login, String email,String password){
		this.id=id;
		this.login=login;
		this.password=password;
		this.email=email;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Character getSexe() {
		return sexe;
	}

	public void setSexe(Character sexe) {
		this.sexe = sexe;
	}

	public Image getAvatar() {
		return avatar;
	}

	public void setAvatar(Image avatar) {
		this.avatar = avatar;
	}

	public Text getDescription() {
		return description;
	}

	public void setDescription(Text description) {
		this.description = description;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
	
}
