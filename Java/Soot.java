import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.Value;
import soot.ValueBox;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.VirtualInvokeExpr;
import java.util.Iterator;
import java.util.*;
import Logging.Logging;
import Conditions.SootConditionChecker;

public class Soot {
	public static List<SootMethod> entryPoints = null;
	public static SootMethod meth = null;
	public static String AndroidAppName = null;
	public static boolean Google = false;
	public static boolean Facebook = false;
	public static boolean Amazon = false;
	public static boolean RunOnce;
	public static String MainActivity = null;
	List<String> ClassNameLogArray = new ArrayList<String>();

	public Soot(String AndroidApplicationName) {
		AndroidAppName = AndroidApplicationName;
		Google = true;
		Facebook = true;
		Amazon = true;
		RunOnce = false;

	}

	public void RunSoot() {
		EntryPointsManager entrypointsmanager = new EntryPointsManager("APK/" + AndroidAppName);
		entrypointsmanager.loadApkEntryPoints();
		entryPoints = entrypointsmanager.getApkEntryPoints();
		// entrypointsmanager.findMainActivity();
		// System.out.println("Main:"+String.valueOf(EntryPointsManager.MainEntryPoint));

		UnitPatchingChain units = null;
		Logging logging = new Logging(AndroidAppName);
		SootConditionChecker sootconditionchecker = new SootConditionChecker();
		String entryPoint = null;

		for (int index = 0; index < entryPoints.size(); index++) {
			SootMethod sootmethodtocheck = entryPoints.get(index);
			entryPoint = String.valueOf(sootmethodtocheck);
			Boolean entryPointHasActiveBody = sootmethodtocheck.hasActiveBody();
			SootClass Class = sootmethodtocheck.getDeclaringClass();
			boolean GoogleLibraryExists = sootconditionchecker.CheckIfClassExists(Class, "google");
			boolean FacebookLibraryExists = sootconditionchecker.CheckIfClassExists(Class, "facebook");
			boolean AmazonLibraryExists = sootconditionchecker.CheckIfClassExists(Class, "amazon");
			if (GoogleLibraryExists && Google) {
				Logging.LogTheLibrary("google");
				System.out.println(Class.toString() + " Library Found");
				Google = false;
			}
			if (FacebookLibraryExists && Facebook) {
				Logging.LogTheLibrary("facebook");
				System.out.println(Class.toString() + " Library Found");
				Facebook = false;
			}
			if (AmazonLibraryExists && Amazon) {
				Logging.LogTheLibrary("amazon");
				System.out.println(Class.toString() + " Library Found");
				Amazon = false;
			}

			if (!(GoogleLibraryExists || FacebookLibraryExists || AmazonLibraryExists) && entryPointHasActiveBody) {
				SootMethod sootmethod = entryPoints.get(index);
				if (sootmethod.hasActiveBody()) {
					units = sootmethod.retrieveActiveBody().getUnits();
				}
				for (Iterator<Unit> unit = units.snapshotIterator(); unit.hasNext();) {
					Unit LastKnownUnit = unit.next();
					String StringLastKnownUnit = LastKnownUnit.toString();
					boolean LastKnownUnitIsAStmt = sootconditionchecker.LastKnownUnitIsAStatement(LastKnownUnit);
					if (LastKnownUnitIsAStmt) {
						for (ValueBox SootValuebox : LastKnownUnit.getUseBoxes()) {
							Value SootValue = SootValuebox.getValue();
							VirtualInvokeExpr VirtualInvokeExpression = null;
							SpecialInvokeExpr SpecialInvokeExpression = null;
							String MethodName = null;
							boolean DeclaringClassHasAdLibrary = false;
							String StringDeclaringClass = null;
							boolean SootValueIsAVirtualInvokeExpr = sootconditionchecker
									.ValueIsAVirtualInvokeExpr(SootValue);
							boolean SootValueIsASpecialInvokeExpr = sootconditionchecker
									.ValueIsASpecialInvokeExpr(SootValue);
							if (RunOnce == false && sootmethod.toString().toLowerCase().contains("mainactivity")
									&& sootmethod.toString().toLowerCase().contains("oncreate(android.os.bundle)")) {
								System.out.println("Found oncreate");
								logging.InjectLogAfterUnit(LastKnownUnit, "App Started:" + StringLastKnownUnit,
										sootmethod);
								Logging.IncrementLogCount("LogCountAppStarted");
								RunOnce = true;
							}
							if (SootValueIsAVirtualInvokeExpr) {
								VirtualInvokeExpression = (VirtualInvokeExpr) SootValue;
								MethodName = VirtualInvokeExpression.getMethod().getName().toLowerCase().toString();
								StringDeclaringClass = VirtualInvokeExpression.getMethodRef().getDeclaringClass()
										.toString();
								DeclaringClassHasAdLibrary = sootconditionchecker
										.CheckIfDeclaringClassHasAdLibrary(StringDeclaringClass);
							}
							if (SootValueIsASpecialInvokeExpr) {
								SpecialInvokeExpression = (SpecialInvokeExpr) SootValue;
								MethodName = SpecialInvokeExpression.getMethod().getName().toLowerCase().toString();
								StringDeclaringClass = SpecialInvokeExpression.getMethodRef().getDeclaringClass()
										.toString();
								DeclaringClassHasAdLibrary = sootconditionchecker
										.CheckIfDeclaringClassHasAdLibrary(StringDeclaringClass);
							}
							if (!(SootValueIsASpecialInvokeExpr || SootValueIsAVirtualInvokeExpr)) {
								MethodName = null;
							}

							if (SootValueIsAVirtualInvokeExpr
									&& sootconditionchecker.ValueToLowerContainsString(SootValue, "loadad()")
									&& sootconditionchecker.CheckifVirtualInvokeExprMethodContains(
											VirtualInvokeExpression, SootValue, "loadad")
									&& DeclaringClassHasAdLibrary) {
								String[] ArrayUnit = StringLastKnownUnit.replaceFirst("virtualinvoke", "")
										.replaceFirst("r", "").replace("$", "").replaceAll("[0-9]", "")
										.replaceFirst(".<", "").replace(" ", "").split(":");
								ClassNameLogArray.add(ArrayUnit[0]);
								logging.InjectLogAfterUnit(LastKnownUnit, "LoadAd:" + StringLastKnownUnit, sootmethod);
								Logging.LogMethodAndClass(Class.toString(), sootmethod.toString());
								Logging.IncrementLogCount("LogCountLoadAd");
								System.out.println("Declaring Class:" + StringDeclaringClass);
							}
							if (SootValueIsAVirtualInvokeExpr
									&& sootconditionchecker.ValueToLowerContainsString(SootValue, "showad()")
									&& sootconditionchecker.CheckifVirtualInvokeExprMethodContains(
											VirtualInvokeExpression, SootValue, "showad")
									&& DeclaringClassHasAdLibrary) {
								String[] ArrayUnit = StringLastKnownUnit.replaceFirst("virtualinvoke", "")
										.replaceFirst("r", "").replace("$", "").replaceAll("[0-9]", "")
										.replaceFirst(".<", "").replaceFirst(" = ", "").replaceFirst("[a-z] ", "")
										.replace(" ", "").split(":");
								ClassNameLogArray.add(ArrayUnit[0]);
								logging.InjectLogAfterUnit(LastKnownUnit, "ShowAd:" + StringLastKnownUnit, sootmethod);
								Logging.LogMethodAndClass(Class.toString(), sootmethod.toString());
								Logging.IncrementLogCount("LogCountShowAd");
								System.out.println("Declaring Class:" + StringDeclaringClass);
							}
							if (SootValueIsAVirtualInvokeExpr
									&& sootconditionchecker.ValueToLowerContainsString(SootValue, "impression")
									&& sootconditionchecker.CheckifVirtualInvokeExprMethodContains(
											VirtualInvokeExpression, SootValue, "append")) {
								String[] ArrayUnit = StringLastKnownUnit.replaceFirst("virtualinvoke", "")
										.replaceFirst("r", "").replace("$", "").replaceAll("[0-9]", "")
										.replaceFirst(".<", "").replaceFirst(" = ", "").replaceFirst("[a-z] ", "")
										.replace(" ", "").split(":");
								ClassNameLogArray.add(ArrayUnit[0]);
								logging.InjectLogAfterUnit(LastKnownUnit, "ClickRegistered:" + StringLastKnownUnit,
										sootmethod);
								Logging.LogMethodAndClass(Class.toString(), sootmethod.toString());
								Logging.IncrementLogCount("LogCountImpression");
								System.out.println("Declaring Class:" + StringDeclaringClass);
							}
							if (SootValueIsAVirtualInvokeExpr
									&& SootValue.toString().toLowerCase().contains("performclick")
									&& sootconditionchecker.CheckifVirtualInvokeExprMethodContains(
											VirtualInvokeExpression, SootValue, "performclick")
									&& DeclaringClassHasAdLibrary) {
								logging.InjectLogAfterUnit(LastKnownUnit, "AdClicked:" + StringLastKnownUnit,
										sootmethod);
								Logging.LogMethodAndClass(Class.toString(), sootmethod.toString());
								Logging.IncrementLogCount("LogCountAdClicked");
								System.out.println("Declaring Class:" + StringDeclaringClass);
							}
							if (SootValueIsAVirtualInvokeExpr
									&& sootconditionchecker.ValueToLowerContainsString(SootValue, "setlistener")
									&& sootconditionchecker.ValueToLowerContainsString(SootValue, "adlayout")
									&& sootconditionchecker.CheckifVirtualInvokeExprMethodContains(
											VirtualInvokeExpression, SootValue, "setlistener")
									&& DeclaringClassHasAdLibrary) {
								logging.InjectLogAfterUnit(LastKnownUnit, "AdClickedListener:" + StringLastKnownUnit,
										sootmethod);
								Logging.LogMethodAndClass(Class.toString(), sootmethod.toString());
								Logging.IncrementLogCount("LogCountAdClickedListener");
								System.out.println("Declaring Class:" + StringDeclaringClass);
							}
						}
					}
				}
			} else {
				AmazonLibraryExists = false;
				GoogleLibraryExists = false;
				FacebookLibraryExists = false;
			}
		}

	}
};
