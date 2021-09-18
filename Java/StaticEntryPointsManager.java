import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.util.Chain;

public class StaticEntryPointsManager {
	
	public Set<SootMethod> StaticEntryPoints = new HashSet<SootMethod>();
	private String AndroidAPKLocation = null;
	
	public StaticEntryPointsManager(String apkLocation){
		AndroidAPKLocation = apkLocation;
		load();
	}
	
	/*
	 * find all the static entry points
	 */
	private void load() {
		Chain<SootClass> classes = Scene.v().getApplicationClasses();
    Map<String, Set<String>> entrypointMap = EntryPointsManager.androidEPLoader.getEPMap();
    
    for (SootClass sootclass : classes) {
        List<SootClass> superTypes = retrieveSuperTypesOf(sootclass);
        StaticEntryPoints.addAll(getEntryMethods(sootclass, superTypes, entrypointMap));
    }
	}

	/*
	 * get all the entry points of given class
	 * @param baseClass: the application class checked
	 * @param superTypes: all the pararent classes of the given application class
	 * @param entrypointMap: Android entry points set mapping
	 * @return: list of entry methods defined in given application class
	 */
	private List<SootMethod> getEntryMethods(SootClass baseClass,
			List<SootClass> superTypes, Map<String, Set<String>> entrypointMap) {
    List<SootMethod> entryMethods = new ArrayList<SootMethod>();
    
    for (SootClass c : superTypes) {
        // find which classes are in ep map
        String className = c.getName().replace('$', '.');
        if(className.contains("android.support")) continue;

        if (entrypointMap.containsKey(className)) {
            Set<String> methods = entrypointMap.get(className);

            for (String method : methods) {
                String signature = "<" + baseClass + method + ">";
                try {
                    entryMethods.add(Scene.v().getMethod(signature));
                } catch (Exception e) {
                }
            }
        }
    }
    
    return entryMethods;
	}

	/*
	 * resolve all the super types of given class
	 * @param: given class
	 * @return: list of parent Soot Classes of given class
	 */
	private List<SootClass> retrieveSuperTypesOf(SootClass sootclass) {
    List<SootClass> superTypes = new ArrayList<SootClass>();
    
    while (sootclass.hasSuperclass()) {
        superTypes.add(sootclass);
        superTypes.addAll(sootclass.getInterfaces());
        sootclass = sootclass.getSuperclass();
    }
    
    return superTypes;
	}

	/*
	 * get the method list of static entry points of given app
	 * @return: list of Soot methods
	 */
	public Set<SootMethod> getStaticEntryPoints(){
		return StaticEntryPoints;
	}

}
