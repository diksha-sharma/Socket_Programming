import java.sql.*;
import java.util.*;
import java.io.*;
import java.net.*;

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

public class DatabaseThread_MA extends Thread
{

	String sName = null;
	String sUserName = "dxs134530";//Database connection username
	String sPassword = "aostokens";//Database connection password
	PriorityQueue<Integer> pqPQueue;
	int iRun = -1;
	int iAlgo = -1;
	int iProcessId = -1;
	int[][] aiRequestOrder;
       // Quorum[] aQuorum; // processID, IpAddress
        
	/*********************************************************************
         * 
         * @param sTempName: Name of the Current thread
         * @param iTempRun : Run number to be used for current execution
         * @param iTempAlgo : Algorithm number of the current execution
         * @param iTempProcessId : ProcessId of the current process id
         */
        DatabaseThread_MA(String sTempName, int iTempRun, int iTempAlgo, int iTempProcessId)
	{
		
		sName = sTempName;
		iRun = iTempRun;
		iAlgo = iTempAlgo;
		iProcessId = iTempProcessId;
		//aQuorum = new Quorum[iTempQuorum.length];
		//aQuorum = iTempQuorum; //check how to copy array   ** this is taken care in TalkerThread and ListenerThread to get qurourm 
                this.start();
	}//End of constructor DatabaseThread_MA()

        /*********************************************
         * 
         * Executed upon creating DatabaseThread to make Connection to database 
         * @exception SQLException while connecting to Database
         * @exception ClassNotFound to check for Oracle Driver
         * @exception  All other exception that can be caused while making connection to database
         * 
         **********************************************/
        public void run()//Implements the run method of the database thread
	{
//            try 
//            {
//                //System.out.println("Establishing database connection ...");
//                Connection conTempConn = makeConnection();//Establish connection with the database
//
//                if (conTempConn != null) 
//                {
//                    System.out.println("Database Thread: Database Connection established.. ");
//                }//End of if(conTempConn != null)
//                else
//                {
//                    System.err.println("Database Thread: Could not establish database connection in DatabaseThread class ... Exiting...");
//                    System.exit(0);
//                }//End of else for if(conTempConn != null)
//
//            }//End of try block for making database connection
//            catch (SQLException e) {
//
//                System.err.println("Database Thread: SQL Exception encountered while establishing database connection in run() method in DatabaseThread class...");
//                System.exit(0);
//
//            }//End of catch block for sql exception
//            catch (ClassNotFoundException e) {
//
//                System.err.println("Database Thread: Class Not Found Exception encountered in run() method  in DatabaseThread class ...");
//                System.exit(0);
//
//            }//End of catch block for registering odbc driver
//            catch (Exception e) {
//
//                System.out.println("Database Thread: Exception in run() method encountered in DatabaseThread class ...");
//                System.exit(0);
//
//            }//End of catch (InterruptedException e) 

		
	}//End of execute() method
         
