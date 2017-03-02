import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class ItemLoader {

	public static void main(String[] args) throws IOException {

		ArrayList<Integer> itemIDs = new ArrayList<>();
		ArrayList<String> varNames = new ArrayList<>();
		ArrayList<String> itemNames = new ArrayList<>();
		ArrayList<Integer> itemDatatypes = new ArrayList<>();
		
		Scanner in = new Scanner(new File("E:/items.txt"));
		while (in.hasNextLine()) {
			itemIDs.add(in.nextInt());
			itemDatatypes.add(in.nextInt());
			String name = in.nextLine();
			varNames.add(name.trim().toUpperCase().replaceAll(" ", "_").replaceAll("\\?", "").replaceAll(":", ""));
			itemNames.add(name.trim());
		}
		in.close();
		
		for (int i = 0; i < itemIDs.size(); i++) {
			String datatype = "Item.Datatype.";
			if (itemDatatypes.get(i)-1 == Item.Datatype.BOOLEAN.ordinal()) datatype += "BOOLEAN";
			else if (itemDatatypes.get(i)-1 == Item.Datatype.INTEGER.ordinal()) datatype += "INTEGER";
			else if (itemDatatypes.get(i)-1 == Item.Datatype.STRING.ordinal()) datatype += "STRING";
			else if (itemDatatypes.get(i)-1 == Item.Datatype.OPTIONS.ordinal()) datatype += "OPTIONS";
			System.out.println("\t\tpublic static final Item " + varNames.get(i)
				+ " = new Item(" + itemIDs.get(i) + ", \"" + itemNames.get(i) + "\", " + datatype + ");");
		}
		
	}

}
