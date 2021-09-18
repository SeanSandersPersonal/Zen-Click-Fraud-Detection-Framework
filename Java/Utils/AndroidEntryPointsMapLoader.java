package Utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AndroidEntryPointsMapLoader {

	public static Map<String, Set<String>> entrypointMap_ = new HashMap<String, Set<String>>();

	public AndroidEntryPointsMapLoader() {
		this("input/EntryPoints.txt");
	}

	public AndroidEntryPointsMapLoader(String entrypointLocation) {

		BufferedReader inputBufferedReader;
		try {
			inputBufferedReader = new BufferedReader(new FileReader(entrypointLocation));
			String LineInFile;
			String lastClass = "";
			while ((LineInFile = inputBufferedReader.readLine()) != null) {
				LineInFile = LineInFile.trim();
				if (LineInFile.isEmpty())
					continue;
				if (LineInFile.startsWith("#")) // don's section of the txt file
					break;

				if (LineInFile.startsWith(":")) // is a method
				{
					if (lastClass.isEmpty())
						System.err.println("error parsing EntryPoints file");
					else
						entrypointMap_.get(lastClass).add(LineInFile);
				} else // is a class
				{
					lastClass = LineInFile;
					entrypointMap_.put(lastClass, new HashSet<String>());

				}
			}
			inputBufferedReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Map<String, Set<String>> getEPMap() {
		return entrypointMap_;
	}

}