        /********************************************
         * 
         * Making Connection to Oracle Database and the access Table to write log messages
         * @return
         * @throws SQLException
         * @throws ClassNotFoundException 
         * 
         *********************************************/
        public Connection makeConnection() throws SQLException, ClassNotFoundException 
        {
            Connection conTempConnection = null;

            try {

                Class.forName("oracle.jdbc.driver.OracleDriver");

                Properties propProperties = new Properties();
                propProperties.put("user", sUserName);
                propProperties.put("password", sPassword);

                conTempConnection = DriverManager.getConnection("jdbc:oracle:thin:@csoracle.utdallas.edu:1521:student", propProperties);

                        //System.out.println("Connected to database");
            }//End of try block for registering odbc driver
            catch (ClassNotFoundException e) {

                System.err.println("Database Thread: Class Not Found Exception in makeConnection() method  in DatabaseThread class ...");
                System.exit(0);

            }//End of catch block for registering odbc driver
            catch (SQLException e) {

                System.err.println("Database Thread: SQL Exception encountered while establishing database connection in makeConnection() method  in DatabaseThread class ...");
                System.exit(0);

            }//End of catch block for sql exception

                    //System.out.println("Database connection made!! .... YAY!!...");
            return conTempConnection;

        }//End of makeConnection() method
        /**
     * **************************
     *
     * @param conTempConnection
     * @param iTempRun
     * @param iTempAlgo
     * @throws SQLException
     */
	public void checkInitiate(Connection conTempConnection, int iTempRun, int iTempAlgo) throws SQLException//Checks if the execution of the algorithm can start
	{
		
		int iTempResult = -1; 
		
		Statement sSQLStmt = null;
		String sTempSQL = "SELECT * FROM log WHERE MESSAGE = 'Initiate' AND run = "+ iRun + " AND algorithm = " + iAlgo;
				
		//System.out.println("iRun: " + iRun);
		//System.out.println("iAlgo: " + iAlgo);
		
	    try //Check if initiate record exists in the database
	    {
	    	    	
	        sSQLStmt = conTempConnection.createStatement();
	        ResultSet rsResultSet = sSQLStmt.executeQuery(sTempSQL);
	        
	        //System.out.println("Found Initiate record for current run .... !");
	        
	        if (!rsResultSet.next())//Outer if condition
	        {
	        	
	        	rsResultSet = sSQLStmt.executeQuery(sTempSQL);
	        		
        		while(!rsResultSet.next())
        		{
        			
        			rsResultSet.close();
        			rsResultSet = sSQLStmt.executeQuery(sTempSQL);
        			System.out.println("Did not found Initiate record for current run so far .... !");
        			
        			//Thread.sleep(6000000);
        		
        			if(rsResultSet.next())
        			{
        				
        				return;
        				
        			}
        			
        		}

	        	
	        }    
	       
	        
	        
	    }//End of try block for executing sql query
	    catch (SQLException e) 
	    {
	    
	    	System.err.println("Database Thread: Issue executing Sql initiate statement");
	    	System.exit(0);
	    
	    }//End of catch block to catch SQLException
	    catch (Exception e) 
	    {
	    
	    	System.err.println("Database Thread: Genarl Issue");
	    	System.exit(0);
	    
	    }//End of catch block to catch SQLException
	    
	    //return iTempResult;
	    
		
	}//End of checkInitiate() method  

         /*******************************************
          * 
         * To check for Termination statement in Database to stop the threads in Listener and Talker class
         * this method is called from ListenerThread to check if all the requests are completed and terminate the program
         * MessageType = null
         * @param conTempConnection
         * @return
         * @throws SQLException 
         */
         	public int checkTerminate(Connection conTempConnection) throws SQLException//Checks if the execution of the algorithm can end
	{

		System.out.println("Database Thread: Checking if all requests have been processed ...");
		
		//System.out.println("Entering for loop:   " + (aiRequestOrder.length-1));
		
		//Find the processid that will make the request last in the given request order
		int[] iTemp = aiRequestOrder[(aiRequestOrder.length-1)];
		int iTempProcessId = iTemp[(iTemp.length -1)];
		
		
		
		int iCount = 0;
		
		//Now check if it occurs more than once in the request order array
		for(int iOutIndex = 0; iOutIndex <= (aiRequestOrder.length-1); iOutIndex++)
		{
			
			//System.out.println("iOutIndex:  " + iOutIndex);
			
			int[] iTempArray = aiRequestOrder[(aiRequestOrder.length-1)];
			
			for(int iInIndex = 0; iInIndex <= (iTempArray.length-1); iInIndex++)
			{
				
				//System.out.println("iInIndex:  " + iInIndex);
				
				if(iTempArray[iInIndex] == iTempProcessId)
				{
					
					iCount = iCount + 1;
					
				}//End of if(iTempArray[iInIndex] == iTempProcessId)
				
			}//End of for loop for iInIndex
				
		}//End of for loop for iOutIndex
		
		//System.out.println("Done getting count");
		
		Statement sSQLStmt = null;
		String sTempSQL = "SELECT * FROM log WHERE MESSAGE = 'Exiting Critical Section' AND sender = " + iTempProcessId +" AND run = "+ iRun + " AND algorithm = " + iAlgo;
		
	    try //Check if terminate record exists in the database
	    {
	    	
	        sSQLStmt = conTempConnection.createStatement();
	        ResultSet rsResultSet = sSQLStmt.executeQuery(sTempSQL);
	        
	        if (rsResultSet.next())
	        {
	        	
	        	/*int iTempResultSet = rsResultSet.getInt(1);
	 	        //System.out.println("iTempResultSet: " + iTempResultSet);
	 	        
	        	if (iTempResultSet == 0)
	        	{
	        		
	        		return 0;//No terminate record found yet
	        		
	        	}//End of if (iTempResultSet == 0)
	        	else if(iTempResultSet < iCount)
	        	{
	        		
	        		return 0; //There are more requests pending yet to be processed for this process
	        		
	        	}
	        	else if(iTempResultSet == iCount)
		        {*/
		        	
	 	       		System.out.println("Database Thread: Request order has been processed ... Ending Execution!");
		        	return 1;
		            
		        //}//End of else for if (iTempResultSet == 0)
	        	
	        }//End of if (rsResultSet.next())       
	        
	    }//End of try block for executing sql query
	    catch (SQLException e) 
	    {
	    
	    	System.err.println("Database Thread: Issue executing Sql terminate statement ...");
	    	System.exit(0);
	    
	    }//End of catch block to catch SQLException
		    
		    
	    return 0;
		
	}//End of checkTerminate() method

