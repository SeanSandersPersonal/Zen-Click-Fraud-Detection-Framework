package FileParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Constants.Constants;

public class LayoutFileParser {
	private File[] layoutFiles;
	private File[] androidAPKLocationuesFile;
	private Map<String, String> idToFile = new HashMap<String, String>();
	private Map<String, String> fileToID = new HashMap<String, String>();
	private Map<String, Set<String>> functionsFromXmlFile = new HashMap<String, Set<String>>();
	private Map<String, List<String>> fileToEmbededFiles = new HashMap<String, List<String>>();
	private List<String> layoutFilesList = new LinkedList<String>();
	private String AndroidAPKLocation;
	private String AndroidApkLayoutFolder;

	public LayoutFileParser(String androidAPKLocation) {
		AndroidAPKLocation = androidAPKLocation;
		AndroidApkLayoutFolder = "./LayoutOutput/" + Constants.AndroidAPKName;
		parseLayoutXmls();
		createLayoutIdAndFileMap();
		getFunctionsFromLayout();
		getEmbededLayoutsForFile();
		// try{
		// Runtime.getRuntime().exec("rmdir "+AndroidApkLayoutFolder+
		// " 2>null");
		// }catch(IOException e){
		// e.printStackTrace();
		// }
	}

	public List<String> getLayoutFilesNameList() {
		return this.layoutFilesList;
	}

	public Map<String, List<String>> getFileToEmbededFiles() {
		return this.fileToEmbededFiles;
	}

	public Map<String, String> getIdToFile() {
		return idToFile;
	}

	public Map<String, Set<String>> getFunctionsFromXmlFile() {
		return functionsFromXmlFile;
	}

	public void parseLayoutXmls() {
		try {
			// Runtime.getRuntime().exec("rmdir " + AndroidApkLayoutFolder+" 2>null");
			Process process = Runtime.getRuntime()
					.exec("java -jar apktool_2.5.0.jar" + " d -f" + AndroidAPKLocation + " " + AndroidApkLayoutFolder);

			int exitVandroidAPKLocationue = process.waitFor();
			if (exitVandroidAPKLocationue == 0) {
				File layoutFolder = new File(AndroidApkLayoutFolder + "/res/layout/");
				layoutFiles = layoutFolder.listFiles();
				File vandroidAPKLocationuesFolder = new File(AndroidApkLayoutFolder + "/res/vandroidAPKLocationues/");
				androidAPKLocationuesFile = vandroidAPKLocationuesFolder.listFiles();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void createLayoutIdAndFileMap() {

		if (androidAPKLocationuesFile == null)
			return;

		// just looping through files in vandroidAPKLocationues directory until we get
		// public.xml file
		int indexvalue;
		for (indexvalue = 0; indexvalue < androidAPKLocationuesFile.length; indexvalue++) {
			if (androidAPKLocationuesFile[indexvalue].toString().contains("public"))
				break;
		}

		BufferedReader Bufferedreader;
		try {
			Bufferedreader = new BufferedReader(new FileReader(androidAPKLocationuesFile[indexvalue].toString()));
			String LineInFile = null;

			while ((LineInFile = Bufferedreader.readLine()) != null) {
				String fields[] = LineInFile.split("\"");
				if (fields.length > 5) {
					if (fields[1].contains("layout")) {
						idToFile.put(fields[5], fields[3]);
						fileToID.put(fields[3], fields[5]);
					}
				}
			}

			Bufferedreader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getFunctionsFromLayout() {
		if (layoutFiles == null)
			return;

		for (int i = 0; i < layoutFiles.length; i++) {
			try {
				BufferedReader Bufferedreader = new BufferedReader(new FileReader(layoutFiles[i].toString()));
				String filePath = layoutFiles[i].toString();
				String subFields[] = filePath.split("/");
				/* ./LayoutOutput/res/layout/activity_group_messenger.xml */
				/* 4th field will have filename */
				String fullFileName[] = subFields[4].split("[/.]");
				/* 0 th filed will have the filename */
				String filename = fullFileName[0];
				layoutFilesList.add(filename);

				String LineInFile = null;
				while ((LineInFile = Bufferedreader.readLine()) != null) {
					if (!LineInFile.contains("android:onClick"))
						continue;

					String fields[] = LineInFile.split("\"");
					int index = 0;
					for (index = 0; index < fields.length; index++) {
						if (fields[index].contains("android:onClick")) {
							break;
						}
					}
					if (index >= fields.length - 1)
						continue;

					// this means the next field contains the onclick candroidAPKLocationlback
					// methods
					// the following if makes sure there is a method defined
					String function = fields[index + 1] + "(android.view.View)";
					if (!functionsFromXmlFile.containsKey(filename))
						functionsFromXmlFile.put(filename, new HashSet<String>());
					functionsFromXmlFile.get(filename).add(function);
				}

				Bufferedreader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * since layout can contain embedded layouts the following function will return
	 * a list of embedded layout file names for given layout file name
	 * 
	 * Assumption: androidAPKLocationl the layout files are placed inside layout
	 * folder there are no two different layout files with the same name, correct?
	 */
	public void getEmbededLayoutsForFile() {
		if (layoutFiles == null)
			return;

		for (int indexval = 0; indexval < layoutFiles.length; indexval++) {
			List<String> embeddedList = new LinkedList<String>();
			// parse layout file
			try {
				String filePath = layoutFiles[indexval].toString();
				String subFields[] = filePath.split("/");
				/* ./LayoutOutput/res/layout/activity_group_messenger.xml */
				/* 4th field will have filename */
				String fullFileName[] = subFields[4].split("[/.]");
				/* 0 th filed will have the filename */
				String filename = fullFileName[0];

				BufferedReader Bufferedreader = new BufferedReader(new FileReader(layoutFiles[indexval]));

				String LineInFile = null;
				while ((LineInFile = Bufferedreader.readLine()) != null) {
					// the embedded layout will use <include layout=""/> structure
					if (LineInFile.contains("include") && LineInFile.contains("@layout/")) {
						String[] fields = LineInFile.split("\"");
						int index = 0;
						for (index = 0; index < fields.length; index++) {
							if (fields[index].contains("@layout/"))
								break;
						}

						// this means we find the embedded layout, retrieve the filename
						int nameIndex = fields[index].indexOf("@layout/") + 8;
						String layoutFileName = fields[index].substring(nameIndex);
						embeddedList.add(layoutFileName);
					}
				}

				fileToEmbededFiles.put(filename, embeddedList);
				Bufferedreader.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
