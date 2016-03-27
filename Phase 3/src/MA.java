
import java.sql.*;
import java.util.*;
import java.io.*;
import java.net.*;

/*
Notes:
1)conTempConnection should we have variable as paremeter for each send and receive
or just 1 global value
TCP connection (with socket 6976)
2) sendMEssage() update with all approprirate SQL
3) confirm how to get the next item in the waiting queue. either from
database or from queue right after called sortChannel(first_Pid,first_Sid)
*/
public class MA 
{    //Defining class variables

    String sUserName = "dxs134530";
    String sPassword = "matq!23";
    
    //***Intialized from AOSTokens Class
    int iOutstandingReplies = 0; //Stores how many more processes need to be locked for the current process to enter CS
    int iProcessId; //Current process's process id    
    int iAlgo =0;
    int iRun = -1;    
    //int[] alPQuorum; //Each of type Process quorum 
    Quorum[] aQuorum;
    int[][] iRequest; //row will indicate sequenceid and column will be proceeid
    int iTotalNodes =0;
 
    ListenerThread_MA thListener;

    //Message Types:
    //0 - REQUEST CS
    //1 - send Lock
    //2 - Request Lock
    //3 - INQUIRE SEND/recieve
    //4 - SEND RELINQUISH/RECIEVE RELINQUISH
    //5 - SEND RELEASE (delete) (SAME AS DELETE REQUEST OF ITS OWN FROM QUEUE)/ RECIEVE RELEASE (SAME AS DELETE REQUEST OF SPECIFIC PROCESSID, 
    //6 - Failed
    //7 - EXECUTE CS (just for database update purpose
    //Defining methods

    /***************************************************************************
     * Starts execution for the MA main class object by instantiating the Listener thread and passing it the required input data
     * @throws Exception 
    ****************************************************************************/
    public void Execute() throws Exception
    {
        thListener = new ListenerThread_MA("Listener", iRun, iAlgo, iProcessId, iRequest, aQuorum, iTotalNodes);			
    }//End of Execute() method
}//End MA Class