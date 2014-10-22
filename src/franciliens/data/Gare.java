package franciliens.data;
import com.googlecode.objectify.annotation.*;

@Entity
@Index
public class Gare {
	@Id String uic;
	String name;
	
	@SuppressWarnings("unused")
	private Gare(){}
	
	public Gare(String uic, String name) {
		this.uic=uic;
		this.name=name;
	}

	public String getUic() {
		return uic;
	}

	public void setUic(String uic) {
		this.uic = uic;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
