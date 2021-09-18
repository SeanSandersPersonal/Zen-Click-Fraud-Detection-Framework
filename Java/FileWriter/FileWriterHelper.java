package FileWriter;

import java.io.FileWriter;
import java.io.IOException;

public class FileWriterHelper {
	public FileWriterHelper() {
	}

	public static void WriteToFile(String FileLocationToWriteTo, String ToWriteToFile) {
		try {
			FileWriter myWriterLogLibrary = new FileWriter(FileLocationToWriteTo, true);
			myWriterLogLibrary.write(ToWriteToFile);
			myWriterLogLibrary.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

	}

}
