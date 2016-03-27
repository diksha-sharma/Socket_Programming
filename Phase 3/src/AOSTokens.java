import java.io.*;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class AOSTokens 
{

	TQ tqAlgo; //Object for new algorithm
        MA maAlgo; //Object for Makewa
	static int iProcessId = -1;//This process's process id - supplied as one of the arguments
	static int iAlgo = -1;// If == 1 runs Maekawa's algorithm and If == 2 then runs new algorithm
	static int iTotalNodes = 0; //Stores total number of nodes for the current run
	static int iRun = -1; //Stores the current run # to look for in database
	static int iQuorumSize = 0; //Stores the max number of members in a quorum
	static Quorum[] aQuorum;//Stores quorum members and their details

	//************************************************************************************************************************************************************
	//Method Description: Starts project execution here
	//Return Type		: void
	//Input Parameters	: String[1] - file name to be read with its location
	//					: String[2] - ProcessId of the current process
	//					: String[3] - Algorithm number to be executed
	//Exceptions		: General Exception type
	//************************************************************************************************************************************************************
	public static void main(String args[])
	{
		System.out.println();
		System.out.println("Starting Execution...");
                
		AOSTokens AOST = new AOSTokens();		
		
		iProcessId = Integer.parseInt(args[1]);//Initialized the process id
		iAlgo = Integer.parseInt(args[2]);//Get which algorithm to execute

		if(iAlgo == 1)
		{
			System.out.println();
			System.out.println("Executing Makewa Algorithm");
			System.out.println();
                        
                        AOST.maAlgo = new MA();
			AOST.readSharedFile(args[0]);//File is present in location: /people/cs/d/dxs134530/AOSTokens.txt on net01.utdallas.edu		
			try
			{
				
				AOST.maAlgo.iAlgo = 1;
				AOST.maAlgo.iProcessId = iProcessId;//Initialize the process id	
				AOST.maAlgo.Execute();

			}
			catch(Exception e)
			{
				
				System.err.println("Issue executing Execute() method of TQ algorithm...");
				System.exit(0);
				
			}
			
			
		}//End of if(AOST.iAlgo == 1)
		//else if(AOST.iAlgo == 2) *********Uncomment this line and delete the one following it during reconciliation
		if(iAlgo == 2)
		{
			
			System.out.println();
			System.out.println("Executing Tokenized Quorum Based Algorithm");
			System.out.println();
			
			AOST.tqAlgo = new TQ();
			AOST.readSharedFile(args[0]);//File is present in location: /people/cs/d/dxs134530/AOSTokens.txt on net01.utdallas.edu
			
			try
			{
				
				AOST.tqAlgo.iAlgo = 2;
				AOST.tqAlgo.iProcessId = iProcessId;//Initialize the process id	
				AOST.tqAlgo.Execute();

			}
			catch(Exception e)
			{
				
				System.err.println("Issue executing Execute() method of TQ algorithm...");
				System.exit(0);
				
			}
			
			
		}//End of if(AOST.iAlgo == 2)
		else if(iAlgo != 2 && iAlgo != 1)
		{
			
			System.err.println("Wrong algorithm number provided ... Exiting...");
			System.exit(0);
			
			
		}//End of if(AOST.iAlgo == 2)
		
	}//End of main method
	
		
	//************************************************************************************************************************************************************
	//Method Description: Reads the shared file and initializes values for AOST class variables and Algorithm class's variables:
	//                    Total Number of nodes: NODES
	//                    Run Number: RUN
	//                    Maximum Quorum Size: MAX_QUORUM_SIZE
	//                    Quorum Members for current process: QUORUM
	//                    Request Order: REQUEST
	//Return Type		: void
	//Input Parameters	: String - file name to be read with its location
	//Exceptions		: General Exception type
	//************************************************************************************************************************************************************
        //Reads the shared file whose location and name is passed as the argument
	public void readSharedFile(String sFile)
	{		
		System.out.println("In readSharedFile() method");		
		try
		{			
			FileInputStream fisInput = new FileInputStream(sFile);
			DataInputStream disInput = new DataInputStream(fisInput);
			BufferedReader brReader = new BufferedReader(new InputStreamReader(disInput));
			
			String sInputLine = null;
			
			//Read the shared file
			while((sInputLine = brReader.readLine()) != null)//Outer while loop
			{
                            if(iAlgo == 1)
                            {
                                if(sInputLine.equals("NODES"))
				{					
					//System.out.println("In NODES");
					
					sInputLine = brReader.readLine();
					iTotalNodes = Integer.parseInt(sInputLine);
					maAlgo.iTotalNodes = Integer.parseInt(sInputLine); //changed
				}//End of if(sInputRead == "NODES")
				if(sInputLine.equals("RUN"))
				{
					sInputLine = brReader.readLine();
					iRun = Integer.parseInt(sInputLine);
                                        maAlgo.iRun = Integer.parseInt(sInputLine);                                       
				}//End of if(sInputRead == "NODES")
				else if (sInputLine.equals("MAX_QUORUM_SIZE"))
				{					
					sInputLine = brReader.readLine();
					iQuorumSize = Integer.parseInt(sInputLine);					
					aQuorum = new Quorum[iQuorumSize];
					maAlgo.aQuorum = new Quorum[iQuorumSize];
					maAlgo.iOutstandingReplies = iQuorumSize; //own processID is not included in quorum
														
				}//End of else if (sInputRead == "MAX_QUORUM_SIZE")
				else if (sInputLine.equals("QUORUM"))
				{
					//System.out.println("In QUORUM");
					
					String[] sInput;
					int iTempInput = 0;
					boolean bMatch = false;

					while(bMatch == false)
					{
						sInputLine = brReader.readLine();						
						sInput = sInputLine.split(",");
						iTempInput = Integer.parseInt(sInput[0]);
						
						if(iTempInput == iProcessId)
						{							
							bMatch = true;
							for(int iIndex = 0; iIndex < (sInput.length-1); iIndex++)
							{										
								iTempInput = Integer.parseInt(sInput[(iIndex+1)]);
								aQuorum[iIndex] = new Quorum();
								maAlgo.aQuorum[iIndex] = new Quorum();
								
								aQuorum[iIndex].iQProcessId = iTempInput;
								maAlgo.aQuorum[iIndex].iQProcessId = iTempInput;
								
							}//End of for loop for initializing quorum
							
						}//End of if(iTempInput == iProcessId)
						
					}//End of inner while((sInputLine = brReader.readLine()) != null)
					
					
				}//End of else if (sInputRead == "QUORUM")
				else if (sInputLine.equals("REQUEST"))//Processing request queue
				{
					String[] sInput;
					int iTempInput;					
					int iRequestLength = Integer.parseInt(sInputLine = brReader.readLine());
										
					for(int iCounter = 0; iCounter < iRequestLength; iCounter++)
					{
						
						sInputLine = brReader.readLine();
						
						sInput = sInputLine.split(",");
						iTempInput = Integer.parseInt(sInput[0]);
						
						if(iCounter == 0)
						{							
							maAlgo.iRequest = new int[iRequestLength][sInput.length];							
						}						
						for(int iIndex = 0; iIndex < sInput.length; iIndex++)
						{							
							iTempInput = Integer.parseInt(sInput[iIndex]);
							maAlgo.iRequest[iCounter][iIndex] = iTempInput;
							
						}//End of for loop for initializing quorum
						
					}//End of inner while((sInputLine = brReader.readLine()) != null)					
					
				}//End of else if (sInputRead == "REQUEST")
                            } // End of if(iAlgo==1)
                            else if (iAlgo == 2)
                            {                             
                                if(sInputLine.equals("NODES"))
				{
					
					//System.out.println("In NODES");
					
					sInputLine = brReader.readLine();
					iTotalNodes = Integer.parseInt(sInputLine);
					
				}//End of if(sInputRead == "NODES")
				if(sInputLine.equals("RUN"))
				{
					sInputLine = brReader.readLine();
					iRun = Integer.parseInt(sInputLine);
                                        tqAlgo.iRun = Integer.parseInt(sInputLine);                                        
				}//End of if(sInputRead == "NODES")
				else if (sInputLine.equals("MAX_QUORUM_SIZE"))
				{					
					sInputLine = brReader.readLine();
					iQuorumSize = Integer.parseInt(sInputLine);					
					aQuorum = new Quorum[iQuorumSize];
					tqAlgo.aQuorum = new Quorum[iQuorumSize];
					//tqAlgo.iOutStandingReplies = iQuorumSize - 1;
														
				}//End of else if (sInputRead == "MAX_QUORUM_SIZE")
				else if (sInputLine.equals("QUORUM"))
				{
					//System.out.println("In QUORUM");					
					String[] sInput;
					int iTempInput = 0;
					boolean bMatch = false;

					while(bMatch == false)
					{

						sInputLine = brReader.readLine();
						
						sInput = sInputLine.split(",");
						iTempInput = Integer.parseInt(sInput[0]);
						
						if(iTempInput == iProcessId)
						{							
							bMatch = true;
							for(int iIndex = 0; iIndex < (sInput.length-1); iIndex++)
							{										
								iTempInput = Integer.parseInt(sInput[(iIndex+1)]);
								aQuorum[iIndex] = new Quorum();
								tqAlgo.aQuorum[iIndex] = new Quorum();
								
								aQuorum[iIndex].iQProcessId = iTempInput;
								tqAlgo.aQuorum[iIndex].iQProcessId = iTempInput;
								
							}//End of for loop for initializing quorum
							
						}//End of if(iTempInput == iProcessId)
						
					}//End of inner while((sInputLine = brReader.readLine()) != null)					
				}//End of else if (sInputRead == "QUORUM")
				else if (sInputLine.equals("REQUEST"))//Processing request queue
				{
					String[] sInput;
					int iTempInput;
					
					int iRequestLength = Integer.parseInt(sInputLine = brReader.readLine());
										
					for(int iCounter = 0; iCounter < iRequestLength; iCounter++)
					{
						
						sInputLine = brReader.readLine();
						
						sInput = sInputLine.split(",");
						iTempInput = Integer.parseInt(sInput[0]);
						
						if(iCounter == 0)
						{
							
							tqAlgo.iRequest = new int[iRequestLength][sInput.length];
							
						}
						
						for(int iIndex = 0; iIndex < sInput.length; iIndex++)
						{
							
							iTempInput = Integer.parseInt(sInput[iIndex]);
							tqAlgo.iRequest[iCounter][iIndex] = iTempInput;
							
						}//End of for loop for initializing quorum
						
					}//End of inner while((sInputLine = brReader.readLine()) != null)					
					
				}//End of else if (sInputRead == "REQUEST")
                            }//End of else if (iAlgo ==2)
			}//End of outer while ((sInputLine = brReader.readLine()) != null)			
			
		}//End of try block to read input file
		catch(Exception e)
		{
			
			System.err.println("Error reading the input file ");
			System.exit(0);
			
		}//End of catch block to read input file
		
	}//End of readSharedFile() method
}//End of AOSTokens Class