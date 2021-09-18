import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.IntType;
import soot.Local;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Unit;
import soot.Value;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.SimpleLiveLocals;
import soot.toolkits.scalar.SmartLocalDefs;
import soot.util.queue.QueueReader;
import FileParser.LayoutFileParser;

public class DynamicEntryPointsManager {
    public Set<SootMethod> DynamicEntryPoints = new HashSet<SootMethod>();
    public LayoutFileParser layoutParser;
    public List<SootMethod> initialentrypointsialEntryPoints;
    private String androidAPKLocationLoc = null;

    public DynamicEntryPointsManager(String androidAPKLocation, List<SootMethod> initialentrypoints) {
        androidAPKLocationLoc = androidAPKLocation;
        initialentrypointsialEntryPoints = initialentrypoints;
        layoutParser = new LayoutFileParser(androidAPKLocationLoc);
        parseLayoutForDynamicEntryPoints();
    }

    /*
     * parse the Android layout files, find user defined entry points in layouts
     */
    private void parseLayoutForDynamicEntryPoints() {
        Map<String, String> idToFile = layoutParser.getIdToFile();
        Map<String, Set<String>> functionsFromXmlFile = layoutParser.getFunctionsFromXmlFile();

        Scene.v().setEntryPoints(initialentrypointsialEntryPoints);
        CHATransformer.v().transform();

        ReachableMethods reachablemethods = Scene.v().getReachableMethods();
        QueueReader<MethodOrMethodContext> removeIt = reachablemethods.listener();

        while (removeIt.hasNext()) {
            SootMethod method = removeIt.next().method();
            if (!method.hasActiveBody())
                continue;

            Body body = method.getActiveBody();

            for (Iterator<Unit> unitIt = body.getUnits().iterator(); unitIt.hasNext();) {
                Stmt statement = (Stmt) unitIt.next();

                if (!statement.containsInvokeExpr())
                    continue;

                SootMethodRef methodRef = statement.getInvokeExpr().getMethodRef();

                if (!methodRef.getName().contains("setContentView") || statement.getInvokeExpr().getArgCount() <= 0)
                    continue;

                Value methodparameter = statement.getInvokeExpr().getArg(0);

                if (!(methodparameter.getType() instanceof IntType))
                    continue;

                String fileName = null;

                try {
                    int layoutIdInt = Integer.parseInt(statement.getInvokeExpr().getArg(0).toString());
                    fileName = idToFile.get("0x" + Integer.toHexString(layoutIdInt));
                } catch (NumberFormatException e) {
                    // TODO: Right now we're only tracing back within the same method.
                    ExceptionalUnitGraph exceptionalUnitGraph = new ExceptionalUnitGraph(body);
                    SmartLocalDefs localDefinitions = new SmartLocalDefs(exceptionalUnitGraph,
                            new SimpleLiveLocals(exceptionalUnitGraph));
                    List<Unit> definitions = localDefinitions
                            .getDefsOfAt((Local) statement.getUseBoxes().get(1).getValue(), statement);
                    Stmt definitionStatement = (Stmt) definitions.get(definitions.size() - 1);
                    Value returnValue = definitionStatement.getUseBoxes().get(0).getValue();

                    if (returnValue instanceof StaticFieldRef) {
                        fileName = ((StaticFieldRef) returnValue).getFieldRef().name();
                    } else {
                        // TODO: This requires backward flow analysis to trace back where it's coming
                        // from
                        // System.err.println("Warning: DynamicEntryPoint skips " +
                        // definitionStatement.toString());
                    }
                }

                if (fileName == null || !layoutParser.getLayoutFilesNameList().contains(fileName))
                    continue;

                List<String> layouts = layoutParser.getFileToEmbededFiles().get(fileName);
                layouts.add(fileName);

                for (String layout : layouts) {

                    if (!functionsFromXmlFile.containsKey(layout))
                        // TODO: this means that we might be skipping some layout files
                        continue;

                    for (Iterator<String> iterator = functionsFromXmlFile.get(layout).iterator(); iterator.hasNext();) {
                        String signature = "<" + methodRef.getDeclaringClass().getName() + ": void " + iterator.next()
                                + ">";

                        try {
                            DynamicEntryPoints.add(Scene.v().getMethod(signature));
                        } catch (RuntimeException e) {
                            System.err.println("Warning: DynamicEntryPoint cannot find " + signature
                                    + " (signature is perhaps wrong)");
                        }
                    }
                }
            }
        }
    }

    /*
     * get all the dynamic entry points of given app
     * 
     * @return: list of dynamic entry points
     */
    public Set<SootMethod> getDynamicEntryPoints() {
        return DynamicEntryPoints;
    }

}
