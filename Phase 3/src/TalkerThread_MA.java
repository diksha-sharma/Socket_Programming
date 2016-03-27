
import java.sql.*;
import java.util.*;
import java.io.*;
import java.net.*;

//sender
/**
 * ***************************************************
 * //Message Types: //0 - REQUEST CS //1 - send Lock //2 - Request Lock //3 -
 * INQUIRE SEND/recieve //4 - SEND RELINQUISH/RECIEVE RELINQUISH //5 - SEND
 * RELEASE (delete) (SAME AS DELETE REQUEST OF ITS OWN FROM QUEUE)/ RECIEVE
 * RELEASE (SAME AS DELETE REQUEST OF SPECIFIC PROCESSID, //6 - Failed //7 -
 * EXECUTE CS (just for database update purpose //8 - Exiting CS
    ******************************************************
 */
public class TalkerThread_MA extends Thread {

    String sName = null;
    int[][] iRTOrder;
    DatabaseThread_MA thDatabase;
    int iRun = -1;
    int iAlgo = -1;
    int iProcessId = -1;
    Connection conTalkerThread = null;
    int iSeqNo = 0;
    Quorum[] aQuorum; // processID, IpAddress
    int iTotalNodes = 0;

    boolean bLocked = false;

    /**
     * *********************************************************************
     *
     * Constructor used to initialize the thread variable
     *
     * @param sTempName: Name of the Current thread
     * @param iTempRun : Run number to be used for current execution
     * @param iTempAlgo : Algorithm number of the current execution
     * @param iTempProcessId : ProcessId of the current process id
     * @param iTempRequestOrder : Request Queue read from the shared file
     * @param aTempQuorum : Quorum members of the current process
     * @param iTempTotalNodes : Total number of nodes that are using the
     * algorithm
     * @param bTempLock : The boolean value determine if the current thread has
     * Lock
     *
     **********************************************************************
     */
    TalkerThread_MA(String sTempName, int iTempRun, int iTempAlgo, int iTempProcessId, int[][] iTempRequestOrder, Quorum[] iTempQuorum, int iTempTotalNodes, boolean bTempLock) {

        sName = sTempName;
        iRun = iTempRun;
        iAlgo = iTempAlgo;
        iProcessId = iTempProcessId;
	System.out.println("Talker iProcessId : " + iProcessId);
	System.out.println("Talker iTempProcessId : " + iTempProcessId);
        iRTOrder = iTempRequestOrder;
        iTotalNodes = iTempTotalNodes;
        bLocked = true; //  delete if not needed
        thDatabase = new DatabaseThread_MA("Database", iTempRun, iTempAlgo, iTempProcessId);

        try {
            conTalkerThread = thDatabase.makeConnection();
        } catch (Exception e)
        {
            System.out.println("Talker Thread  : Exception getting database connection in thread class...");
            System.exit(0);
        }//End of catch (InterruptedException e)

        aQuorum = new Quorum[iTempQuorum.length];
        aQuorum = iTempQuorum; //check how to copy array
        this.start();
    }//End of constructor TalkerThread_MA()

