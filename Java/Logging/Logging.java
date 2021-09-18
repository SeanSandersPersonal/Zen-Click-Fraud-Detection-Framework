package Logging;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.Value;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.StringConstant;
import java.util.Iterator;
import java.util.*;
import Logging.Logging;
import FileWriter.FileWriterHelper;
import FileHandler.FileHandler;

public class Logging {
	private static String AndroidAppName;
	public static int LogCount = 0;
	public static int LogCountAppStarted = 0;
	public static int LogCountLoadAd = 0;
	public static int LogCountShowAd = 0;
	public static int LogCountImpression = 0;
	public static int LogCountAdClickedListener = 0;
	public static int LogCountAdClicked = 0;
	public static int LogCountAdRegistered = 0;
	public static int LogCountAdListenerCreated = 0;
	public static int LogCountAdListenerSet = 0;
	public static int LogCountAdOptionsSet = 0;
	public static int LogCountAdLoadedSuccessfully = 0;

	public Logging(String appname) {
		AndroidAppName = appname;
	}

	public static void IncrementLogCount(String StateName) {
		LogCount += 1;
		switch (StateName) {
			case "LogCountAppStarted":
				LogCountAppStarted += 1;
				break;
			case "LogCountLoadAd":
				LogCountLoadAd += 1;
				break;
			case "LogCountShowAd":
				LogCountShowAd += 1;
				break;
			case "LogCountImpression":
				LogCountImpression += 1;
				break;
			case "LogCountAdClickedListener":
				LogCountAdClickedListener += 1;
				break;
			case "LogCountAdClicked":
				LogCountAdClicked += 1;
				break;
			case "LogCountAdRegistered":
				LogCountAdRegistered += 1;
				break;
			case "LogCountAdListenerCreated":
				LogCountAdListenerCreated += 1;
				break;
			case "LogCountAdListenerSet":
				LogCountAdListenerSet += 1;
				break;
			case "LogCountAdOptionsSet":
				LogCountAdOptionsSet += 1;
				break;
			case "LogCountAdLoadedSuccessfully":
				LogCountAdLoadedSuccessfully += 1;
				break;
		}
		// System.out.println("Incrementing LogCount for:"+StateName);
	}

	public static void LogMethodAndClass(String Class, String method) {
		String PathToWriteTo = "AnalysisFileLogs/OutputFilesFromAnalysis/LogsAPK/" + AndroidAppName + ".txt";
		FileHandler filehandler = new FileHandler(AndroidAppName);
		if (!filehandler.CheckIfFileContains(Class)) {
			FileWriterHelper.WriteToFile(PathToWriteTo, "\r\nClass: " + Class);
		}
		String MethodToWrite = method.replace("<", "").replace(">", "").replace(":", "").replace(Class, "");
		if (!filehandler.CheckIfFileContainsLastLine(MethodToWrite)) {
			FileWriterHelper.WriteToFile(PathToWriteTo, "\r\n" + AndroidAppName + ":" + MethodToWrite);
		}
	}

	public static void LogTheLibrary(String Library) {
		String ToWriteToFile = AndroidAppName + ":" + Library + "\n";
		FileWriterHelper.WriteToFile("AnalysisFileLogs/OutputFilesFromAnalysis/AdLibrary.txt", ToWriteToFile);
	}

	public static void LogTheLogCount() {
		List<String> ToWrite = new ArrayList<String>();
		String PathToWriteTo = "AnalysisFileLogs/OutputFilesFromAnalysis/LogCount/LogCount.txt";
		String ToWriteToFile = AndroidAppName + ":TotalNumberOfLogs:" + String.valueOf(LogCount) + "\n";
		if (LogCount != 0) {
			FileWriterHelper.WriteToFile(PathToWriteTo, ToWriteToFile);
			ToWrite.add(ToWriteToFile);
		}
		ToWriteToFile = AndroidAppName + ":App Started:" + String.valueOf(LogCountAppStarted) + "\n";
		if (LogCountAppStarted != 0) {
			FileWriterHelper.WriteToFile(PathToWriteTo, ToWriteToFile);
			ToWrite.add(ToWriteToFile);
		}
		ToWriteToFile = AndroidAppName + ":LoadAd:" + String.valueOf(LogCountLoadAd) + "\n";
		if (LogCountLoadAd != 0) {
			FileWriterHelper.WriteToFile(PathToWriteTo, ToWriteToFile);
			ToWrite.add(ToWriteToFile);
		}
		ToWriteToFile = AndroidAppName + ":ShowAd:" + String.valueOf(LogCountShowAd) + "\n";
		if (LogCountShowAd != 0) {
			FileWriterHelper.WriteToFile(PathToWriteTo, ToWriteToFile);
			ToWrite.add(ToWriteToFile);
		}
		ToWriteToFile = AndroidAppName + ":AdClicked:" + String.valueOf(LogCountAdClicked) + "\n";
		if (LogCountAdClicked != 0) {
			FileWriterHelper.WriteToFile(PathToWriteTo, ToWriteToFile);
			ToWrite.add(ToWriteToFile);
		}
		ToWriteToFile = AndroidAppName + ":AdClickedListener:" + String.valueOf(LogCountAdClickedListener) + "\n";
		if (LogCountAdClickedListener != 0) {
			FileWriterHelper.WriteToFile(PathToWriteTo, ToWriteToFile);
			ToWrite.add(ToWriteToFile);
		}
		ToWriteToFile = AndroidAppName + ":ClickRegistered:" + String.valueOf(LogCountImpression) + "\n";
		if (LogCountImpression != 0) {
			FileWriterHelper.WriteToFile(PathToWriteTo, ToWriteToFile);
			ToWrite.add(ToWriteToFile);
		}
	}

