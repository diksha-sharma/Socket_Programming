import java.sql.*;
import java.util.*;
import java.io.*;
import java.net.*;

//receiving and message or like server
/*****************************************************
    //Message Types:
    //0 - REQUEST CS
    //1 - send Lock
    //2 - Request Lock
    //3 - INQUIRE SEND/recieve
    //4 - SEND RELINQUISH/RECIEVE RELINQUISH
    //5 - SEND RELEASE (delete) (SAME AS DELETE REQUEST OF ITS OWN FROM QUEUE)/ RECIEVE RELEASE (SAME AS DELETE REQUEST OF SPECIFIC PROCESSID, 
    //6 - Failed
    //7 - EXECUTE CS (just for database update purpose
    //8 - Exiting CS
*******************************************************/

public class ListenerThread_MA extends Thread
{
	String sName = null;
	RequestQueue[] rqRQueue;//This is where all incoming messages are queued
	DatabaseThread_MA thDatabase;
	TalkerThread_MA thTalker;
	Connection conListenerThread = null;        
        
	int iRun = -1;
	int iAlgo = -1;
	int iRIndex = -1;
	int iSeqNo = 0;
	int iProcessId = -1;
        int iOutstandingReplies = 0;
        
	//boolean bToken = false;
	boolean bCS = false;
	//int iHolder = -1;
        boolean bLocked = false;//Whether the current process is blocked for a process
   	boolean bRequestingCS= false;
	
        int[][] iRLOrder; //This is the orginal request order that the processes are supposed to make requests in
	Quorum[] aQuorum;
	int iTotalNodes = 0;
	
        // may have to delete 
        //Map<Integer, Integer> iMap_Queue = new LinkedHashMap <>();
        int iFirst_Pid = 0; // Map --> use these 2 variables in receieverequest()  for if statements
        int iFirst_Sid = 0; //Map        

        int iInquireProcessId; //The process which sent the inquire message   
        int iLockedForProcessId = -1; //The process for which the current process is blocked (process id)
        int iLockedForSequenceId = -1; //The process for which the current process is blocked (sequenceid)

       // boolean bRelease = false; //When true send messages to quorum memebers that the current process has released CS
        boolean bFailed = false;//When true sends a failed message to all the process in channel queue except the first process in the queue. The queue should be sorted first.
        //boolean bInquire = false;//When true will mean the process has received an inquire message so needs to reply to other process in case of a failed scenario with relinquish message.
                                          //If it frees itself from its own lock as other request
                                          //preceeds its request then this variable will be false
                                          //to indicate that no replies need to be sent to other
        int iOriginalRequestor;
        /***********************************************************
         * 
         * Constructor used to initialize the thread variable
         * @param sTempName: Name of the Current thread
         * @param iTempRun : Run number to be used for current execution
         * @param iTempAlgo : Algorithm number of the current execution
         * @param iTempProcessId : ProcessId of the current process id
         * @param iTempRequest : Request Queue read from the shared file
         * @param aTempQuorum : Quorum members of the current process
         * @param iTempTotalNodes : Total number of nodes that are using the algorithm
         *
         ************************************************************/
        
        ListenerThread_MA(String sTempName, int iTempRun, int iTempAlgo, int iTempProcessId, int[][] iTempRequest, Quorum[] aTempQuorum, int iTempTotalNodes)
	{		
		sName = sTempName;
		iRun = iTempRun;
		iAlgo = iTempAlgo;
		iProcessId = iTempProcessId;
		iOutstandingReplies = aTempQuorum.length; // # of replies to execute CS
                iRLOrder = iTempRequest;
		aQuorum = aTempQuorum;
                iOriginalRequestor = iProcessId;
		iTotalNodes = iTempTotalNodes;
		thDatabase = new DatabaseThread_MA ("Database", iTempRun, iTempAlgo, iTempProcessId);
		thTalker = new TalkerThread_MA ("Talker", iTempRun, iTempAlgo, iTempProcessId, iTempRequest, aTempQuorum, iTotalNodes, bLocked);
		//System.out.println("Listener thread Initialized...");
		this.start();
		
	}//End of constructor ListenerThread_MA()
        
