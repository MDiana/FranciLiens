package franciliens.data;

import com.googlecode.objectify.annotation.*;

@Entity
@Index
public class Train {

	@Id private String num;
	private String dateHeure;
	private String mission;
	private Character etat;
	private int codeUICGareDepart;
	private int codeUICTerminus;

	@SuppressWarnings("unused")
	private Train() {}

	public Train(String num, String dateHeure, String mission, char etat, int codeUICGareDepart, int codeUICTerminus){
		this.num = num;
		this.dateHeure = dateHeure;
		this.mission = mission;
		this.etat = etat;
		this.codeUICGareDepart = codeUICGareDepart;
		this.codeUICTerminus = codeUICTerminus;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getDateHeure() {
		return dateHeure;
	}

	public void setDateHeure(String dateHeure) {
		this.dateHeure = dateHeure;
	}

	public String getMission() {
		return mission;
	}

	public void setMission(String mission) {
		this.mission = mission;
	}

	public char getEtat() {
		return etat;
	}

	public void setEtat(char etat) {
		this.etat = etat;
	}

	public int getCodeUICGareDepart() {
		return codeUICGareDepart;
	}

	public void setCodeUICGareDepart(int codeUICGareDepart) {
		this.codeUICGareDepart = codeUICGareDepart;
	}

	public int getCodeUICTerminus() {
		return codeUICTerminus;
	}

	public void setCodeUICTerminus(int codeUICTerminus) {
		this.codeUICTerminus = codeUICTerminus;
	}



}

