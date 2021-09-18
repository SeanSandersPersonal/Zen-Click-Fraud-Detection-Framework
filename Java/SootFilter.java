import soot.Local;
import soot.Value; 

public class SootFilter
{
    
    static String EntryPointApp = "com.example.simpleapplication.MainActivity";
    static String EntryPointBlockchain = "com.example.simpleapplication.Hello";
    static String RemoteFunctionCallWeb3J = "org.web3j.protocol.core.RemoteFunctionCall";
    static String SendAsync = "org.web3j.protocol.core.RemoteFunctionCall: java.util.concurrent.CompletableFuture sendAsync()";
    static Local ContractReference = null;
    static Value BlockchainContractStmt = null;
    
    public static void main(String[] args)
    {       
        
        soot.Main.main(args);
    }
    
  
}