        /**********************************************************
        *
        * Executed once a ListenerThread object. 
        * 1) check if current process can send request for critical section
        * 2) make connection to the database thread
        * 3) check if the the process need to terminated as all the requests are granted
        * 4) keep the input stream active and read in the message send by other processes with specific message type
        * 5) If input stream is empty check the request queue to process the requests from quorum members.
        * 6) exist the method when the counter to terminate is true (iContinue is 1)
        *
        *********************************************************/
        public void run()
	{
		try
		{	//thDatabase.start();			
			//System.out.println("Talker thread starting execution ...");			
			int iPreviousOutIndex = -1;
			int iPreviousInIndex = -1;
			boolean bFirstProcess = false;			
			int iPreviousProcessId = 0;			
			System.out.println("Listener Thread: Finding if it is current process's turn to request critical section ...");
			
                        ServerSocket ssocListener = new ServerSocket(1234);
			//System.out.println("iRTOrder.length-1:  " + (iRTOrder.length-1));
			
			//Find out when is current process's turn to request critical section
			for(int iOutIndex = 0; iOutIndex <= (iRLOrder.length-1); iOutIndex++)
			{				
				int[] iTempArray = iRLOrder[(iRLOrder.length-1)];
				//System.out.println("iTempArray.length-1:  " + (iTempArray.length-1));
				
				for(int iInIndex = 0; iInIndex <= (iTempArray.length-1); iInIndex++)
				{					
					//System.out.println("My processid:  " + iProcessId);
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
						}//End of else if (iInIndex == 0 && iOutIndex == 0)						
					}//End of if(iTempArray[iInIndex] == iTempProcessId)					
				}//End of for loop for iInIndex					
			}//End of for loop for iOutIndex
			
                        conListenerThread = thDatabase.makeConnection();
			int iContinue = 0; //When database thread reads last request was serviced then it send code 1 to remaining threads - that when all threads terminate execution
			//int iOriginalRequestor = -1;
			
			//System.out.println("Listener thread started execution ...");
			
                        thDatabase.checkInitiate(conListenerThread, iRun, iAlgo);
			System.out.println("Listener Thread: Returning from initiate ");
			
			if(bFirstProcess == false)
			{				
				System.out.println("Listener Thread: I am not the first process in queue ...");	
                                System.out.println("Listener Thread: Checking is previous process by going to Database Thread ...");
				
                                int iCheckPrevious = thDatabase.checkPreviousRequest(iRLOrder[iPreviousOutIndex][iPreviousInIndex], conListenerThread);
				System.out.println("Listener Thread: After check previous");
				while(iCheckPrevious != 1)
				{					
                                    iCheckPrevious = thDatabase.checkPreviousRequest(iRLOrder[iPreviousOutIndex][iPreviousInIndex], conListenerThread);		
				}//End of while(iCheckPrevious != 1)
				
				if(iCheckPrevious == 1)
				{
                                    System.out.println("Listener Thread: in checkprevious == 1");
                                    System.out.println("Listener Thread: My previous process has requested critical section now it is my turn!! YAY!! ...");
                                    thTalker.requestCriticalSection();					
				}//End of if(iCheckPrevious == 1)
				
			}//End of if(bFirstProcess == false)
			else
			{				
                            System.out.println("Listener Thread: Sending my request for critical section to my quorum members ...");
                            thTalker.requestCriticalSection();				
			}//End of else of if(bFirstProcess == fals)				
			//System.out.println("Created request queue");
                        
                        
			rqRQueue = new RequestQueue[100];
                        System.out.println("Listener Thread: Before socket accept()");
                        
			Socket socListener = ssocListener.accept();
                        System.out.println("Listener Thread: After socket accept()");
			BufferedReader brInput = new BufferedReader(new InputStreamReader(socListener.getInputStream()));
                        
			String sInput = null;
                        sInput = brInput.readLine();
			System.out.println("Listener Thread: Read Input Message:  " + sInput);
			//DataInputStream disInput = null; 
			//Socket socListener = new Socket("net01.utdallas.edu", 1234);
                        //System.out.println("Socket created ");			
			//System.out.println("Receiving message...");		
	        //conListenerThread = thDatabase.makeConnection();
	        
