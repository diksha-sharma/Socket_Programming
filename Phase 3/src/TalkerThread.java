import java.sql.*;
import java.io.*;
import java.net.*;

public class TalkerThread  extends Thread
{

	String sName = null;
	int[][] iRTOrder;
	DatabaseThread thDatabase;
	int iRun = -1;
	int iAlgo = -1;
	int iProcessId = -1;
	Connection conTalkerThread = null;
	int iSeqNo = 0;
	Quorum[] aQuorum;
	int iTotalNodes = 0;
	boolean bToken = false;
	
	//************************************************************************************************************************************************************
	//************************************************************************************************************************************************************
	TalkerThread(String sTempName, int iTempRun, int iTempAlgo, int iTempProcessId, int[][] iTempRequestOrder, Quorum[] iTempQuorum, int iTempTotalNodes, boolean bTempToken)
	{
		
		sName = sTempName;
		iRun = iTempRun;
		iAlgo = iTempAlgo;
		iProcessId = iTempProcessId;
		System.out.println("Talker iProcessId : " + iProcessId);
		System.out.println("Talker iTempProcessId : " + iTempProcessId);
		iRTOrder = iTempRequestOrder;
		iTotalNodes = iTempTotalNodes;
		bToken = bTempToken;
		thDatabase = new DatabaseThread ("Database", iTempRun, iTempAlgo, iTempProcessId, iTempRequestOrder);
		
		try
		{
			
			System.out.println("Got db connection for talker");
			conTalkerThread = thDatabase.makeConnection();
			
		}
		catch (Exception e) 
		{
	    	
	         System.out.println("Talker Thread  : Exception getting database connection in thread class...");
	         System.exit(0);
	         
		}//End of catch (InterruptedException e)
		
		aQuorum = iTempQuorum;

		System.out.println("Talker thread initialized ...");
		
		this.start();		
		
		
		
	}//End of constructor TalkerThread()
	
	
	//************************************************************************************************************************************************************
	//************************************************************************************************************************************************************
	public void run()
	{
		
		System.out.println("Talker thread starting execution ...");

	}//End of run() method

