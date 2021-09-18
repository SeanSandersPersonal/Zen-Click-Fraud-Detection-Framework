import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import soot.SootMethod;
import Utils.AndroidEntryPointsMapLoader;

public class EntryPointsManager {
	public static List<SootMethod> entryPoints = new ArrayList<SootMethod>();
	private String AndroidAPKLocation = null;
	public static AndroidEntryPointsMapLoader androidEPLoader = null;

	public EntryPointsManager(String AndroidApkLocation){
		AndroidAPKLocation = AndroidApkLocation;

		//load the pre-computed Android entry points set into BlueSeal
		androidEPLoader = new AndroidEntryPointsMapLoader();
	}
	/*
	 * find all the entry points of analyzed app
	 * update the entry points list in two steps:
	 * 1. load static entry points
	 * 2. analyze dynamic entry points
	 */
	public void loadApkEntryPoints(){
		StaticEntryPointsManager staticentrypointsmanager = new StaticEntryPointsManager(AndroidAPKLocation);
		entryPoints.addAll(staticentrypointsmanager.getStaticEntryPoints());

		DynamicEntryPointsManager dynamicentrypointsmanager = new DynamicEntryPointsManager(AndroidAPKLocation, entryPoints);
		entryPoints.addAll(dynamicentrypointsmanager.getDynamicEntryPoints());

		//prone all entry points from "android.support" library
		Set<SootMethod> removeSet = new HashSet<SootMethod>();
		for(SootMethod method : entryPoints){
			if(method.getDeclaringClass().getName().contains("android.support.")){
				removeSet.add(method);
			}
		}
		entryPoints.removeAll(removeSet);
	}
	public List<SootMethod> getApkEntryPoints(){
		return entryPoints;
	}
	public void printApkEntryPoints(){
		for (int index=0; index < entryPoints.size(); index++) {
			System.out.println(entryPoints.get(index));
		}
	}
}