	        iContinue = thDatabase.checkTerminate(conListenerThread);//Check for the terminate record in the database - 0 for no terminate yet 1- for terminate
	        
	        while (iContinue == 0) 
	        {	        	
	        	//System.out.println("Executing while");	        	
	        	//Socket socListeningSocket = socListener.accept();
	        	//BufferedReader brReader = new BufferedReader(new InputStreamReader(socListeningSocket.getInputStream()));	        	
	        	if(!sInput.equals(null))
	        	{
		        	//Get packets from the port
	        		//sInput = brReader.readLine();	        		
	        		System.out.println("Listener Thread: Message received:  " + sInput); // Read one line and output it
		        	addMessage(sInput);
		        	System.out.println("Listener Thread: Message added:  " + sInput); // Read one line and output it	        		
	        	}//End of if(sInput.equals(null))
                        else 
                        {
                            if(iRIndex < 0)
                            {
                                Thread.sleep(500);
                            }
                        }
                                
	        	if(iRIndex != -1) //requesst pending
    			{    				
	        		//Message received will be of String format: originalSender,sequenceno,fromprocessid,typeofmessage <No spaces>
    				String sTopOfQueue = get();    				
    				String[] sTopItem = sTopOfQueue.split(",");    
                                
    				int iTempOriginalRequestor = Integer.parseInt(sTopItem[0]); // In MA same
    				int iMessageType = Integer.parseInt(sTopItem[3]);
                                
                                System.out.println("iTempOriginalRequestor..." + iTempOriginalRequestor);
    				System.out.println("iOriginalRequestor..." + iOriginalRequestor);
    				System.out.println("sTopOfQueue..." + sTopOfQueue);
    				System.out.println("sTopItem[0]..." + sTopItem[0]);
    				System.out.println("sTopItem[1]..." + sTopItem[1]);
    				System.out.println("sTopItem[2]..." + sTopItem[2]);
    				System.out.println("sTopItem[3] ..." + sTopItem[3]);
                                
                           	if(iTempOriginalRequestor == iProcessId) //itself
    				{    					
    					if(iMessageType == 0)//Requesting Critical Section for myself
    					{    		
                                                //call method to check outstandingreplies
    						System.out.println("Listener Thread: Processing requesting critical section request ..."); 
                                                if(iOutstandingReplies == 0 && bRequestingCS == true && bCS == false)//If all processes from quorum have sent the locked replies to the current process and it possesses the token then enter CS
                                                { 
                                                    bLocked = true;
                                                    iLockedForProcessId = Integer.parseInt(sTopItem[2]);//Find the process to whom the token needs to be forwarded to
                                                    iLockedForSequenceId = Integer.parseInt(sTopItem[1]);// sequence Id
                                                    bCS = true; //Set this value to true as the process will not enter CS
                                                    executeCS(); //Execute CS
                                                    thTalker.sendReleaseRequest(iProcessId, iSeqNo);
                                                    pop(); // deleted itself                                                    
                                                    bCS = false; //Set value to false as the process is done executing CS 
                                                    bLocked = false;
                                                }
    					 	else if(iOutstandingReplies != 0) // one once we will be in here 
        					{        						
                                                    System.out.println("Listener Thread: Requesting Lock from quorum members ...");        						
                                                    thTalker.requestLockFromQuorum(sTopOfQueue, iProcessId);        						
        					}//End of else if(bToken == false)
    					}//End of if(iMessageType == 4)    					
    					
    				}//End of if (iHolder == iProcessId && bRequestingCS == true)
                                //so this will be either  Inquire, requestLock, REceive Failed, Relinquish
    				else if (iTempOriginalRequestor != iProcessId) //Request of some other process is at the top of the queue
    				{
    					if(iMessageType == 0)//If the process is requesting for CS 
    					{    						
    						System.out.println("Listener Thread: Processing requesting critical section request ...");    						
    						if(bLocked == false)//And I have the Lock avialab e, so sendLock
    						{    							
    							int iForwardProcess = Integer.parseInt(sTopItem[2]);//Find the process to whom the token needs to be forwarded to
        						int iForwardSid = Integer.parseInt(sTopItem[1]);// sequence Id
                                                        iLockedForProcessId = iForwardProcess;
                                                        iLockedForSequenceId = iForwardSid;                                                        
                                                        thTalker.sendLocked(iForwardProcess);//Send token to that process        						
        						System.out.println("Listener Thread: Token forwarded ...");        						
        						//pop(); //wait till we receieve relesae to pop
        						bLocked = true;  							
    						}//End of if(bToken == true)
    						else if(bLocked == true)//If I don't have the Lock then I would ask my quorum members if they have the token and send the request
    						{    							
    							//do nothing have to wait till bLocked == false    							
    						}//End of else if(bToken == false) 	
    						
    					}//End of if(Integer.parseInt(sTopItem[3]) == 4)
  

    				}//End of else of if (iHolder == iProcessId && bRequestingCS == true)

                            }//End of if(iRIndex != -1) 
	        	iContinue = thDatabase.checkTerminate(conListenerThread);
	        	
	        }//End of while (iContinue == 0) 
	        
