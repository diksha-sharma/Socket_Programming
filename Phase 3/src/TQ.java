public class TQ
{

	Quorum[] aQuorum; //Stores quorum members for the current process
	int[][] iRequest; //Stores the request queue to be followed for current execution
	int iAlgo = 0; //Algorithm to be executed
	int iRun = -1; //Run number to be used for the current execution
	int iProcessId; //Current process's processid
	int iTotalNodes = 0; //Total number of nodes participating in the current execution
	
	ListenerThread thListener; //Listener thread

	//************************************************************************************************************************************************************
	//Method Description: Starts execution for the class's object by instantiating the Listener thread and passing it the required input data
	//Return Type		: void
	//Input Parameters	: null
	//Exceptions		: General Exception type
	//************************************************************************************************************************************************************
	public void Execute() throws Exception
	{

		thListener = new ListenerThread ("Listener", iRun, iAlgo, iProcessId, iRequest, aQuorum, iTotalNodes);
		//System.out.println("Created Listener thread...");
				
	}//End of Execute() method

	
}//End of class TQ