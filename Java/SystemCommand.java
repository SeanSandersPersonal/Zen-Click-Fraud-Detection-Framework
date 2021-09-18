import java.util.ArrayList;
import java.util.List;
import java.io.*;
public class SystemCommand
{
	public static String ToSearch;
	public static String Loc;
	public static List<String> ArrayClasses = new ArrayList<String>();
	public static List<String> ArrayLines = new ArrayList<String>();
	SystemCommand(String SearchFor, String Location){
		ToSearch = SearchFor;
		Loc = Location;
	}
	public static void SearchForKeywordInMethod(){
		String command = "grep -ri "+ ToSearch + " " + Loc;  
		try {
		    Process process = Runtime.getRuntime().exec(command);
		    BufferedReader reader = new BufferedReader(
			    new InputStreamReader(process.getInputStream()));
		    String line;
		    String[] ArrayData;
		    while ((line = reader.readLine()) != null) {
			line = line.replaceAll(Loc, "").replaceAll(".jimple","");
			if(line.contains("public") && ! line.endsWith(";")){
				ArrayData = line.split(":  ");
				ArrayClasses.add(ArrayData[0].trim());
				ArrayLines.add(ArrayData[1].trim().replaceAll("public ","").trim().replaceAll(", ",","));
			}
		    }
		    reader.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
}