	        if(iContinue == 1) //termination
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

        /****************************************************************************
         * 
         * @param sTemp 
         ****************************************************************************/
        
        public void addMessage(String sTemp)
	{
		//Add and sort the messages by smallest sequence no and then by process id if a tie
		//Message received will be of String format: originalSender,sequenceno,fromprocessid,typeofmessage <No spaces>
        //Message Types TQ			//Message Types MA
	//0 - REQUEST TOKEN			//0 - REQUEST CS
	//1 - SEND TOKEN			//1 - Send LOCKED
	//2 - EXECUTING CS			//2 - Request Locked
	//3 - DELETE REQUEST                    //3 - INQUIRE SEND/recieve
	//4 - REQUEST CS                	//4 - SEND RELINQUISH/RECIEVE RELINQUISH
						//5 - SEND RELEASE (delete) (SAME AS DELETE REQUEST OF ITS OWN FROM QUEUE)/ RECIEVE RELEASE (SAME AS DELETE REQUEST OF SPECIFIC PROCESSID, 
						//6 - Failed
						//7 - EXECUTE CS (just for database update purpose
		String[] sNewMessage = sTemp.split(",");
		int iTempOriginalSender = Integer.parseInt(sNewMessage[0]);
		int iTempSeqNo = Integer.parseInt(sNewMessage[1]);
		int iTempSender = Integer.parseInt(sNewMessage[2]); //same as Original Sender_MA
		int iTempMsgType = Integer.parseInt(sNewMessage[3]);
				
		thDatabase.updateDatabaseRecieveRequest(iTempOriginalSender, iTempSeqNo, iTempSender, iTempMsgType, conListenerThread);		
		if(iTempMsgType == 6) //receive Failed
                {
                    bLocked = false;
                    bFailed = true;
                    //receiveFailed(sNewMessage);
                }
                else if(iTempMsgType == 5)//There is no need to add delete request message to the request queue
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
                        //Temporarily check
                        
		}//End of if(iTempMsgType == 5)
               
