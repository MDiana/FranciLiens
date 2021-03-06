package franciliens.data;

import com.googlecode.objectify.annotation.*;

@Entity
@Index
public class Trajet {

	@Id private Long id;
	private Long idPassage;

	private String pseudoUsager;

	@SuppressWarnings("unused")
	private Trajet() {}

	public Trajet(Long idPassage, String pseudoUsager) {
		super();
		this.idPassage = idPassage;
		this.pseudoUsager = pseudoUsager;
	}

	public Long getId() {
		return id;
	}

	public Long getIdPassage() {
		return idPassage;
	}
	
	public void setIdPassage(Long idPassage) {
		this.idPassage = idPassage;
	}

	public String getPseudoUsager() {
		return pseudoUsager;
	}
	
}
