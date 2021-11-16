pragma solidity >=0.5.15;
pragma experimental ABIEncoderV2;



contract ApplicationAdvert{
    
    // ----------------------STRUCT FOR APPLICATION SIDE-----------------------------------------
    struct Application{
        address AppAddrUsed;
        string AppName;
        string[] ExternalIpAddr;
        uint clicks;
        uint invalidclicks;
    }
    
    string private AppNameSet;
    // -------------------------------MAPPINGS-----------------------------------------------
    mapping(string => Application) private app;
    
   
    function SetAppName(string memory name) public{
            AppNameSet = name;
	        app[name].AppName=name;
	        app[name].AppAddrUsed = msg.sender;
	   
	}
	function SetIpAddr(string memory IpAddr) public{
            
	        app[AppNameSet].ExternalIpAddr.push(IpAddr);
	   
	}
	function IncrementClickCount() public{
	
	    app[AppNameSet].clicks=app[AppNameSet].clicks+1;
	}
	
 
	function IncrementInvalidClickCount() public{
	
	    app[AppNameSet].invalidclicks=app[AppNameSet].invalidclicks+1;
	}
    function ReturnStructAppName(string memory AppName) public view returns(string memory){
	   return app[AppName].AppName; 
	}
	
	function ReturnClicks(string memory AppName) public view returns(uint){
	   return app[AppName].clicks; 
	}
    function ReturnInvalidClicks(string memory AppName) public view returns(uint){
	   return app[AppName].invalidclicks; 
	}
	function ReturnExternalIPAddr(string memory AppName) public view returns(string[] memory){
	   return app[AppName].ExternalIpAddr; 
	}
}