        /*****************************************************************
         * 
         * To avoid deadlock or many inquire messages due to network delay 
         * and same sequence ID for a request. 
         * This method is called from ListenerThread whenever we receive a request() to enter CS
         * it will acquire and return the highest sequence ID that we have Logged in the Database
         * by this process .
         * MessageType = null
         * @param iTempProcessId
         * @param conTempConnection
         * @return iTempSeqNo highest sequence ID used by the process so far
         * 
         ******************************************************************/
        public int getUpdatedSeqNo(int iTempProcessId, Connection conTempConnection)
	{
		
		System.out.println("Database Thread: Fetching updated sequence number from the database ...");
		
		int iTempSeqNo = 0;
		
		Statement sSQLStmt = null;
		String sTempSQL = "SELECT MAX(sequence_no) FROM log WHERE sender = " + iTempProcessId +" AND run = "+ iRun + " AND algorithm = " + iAlgo + "GROUP BY sender";
		
	    try //Check if terminate record exists in the database
	    {
	    	
	        sSQLStmt = conTempConnection.createStatement();
	        ResultSet rsResultSet = sSQLStmt.executeQuery(sTempSQL);
	        
	        if (rsResultSet.next())
	        {
	        	
	        	int iTempResultSet = rsResultSet.getInt(1);
	 	        System.out.println("Database Thread: Max sequence no returned: " + iTempResultSet);
	 	        
	        	if (iTempResultSet > 0)
	        	{
	        		
	        		iTempSeqNo = iTempResultSet;
	        		
	        	}//End of if (iTempResultSet > 0)
	        	
	        }//End of if (rsResultSet.next()) 
	        else
	        {
	        	
	        	iTempSeqNo = 0;
	        	System.out.println("Database Thread: Max sequence no returned: " + iTempSeqNo);
	        	
	        }//End of else
	        
	    }//End of try block for executing sql query
	    catch (SQLException e) 
	    {
	    
	    	System.err.println("Database Thread: Issue executing Sql terminate statement ...");
	    	System.exit(0);
	    
	    }//End of catch block to catch SQLException		
		
		return iTempSeqNo;
		
	}//End of getUpdatedSeqNo() method
         