               else if(iTempMsgType == 4) //receive relinquish
                {
                    bLocked = false;
                    //recieveRelinquish(sNewMessage);
                }                
               else if(iTempMsgType == 3) //receive Inquire
                {
                    recieveInquire(sNewMessage);
                }
                else if(iTempMsgType == 1)//send lock ( you got lock from some x process (you RECEIEVEd Lock
		{			
                    System.out.println("Listener Thread: Processing send token message at the socket ...");			
                    iOutstandingReplies = iOutstandingReplies - 1;                    
//                    if(iOutstandingReplies == 0)
//                    {
//                        executeCS(); //taken care in run method if(iRIndex>0)
//                    }
		}//End of if(iTempMsgType == 3)
		else if(iTempMsgType == 0) //Request CS = 2 requestLock
		{						
			System.out.println("Listener Thread: Processing requesting critical section message at the socket ...");
			//System.out.println("Listener Thread: iRIndex value ..." + iRIndex);			
			int iTempIndex = -1;			
			if(iRIndex < 0)
			{				
				iRIndex = iRIndex + 1;
				rqRQueue[iRIndex] = new RequestQueue();
				rqRQueue[iRIndex].iSeqNo = iTempSeqNo;
				rqRQueue[iRIndex].iSender = iTempSender;
				rqRQueue[iRIndex].iMsgType = iTempMsgType;				
				System.out.println("Listener Thread: Adding first request in the queue...");				
			}
			else if(iRIndex >= 0) //there is already a request in the queue
			{				
				for(int iIndex = 0; iIndex < iRIndex ; iIndex++)
				{
					if(rqRQueue[iIndex].iSeqNo > iTempSeqNo)
					{						
						iTempIndex = iIndex; //????????????????
						System.out.println("Listener Thread: Inside if(rqRQueue[iIndex].iSeqNo > iTempSeqNo)");						
						break;				
					}				
				}//End of for loop for iIndex			
				if(rqRQueue[(iTempIndex-1)].iSeqNo < iTempSeqNo)
				{					
					iRIndex = iRIndex + 1; // increasing the size of queueu
					System.out.println("Listener Thread: Inside if(rqRQueue[(iTempIndex-1)].iSeqNo < iTempSeqNo)");					
					rqRQueue[iRIndex] = new RequestQueue();
					for(int iIndex = iRIndex; iIndex > iTempIndex; iIndex--)
					{						
						rqRQueue[iIndex] = rqRQueue[iIndex-1];				
					}//End of for loop for iIndex
					
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
			
                       // take care of sending Failed/Inquire for recently receive message
                       if(bLocked == true)
                       {
                           send_Failed_Inquire(sNewMessage);
                       }
		}//End of else for if(iTempMsgType == 0)		
	}//End of add() method
        
        /*********************************************************************************
        * 
        * @param TempMessage 
        **********************************************************************************/
        public void send_Failed_Inquire(String[] TempMessage)
        {
                //newly inserted
            	int iTempOriginalSender = Integer.parseInt(TempMessage[0]);
		int iTempSeqNo = Integer.parseInt(TempMessage[1]);
		int iTempSender = Integer.parseInt(TempMessage[2]); //same as Original Sender_MA
		int iTempMsgType = Integer.parseInt(TempMessage[3]);
                
            //Message received will be of String format: originalSender,sequenceno,fromprocessid,typeofmessage <No spaces>
    		String sTopOfQueue = get();    				
    		String[] sTopItem = sTopOfQueue.split(",");                              
    		iFirst_Pid = Integer.parseInt(sTopItem[0]); // In MA same
                iFirst_Sid = Integer.parseInt(sTopItem[1]);
    		int iMessageType = Integer.parseInt(sTopItem[3]);
                //(alChannel[0].ProcessId  =iFirst_Pid
                if(iFirst_Pid == iLockedForProcessId) //When the current process is locked for a process that it should be locked for
                {
                    if (iLockedForProcessId != iTempOriginalSender) 
                    {
                        //Send failed message to the sender
                        thTalker.sendFailedRequest(iTempOriginalSender);//,iTempSeqNo);
                    }
                }                         //(alChannel[0].ProcessId 
                else if (iFirst_Pid != iLockedForProcessId) //When current process is locked by a process that may not be the first one who should be granted to access CS
                {
                    //(alChannel[0].ProcessId 
                if ((iFirst_Pid != iTempOriginalSender) && (iLockedForProcessId != iTempOriginalSender)) //A third non priority process is requesting for the CS
                {
                    //Send failed message to the sender
                    thTalker.sendFailedRequest(iTempOriginalSender);//,iTempSeqNo);
                }
                    //alChannel[0].iSequenceID
                if (iFirst_Sid < iLockedForSequenceId) //The process in the queue should get to CS first than the one for which the current process is locked
                {
                    //Send inquire message to the process the current process is locked for
                    thTalker.sendInquireRequest(iLockedForProcessId);//,iLockedForSequenceId);
                } 
                       // alChannel[0].iSequenceID
                else if (iFirst_Sid> iLockedForSequenceId) //The process in the queue should get to CS later than the one for which the current process is locked
                {
                    //Do nothing
                } 
                        //alChannel[0].iSequenceID
                else if (iFirst_Sid == iLockedForSequenceId) //The process in the queue and one for which the current process is locked have same sequence id - now we compare process id
                {
                        //alChannel[0].ProcessId
                    if (iFirst_Pid < iLockedForProcessId) 
                    {
                        //Send inquire message to the process that the current process is locked for
                        thTalker.sendInquireRequest(iLockedForProcessId);//,iLockedForSequenceId);
                    } 
                    else 
                    {
                        //Do nothing
                    }
                }
            }

        } //end of Checking_Failed_Inquire()
        
        /************************************************************************
         * 
         * @param TempMessage 
        ************************************************************************/
        
        public void recieveInquire(String[] TempMessage)
        {
             //newly inserted
            	int iTempOriginalSender = Integer.parseInt(TempMessage[0]);
		int iTempSeqNo = Integer.parseInt(TempMessage[1]);
		int iTempSender = Integer.parseInt(TempMessage[2]); //same as Original Sender_MA
		int iTempMsgType = Integer.parseInt(TempMessage[3]);
                
                //Message received will be of String format: originalSender,sequenceno,fromprocessid,typeofmessage <No spaces>
                while(bFailed == false && bLocked == true) //?? check  whenever it is unlocked we will go out of the loop , so just treated as normal
                {
                    //do nothing
                }
                if(bFailed == true) //send relinquish
                {
                    thTalker.sendRelinquishRequest(iTempSender);
                }   
        }
        
       /************************************************************************
        * 
        * @param TempMessage 
       *************************************************************************/
       public void recieveRelinquish(String[] TempMessage) 
       {
//                int iTempOriginalSender = Integer.parseInt(TempMessage[0]);
//		int iTempSeqNo = Integer.parseInt(TempMessage[1]);
//		int iTempSender = Integer.parseInt(TempMessage[2]); //same as Original Sender_MA
//		int iTempMsgType = Integer.parseInt(TempMessage[3]);
//                
//                String sTopOfQueue = get();    				
//    		String[] sTopItem = sTopOfQueue.split(",");                              
//    		iFirst_Pid = Integer.parseInt(sTopItem[0]); // In MA same
//                iFirst_Sid = Integer.parseInt(sTopItem[1]);
//    		int iMessageType = Integer.parseInt(sTopItem[3]);
//                
//                iLockedForProcessId = iFirst_Pid;//((int)(alChannel.get(0)));
//                                    //alChannel[0].iSequenceId
//                iLockedForSequenceId = iFirst_Sid; //((int)(alChannel.get(0)));
//            // good idea to place iLockedFor in Send Locked to avoid any loss of messages
//                thTalker.sendLocked(iLockedForProcessId);//iLockedForSequenceId);  
                bLocked = false; // so next available reqeust in the queue can get access to the CS
                
        }

       /************************************************************************
        * 
        * @param iTempSequenceNo 
       *************************************************************************/
       public void updateSequenceNo(int iTempSequenceNo)//updates sequence no of the current process
        {		
		if (iSeqNo < iTempSequenceNo)//Only if iSequenceNo is smaller than the new value passed as argument - update the sequence# else do nothing
		{
                    iSeqNo = iTempSequenceNo;
		}//End of if (iSequenceNo < iTempSequenceNo)
		
        }//End of updateSequenceNo()
       
        /***********************************************************************
         * 
         * @return 
        ************************************************************************/
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
		
        /***********************************************************************
         * To remove the request at the head of queue after exiting the critical
         * section
         ************************************************************************/
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
        
        /***********************************************************************
        * Upon receiving a LOCKED message from every member of quorum, current
        * process will enters its critical section. After accessing Critical
        * Section, current process exits its critical section, it sends a RELEASE
        * message to all processes in quorum, after deleting its own request from
        * its own queue.
        ************************************************************************/
        
        public void executeCS()
	{
            try
            {
                System.out.println("Listener Thread: Executing critical section...");

            if(bLocked == true && iOutstandingReplies == 0)
            {				
                thDatabase.updateExecutingCS(iProcessId, iSeqNo, conListenerThread);
                Thread.sleep(1000);
                thDatabase.exitingExecutingCS(iProcessId, iSeqNo, conListenerThread);
            }//End of if(bToken == true & bRequestingCS == true && bCS == false)			
            }//End of try block
            catch(Exception e)
            {
                System.out.println("Listener Thread: Thread exception while excuting critical section ...");
                System.exit(0);			
            }//End of catch block				
	}//End of executeCS() method

 }//End of class ListenerThread_MA