    /**
     * *********************************************************************
     *
     ***********************************************************************
     */
    public void run() {
        System.out.println("Talker thread starting execution ...");
//		try
//		{
//			int iPreviousOutIndex = -1;
//			int iPreviousInIndex = -1;
//			boolean bFirstProcess = false;
//			
//			int iPreviousProcessId = 0;
//			
//			//Find out when is current process's turn to request critical section
//			for(int iOutIndex = 0; iOutIndex < (iRTOrder.length-1); iOutIndex++)
//			{				
//				int[] iTempArray = iRTOrder[(iRTOrder.length-1)];
//				
//				for(int iInIndex = 0; iInIndex < (iTempArray.length-1); iInIndex++)
//				{
//					
//					if(iTempArray[iInIndex] == iProcessId)
//					{
//						
//						if(iInIndex > 0)
//						{							
//							iPreviousOutIndex = iOutIndex;
//							iPreviousInIndex = (iInIndex -1);
//							iPreviousProcessId = iRTOrder[iPreviousOutIndex][iPreviousInIndex];
//							
//						}//End of if(iInIndex > 0)
//						else if(iInIndex == 0 && iOutIndex > 0)
//						{							
//							iPreviousOutIndex = (iOutIndex-1);
//							iPreviousInIndex = (iTempArray.length-1);
//							iPreviousProcessId = iRTOrder[iPreviousOutIndex][iPreviousInIndex];
//							
//						}//End of else if(iInIndex == 0 && iOutIndex > 0)
//						else if (iInIndex == 0 && iOutIndex == 0)
//						{							
//							bFirstProcess = true;
//							
//						}//End of else if (iInIndex == 0 && iOutIndex == 0)											
//					}//End of if(iTempArray[iInIndex] == iTempProcessId)					
//				}//End of for loop for iInIndex					
//			}//End of for loop for iOutIndex
//			
//			if(bFirstProcess == false)
//			{				
//				int iCheckPrevious = thDatabase.checkPreviousRequest(iRTOrder[iPreviousOutIndex][iPreviousInIndex], conTalkerThread);
//				
//				while(iCheckPrevious != 1)
//				{					
//					iCheckPrevious = thDatabase.checkPreviousRequest(iRTOrder[iPreviousOutIndex][iPreviousInIndex], conTalkerThread);
//					
//				}//End of while(iCheckPrevious != 1)
//				
//				if(iCheckPrevious == 1)
//				{					
//                                    requestCriticalSection();
//					
//				}//End of if(iCheckPrevious == 1)
//				
//			}//End of if(bFirstProcess == false)
//			else
//			{				
//                            requestCriticalSection();
//				
//			}//End of else of if(bFirstProcess == false)		
//			
//			
//		}//End of try block
//		catch (Exception e) 
//		{
//	    	
//	         System.out.println("Thread exception in Talker thread ...");
//	         System.exit(0);
//	         
//		}//End of catch (InterruptedException e) 

    }//End of run() method

    /**
     * *********************************************************************
     *
     ***********************************************************************
     */
        //done
    //??? don't need this as we will use reqeustLockFrom Quorum -check used for 1st request send based on previousrequest 
    public void requestCriticalSection() // Requesting Locks 
    {
        String inputS = null;
        System.out.println("Talker Thread  : Udpating sequence number before requesting critical section ...");
        String sCurrentProcess = null;
        
        int iTempSeqNo = thDatabase.getUpdatedSeqNo(iProcessId, conTalkerThread);
        iSeqNo = iTempSeqNo + 1;

        try {
            String sMessage = iProcessId + "," + iSeqNo + "," + iProcessId + "," + ",0";
            System.out.println("Talker Thread  : Sending message: " + sMessage);
            
            if (iProcessId < 10) 
            {
                sCurrentProcess = "net0" + iProcessId + ".utdallas.edu";
            }
            else 
            {
                sCurrentProcess = "net" + iProcessId + ".utdallas.edu";
            }
            System.out.println("Talker Thread  : Sending message to : " + sCurrentProcess);

            //String sCurrentProcess = "net"+ iProcessId +".utdallas.edu";
            System.out.println("Talker Thread: Before Creating Socket object");
            Socket socTalker = new Socket("localhost", 1234); // 
            System.out.println("Talker Thread: After Creating Socket object");
            PrintWriter pwTalker = new PrintWriter(socTalker.getOutputStream(), true);
            pwTalker.print(sMessage);

            //sending request its own queue
            thDatabase.updateDatabaseCriticalSectionRequestSent(iSeqNo, conTalkerThread, iAlgo, iRun, iProcessId);
            pwTalker.close();
            socTalker.close();

            String sQuorumMember = null;

            //semding request to all the quorum members
            for (int iQIndex = 0; iQIndex < aQuorum.length; iQIndex++) 
            {
                System.out.println("Talker Thread  : Sent message requesting critical section to quorum member...");
	    		
	        if (aQuorum[iQIndex].iQProcessId < 10) 
                {
                    
                    sQuorumMember = "net0" + aQuorum[iQIndex].iQProcessId + ".utdallas.edu";
                }
                else 
                {
                    sQuorumMember = "net" + aQuorum[iQIndex].iQProcessId + ".utdallas.edu";
                }

                System.out.println("Talker Thread: Quorum member Before Creating Socket object ");
                socTalker = new Socket(sQuorumMember, 1234);
                System.out.println("Talker Thread: Quorum member After Creating Socket object");
                pwTalker = new PrintWriter(socTalker.getOutputStream(), true);
                pwTalker.print(sMessage);
                
                thDatabase.updateDatabaseForQuorumMember(iSeqNo, conTalkerThread, iAlgo, iRun, aQuorum[iQIndex].iQProcessId, iProcessId);

                pwTalker.close();
                socTalker.close();

            }//End of for loop for iQIndex

        }//End of try block
        catch (UnknownHostException e) {

            System.err.println("Talker Thread  : Unknown host: " + sCurrentProcess);
            System.exit(0);

        } catch (IOException e) {

            System.err.println("Talker Thread  : No I/O on the socket..." + sCurrentProcess);
            System.exit(0);

        } catch (Exception e) {

            System.err.println("Talker Thread  : Issue sending socket messages while requesting Critical section in requestCriticalSection() method of TalkerThread class ....");
            System.exit(0);

        }//End of catch block

    }//End of requestCriticalSection() method

