import java.util.ArrayList;
import java.util.Collection;

public class PrescoutingForm extends Form {

	ArrayList<String> scoutNames;

	public PrescoutingForm(int tabletNum, int teamNum, ArrayList<String> scoutNames) {
		super(FormTypes.PRESCOUTING_FORM, tabletNum, teamNum);
		this.scoutNames = scoutNames;
	}
	
	public PrescoutingForm(int reportID, int tabletNum, int teamNum, ArrayList<String> scoutNames) {
		super(reportID, FormTypes.PRESCOUTING_FORM, tabletNum, teamNum);
		this.scoutNames = scoutNames;
	}
	
	public PrescoutingForm(int tabletNum, int teamNum, String... scoutNames) {
		super(FormTypes.PRESCOUTING_FORM, tabletNum, teamNum);
		for (int i = 0; i < scoutNames.length; i++) this.scoutNames.add(scoutNames[i]);
	}
	
	public PrescoutingForm(int reportID, int tabletNum, int teamNum, String... scoutNames) {
		super(reportID, FormTypes.PRESCOUTING_FORM, tabletNum, teamNum);
		for (int i = 0; i < scoutNames.length; i++) this.scoutNames.add(scoutNames[i]);
	}
	
	public PrescoutingForm(int tabletNum, int teamNum) {
		super(FormTypes.PRESCOUTING_FORM, tabletNum, teamNum);
		scoutNames = new ArrayList<>();
	}
	
	public PrescoutingForm(int reportID, int tabletNum, int teamNum) {
		super(reportID, FormTypes.PRESCOUTING_FORM, tabletNum, teamNum);
		scoutNames = new ArrayList<>();
	}
	
	public boolean addScout(String scoutName) {
		return scoutNames.add(scoutName);
	}
	
	public boolean addScouts(Collection<? extends String> scoutNames) {
		return this.scoutNames.addAll(scoutNames);
	}
	
	public boolean addScouts(String[] scoutNames) {
		boolean bool = true;
		for (int i = 0; i < scoutNames.length; i++) 
			if (this.scoutNames.add(scoutNames[i]) == false) bool = false;
		return bool;
	}

	public String getScout(int index) {
		return scoutNames.get(index);
	}
	
	public String removeScout(int index) {
		return scoutNames.remove(index);
	}
	
	public boolean removeScout(String scoutName) {
		return scoutNames.remove(scoutName);
	}

	public boolean removeScouts(Collection<? extends String> scoutNames) {
		return this.scoutNames.removeAll(scoutNames);
	}

	public boolean removeScouts(String[] scoutNames) {
		boolean bool = true;
		for (int i = 0; i < scoutNames.length; i++) 
			if (this.scoutNames.remove(scoutNames[i]) == false) bool = false;
		return bool;
	}
	
}
