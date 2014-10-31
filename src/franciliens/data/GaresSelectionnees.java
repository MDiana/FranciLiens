package franciliens.data;

public enum GaresSelectionnees {

	CDG1(87271460, "Aéroport Ch. De Gaulle 1"),
//	CDG2(87001479, "Aéroport Ch. De Gaulle 2 TGV"),
	ARG(87381848, "Argenteuil"),
	AUL(87271411, "Aulnay-Sous-Bois"),
	BEL(87393116, "Bellevue"),
//	BIB(87328328, "Bibliothèque François Mitterrand"),
//	BON(87113407, "Bondy"),
	CHA(87758607, "Châtelet les Halles"),
//	COL(87381087, "Colombes"),
//	GEN(87271205, "Gennevilliers"),
	DEF(87758011, "Le Défense Grande Arche"),
//	MUR(87386680, "Les Mureaux"),
	NAN(87386318, "Nanterres Université"),
	EXP(87271486, "Parc des Expositions"),
	AUS(87547026, "Paris Austerlitz"),
	BER(87686667, "Paris Bercy"),
	EST(87113001, "Paris Est"),
	LYON(87686006, "Paris Gare de Lyon"),
	MON(87391003, "Paris Montparnasse"),
	NORD(87271007, "Paris Nord"),
	LAZ(87384008, "Paris Saint-Lazare"),
//	PUT(87382382, "Puteaux"),
//	CYR(87393223, "Saint-Cyr"),
	GER(87382804, "Saint-Germain-En-Laye GC"),
	QUEN(87393843, "Saint-Quentin en Y. Montigny le B."),
	STDF(87164780, "Stade de France Saint-Denis"),
	CHANT(87393009, "Versailles Chantiers"),
	CHAT(87393157, "Versailles Château Rive Gauche");
//	VIT(87545293, "Vitry-Sur-Seine"),
//	VIL(87113795, "Villiers-Sur-Marne Le Plessis Trévise");
	
	private int code=0;
	private String nom="";
	
	private GaresSelectionnees(int code, String nom) {
		this.code=code;
		this.nom=nom;
	}

	public int getCode() {
		return code;
	}

	public String getNom() {
		return nom;
	}
	
	public static String getNom(int code) {
		for (GaresSelectionnees g : GaresSelectionnees.values()) {
			if (g.getCode()==code) {
				return g.getNom();
			}
		}
		return null;
	}
}
