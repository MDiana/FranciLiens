package franciliens.data;

import com.googlecode.objectify.annotation.*;

@Entity
@Index
public class Trajet {

	@Id private Long id;
	private int numTrain;
	private String pseudoUsager;

	@SuppressWarnings("unused")
	private Trajet() {}

	public Trajet(int numTrain, String pseudoUsager) {
		super();
		this.numTrain = numTrain;
		this.pseudoUsager = pseudoUsager;
	}

	public int getNumTrain() {
		return numTrain;
	}

	public String getPseudoUsager() {
		return pseudoUsager;
	}
	
}