    /**
     * *********************************************************************
     *
     * @param iTempProcessId
     * @param iTempSeqNo 
        ***********************************************************************
     */
	//sendDeleteRequest = Release
    //done need
    //place this in Listner after executing CS
    public void sendReleaseRequest(int iTempProcessId, int iTempSeqNo) {
        System.out.println("Talker Thread  : Sending a delete/release a request from the queue to all processes ...");
        try {
            String sMessage = iProcessId + "," + iSeqNo + "," + iProcessId + "," + ",5";
            String sProcess = null;
//                //TO delte the request from it's own queue pop() method takes care of this
//	    	if(iProcessId < 10)
//    		{    			
//	    		sProcess = "net0"+ iProcessId +".utdallas.edu";    			
//    		}
//    		else
//    		{    			
//    			sProcess = "net"+ iProcessId +".utdallas.edu";    			
//    		}
//	    	
//	    	//String sCurrentProcess = "net"+ iProcessId +".utdallas.edu";
//			Socket socTalker = new Socket(sProcess, 1234);
//			PrintWriter pwTalker = new PrintWriter(socTalker.getOutputStream(), true);
//			pwTalker.print(sMessage);
//			
//                        thDatabase.updateDatabaseForRelease(iSeqNo, conTalkerThread, iAlgo, iRun, iProcessId);
//			pwTalker.close();
//                        socTalker.close();

            String sQuorumMember = null;
            Socket socTalker;
            PrintWriter pwTalker;
            //broadcast release to quorum members
            for (int iQIndex = 0; iQIndex < aQuorum.length; iQIndex++) {
                if (aQuorum[iQIndex].iQProcessId < 10) {
                    sQuorumMember = "net0" + aQuorum[iQIndex].iQProcessId + ".utdallas.edu";
                } else {
                    sQuorumMember = "net" + aQuorum[iQIndex].iQProcessId + ".utdallas.edu";

                }
                
                System.out.println("Talker Thread: sendReleaseRequest for Quorum Before Creating Socket object ");
                socTalker = new Socket(sQuorumMember, 1234);
                System.out.println("Talker Thread: sendReleaseRequest for Quorum After Creating Socket object ");
                pwTalker = new PrintWriter(socTalker.getOutputStream(), true);
                pwTalker.print(sMessage);
                thDatabase.updateDatabaseForReleaseQuorumMember(iSeqNo, conTalkerThread, iAlgo, iRun, aQuorum[iQIndex].iQProcessId, iProcessId);
                //thDatabase.updateDatabaseForDeleteQuorumMember(iSeqNo, conTalkerThread, iAlgo, iRun, iNIndex, iProcessId);
                pwTalker.close();
                socTalker.close();

            }//End of for loop for iQIndex

        }//End of try block
        catch (Exception e) {

            System.err.println("Issue sending socket messages while releasing lock in sendReleaseRequest() method of TalkerThread class ....");
            System.exit(0);

        }//End of catch block             

    }//End if sendReleaseRequest() method

