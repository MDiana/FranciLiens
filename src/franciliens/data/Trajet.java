package franciliens.data;

import com.googlecode.objectify.annotation.*;

@Entity
@Index
public class Trajet {

	@Id private Long id;
	private String numTrain;
	private String pseudoUsager;

	@SuppressWarnings("unused")
	private Trajet() {}

	public Trajet(String numTrain, String pseudoUsager) {
		super();
		this.numTrain = numTrain;
		this.pseudoUsager = pseudoUsager;
	}

	public String getNumTrain() {
		return numTrain;
	}

	public String getPseudoUsager() {
		return pseudoUsager;
	}
	
}