        /******************************************************************
         * 
         * Check the previous request in  database base and reutrn the processID.
         * This will help a process to send its CS request in the order given in test case.
         * return iTempResult is the of last element in the request queue that I have processed its 
         * requestCriticalSection.
         * MessageType = 4
         * @param iTempPreviousProcessId
         * @param conTempConnection
         * @return iTempResult
         * 
         ******************************************************************/
        public int checkPreviousRequest(int iTempPreviousProcessId, Connection conTempConnection)
	{

            //System.out.println("Database Thread: Checking is previous process has already requested critical section in database ...");
            int iTempResult = -1;

            Statement sSQLStmt = null;
            String sTempSQL = "SELECT * FROM log WHERE sender = " + iTempPreviousProcessId + " AND run = " + iRun + " AND algorithm = " + iAlgo + "AND message_type = 4 ";

            try //Check if record exists in the database
            {
                sSQLStmt = conTempConnection.createStatement();
                ResultSet rsResultSet = sSQLStmt.executeQuery(sTempSQL);

                if (rsResultSet.next()) 
                {
                    int iTempResultSet = rsResultSet.getInt(1);
                    
                    if (iTempResultSet > 0) 
                    {
                        iTempResult = 1;
                    }//End of if (iTempResultSet > 0)
                }//End of if (rsResultSet.next()) 
                else 
                {
                    iTempResult = 0;
                }//End of else

            }//End of try block for executing sql query
            catch (SQLException e) 
            {
                System.err.println("Database Thread: Issue executing Sql statement while checking for previous process request ...");
                System.exit(0);
            }//End of catch block to catch SQLException		
            return iTempResult;

        }//End of checkPreviousRequest() method
       
	//done same updateDatbaseForRequestLock
        /********************************************************************
         * 
         * Update/Log message in Database after sending CriticalSectionRequst. 
         * This method is update that you have added the request to your own queue
         * MessageType = 0
         * @param iTempSeqNo
         * @param conTalkerThread
         * @param iTempAlgo
         * @param iTempRun
         * @param iTempProcessId 
         * 
         *********************************************************************/
        public void updateDatabaseCriticalSectionRequestSent(int iTempSeqNo, Connection conTalkerThread, int iTempAlgo, int iTempRun, int iTempProcessId)
	{		
		System.out.println("Database Thread: Updating database that I sent the request for critical section to myself...");
		Statement sSQLStmt = null;
		String sTempSQL = "INSERT INTO log VALUES (" + iTempAlgo + ", " + iTempRun + ", message_no.nextval, 'Requesting Critical Section', " + iTempProcessId + ", " + iTempProcessId + ", sysdate, " + 0 + ", " + iTempSeqNo + ", " + iTempProcessId + ")";
		 
	    try//Outer try block to execute Insert statement
	    {	    	
	        sSQLStmt = conTalkerThread.createStatement();
	        sSQLStmt.executeUpdate(sTempSQL);	                
	    }//End of Outer try block to execute Insert statement
	    catch (SQLException e) 
	    {	    
	    	System.err.println("Database Thread: Issue executing Insert statement for requesting Critical section in updateDatabaseCriticalSectionRequestSent() method of TalkerThread class ....");
	    	System.exit(0);	    
	    }//End of catch block to catch SQLException	
	}//End of updateDatabaseCriticalSectionRequestSent() method

        //send broadcast request for CS
        //done
        /********************************************************************
         * 
         * Update/Log message in Database after sending CriticalSectionRequst to all the Quorum Members
         * MessageType = 0
         * @param iTempSeqNo
         * @param conTalkerThread
         * @param iTempAlgo
         * @param iTempRun
         * @param iTempQuorumMemberId
         * @param iTempProcessId 
         *
         *********************************************************************/
        