        //???to check if need more parameters from run () of ListenerThread
    //similar to sending token because only inquire to 1 process we previously locked for 
    // similar to send Locked only to 1 process  (sendToken in the TQ)
    //done in db
    /**
     * *********************************************************************
     *
     * @param iTempForwardProcess 
        ***********************************************************************
     */
    public void sendInquireRequest(int iTempForwardProcess) {
        System.out.println("Talker Thread  : Sending Inquire to a process ...");
        String sForwardProcess = null;
        String sMessage = iProcessId + "," + iSeqNo + "," + iProcessId + ",3";

        try {
            if (iTempForwardProcess < 10) 
            {
                sForwardProcess = "net0" + iTempForwardProcess + ".utdallas.edu";
            } 
            else 
            {
                sForwardProcess = "net" + iTempForwardProcess + ".utdallas.edu";

            }
            System.out.println("Talker Thread: sendInquireRequest for Quorum Before Creating Socket object ");
            Socket socTalker = new Socket(sForwardProcess, 1234);
            System.out.println("Talker Thread: sendInquireRequest for Quorum After Creating Socket object ");
            PrintWriter pwTalker = new PrintWriter(socTalker.getOutputStream(), true);
            pwTalker.print(sMessage);
            thDatabase.updateDatabaseForSendInquire(iSeqNo, conTalkerThread, iAlgo, iRun, iProcessId, iTempForwardProcess);

            pwTalker.close();
            socTalker.close();

        }//End of try block
        catch (Exception e) {
            System.err.println("Issue sending socket messages while sending inquire in sendInquireRequest() method of TalkerThread class ....");
            System.exit(0);

        }//End of catch block	

    }//End of sendInquireRequest() method
    // similar to send Locked only to 1 process  (sendToken in the TQ)
    //done in db

    /**
     * *********************************************************************
     *
     * @param iTempForwardProcess 
        ***********************************************************************
     */
    public void sendRelinquishRequest(int iTempForwardProcess) {
        System.out.println("Talker Thread  : Sending Relinquish Message to other process ...");

        String sForwardProcess = null;
        String sMessage = iProcessId + "," + iSeqNo + "," + iProcessId + ",4";

        try 
        {
            if (iTempForwardProcess < 10) 
            {
                sForwardProcess = "net0" + iTempForwardProcess + ".utdallas.edu";
            } else
            {
                sForwardProcess = "net" + iTempForwardProcess + ".utdallas.edu";
            }

            System.out.println("Talker Thread: sendRelinquishRequest for Quorum Before Creating Socket object ");
            Socket socTalker = new Socket(sForwardProcess, 1234);
            System.out.println("Talker Thread: sendRelinquishRequest for Quorum Before Creating Socket object ");
            PrintWriter pwTalker = new PrintWriter(socTalker.getOutputStream(), true);
            pwTalker.print(sMessage);
            thDatabase.updateDatabaseForSendRelinquish(iSeqNo, conTalkerThread, iAlgo, iRun, iProcessId, iTempForwardProcess);

            pwTalker.close();
            socTalker.close();

        }//End of try block
        catch (Exception e) {

            System.err.println("Issue sending socket messages while sending Reqlinquish message in sendRelinquishRequest() method of TalkerThread class ....");
            System.exit(0);

        }//End of catch block

    }//End of sendRelinquishRequest() method
    // similar to send Locked only to 1 process  (sendToken in the TQ)
    //done in db

    /**
     * *********************************************************************
     *
     * @param iTempForwardProcess 
        ***********************************************************************
     */
    public void sendFailedRequest(int iTempForwardProcess) {
        System.out.println("Talker Thread  : Sending Failed Message to other process ...");
        String sForwardProcess = null;
        String sMessage = iProcessId + "," + iSeqNo + "," + iProcessId + ",2";

        try {
            if (iTempForwardProcess < 10) 
            {

                sForwardProcess = "net0" + iTempForwardProcess + ".utdallas.edu";

            } 
            else 
            {
                sForwardProcess = "net" + iTempForwardProcess + ".utdallas.edu";
            }

            System.out.println("Talker Thread: sendFailedRequest for Quorum Before Creating Socket object ");
            Socket socTalker = new Socket(sForwardProcess, 1234);
            System.out.println("Talker Thread: sendFailedRequest for Quorum After Creating Socket object ");
            PrintWriter pwTalker = new PrintWriter(socTalker.getOutputStream(), true);
            pwTalker.print(sMessage);
            thDatabase.updateDatabaseForSendFailed(iSeqNo, conTalkerThread, iAlgo, iRun, iProcessId, iTempForwardProcess);

            pwTalker.close();
            socTalker.close();

        }//End of try block
        catch (Exception e) {

            System.err.println("Issue sending socket messages while sending Failed message in sendFailedRequest() method of TalkerThread class ....");
            System.exit(0);

        }//End of catch block

    }//End of sendFailedRequest() method

