import soot.Body;
import soot.BodyTransformer;
import soot.PackManager;
import soot.Transform;
import soot.Scene;
import soot.SootClass;
import java.util.*;
import Logging.Logging;

public class SootAnalysisV4 {
    public static void main(String[] args) {
        Once once = new Once();

        Scene.v().addBasicClass("android.util.Log", SootClass.SIGNATURES);
        PackManager.v().getPack("jtp").add(new Transform("jtp.myLogger", new BodyTransformer() {
            @Override
            protected void internalTransform(final Body body, String phaseName,
                    @SuppressWarnings("rawtypes") Map options) {
                once.run(new Runnable() {
                    @Override
                    public void run() {
                        String AppName = args[11].replace("APK/", "").replace(".apk", "");
                        Soot st = new Soot(AppName);
                        st.RunSoot();
                        new Logging(AppName);
                        Logging.LogTheLogCount();
                        // st.LogTheLogCount();
                    }
                });
            }
        }));
        soot.Main.main(args);
    }
}
