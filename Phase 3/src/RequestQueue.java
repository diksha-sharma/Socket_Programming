/*******************************************************************************
 * 
 * @author Sruthi
 * Request Queue to store the critical section request received from quorum members
 * with SeqeunceId , process Id of sender who is requesting Critical Section,
 * Message type to specify the action need to be performed by receiver
 */

public class RequestQueue 
{

	int iOriginalSender = -1;
	int iSeqNo = 0;
	int iSender = 0; // this will same as originalsender in Maekawa Algorithm
	int iMsgType = -1;
        
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
        
}//End of RqeuestQueue class
