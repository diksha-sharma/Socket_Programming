import java.sql.*;
import java.util.*;

//Input parameters:
///people/cs/d/dxs134530/AOS/AOSTokens.txt 1 2
public class DatabaseThread extends Thread
{

	String sName = null; //Current thread's name
	String sUserName = "dxs134530";//Database connection username
	String sPassword = "aostokens";//Database connection password
	int iRun = -1; //Run number to be used for current execution
	int iAlgo = -1; //Algorithm to be executed
	int iProcessId = -1; //Current process's process id
	int[][] aiRequestOrder; //Request order for the current execution
	Connection conTempConn = null;

	//************************************************************************************************************************************************************
	//************************************************************************************************************************************************************
	DatabaseThread(String sTempName, int iTempRun, int iTempAlgo, int iTempProcessId, int[][] iTempRequestOrder)
	{
		
		sName = sTempName;
		iRun = iTempRun;
		iAlgo = iTempAlgo;
		iProcessId = iTempProcessId;
		aiRequestOrder = iTempRequestOrder;
		
		System.out.println("Database Thread: Database thread initialized ...");
		this.start();
		
	}//End of constructor DatabaseThread()

	
	//************************************************************************************************************************************************************
	//************************************************************************************************************************************************************
	public void run()//Implements the run method of the database thread
	{
		
		try
		{
			
			System.out.println("Database Thread: Establishing database connection ...");
			
			conTempConn = makeConnection();//Establish connection with the database
			
			if(conTempConn != null)
			{
				
				System.out.println("Database Thread: Database Connection established.. ");
				
			}//End of if(conTempConn != null)
			else
			{
				
				System.err.println("Database Thread: Could not establish database connection in DatabaseThread class ... Exiting...");
				System.exit(0);
				
			}//End of else for if(conTempConn != null)
			
			
		}//End of try block for making database connection
		catch (SQLException e) 
		{
 
			System.err.println("Database Thread: SQL Exception encountered while establishing database connection in run() method in DatabaseThread class...");
			System.exit(0);
 
		}//End of catch block for sql exception
		catch (ClassNotFoundException e) 
		{
 
			System.err.println("Database Thread: Class Not Found Exception encountered in run() method  in DatabaseThread class ...");
			System.exit(0);
 
		}//End of catch block for registering odbc driver
		 catch (Exception e) 
		{
	    	
	         System.out.println("Database Thread: Exception in run() method encountered in DatabaseThread class ...");
	         System.exit(0);
	         
		}//End of catch (InterruptedException e) 
		
		
	}//End of execute() method
	
	
	//************************************************************************************************************************************************************
	//************************************************************************************************************************************************************	
	public Connection makeConnection() throws SQLException, ClassNotFoundException
	{
		
		Connection conTempConnection = null;
		
		try 
		{
			 
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
		    Properties propProperties = new Properties();
		    propProperties.put("user", sUserName);
		    propProperties.put("password", sPassword);
		    
		    conTempConnection = DriverManager.getConnection("jdbc:oracle:thin:@csoracle.utdallas.edu:1521:student", propProperties);

		}//End of try block for registering odbc driver
		catch (ClassNotFoundException e) 
		{
 
			System.err.println("Database Thread: Class Not Found Exception in makeConnection() method  in DatabaseThread class ...");
			System.exit(0);
 
		}//End of catch block for registering odbc driver
		catch (SQLException e) 
		{
 
			System.err.println("Database Thread: SQL Exception encountered while establishing database connection in makeConnection() method  in DatabaseThread class ...");
			System.exit(0);
 
		}//End of catch block for sql exception
		
		return conTempConnection;

	}//End of makeConnection() method
	

	
	//************************************************************************************************************************************************************
	//************************************************************************************************************************************************************
	public int checkTerminate(Connection conTempConnection) throws SQLException//Checks if the execution of the algorithm can end
	{

		System.out.println("Database Thread: Checking if all requests have been processed ...");
		
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
				
				if(iTempArray[iInIndex] == iTempProcessId)
				{
					
					iCount = iCount + 1;
					
				}//End of if(iTempArray[iInIndex] == iTempProcessId)
				
			}//End of for loop for iInIndex
				
		}//End of for loop for iOutIndex
		
		Statement sSQLStmt = null;
		String sTempSQL = "SELECT * FROM log WHERE MESSAGE = 'Exiting Critical Section' AND sender = " + iTempProcessId +" AND run = "+ iRun + " AND algorithm = " + iAlgo;
		
	    try //Check if terminate record exists in the database
	    {
	    	
	        sSQLStmt = conTempConnection.createStatement();
	        ResultSet rsResultSet = sSQLStmt.executeQuery(sTempSQL);
	        
	        if (rsResultSet.next())
	        {
	        	
	        	System.out.println("Database Thread: Request order has been processed ... Ending Execution!");
	        	return 1;

	        	
	        }//End of if (rsResultSet.next())       
	        
	    }//End of try block for executing sql query
	    catch (SQLException e) 
	    {
	    
	    	System.err.println("Database Thread: Issue executing Sql terminate statement ...");
	    	System.exit(0);
	    
	    }//End of catch block to catch SQLException
		    
		    
	    return 0;
		
	}//End of checkTerminate() method


	//************************************************************************************************************************************************************
	//************************************************************************************************************************************************************
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

	//************************************************************************************************************************************************************
	//************************************************************************************************************************************************************
	public int checkPreviousRequest(int iTempPreviousProcessId, Connection conTempConnection)
	{
		
		int iTempResult = -1;
		
		Statement sSQLStmt = null;
		String sTempSQL = "SELECT * FROM log WHERE sender = " + iTempPreviousProcessId +" AND run = "+ iRun + " AND algorithm = " + iAlgo + "AND message_type = 4 ";
		
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
	
	//************************************************************************************************************************************************************
	//************************************************************************************************************************************************************	
	public void updateDatabaseCriticalSectionRequestSent(int iTempSeqNo, Connection conTalkerThread, int iTempAlgo, int iTempRun, int iTempProcessId)
	{
		
		System.out.println("Database Thread: Updating database that I sent the request for critical section to myself...");
		
		Statement sSQLStmt = null;
		String sTempSQL = "INSERT INTO log VALUES (" + iTempAlgo + ", " + iTempRun + ", message_no.nextval, 'Requesting Critical Section', " + iTempProcessId + ", " + iTempProcessId + ", sysdate, 4 , " + iTempSeqNo + ", " + iTempProcessId + ")";
		 
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

	//************************************************************************************************************************************************************
	//************************************************************************************************************************************************************
	public void updateDatabaseForQuorumMember(int iTempSeqNo, Connection conTalkerThread, int iTempAlgo, int iTempRun, int iTempQuorumMemberId, int iTempProcessId)
	{
		
		System.out.println("Database Thread: Updating database that I sent the request for critical section to my quorum  members ...");
		
		Statement sSQLStmt = null;
		String sTempSQL = "INSERT INTO log VALUES (" + iTempAlgo + ", " + iTempRun + ", message_no.nextval, 'Requesting Critical Section', " + iTempProcessId + ", " + iTempQuorumMemberId + ", sysdate, 4 , " + iTempSeqNo + ", " + iTempProcessId + ")";
		 
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
	
	
	//************************************************************************************************************************************************************
	//************************************************************************************************************************************************************
	public void checkInitiate(Connection conTempConnection, int iTempRun, int iTempAlgo) throws SQLException//Checks if the execution of the algorithm can start
	{
		
		int iTempResult = -1; 
		
		Statement sSQLStmt = null;
		String sTempSQL = "SELECT * FROM log WHERE MESSAGE = 'Initiate' AND run = "+ iRun + " AND algorithm = " + iAlgo;
	
	    try //Check if initiate record exists in the database
	    {
	    	    	
	        sSQLStmt = conTempConnection.createStatement();
	        ResultSet rsResultSet = sSQLStmt.executeQuery(sTempSQL);
   
	        if (!rsResultSet.next())//Outer if condition
	        {
	        	
	        	rsResultSet = sSQLStmt.executeQuery(sTempSQL);
	        		
        		while(!rsResultSet.next())
        		{
        			
        			rsResultSet.close();
        			rsResultSet = sSQLStmt.executeQuery(sTempSQL);
        			System.out.println("Did not found Initiate record for current run so far .... !");
        			
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
	    
	}//End of checkInitiate() method
	
	
	//************************************************************************************************************************************************************
	//************************************************************************************************************************************************************
	public void updateDatabaseForRequestToken(int iTempSeqNo, Connection conTalkerThread, int iTempAlgo, int iTempRun, int iTempQuorumMemberId, int iTempProcessId, int iTempOriginalR)
	{
		
		System.out.println("Database Thread: Updating database that I sent the request for token to my quorum members ...");
		
		Statement sSQLStmt = null;
		String sTempSQL = "INSERT INTO log VALUES (" + iTempAlgo + ", " + iTempRun + ", message_no.nextval, 'Requesting Token', " + iTempProcessId + ", " + iTempQuorumMemberId + ", sysdate, 0 , " + iTempSeqNo + ", " + iTempOriginalR + ")";
		 
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
		
		
	}//End of updateDatabaseCriticalSectionRequestSent() method
	
	
	//************************************************************************************************************************************************************
	//************************************************************************************************************************************************************
	public void updateDatabaseForSendToken(int iTempSeqNo, Connection conTalkerThread, int iTempAlgo, int iTempRun, int iTempProcessId, int iTempForwardProcess)
	{
		
		System.out.println("Database Thread: Updating database that I sent the token to other process ...");
		
		Statement sSQLStmt = null;
		String sTempSQL = "INSERT INTO log VALUES (" + iTempAlgo + ", " + iTempRun + ", message_no.nextval, 'Sending Token', " + iTempProcessId + ", " + iTempForwardProcess + ", sysdate, 1 , " + iTempSeqNo + ", null)";
		 
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
	
	
	//************************************************************************************************************************************************************
	//************************************************************************************************************************************************************
	public void updateDatabaseForDeleteQuorumMember(int iTempSeqNo, Connection conTalkerThread, int iTempAlgo, int iTempRun, int iTempQuorumMemberId, int iTempProcessId)
	{
		
		System.out.println("Database Thread: Updating database that I sent the delete request to quorum members ...");
		
		Statement sSQLStmt = null;
		String sTempSQL = "INSERT INTO log VALUES (" + iTempAlgo + ", " + iTempRun + ", message_no.nextval, 'Delete Request', " + iTempProcessId + ", " + iTempQuorumMemberId + ", sysdate, 3 , " + iTempSeqNo + ", " + iTempProcessId + ")";
		 
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
	

	//************************************************************************************************************************************************************
	//************************************************************************************************************************************************************
	public void updateDatabaseRecieveRequest(int iTempOriginalSender, int iTempSeqNo, int iTempSender, int iTempMsgType, Connection conTempConnection)
	{
		
		System.out.println("Database Thread: Updating database that I received a request at the socket ...");
		
		Connection conConnection = conTempConnection;
		Statement sSQLStmt = null;
		String sTempSQL = "INSERT INTO log VALUES (" + iAlgo + ", " + iRun + ", message_no.nextval, 'Received message', " + iTempSender + ", " + iProcessId + ", sysdate, " + iTempMsgType + ", " + iTempSeqNo + ", " + iTempOriginalSender + ")";
		 
	    try//Outer try block to execute Insert statement
	    {
	    	
	        sSQLStmt = conTempConnection.createStatement();
	        sSQLStmt.executeUpdate(sTempSQL);
	                
	    }//End of Outer try block to execute Insert statement
	    catch (SQLException e) 
	    {
	    
	    	System.err.println("Database Thread: Issue executing Insert statement for request received in updateDatabase() method of ListenerThread class ....");
	    	System.exit(0);
	    
	    }//End of catch block to catch SQLException
		
		
	}//End of updateDatabase() method
	
	//************************************************************************************************************************************************************
	//************************************************************************************************************************************************************	
	public void updateExecutingCS(int iTempProcessId, int iTempSeqNo, Connection conTempConnection)
	{
		
		System.out.println("Database Thread: Updating database that I am executing my critical section ...");
		
		Statement sSQLStmt = null;
		String sTempSQL = "INSERT INTO log VALUES (" + iAlgo + ", " + iRun + ", message_no.nextval, 'Entering Critical Section', " + iTempProcessId + ", " + iTempProcessId + ", sysdate, " + 2 + ", " + iTempSeqNo + ", " + iTempProcessId + ")";
		 
	    try//Outer try block to execute Insert statement
	    {
	    	
	        sSQLStmt = conTempConnection.createStatement();
	        sSQLStmt.executeUpdate(sTempSQL);
	                
	    }//End of Outer try block to execute Insert statement
	    catch (SQLException e) 
	    {
	    
	    	System.err.println("Database Thread: Issue executing Insert statement for request received in updateExecutingCS() method of ListenerThread class ....");
	    	System.exit(0);
	    
	    }//End of catch block to catch SQLException

		
	}//End of updateExecutingCS() method
	

	//************************************************************************************************************************************************************
	//************************************************************************************************************************************************************	
	public void existingExecutingCS(int iTempProcessId, int iTempSeqNo, Connection conTempConnection)
	{
		
		System.out.println("Database Thread: Updating database that I am done executing my critical section ...");
		
		Statement sSQLStmt = null;
		String sTempSQL = "INSERT INTO log VALUES (" + iAlgo + ", " + iRun + ", message_no.nextval, 'Exiting Critical Section', " + iTempProcessId + ", " + iTempProcessId + ", sysdate, " + 2 + ", " + iTempSeqNo + ", " + iTempProcessId + ")";
		 
	    try//Outer try block to execute Insert statement
	    {
	    	
	        sSQLStmt = conTempConnection.createStatement();
	        sSQLStmt.executeUpdate(sTempSQL);
	                
	    }//End of Outer try block to execute Insert statement
	    catch (SQLException e) 
	    {
	    
	    	System.err.println("Database Thread: Issue executing Insert statement for request received in updateExecutingCS() method of ListenerThread class ....");
	    	System.exit(0);
	    
	    }//End of catch block to catch SQLException

		
	}//End of updateExecutingCS() method
	
}//End of DatabaseThread class