        public void updateDatabaseForQuorumMember(int iTempSeqNo, Connection conTalkerThread, int iTempAlgo, int iTempRun, int iTempQuorumMemberId, int iTempProcessId)
	{		
            System.out.println("Database Thread: Updating database that I sent the request for critical section to my quorum  members ...");
            Statement sSQLStmt = null;
            String sTempSQL = "INSERT INTO log VALUES (" + iTempAlgo + ", " + iTempRun + ", message_no.nextval, 'Requesting Critical Section', " + iTempProcessId + ", " + iTempQuorumMemberId + ", sysdate, " + 0 + ", "+ iTempSeqNo + ", " + iTempProcessId + ")";
		 
	    try//Outer try block to execute Insert statement
	    {	    	
	        sSQLStmt = conTalkerThread.createStatement();
	        sSQLStmt.executeUpdate(sTempSQL);           
	    }//End of Outer try block to execute Insert statement
	    catch (SQLException e) 
	    {
	    	System.err.println("Database Thread: Issue executing Insert statement for requesting Critical section in updateDatabaseCriticalSectionRequestSent() method of TalkerThread class ....");
	    	System.exit(0);	    
	    }//End of catch block to catch SQLException		
	}//End of updateDatabaseCriticalSectionRequestSent() method
       
        /********************************************************
         * 
         * Update/Log message in Database after sending Release ()
         * which indicates that the process has deleted it's request
         * from requesting queue once done using critical section.
         * MessageType = 5
         * @param iTempSeqNo
         * @param conTalkerThread
         * @param iTempAlgo
         * @param iTempRun
         * @param iTempProcessId 
         * 
         *********************************************************/
        public void updateDatabaseForRelease(int iTempSeqNo, Connection conTalkerThread, int iTempAlgo, int iTempRun, int iTempProcessId)
	{
		System.out.println("Database Thread: Updating database that I sent delete request for my queue...");
		
		Statement sSQLStmt = null;
		String sTempSQL = "INSERT INTO log VALUES (" + iTempAlgo + ", " + iTempRun + "message_no.nextval, 'Deleting request for own queue', " + iTempProcessId + ", " + iTempProcessId + ", sysdate, " + 5 + ", "+ iTempSeqNo + ", " + iTempProcessId + ")";
		 
	    try//Outer try block to execute Insert statement
	    {
	    	
	        sSQLStmt = conTalkerThread.createStatement();
	        sSQLStmt.executeUpdate(sTempSQL);
	                
	    }//End of Outer try block to execute Insert statement
	    catch (SQLException e) 
	    {
	    
	    	System.err.println("Issue executing Insert statement for deleting request from its own queue in updateDatabaseForDelete() method of TalkerThread class ....");
	    	System.exit(0);
	    
	    }//End of catch block to catch SQLException
	}//End of updateDatabaseForDelete() method
        //Done to all the quorum member queue
        /***************************************************************************
         * 
         * Update/Log message in Database after sending Release ()to all the Quorum Members
         * once done using critical section.
         * MessageType = 5
         * @param iTempSeqNo
         * @param conTalkerThread
         * @param iTempAlgo
         * @param iTempRun
         * @param iTempQuorumMemberId
         * @param iTempProcessId 
         *
         *********************************************************************/        
	public void updateDatabaseForReleaseQuorumMember(int iTempSeqNo, Connection conTalkerThread, int iTempAlgo, int iTempRun, int iTempQuorumMemberId, int iTempProcessId)
	{
            System.out.println("Database Thread: Updating database that I sent the delete request to quorum members ...");
		
            Statement sSQLStmt = null;
            String sTempSQL = "INSERT INTO log VALUES (" + iTempAlgo + ", " + iTempRun + ", message_no.nextval, 'Delete Request', " + iTempProcessId + ", " + iTempQuorumMemberId + ", sysdate, " + 5 + ", "+ iTempSeqNo + ", " + iTempProcessId + ")";
		 
	    try//Outer try block to execute Insert statement
	    {	    	
	        sSQLStmt = conTalkerThread.createStatement();
	        sSQLStmt.executeUpdate(sTempSQL);
	                
	    }//End of Outer try block to execute Insert statement
	    catch (SQLException e) 
	    {	    
	    	System.err.println("Database Thread: Issue executing Insert statement for deleting request in updateDatabaseForDeleteQuorumMember() method of TalkerThread class ....");
	    	System.exit(0);	    
	    }//End of catch block to catch SQLException		
	}//End of updateDatabaseForDeleteQuorumMember() method
	