    /**
     * *********************************************************************
     *
     * @param sTempRequest
     * @param iTempProcessId 
        ***********************************************************************
     */
        //requestTokenFromQuorum is like requesting locked
    //
    public void requestLockFromQuorum(String sTempRequest, int iTempProcessId) {
        System.out.println("Talker Thread  : Requesting Lock from my quorum members ...");

        if (iTempProcessId == iProcessId) {
            String[] sNR = sTempRequest.split(",");
            String sNR0 = Integer.toString(iProcessId);
            String sNR1 = sNR[1];//Sequence#
            String sNR2 = sNR[0];//original Requestor
            String sNR3 = sNR[3];//Message Type

            //for MA sNR0 == sNR2
            String sNewRequest = sNR2 + "," + sNR1 + "," + sNR0 + "," + sNR3;

            try {   //String sCurrentProcess = "net"+ iProcessId +".utdallas.edu";
                //Socket socTalker = new Socket(sCurrentProcess, 1234);
                //PrintWriter pwTalker = new PrintWriter(socTalker.getOutputStream(), true);
                //pwTalker.print(sNewRequest);
                //thDatabase.updateDatabaseCriticalSectionRequestSent(iSeqNo, conTalkerThread, iAlgo, iRun, iProcessId);
                //pwTalker.close();
                //socTalker.close();
                String sQuorumMember = null;
                //broadcast to all members (doens't have to send it to itself
                for (int iQIndex = 0; iQIndex < aQuorum.length; iQIndex++) {

                    if (aQuorum[iQIndex].iQProcessId < 10) {

                        sQuorumMember = "net0" + aQuorum[iQIndex].iQProcessId + ".utdallas.edu";

                    } else {

                        sQuorumMember = "net" + aQuorum[iQIndex].iQProcessId + ".utdallas.edu";

                    }
                    System.out.println("Talker Thread: requestLockFromQuorum for Quorum Before Creating Socket object ");
                    Socket socTalker = new Socket(sQuorumMember, 1234);
                    System.out.println("Talker Thread: requestLockFromQuorum for Quorum After Creating Socket object ");
                    PrintWriter pwTalker = new PrintWriter(socTalker.getOutputStream(), true);
                    pwTalker.print(sNewRequest);
                    thDatabase.updateDatabaseForRequestLock(iSeqNo, conTalkerThread, iAlgo, iRun, aQuorum[iQIndex].iQProcessId, Integer.parseInt(sNR0), Integer.parseInt(sNR2));

                    pwTalker.close();
                    socTalker.close();

                }//End of for loop for iQIndex

            }//End of try block
            catch (Exception e) {

                System.err.println("Issue sending socket messages while requesting Lock in requestLockedFromQuorum() method of TalkerThread class ....");
                System.exit(0);

            }//End of catch block

        }//End of if(iTempProcessId == iProcessId)	

    }//End of requestLockedFromQuorum()

    /**
     * *********************************************************************
     *
     * @param iTempForwardProcess 
        ***********************************************************************
     */
    //this is called from Listener
    public void sendLocked(int iTempForwardProcess) {
        System.out.println("Talker Thread  : Sending Lock to other process ...");
        String sForwardProcess = null;
        String sLock = iProcessId + "," + iSeqNo + "," + iProcessId + ",1";

        try {
            if (iTempForwardProcess < 10) 
            {
                sForwardProcess = "net0" + iTempForwardProcess + ".utdallas.edu";
            } else 
            {
                sForwardProcess = "net" + iTempForwardProcess + ".utdallas.edu";
            }

            
            System.out.println("Talker Thread: sendLocked for Quorum Before Creating Socket object ");
            Socket socTalker = new Socket(sForwardProcess, 1234);
            System.out.println("Talker Thread: sendLocked for Quorum After Creating Socket object ");
            PrintWriter pwTalker = new PrintWriter(socTalker.getOutputStream(), true);
            pwTalker.print(sLock);
            thDatabase.updateDatabaseForSendLock(iSeqNo, conTalkerThread, iAlgo, iRun, iProcessId, iTempForwardProcess);

            pwTalker.close();
            socTalker.close();

        }//End of try block
        catch (Exception e) {
            System.err.println("Issue sending socket messages while sending lock sendLocked() method of TalkerThread class ....");
            System.exit(0);

        }//End of catch block

    }//End of sendLocked() method

}//End of class TalkerThread_MA