	//************************************************************************************************************************************************************
	//************************************************************************************************************************************************************
	public void requestMyselfCriticalSection(int iTempProcessId) throws IOException
	{
		String sCurrentProcess = null;
		String inputS = null;
		System.out.println("Talker Thread  : Udpating sequence number before requesting critical section ...");
		
		int iTempSeqNo = thDatabase.getUpdatedSeqNo(iTempProcessId, conTalkerThread);
		
		iSeqNo = iTempSeqNo + 1;
		//System.out.println("New sequence no: " + iSeqNo);

	    try
	    {
	    	
	    	String sMessage = iTempProcessId + "," + iSeqNo + "," + iTempProcessId + "," +"4";
	    	System.out.println("Talker Thread  : Sending message: " + sMessage);
	    	
	    	if(iProcessId < 10)
    		{
    			
	    		sCurrentProcess = "net0"+ iTempProcessId +".utdallas.edu";
    			
    		}
    		else
    		{
    			
    			sCurrentProcess = "net"+ iTempProcessId +".utdallas.edu";
    			
    		}
	    	
	    	System.out.println("Talker Thread  : Sending message to : " + sCurrentProcess);

	    	
	    	Socket socTalker = new Socket("localhost", 1234);
	    	
			PrintWriter pwTalker = new PrintWriter(socTalker.getOutputStream(), true);
			
			pwTalker.println(sMessage);
			
			thDatabase.updateDatabaseCriticalSectionRequestSent(iSeqNo, conTalkerThread, iAlgo, iRun, iTempProcessId);
			
			pwTalker.close();
	        socTalker.close();


	    }//End of try block
	    catch (UnknownHostException e) 
	    {
	        
	    	System.err.println("Talker Thread  : Unknown host: " + sCurrentProcess);
	        System.exit(0);
	        
	    }
	    catch  (IOException e) 
	    {
	        
	    	System.err.println("Talker Thread  : No I/O on the socket..." + sCurrentProcess);
	        System.exit(0);
	        
	    }
	    catch(Exception e)
	    {
	    	
	    	System.err.println("Talker Thread  : Issue sending socket messages while requesting Critical section in requestMyselfCriticalSection() method of TalkerThread class ....");
	    	System.exit(0);
	    	
	    }//End of catch block
	    

		
	}//End of requestCriticalSection() method
	
	
	//************************************************************************************************************************************************************
	//************************************************************************************************************************************************************
	public void requestQuorumCriticalSection(int iTempProcessId)
	{
		
		String sCurrentProcess = null;
		String inputS = null;

	    try
	    {
	    	
	    	String sMessage = iTempProcessId + "," + iSeqNo + "," + iTempProcessId + "," +"4";
	    	System.out.println("Talker Thread  : Sending message: " + sMessage);

			String sQuorumMember = null;
			int iPort = 1234;
	    	
	    	for(int iQIndex = 0; iQIndex < aQuorum.length; iQIndex++)
	    	{
	    	
	    		System.out.println("Talker Thread  : Sent message requesting critical section to quorum member...");
	    		
	    		if(aQuorum[iQIndex].iQProcessId < 10)
	    		{
	    			
	    			sQuorumMember = "net0"+ aQuorum[iQIndex].iQProcessId +".utdallas.edu";
	    			iPort = iPort + 1;
	    			
	    		}
	    		else
	    		{
	    			
	    			sQuorumMember = "net"+ aQuorum[iQIndex].iQProcessId +".utdallas.edu";
	    			iPort = iPort + 1;
	    			
	    		}

		    	Socket socTalker = new Socket("localhost", iPort);		    	
		    	//socTalker.connect(socTalker.getRemoteSocketAddress(), 0);
	    		PrintWriter pwTalker = new PrintWriter(socTalker.getOutputStream(), true);
	    		pwTalker.println(sMessage);		      
	    		
		        thDatabase.updateDatabaseForQuorumMember(iSeqNo, conTalkerThread, iAlgo, iRun, aQuorum[iQIndex].iQProcessId, iTempProcessId);
		        
		        pwTalker.close();
		        socTalker.close();
	    		
	    	}//End of for loop for iQIndex

	    }//End of try block
	    catch (UnknownHostException e) 
	    {
	        
	    	System.err.println("Talker Thread  : Unknown host: " + sCurrentProcess);
	        System.exit(0);
	        
	    }
	    catch  (IOException e) 
	    {
	        
	    	System.err.println("Talker Thread  : No I/O on the socket..." + sCurrentProcess);
	        System.exit(0);
	        
	    }
	    catch(Exception e)
	    {
	    	
	    	System.err.println("Talker Thread  : Issue sending socket messages while requesting Critical section in requestQuorumCriticalSection() method of TalkerThread class ....");
	    	System.exit(0);
	    	
	    }//End of catch block
	    

		
	}//End of requestCriticalSection() method
	