        //kind of same as Requesting CS 
        //same update for RequestToken
        /********************************************************************
         *
         * Update/Log message in Database after sending Request asking Lock to all the Quorum Members
         * MessageType = 2
         * @param iTempSeqNo
         * @param conTalkerThread
         * @param iTempAlgo
         * @param iTempRun
         * @param iTempQuorumMemberId
         * @param iTempProcessId
         * @param iTempOriginalR 
         * 
         *********************************************************************/
	public void updateDatabaseForRequestLock(int iTempSeqNo, Connection conTalkerThread, int iTempAlgo, int iTempRun, int iTempQuorumMemberId, int iTempProcessId, int iTempOriginalR)
	{
		System.out.println("Database Thread: Updating database that I sent the request for lock to my quorum members ...");
			
		Statement sSQLStmt = null;
		String sTempSQL = "INSERT INTO log VALUES (" + iTempAlgo + ", " + iTempRun + "message_no.nextval, 'Requesting Token', " + iTempProcessId + ", " + iTempQuorumMemberId + ", sysdate, " + 2 + ", " + iTempSeqNo + ", " + iTempOriginalR + ")";
		 
	    try//Outer try block to execute Insert statement
	    {	    	
	        sSQLStmt = conTalkerThread.createStatement();
	        sSQLStmt.executeUpdate(sTempSQL);
	                
	    }//End of Outer try block to execute Insert statement
	    catch (SQLException e) 
	    {
	    
	    	System.err.println("Database Thread: Issue executing Insert statement for requesting Critical section in updateDatabaseForRequestToken() method of TalkerThread class ....");
	    	System.exit(0);
	    
	    }//End of catch block to catch SQLException
		
		
	}//End of updateDatabaseForRequestLock() method
	
        //ForSendToken

         /**********************************************************
         * 
         * Update/Log message in Database after sending lock to the request at 
         * the top of request queue
         * MessageType = 1
         * @param iTempSeqNo
         * @param conTalkerThread
         * @param iTempAlgo
         * @param iTempRun
         * @param iTempProcessId
         * @param iTempForwardProcess 
         * 
         ***********************************************************/
	public void updateDatabaseForSendLock(int iTempSeqNo, Connection conTalkerThread, int iTempAlgo, int iTempRun, int iTempProcessId, int iTempForwardProcess)
	{
		
		System.out.println("Database Thread: Updating database that I sent the Lock to other process ...");
		Statement sSQLStmt = null;
		String sTempSQL = "INSERT INTO log VALUES (" + iTempAlgo + ", " + iTempRun + "message_no.nextval, 'Sending Token', " + iTempProcessId + ", " + iTempForwardProcess + ", sysdate, " + 1 + ", "+ iTempSeqNo + ", null)";
		 
	    try//Outer try block to execute Insert statement
	    {
	    	
	        sSQLStmt = conTalkerThread.createStatement();
	        sSQLStmt.executeUpdate(sTempSQL);
	                
	    }//End of Outer try block to execute Insert statement
	    catch (SQLException e) 
	    {
	    
	    	System.err.println("Database Thread: Issue executing Insert statement for requesting Critical section in updateDatabaseForSendToken() method of TalkerThread class ....");
	    	System.exit(0);
	    
	    }//End of catch block to catch SQLException	
		
	}//End of updateDatabaseCriticalSectionRequestSent() method

