package parser.highcaliberline;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="HighCalColors")
@Entity
public class HighCalColorEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -226942991482442498L;

	@Id
	@Column(name="colorId")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int colorid;

	@Column(name="ColorVals")
	private String colorvalue;

	public int getColorid() {
		return colorid;
	}

	public void setColorid(int colorid) {
		this.colorid = colorid;
	}

	public String getColorvalue() {
		return colorvalue;
	}

	public void setColorvalue(String colorvalue) {
		this.colorvalue = colorvalue;
	}
	
	
}
