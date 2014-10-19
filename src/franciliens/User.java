package franciliens;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.annotation.*;

@Entity
@Index
public class User {
	String login;
	@Unindex String password;
	@Id String email;
	Character sexe;
	@Unindex String avatarURL;
	@Unindex Text description;
	int age;

	@SuppressWarnings("unused")
	private User() {}

	public User(String login, String email, String password){
		this.login=login;
		this.password=password;
		this.email=email;
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

	public String getAvatarURL() {
		return avatarURL;
	}

	public void setAvatarURL(String avatarURL) {
		this.avatarURL = avatarURL;
	}

	
}
