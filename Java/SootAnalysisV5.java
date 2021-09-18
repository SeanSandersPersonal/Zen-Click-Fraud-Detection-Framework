import soot.Body;
import soot.BodyTransformer;
import soot.PackManager;
import soot.Transform;
import java.util.*;

public class SootAnalysisV5
{
    public static void main(String[] args)
    {       
         PackManager.v().getPack("jtp").add(new Transform("jtp.myInstrumenter", new BodyTransformer() {  
            @Override
            protected void internalTransform(final Body body, String phaseName, @SuppressWarnings("rawtypes") Map options) { 
            } //End of InternalTransform
        })); //End of PackManager.v()
        soot.Main.main(args);
    }
}