	public InvokeStmt ReturnStmtLog(String Message) {
		List<Value> listArgs = new ArrayList<Value>();
		listArgs.add(StringConstant.v("DebugInfo"));
		listArgs.add(StringConstant.v(Message));
		StaticInvokeExpr LogInvokeStmt = Jimple.v().newStaticInvokeExpr(
				Scene.v().getMethod("<android.util.Log: int d(java.lang.String,java.lang.String)>").makeRef(),
				listArgs);
		InvokeStmt InvokeStatementLog = Jimple.v().newInvokeStmt(LogInvokeStmt);
		System.out.println(InvokeStatementLog.toString());
		return InvokeStatementLog;
	}

	public void InjectLogIntoMethod(String KeywordToSearchFor, String Message, SootMethod method) {
		UnitPatchingChain units = method.retrieveActiveBody().getUnits();
		// Unit UnitToInsertAfter = null;
		Unit onCreateUnit = null;
		for (Iterator<Unit> unit = units.iterator(); unit.hasNext();) {
			Unit LastKnownUnit = unit.next();
			String StringLastKnownUnit = LastKnownUnit.toString();
			if (StringLastKnownUnit.contains(KeywordToSearchFor)) {
				onCreateUnit = LastKnownUnit;
				break;
			}
		}
		if (onCreateUnit != null) {
			InvokeStmt InvokeStatementLog = ReturnStmtLog(Message);
			units.insertAfter(InvokeStatementLog, onCreateUnit);
		}
	}

	public void InjectLogIntoMethodAtBeginning(String Message, SootMethod method) {
		UnitPatchingChain units = method.retrieveActiveBody().getUnits();
		// Unit UnitToInsertAfter = null;
		Unit onCreateUnit = null;
		for (Iterator<Unit> u = units.iterator(); u.hasNext();) {
			Unit LastKnownUnit = u.next();
			String StringLastKnownUnit = LastKnownUnit.toString();
			if (StringLastKnownUnit.contains("virtualinvoke") || StringLastKnownUnit.contains("specialinvoke")
					|| StringLastKnownUnit.contains(" = ")) {
				onCreateUnit = LastKnownUnit;
				break;
			}
		}
		if (onCreateUnit != null) {
			InvokeStmt InvokeStatementLog = ReturnStmtLog(Message);
			units.insertAfter(InvokeStatementLog, onCreateUnit);
		}
	}

	public void InjectLogAfterUnit(Unit unit, String Message, SootMethod method) {
		if (method.hasActiveBody()) {
			UnitPatchingChain units = method.retrieveActiveBody().getUnits();
			InvokeStmt InvokeStatementLog = ReturnStmtLog(AndroidAppName + ":" + Message);
			units.insertAfter(InvokeStatementLog, unit);
		}
	}

	public void InjectLogBeforeUnit(Unit unit, String Message, SootMethod method) {

		UnitPatchingChain units = method.retrieveActiveBody().getUnits();
		InvokeStmt InvokeStatementLog = ReturnStmtLog(Message);
		units.insertBefore(InvokeStatementLog, unit);
	}

	public void InjectLogIntoMethodAfterSearchWordFoundInUnit(String KeywordToSearchFor, String Message,
			SootMethod method) {
		UnitPatchingChain units = method.retrieveActiveBody().getUnits();
		for (Iterator<SootClass> ClassIterator = Scene.v().getApplicationClasses().iterator(); ClassIterator
				.hasNext();) {
			final SootClass Class = ClassIterator.next();
			for (Iterator<SootMethod> MethodIterator = Class.getMethods().iterator(); MethodIterator.hasNext();) {
				final SootMethod Method = MethodIterator.next();
				if (Method.isConcrete()) {
					units = Method.retrieveActiveBody().getUnits();

					for (Iterator<Unit> unit = units.snapshotIterator(); unit.hasNext();) {
						Unit LastKnownUnit = unit.next();
						String StringLastKnownUnit = LastKnownUnit.toString();
						if (StringLastKnownUnit.toLowerCase().contains(KeywordToSearchFor)
								&& StringLastKnownUnit.contains("virtualinvoke")
								&& !StringLastKnownUnit.contains(":=")) {
							InjectLogBeforeUnit(LastKnownUnit, Class.toString() + "-->" + Method.getName().toString()
									+ "-->" + StringLastKnownUnit, Method);
						}
					}
				}
			}
		}
	}
}
