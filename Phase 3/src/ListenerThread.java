import java.sql.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class ListenerThread extends Thread
{

	String sName = null;
	RequestQueue[] rqRQueue;//This is where all incoming messages are queued
	DatabaseThread thDatabase;
	TalkerThread thTalker;
	Connection conListenerThread = null;
	int iRun = -1;
	int iAlgo = -1;
	int iRIndex = -1;
	int iSeqNo = 0;
	int iProcessId = -1;
	boolean bToken = false;
	boolean bCS = false;
	int iHolder = -1;
	boolean bAsked = false;
	//boolean bRequesting= false;
	int[][] iRLOrder; //This is the orginal request order that the processes are supposed to make requests in
	Quorum[] aQuorum;
	int iTotalNodes = 0;
	int iOriginalRequestor;
	
	//************************************************************************************************************************************************************
	//************************************************************************************************************************************************************
	ListenerThread(String sTempName, int iTempRun, int iTempAlgo, int iTempProcessId, int[][] iTempRequest, Quorum[] aTempQuorum, int iTempTotalNodes)
	{
		
		sName = sTempName;
		iRun = iTempRun;
		iAlgo = iTempAlgo;
		iProcessId = iTempProcessId;
		System.out.println("Listener Thread: Process Id:  " + iProcessId);
		iOriginalRequestor = iProcessId;
		
		if(iProcessId == iTempRequest[0][0])
		{
			
			bToken = true;
			
		}//End of if(iProcessId == 1)
		
		iRLOrder = iTempRequest;
		aQuorum = aTempQuorum;
		iTotalNodes = iTempTotalNodes;
		thDatabase = new DatabaseThread ("Database", iTempRun, iTempAlgo, iTempProcessId, iTempRequest);
		thTalker = new TalkerThread ("Talker", iTempRun, iTempAlgo, iProcessId, iTempRequest, aTempQuorum, iTotalNodes, bToken);

		System.out.println("Listener thread Initialized...");
		this.start();
				
		
	}//End of constructor ListenerThread()
	
	//************************************************************************************************************************************************************
	//************************************************************************************************************************************************************
	public void run()
	{
		
		try
		{

			//BufferedReader brInput = new BufferedReader(new InputStreamReader(socListener.getInputStream()));
			//System.out.println("Got buffered input stream");
			
			ServerSocket ssocListener = new ServerSocket(1234);
			//System.out.println("After server socket");
			//Socket socListener = ssocListener.accept();
			//System.out.println("Server socket accept");
			//BufferedReader brInput = new BufferedReader(new InputStreamReader(socListener.getInputStream()));
			//System.out.println("Got buffered input stream");
			
			//thDatabase.start();			
			int iPreviousOutIndex = -1;
			int iPreviousInIndex = -1;
			boolean bFirstProcess = false;
			
			int iPreviousProcessId = 0;
			
			System.out.println("Listener Thread: Finding if it is current process's turn to request critical section ...");
			
			//Find out when is current process's turn to request critical section
			for(int iOutIndex = 0; iOutIndex <= (iRLOrder.length-1); iOutIndex++)
			{
				
				int[] iTempArray = iRLOrder[(iRLOrder.length-1)];
				
				for(int iInIndex = 0; iInIndex <= (iTempArray.length-1); iInIndex++)
				{
					
					if(iTempArray[iInIndex] == iProcessId)
					{
						
						if(iInIndex > 0)
						{
							
							iPreviousOutIndex = iOutIndex;
							iPreviousInIndex = (iInIndex -1);
							iPreviousProcessId = iRLOrder[iPreviousOutIndex][iPreviousInIndex];
							
						}//End of if(iInIndex > 0)
						else if(iInIndex == 0 && iOutIndex > 0)
						{
							
							iPreviousOutIndex = (iOutIndex-1);
							iPreviousInIndex = (iTempArray.length-1);
							iPreviousProcessId = iRLOrder[iPreviousOutIndex][iPreviousInIndex];
							
						}//End of else if(iInIndex == 0 && iOutIndex > 0)
						else if (iInIndex == 0 && iOutIndex == 0)
						{
							
							System.out.println("Listener Thread: I am the first process to request critical section !! YAY !!...");
							
							bFirstProcess = true;
							System.out.println("Listener Thread: bFirstProcess set to true");
							
						}//End of else if (iInIndex == 0 && iOutIndex == 0)				
						
						
					}//End of if(iTempArray[iInIndex] == iTempProcessId)
					
				}//End of for loop for iInIndex
					
			}//End of for loop for iOutIndex
			
			conListenerThread = thDatabase.makeConnection();
			int iContinue = 0; //When database thread reads last request was serviced then it send code 1 to remaining threads - that when all threads terminate execution
			
			rqRQueue = new RequestQueue[100];
			
			thDatabase.checkInitiate(conListenerThread, iRun, iAlgo);
			System.out.println("Listener Thread: Returning from initiate ");
						
			if(bFirstProcess == false)
			{
				
				System.out.println("Listener Thread: I am not the first process in queue ...");
				
				/*int iCheckPrevious = thDatabase.checkPreviousRequest(iRLOrder[iPreviousOutIndex][iPreviousInIndex], conListenerThread);

				while(iCheckPrevious != 1)
				{
					
					iCheckPrevious = thDatabase.checkPreviousRequest(iRLOrder[iPreviousOutIndex][iPreviousInIndex], conListenerThread);
					
				}//End of while(iCheckPrevious != 1)
				*/
				//if(iCheckPrevious == 1)
				{

					System.out.println("Listener Thread: My previous process has requested critical section now it is my turn to request critical section ...");	
					if(bToken == true)
					{
						
						System.out.println("Listener Thread: Sending my request for critical section to myself ...");
						thTalker.requestMyselfCriticalSection(iProcessId);
						
					}
					else
					{
						
						System.out.println("Listener Thread: Sending my request for critical section to myself and my quorum members ...");
						thTalker.requestMyselfCriticalSection(iProcessId);
						thTalker.requestQuorumCriticalSection(iProcessId);				
						
					}
					
				}//End of if(iCheckPrevious == 1)
				
			}//End of if(bFirstProcess == false)
			else
			{
				
				if(bToken == true)
				{
					
					System.out.println("Listener Thread: Sending my request for critical section to myself ...");
					thTalker.requestMyselfCriticalSection(iProcessId);
					
				}
				else
				{
					
					System.out.println("Listener Thread: Sending my request for critical section to myself and my quorum members ...");
					thTalker.requestMyselfCriticalSection(iProcessId);
					thTalker.requestQuorumCriticalSection(iProcessId);				
					
				}
				
				
			}//End of else of if(bFirstProcess == false)		

			
			//int iContinue = 0; //When database thread reads last request was serviced then it send code 1 to remaining threads - that when all threads terminate execution
			//int iOriginalRequestor = -1;
			
			//System.out.println("Listener thread started execution ...");

			//System.out.println("Created request queue");
			//ServerSocket ssocListener = new ServerSocket(1234);
			System.out.println("Listener Thread: Before socket accept()");
			
			Socket socListener = ssocListener.accept();
			System.out.println("Listener Thread: After socket accept()");
			
			BufferedReader brInput = new BufferedReader(new InputStreamReader(socListener.getInputStream()));
			String sInput = null;
	        sInput = brInput.readLine();
        
	        iContinue = thDatabase.checkTerminate(conListenerThread);//Check for the terminate record in the database - 0 for no terminate yet 1- for terminate
 
	        
	        while (iContinue == 0) 
	        {
	        	
	        	if(!sInput.equals(null))
	        	{

		        	addMessage(sInput);
	        		
	        	}//End of if(sInput.equals(null))
	        	else
	        	{
	        		
	        		if(iRIndex < 0)
	        		{
	        			
	        			Thread.sleep(500);
	        			
	        		}
	        		
	        	}
	        	
	        	if(iRIndex != -1 && bAsked == false)
    			{
    				
	        		//Message received will be of String format: originalSender,sequenceno,fromprocessid,typeofmessage <No spaces>
    				String sTopOfQueue = get();
    				
    				String[] sTopItem = sTopOfQueue.split(",");
    				
    				int iTempOriginalRequestor = Integer.parseInt(sTopItem[0]); 
    				int iMessageType = Integer.parseInt(sTopItem[3]);
    				
    				if(iTempOriginalRequestor == iProcessId)
    				{
    					
    					if(iMessageType == 4)//Requesting Critical Section for myself
    					{
    						
    						System.out.println("Listener Thread: Processing requesting critical section request for myself ...");
    						
    						if(bToken == true)
        					{
        						
    							executeCS();				
        						pop();
        						bAsked = false;
        						
        					}//End of if(bToken == true)
        					else if(bToken == false)
        					{
        						
        						System.out.println("Listener Thread: Requesting token from quorum members ...");
        						
        						thTalker.requestTokenFromQuorum(sTopOfQueue, iProcessId);
        						bAsked = true;
        						
        					}//End of else if(bToken == false)
    						
    						
    					}//End of if(iMessageType == 4)
    					
    					
    				}//End of if (iHolder == iProcessId && bRequesting == true)
    				else if (iTempOriginalRequestor != iProcessId) //Request of some other process is at the top of the queue
    				{

    					if(iMessageType == 4)//If the process is requesting for CS 
    					{
    						
    						System.out.println("Listener Thread: Processing requesting critical section request ...");
    						
    						if(bToken == true)//And I have the token then send the token
    						{
    							
    							int iForwardProcess = Integer.parseInt(sTopItem[2]);//Find the process to whom the token needs to be forwarded to
        						thTalker.sendToken(iForwardProcess);//Send token to that process
        						
        						System.out.println("Listener Thread: Token forwarded ...");
        						
        						pop();
        						bToken = false;
        						bAsked = false;
    							
    						}//End of if(bToken == true)
    						else if(bToken == false)//If I don't have the token then I would ask my quorum members if they have the token and send the request
    						{
    							
    							System.out.println("Listener Thread: Requesting token from quorum members ...");
    							
    							thTalker.requestTokenFromQuorum(sTopOfQueue, iProcessId);
    							bAsked = true;
    							
    						}//End of else if(bToken == false)
    						
    						
    					}//End of if(Integer.parseInt(sTopItem[3]) == 4)
  

    				}//End of else of if (iHolder == iProcessId && bRequesting == true)
    				

    			}//End of if(iRIndex != -1)       	
	        	
	        	iContinue = thDatabase.checkTerminate(conListenerThread);
	        		        	
	        }//End of while (iContinue == 0) 
	        
	        //if(iContinue == 1)
	        {
	        	
	        	brInput.close();
	        	System.out.println("Listener Thread: All requests have been processed ...Ending execution ...");
	        	
	        }//If terminate record found then terminate all connections and thread
	        
			
		}//End of try block
		catch (Exception e) 
		{
	    	
	         System.out.println("Listener Thread: Thread exception in run() method of ListenerThread ...");
	         System.exit(0);
	         
		}//End of catch (InterruptedException e)
		
		 
	}//End of run() method
		
	
	//************************************************************************************************************************************************************
	//Method Description: Updates the IP address for the quorum members
	//Return Type		: void
	//Input Parameters	: int - Index of the object in array that needs to be updated with the IP Address
	//					: int - ProcessId of the quorum member
	//Exceptions		: General Exception type
	//************************************************************************************************************************************************************
	public void addMessage(String sTemp)
	{

		//Add and sort the messages by smallest sequence no and then by process id if a tie
		//Message received will be of String format: originalSender,sequenceno,fromprocessid,typeofmessage <No spaces>
		//Message Types
		//0 - REQUEST TOKEN
		//1 - SEND TOKEN
		//2 - EXECUTING CS
		//3 - DELETE REQUEST
		//4 - REQUEST CS
		
		String[] sNewMessage = sTemp.split(",");
		int iTempOriginalSender = Integer.parseInt(sNewMessage[0]);
		int iTempSeqNo = Integer.parseInt(sNewMessage[1]);
		int iTempSender = Integer.parseInt(sNewMessage[2]);
		int iTempMsgType = Integer.parseInt(sNewMessage[3]);
		
		System.out.println("Listener Thread: iTempOriginalSender" + iTempOriginalSender);
		System.out.println("Listener Thread: iTempSeqNo" + iTempSeqNo);
		System.out.println("Listener Thread: iTempSender" + iTempSender);
		System.out.println("Listener Thread: iTempMsgType" + iTempMsgType);
		
		thDatabase.updateDatabaseRecieveRequest(iTempOriginalSender, iTempSeqNo, iTempSender, iTempMsgType, conListenerThread);
		
		if(iTempMsgType == 3)//There is no need to add delete request message to the request queue
		{
			
			System.out.println("Listener Thread: Processing delete message at the socket ...");
			
			int iDRIndex = -1;
			
			for(int iDIndex = 0; iDIndex < iRIndex; iDIndex++)
			{
				
				if(iTempOriginalSender == rqRQueue[iDIndex].iOriginalSender  && iTempSeqNo == rqRQueue[iDIndex].iSeqNo)
				{
					
					iDRIndex = iDIndex;
					
					for(int iDRIndex2 = iDRIndex; iDRIndex2 < (iRIndex-1) ; iDRIndex2++)
					{
						
						rqRQueue[iDRIndex2] = rqRQueue[(iDRIndex2+1)];
						iRIndex = iRIndex - 1;
						
						
					}//End of for loop for iDRIndex2
					
				}//End of if(iTempOriginalSender == rqRQueue[iDIndex].iOriginalSender  && iTempSeqNo == rqRQueue[iDIndex].iSeqNo)				
				
			}//End of for loop for iDIndex

			
		}//End of if(iTempMsgType == 3)
		if(iTempMsgType == 1)//There is no need to add delete request message to the request queue
		{
			
			System.out.println("Listener Thread: Processing send token message at the socket ...");
			
			bToken = true;
			
		}//End of if(iTempMsgType == 3)
		else if(iTempMsgType == 4)
		{
			
			
			System.out.println("Listener Thread: Processing requesting critical section message at the socket ...");
			
			int iTempIndex = -1;
			
			if(iRIndex < 0)
			{
				
				iRIndex = iRIndex + 1;
				rqRQueue[iRIndex] = new RequestQueue();
				rqRQueue[iRIndex].iOriginalSender = iTempOriginalSender;
				rqRQueue[iRIndex].iSeqNo = iTempSeqNo;
				rqRQueue[iRIndex].iSender = iTempSender;
				rqRQueue[iRIndex].iMsgType = iTempMsgType;
				
				System.out.println("Listener Thread: Adding first request in the queue...");
				
			}
			else if(iRIndex >= 0)
			{
				
				for(int iIndex = 0; iIndex < iRIndex ; iIndex++)
				{

					if(rqRQueue[iIndex].iSeqNo > iTempSeqNo)
					{
						
						iTempIndex = iIndex;
						System.out.println("Listener Thread: Inside if(rqRQueue[iIndex].iSeqNo > iTempSeqNo)");
						
						break;				
						
					}
				
					
				}//End of for loop for iIndex
				
				if(rqRQueue[(iTempIndex-1)].iSeqNo < iTempSeqNo)
				{
					
					iRIndex = iRIndex + 1;
					System.out.println("Listener Thread: Inside if(rqRQueue[(iTempIndex-1)].iSeqNo < iTempSeqNo)");
					
					rqRQueue[iRIndex] = new RequestQueue();
					for(int iIndex = iRIndex; iIndex > iTempIndex; iIndex--)
					{
						
						rqRQueue[iIndex] = rqRQueue[iIndex-1];
						
						
					}//End of for loop for iIndex
					
					rqRQueue[(iTempIndex-1)].iOriginalSender = iTempOriginalSender;
					rqRQueue[(iTempIndex-1)].iSeqNo = iTempSeqNo;
					rqRQueue[(iTempIndex-1)].iSender = iTempSender;
					rqRQueue[(iTempIndex-1)].iMsgType = iTempMsgType;
					
				}//End of if(rqRQueue[(iTempIndex-1)].iSeqNo < iTempSeqNo)
				else if(rqRQueue[(iTempIndex-1)].iSeqNo == iTempSeqNo)
				{
					
					System.out.println("Listener Thread: Inside else if(rqRQueue[(iTempIndex-1)].iSeqNo == iTempSeqNo)");
					if(rqRQueue[(iTempIndex-1)].iSender > iTempSender)
					{
						
						iRIndex = iRIndex + 1;
						rqRQueue[iRIndex] = new RequestQueue();
						System.out.println("Listener Thread: Inside if(rqRQueue[(iTempIndex-1)].iSender > iTempSender)");
						for(int iIndex = iRIndex; iIndex >= iTempIndex; iIndex--)
						{
							
							rqRQueue[iIndex] = rqRQueue[iIndex-1];
							
							
						}//End of for loop for iIndex
						
						rqRQueue[(iTempIndex-1)].iOriginalSender = iTempOriginalSender;
						rqRQueue[(iTempIndex-1)].iSeqNo = iTempSeqNo;
						rqRQueue[(iTempIndex-1)].iSender = iTempSender;
						rqRQueue[(iTempIndex-1)].iMsgType = iTempMsgType;
						
					}//End of if(rqRQueue[(iTempIndex-1)].iSender > iTempSender)
					else if(rqRQueue[(iTempIndex-1)].iSender < iTempSender)
					{
						
						iRIndex = iRIndex + 1;
						rqRQueue[iRIndex] = new RequestQueue();
						System.out.println("Listener Thread: Inside else if(rqRQueue[(iTempIndex-1)].iSender < iTempSender)");
						for(int iIndex = iRIndex; iIndex > iTempIndex; iIndex--)
						{
							
							rqRQueue[iIndex] = rqRQueue[iIndex-1];
							
							
						}//End of for loop for iIndex
						
						rqRQueue[(iTempIndex-1)].iOriginalSender = iTempOriginalSender;
						rqRQueue[(iTempIndex-1)].iSeqNo = iTempSeqNo;
						rqRQueue[(iTempIndex-1)].iSender = iTempSender;
						rqRQueue[(iTempIndex-1)].iMsgType = iTempMsgType;				
						
					}//End of else if(rqRQueue[(iTempIndex-1)].iSender < iTempSender)
					else if (rqRQueue[(iTempIndex-1)].iSender == iTempSender)
					{
						
						System.out.println("Listener Thread: Inside else if (rqRQueue[(iTempIndex-1)].iSender == iTempSender)");
						System.out.println("Listener Thread: Rejecting duplicate message at the socket ...");
						//Don't add the duplicate request to the queue
						//Do nothing
						
						
					}//End of else if(rqRQueue[(iTempIndex-1)].iSender == iTempSender)
								
					
				}//End of else if(rqRQueue[(iTempIndex-1)].iSeqNo == iTempSeqNo)
				
				System.out.println("Listener Thread: Updating sequence number ...");
				
				int iTempUpdatedSeqNo = thDatabase.getUpdatedSeqNo(iProcessId, conListenerThread);
				updateSequenceNo(iTempUpdatedSeqNo);				
				
			}
			
			System.out.println("Listener Thread: Returning ...");
			
		}//End of else for if(iTempMsgType == 4)

		
	}//End of add() method


	//************************************************************************************************************************************************************
	//Method Description: Updates the IP address for the quorum members
	//Return Type		: void
	//Input Parameters	: int - Index of the object in array that needs to be updated with the IP Address
	//					: int - ProcessId of the quorum member
	//Exceptions		: General Exception type
	//************************************************************************************************************************************************************
	public void updateSequenceNo(int iTempSequenceNo)//updates sequence no of the current process
	{
		
		if (iSeqNo < iTempSequenceNo)//Only if iSequenceNo is smaller than the new value passed as argument - update the sequence# else do nothing
		{

			iSeqNo = iTempSequenceNo;

		}//End of if (iSequenceNo < iTempSequenceNo)
		
	}//End of updateSequenceNo()
	

	//************************************************************************************************************************************************************
	//Method Description: Updates the IP address for the quorum members
	//Return Type		: void
	//Input Parameters	: int - Index of the object in array that needs to be updated with the IP Address
	//					: int - ProcessId of the quorum member
	//Exceptions		: General Exception type
	//************************************************************************************************************************************************************
	public String get()
	{
		
		String sTempReturn = null;
		
		System.out.println("Listener Thread: Returning the first request in the queue ...");
		
		if(iRIndex > -1)
		{
					
			sTempReturn = rqRQueue[0].iOriginalSender + "," + rqRQueue[0].iSeqNo + "," + rqRQueue[0].iSender + "," + rqRQueue[0].iMsgType;
					
		}//End of if(iRIndex > -1)		
		
		return sTempReturn;
		
		
	}//End of get() method
	
	
	//************************************************************************************************************************************************************
	//Method Description: Updates the IP address for the quorum members
	//Return Type		: void
	//Input Parameters	: int - Index of the object in array that needs to be updated with the IP Address
	//					: int - ProcessId of the quorum member
	//Exceptions		: General Exception type
	//************************************************************************************************************************************************************
	public void pop()
	{
		
		System.out.println("Listener Thread: Deleting first request from the queue as it has been handled ...");
		
		if(iRIndex > -1)
		{
		
			for(int iIndex = 1; iIndex <= iRIndex ; iIndex++)
			{
				
				rqRQueue[(iIndex-1)] = rqRQueue[iIndex];
				
			}//End of for loop for iIndex
			
			rqRQueue[iRIndex].iMsgType = -1;
			rqRQueue[iRIndex].iSender = -1;
			rqRQueue[iRIndex].iSeqNo = -1;
			
			iRIndex = iRIndex - 1;
					
		}//End of if(iRIndex > -1)
		
		
	}//End of pop() method
	
	
	//************************************************************************************************************************************************************
	//Method Description: Updates the IP address for the quorum members
	//Return Type		: void
	//Input Parameters	: int - Index of the object in array that needs to be updated with the IP Address
	//					: int - ProcessId of the quorum member
	//Exceptions		: General Exception type
	//************************************************************************************************************************************************************
	public void executeCS()
	{
		
		try
		{
			
			System.out.println("Listener Thread: Executing critical section...");
			
			if(bToken == true && bCS == false)
			{
				
				thDatabase.updateExecutingCS(iProcessId, iSeqNo, conListenerThread);
				Thread.sleep(1000);
				thDatabase.existingExecutingCS(iProcessId, iSeqNo, conListenerThread);
				
			}//End of if(bToken == true & bRequesting == true && bCS == false)
			
			
		}//End of try block
		catch(Exception e)
		{
			
			System.out.println("Listener Thread: Thread exception while excuting critical section ...");
	         System.exit(0);
			
		}//End of catch block		
		
		
	}//End of executeCS() method
	

}//End of class ListenerThread