	//************************************************************************************************************************************************************
	//************************************************************************************************************************************************************
	public void sendDeleteRequest(int iTempProcessId, int iTempSeqNo)
	{
		
		System.out.println("Talker Thread  : Sending a delete a request from the queue to all processes ...");
		
		String sMessage = iTempProcessId + "," + iTempSeqNo + ",0" +",3";
		String sProcess = null;
		
		//Assuming all nodes are in increasing order of numbers - 1,2,3,4,5,6 etc...no gaps
		for(int iNIndex = 1; iNIndex <= iTotalNodes; iNIndex++)
		{
			
			try
		    {
		    	
	    		if(iNIndex < 10)
	    		{
	    			
	    			sProcess = "net0"+ iNIndex +".utdallas.edu";
	    			
	    		}//End of if(iNIndex < 10)
	    		else
	    		{
	    			
	    			sProcess = "net"+ iNIndex +".utdallas.edu";
	    			
	    		}//End of else for if(iNIndex < 10)

	    		Socket socTalker = new Socket(sProcess, 1234);		        
	    		PrintWriter pwTalker = new PrintWriter(socTalker.getOutputStream(), true);
		        pwTalker.print(sMessage);		        
		        thDatabase.updateDatabaseForDeleteQuorumMember(iSeqNo, conTalkerThread, iAlgo, iRun, iNIndex, iProcessId);
		        
		        pwTalker.close();
		        socTalker.close();
		  
		    }//End of try block
		    catch(Exception e)
		    {
		    	
		    	System.err.println("Talker Thread  : Issue sending socket messages while requesting Critical section in sendDeleteRequest() method of TalkerThread class ....");
		    	System.exit(0);
		    	
		    }//End of catch block
			
			
		}//End of for loop for iNIndex
		
		
	}//End if sendDeleteRequest() method
	
	
	//************************************************************************************************************************************************************
	//************************************************************************************************************************************************************	
	public void requestTokenFromQuorum(String sTempRequest, int iTempProcessId)
	{
		
		System.out.println("Talker Thread  : Requesting token from my quorum members ...");
		
		if(iTempProcessId == iProcessId)
		{
			
			String[] sNR = sTempRequest.split(",");
			
			String sNR0 = Integer.toString(iProcessId);
			String sNR1 = sNR[1];//Sequence#
			String sNR2 = sNR[0];//original Requestor
			String sNR3 = sNR[3];//Message Type
			
			String sNewRequest = sNR2 + "," + sNR1 + "," + sNR0 + "," + sNR3;
			
			try
		    {
		        
				String sQuorumMember = null;
		    	
		    	for(int iQIndex = 0; iQIndex < aQuorum.length; iQIndex++)
		    	{
		    	
		    		System.out.println("Talker Thread  : Sent message requesting critical section to quorum member...");
		    		
		    		if(aQuorum[iQIndex].iQProcessId < 10)
		    		{
		    			
		    			sQuorumMember = "net0"+ aQuorum[iQIndex].iQProcessId +".utdallas.edu";
		    			
		    		}
		    		else
		    		{
		    			
		    			sQuorumMember = "net"+ aQuorum[iQIndex].iQProcessId +".utdallas.edu";
		    			
		    		}

		    		Socket socTalker = new Socket(sQuorumMember, 1234);		        
		    		PrintWriter pwTalker = new PrintWriter(socTalker.getOutputStream(), true);
			        pwTalker.print(sNewRequest);		        
			        thDatabase.updateDatabaseForRequestToken(iSeqNo, conTalkerThread, iAlgo, iRun, aQuorum[iQIndex].iQProcessId, Integer.parseInt(sNR0), Integer.parseInt(sNR2));
			        
			        pwTalker.close();
			        socTalker.close();
		    		
		    	}//End of for loop for iQIndex

		    }//End of try block
		    catch(Exception e)
		    {
		    	
		    	System.err.println("Talker Thread  : Issue sending socket messages while requesting Critical section in requestTokenFromQuorum() method of TalkerThread class ....");
		    	System.exit(0);
		    	
		    }//End of catch block
			
		}//End of if(iTempProcessId == iProcessId)	
		
		
	}//End of requestTokenFromQuorum()

	
	//************************************************************************************************************************************************************
	//************************************************************************************************************************************************************	
	public void sendToken(int iTempForwardProcess)
	{
		
		System.out.println("Talker Thread  : Sending token to other process ...");
		
		String sForwardProcess = null;
		String sToken = iProcessId + "," + iSeqNo + "," + iProcessId + ",1";
		
		try
	    {
			
			if(iTempForwardProcess < 10)
			{
				
				sForwardProcess = "net0"+ iTempForwardProcess +".utdallas.edu";
				
			}
			else
			{
				
				sForwardProcess = "net"+ iTempForwardProcess +".utdallas.edu";
				
			}

			Socket socTalker = new Socket(sForwardProcess, 1234);		        
			PrintWriter pwTalker = new PrintWriter(socTalker.getOutputStream(), true);
	        pwTalker.print(sToken);		        
	        thDatabase.updateDatabaseForSendToken(iSeqNo, conTalkerThread, iAlgo, iRun, iProcessId, iTempForwardProcess);
	        
	        pwTalker.close();
	        socTalker.close();
			
			
	    }//End of try block
		 catch(Exception e)
	    {
	    	
	    	System.err.println("Talker Thread  : Issue sending socket messages while requesting Critical section in sendToken() method of TalkerThread class ....");
	    	System.exit(0);
	    	
	    }//End of catch block
		

	}//End of sendToken() method
	
}//End of class TalkerThread
