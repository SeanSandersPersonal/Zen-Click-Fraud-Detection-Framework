package FileHandler;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class FileHandler {
	private static String AndroidAppName;

	public FileHandler(String AndroidApplicationName) {
		AndroidAppName = AndroidApplicationName;
	}

	public boolean CheckIfFileContainsLastLine(String StringLineToSearchFor) {
		File file = new File("AnalysisFileLogs/OutputFilesFromAnalysis/LogsAPK/" + AndroidAppName + ".txt");
		try {
			String LastLineInFile = null;
			Scanner filescanner = new Scanner(file);
			while (filescanner.hasNextLine()) {
				String CurrentLineInFile = filescanner.nextLine();
				LastLineInFile = CurrentLineInFile;
			}
			filescanner.close();
			if (LastLineInFile != null && LastLineInFile.contains(StringLineToSearchFor)
					&& LastLineInFile.contains("Class:")) {
				return true;
			}
		} catch (FileNotFoundException e) {
			// handle this
		}
		return false;

	}

	public boolean CheckIfFileContains(String StringToSearchFor) {
		File file = new File("AnalysisFileLogs/OutputFilesFromAnalysis/LogsAPK/" + AndroidAppName + ".txt");
		try {
			Scanner filescanner = new Scanner(file);
			while (filescanner.hasNextLine()) {
				String CurrentLineInFile = filescanner.nextLine();
				if (CurrentLineInFile.contains(StringToSearchFor)) {
					return true;
				}
			}
			filescanner.close();
		} catch (FileNotFoundException e) {
			// handle this
		}
		return false;
	}

	public boolean CheckIfLogCountFileContains(String StringToSearchFor) {
		File file = new File("AnalysisFileLogs/OutputFilesFromAnalysis/LogCount/LogCount.txt");

		try {
			Scanner filescanner = new Scanner(file);
			while (filescanner.hasNextLine()) {
				String CurrentLineInFile = filescanner.nextLine();
				if (CurrentLineInFile.contains(StringToSearchFor)) {
					return true;
				}
			}
			filescanner.close();
		} catch (FileNotFoundException e) {
			// handle this
		}
		return false;
	}
}