        /************************************************************
         * 
         * Update/Log message in Database after sending Inquire message to the process
         * that it is correctly locked for.
         * MessageType = 3
         * @param iTempSeqNo
         * @param conTalkerThread
         * @param iTempAlgo
         * @param iTempRun
         * @param iTempProcessId
         * @param iTempForwardProcess 
         * 
         *********************************************************************/        
	public void updateDatabaseForSendInquire(int iTempSeqNo, Connection conTalkerThread, int iTempAlgo, int iTempRun, int iTempProcessId, int iTempForwardProcess)
	{		
		Statement sSQLStmt = null;
		String sTempSQL = "INSERT INTO log VALUES (" + iTempAlgo + ", " + iTempRun + "message_no.nextval, 'Sending Token', " + iTempProcessId + ", " + iTempForwardProcess + ", sysdate, " + 3 + ", "+ iTempSeqNo + ", null)";
		 
	    try//Outer try block to execute Insert statement
	    {
	    	
	        sSQLStmt = conTalkerThread.createStatement();
	        sSQLStmt.executeUpdate(sTempSQL);
	                
	    }//End of Outer try block to execute Insert statement
	    catch (SQLException e) 
	    {
	    
	    	System.err.println("Issue executing Insert statement for sending INQUIRE in updateDatabaseForSendInquire() method of TalkerThread class ....");
	    	System.exit(0);
	    
	    }//End of catch block to catch SQLException
		
		
	}//End of updateDatabaseForSendInquire() method

        /*************************************************************
         * 
         * Update/Log message in Database after sending Relinquish leaving the lock it received
         * MessageType = 4
         * @param iTempSeqNo
         * @param conTalkerThread
         * @param iTempAlgo
         * @param iTempRun
         * @param iTempProcessId
         * @param iTempForwardProcess 
         * 
         ***************************************************************/
        public void updateDatabaseForSendRelinquish(int iTempSeqNo, Connection conTalkerThread, int iTempAlgo, int iTempRun, int iTempProcessId, int iTempForwardProcess)
	{		
		Statement sSQLStmt = null;
		String sTempSQL = "INSERT INTO log VALUES (" + iTempAlgo + ", " + iTempRun + "message_no.nextval, 'Sending Token', " + iTempProcessId + ", " + iTempForwardProcess + ", sysdate, " + 4 + ", "+ iTempSeqNo + ", null)";
	    try//Outer try block to execute Insert statement
	    {
	    	
	        sSQLStmt = conTalkerThread.createStatement();
	        sSQLStmt.executeUpdate(sTempSQL);
	                
	    }//End of Outer try block to execute Insert statement
	    catch (SQLException e) 
	    {
	    
	    	System.err.println("Issue executing Insert statement for sending Relinquish in updateDatabaseForSendRelinquish() method of TalkerThread class ....");
	    	System.exit(0);
	    
	    }//End of catch block to catch SQLException
		
		
	}//End of updateDatabaseForSendRelinquish() method

        /***************************************************
         * 
         * Update/Log message in Database after sending Failed message 
         * to a quorum member requested the lock
         * MessageType = 6
         * @param iTempSeqNo
         * @param conTalkerThread
         * @param iTempAlgo
         * @param iTempRun
         * @param iTempProcessId
         * @param iTempForwardProcess 
         *
         ****************************************************/
        public void updateDatabaseForSendFailed(int iTempSeqNo, Connection conTalkerThread, int iTempAlgo, int iTempRun, int iTempProcessId, int iTempForwardProcess)
	{
		
		Statement sSQLStmt = null;
		String sTempSQL = "INSERT INTO log VALUES (" + iTempAlgo + ", " + iTempRun + "message_no.nextval, 'Sending Token', " + iTempProcessId + ", " + iTempForwardProcess + ", sysdate, " + 6 + ", " + iTempSeqNo + ", null)";
		 
	    try//Outer try block to execute Insert statement
	    {	    	
	        sSQLStmt = conTalkerThread.createStatement();
	        sSQLStmt.executeUpdate(sTempSQL);	                
	    }//End of Outer try block to execute Insert statement
	    catch (SQLException e) 
	    {	    
	    	System.err.println("Issue executing Insert statement for sending FAILED messge in updateDatabaseForSendFailed() method of TalkerThread class ....");
	    	System.exit(0);
	    
	    }//End of catch block to catch SQLException	
	}//End of updateDatabaseForSendFailed() method
        
