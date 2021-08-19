pragma solidity >=0.5.15;
pragma experimental ABIEncoderV2;



contract ApplicationAdvert{
    
    // ----------------------STRUCT FOR APPLICATION SIDE-----------------------------------------
    struct Application{
        address AppAddrUsed;
        string AppName;
        uint clicks;
    }
    
    string private AppNameSet;
    // -------------------------------MAPPINGS-----------------------------------------------
    mapping(string => Application) private app;
    
   
    function SetAppName(string memory name) public{
            AppNameSet = name;
	        app[name].AppName=name;
	        app[name].AppAddrUsed = msg.sender;
	   
	}
	
	function IncrementClickCount() public{
	
	    app[AppNameSet].clicks=app[AppNameSet].clicks+1;
	}
	
 
	
    function ReturnStructAppName() public view returns(string memory){
	   return app[AppNameSet].AppName; 
	}
	
	function ReturnClicks() public view returns(uint){
	   return app[AppNameSet].clicks; 
	}

}
