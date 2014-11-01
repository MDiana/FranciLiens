package franciliens.data;

import java.util.Date;

import com.googlecode.objectify.annotation.*;

@Entity
@Unindex
public class PassageEnGare {

	@Id Long id;
	@Index private String num;
	@Index private Date dateHeure;
	private String mission;
	private String etat;
	@Index private int codeUICGareDepart;
	private int codeUICTerminus;

	@SuppressWarnings("unused")
	private PassageEnGare() {}
	
	public PassageEnGare(String num, Date dateHeure, String mission, int codeUICGareDepart, int codeUICTerminus){
		this.num = num;
		this.dateHeure = dateHeure;
		this.mission = mission;
		this.etat = "A l'heure";
		this.codeUICGareDepart = codeUICGareDepart;
		this.codeUICTerminus = codeUICTerminus;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public Date getDateHeure() {
		return dateHeure;
	}

	public void setDateHeure(Date dateHeure) {
		this.dateHeure = dateHeure;
	}

	public String getMission() {
		return mission;
	}

	public void setMission(String mission) {
		this.mission = mission;
	}

	public String getEtat() {
		return etat;
	}

	public void setEtat(String etat) {
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

	// equals peut-être utile: deux trains sont équivalents si même numéro, même date, même gare départ
	public boolean equals(PassageEnGare t){
		if((this.dateHeure==t.getDateHeure()) &&(this.num == t.num) && (this.codeUICGareDepart == t.getCodeUICGareDepart())){
			return true;
		} else return false;
	}

}