        //From ListenerThread
        /**********************************************************
         * 
         * Update/Log message in Database after Receive Request to access 
         * Critical Section from a quorum member requesting Critical Section
         * MessageType = 0
         * @param iTempOriginalSender
         * @param iTempSeqNo
         * @param iTempSender
         * @param iTempMsgType
         * @param conTempConnection 
         ***********************************************************/
        public void updateDatabaseRecieveRequest(int iTempOriginalSender, int iTempSeqNo, int iTempSender, int iTempMsgType, Connection conTempConnection)
	{
		
		Connection conConnection = conTempConnection;
		Statement sSQLStmt = null;
		String sTempSQL = "INSERT INTO log VALUES (" + iAlgo + ", " + iRun + "message_no.nextval, 'Received message', " + iTempSender + ", " + iProcessId + ", sysdate, " + iTempMsgType + ", " + iTempSeqNo + ", " + iTempOriginalSender + ")";
		 
	    try//Outer try block to execute Insert statement
	    {
	    	
	        sSQLStmt = conTempConnection.createStatement();
	        sSQLStmt.executeUpdate(sTempSQL);
	                
	    }//End of Outer try block to execute Insert statement
	    catch (SQLException e) 
	    {
	    
	    	System.err.println("Issue executing Insert statement for request received in updateDatabase() method of ListenerThread class ....");
	    	System.exit(0);
	    
	    }//End of catch block to catch SQLException
		
		
	}//End of updateDatabase() method
        
        //From ListenerThread
        
        /*********************************************
         * 
         * Update/Log message in Database once a process executes Critical 
         * Section
         * MessageType = 7
         * @param iTempProcessId
         * @param iTempSeqNo
         * @param conTempConnection 
         *
         *********************************************/
        public void updateExecutingCS(int iTempProcessId, int iTempSeqNo, Connection conTempConnection)
	{
		
		Statement sSQLStmt = null;
		String sTempSQL = "INSERT INTO log VALUES (" + iAlgo + ", " + iRun + "message_no.nextval, 'Entering Critical Section', " + iTempProcessId + ", " + iTempProcessId + ", sysdate, " + 7 + ", " + iTempSeqNo + ", " + iTempProcessId + ")";
		 
	    try//Outer try block to execute Insert statement
	    {
	    	
	        sSQLStmt = conTempConnection.createStatement();
	        sSQLStmt.executeUpdate(sTempSQL);
	                
	    }//End of Outer try block to execute Insert statement
	    catch (SQLException e) 
	    {
	    
	    	System.err.println("Issue executing Insert statement for request received in updateExecutingCS() method of ListenerThread class ....");
	    	System.exit(0);
	    
	    }//End of catch block to catch SQLException

		
	}//End of updateExecutingCS() method

        //From ListenerThread
        /***************************************************************
         * 
         * Update/Log message in Database after exiting Critical Section
         * MessageType = 8
         * @param iTempProcessId
         * @param iTempSeqNo
         * @param conTempConnection 
         * 
         ****************************************************************/
        public void exitingExecutingCS(int iTempProcessId, int iTempSeqNo, Connection conTempConnection)
	{		
		Statement sSQLStmt = null;
		String sTempSQL = "INSERT INTO log VALUES (" + iAlgo + ", " + iRun + "message_no.nextval, 'Exiting Critical Section', " + iTempProcessId + ", " + iTempProcessId + ", sysdate, " + 8 + ", " + iTempSeqNo + ", " + iTempProcessId + ")";
		 
	    try//Outer try block to execute Insert statement
	    {
	    	
	        sSQLStmt = conTempConnection.createStatement();
	        sSQLStmt.executeUpdate(sTempSQL);
	                
	    }//End of Outer try block to execute Insert statement
	    catch (SQLException e) 
	    {	    
	    	System.err.println("Issue executing Insert statement for request received in updateExecutingCS() method of ListenerThread class ....");
	    	System.exit(0);	    
	    }//End of catch block to catch SQLException		
	}//End of updateExecutingCS() method
	
}//End of DatabaseThread_MA class
