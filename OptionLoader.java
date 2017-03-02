import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class OptionLoader {

	public static void main(String[] args) throws IOException {

		ArrayList<String> varNames = new ArrayList<>();
		ArrayList<Integer> optionVals = new ArrayList<>();
		ArrayList<Integer> optionFks = new ArrayList<>();
		ArrayList<String> itemNames = new ArrayList<>();
		
		Scanner in = new Scanner(new File("E:/option.txt"));
		while (in.hasNextLine()) {
			varNames.add(in.next());
			optionVals.add(in.nextInt());
			optionFks.add(in.nextInt());
			itemNames.add(in.nextLine().trim().toUpperCase().replaceAll(" ", "_").replaceAll("\\?", "").replaceAll(":", "").replaceAll("-", "_"));
 		}
		in.close();
		
		ArrayList<String> finalClasses = new ArrayList<>();
		
		String lastClass = "";
		for (int i = 0; i < itemNames.size(); i++) 
			if (!lastClass.equals(itemNames.get(i))) {
				finalClasses.add("\t\tpublic static final class " + itemNames.get(i) + " {\n");
				lastClass = itemNames.get(i);
			}
		
		int currentFk = -1;
		int currentFinalClass = -1;
		for (int i = 0; i < varNames.size(); i++) {
			if (optionFks.get(i) != currentFk) {
				if (currentFk != -1) finalClasses.set(currentFinalClass, finalClasses.get(currentFinalClass) + "\t\t}\n");
				currentFinalClass++;
				currentFk = optionFks.get(i);
			}
			String varName =
					varNames.get(i).trim().toUpperCase().replaceAll(" ", "_").replaceAll("\\?", "")
					.replaceAll(":", "").replace("-", "_TO_").replaceAll("\\+", "").replaceAll("%", "");
			if ((varNames.get(i).charAt(0) >= 48) && (varNames.get(i).charAt(0) <= 57)) varName = "FROM_" + varName;
			
			finalClasses.set(currentFinalClass, finalClasses.get(currentFinalClass) + "\t\t\tpublic static final Option "
					+ varName + " = new Option(\"" + varNames.get(i) + "\", " + optionVals.get(i) + ", " + optionFks.get(i) + ");\n");
		}
		finalClasses.set(currentFinalClass, finalClasses.get(currentFinalClass) + "\t\t}\n");
		for (int i = 0; i < finalClasses.size(); i++) System.out.println(finalClasses.get(i));
		
	}

}