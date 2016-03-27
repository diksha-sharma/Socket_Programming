public class Process_TokenizedQuorum
{

	//Input file structure (Example of 2 processes):
	//Quorums:
	//1,2
	//2,3
	//3,1

	//Defining class variables
	bookean bToken = false; //Is true when the current process possesses the token
	int iHolder; //Holds the process id of the process at the top of the channel for the current process.
	boolean bAsked = false; //Will be true when the current process has requested for the token and has not received it yet.
	static int siProcessid; //Current process's process id
	int iSequenceId = 0; //Highest sequence id current process knows
	Arraylist alPQuorum; //Each of type Process
	Arraylist alChannel; //This is the request queue
	boolean bCS = false; ; //True when the current process is executing its CS
	boolean bRequestingCS = false; ; //True when the current process is requesting CS
	boolean bLocked = false; ; //Whether the current process is blocked for a process
	int iInquireProcessId; //The process which sent the inquire message
	int iOutstandingReplies; //Stores how many more processes need to be locked for the current process to enter CS
	int iLockedForProcessId; //The process for which the current process is blocked (process id)
	int iLockedForSequenceId; //The process for which the current process is blocked (sequenceid)
	boolean bRelease = false; ; //When true send messages to quorum memebers that the current process has released CS
	boolean bFailed = false; ; //When true sends a failed message to all the process in channel queue except the first process in the queue. The queue should be sorted first.
	boolean bInquire = false; ; //When true will mean the process has received an inquire message so needs to reply to other process in case of a failed scenario with relinquish message. If it frees itself from its own lock as other request preceeds its request then this variable will be false to indicate that no replies need to be sent to other processes.

	//Message Types:
	//0 - REQUEST TOKEN
	//1 - SEND TOKEN

	//Defining methods
	public void static main(String args[])
	{

		try
		{

			//Access the shared input file
			FileInputStream fisInputFile = new FileInputStream(sFile);
			DataInputStream disInputData = new DataInputStream(fisInputFile);
			BufferedReader brInputReader = new BufferedReader(new InputStreamReader(disInputData));
			String sInputLine = null;

			while(//<Condition> Read the quorums from the shared file) //Process ids are hardcoded for each instance - part of argument list
			{

				//Process the file for Quorums for each process
				Step 1: initialize Quorums; - Read shared file for the process's own quorum - Quorum array - Has IP address, process id#
				Step 2: Get request order from shared file - Who starts first and check for initiate and terminate record - store request order of each process and if already processed - check in database for
				this. The last process in the request order to check for terminate
				Intermediate step: Create DB connection to update and read table log
				Step 4: Set iOutstandingReplies = # of members; //Initialize this to the # of processes in the quorum. (including itself)
				Step 5: Create threads - Listener and Talker
				Step 6: Open TCP IP connections on each of the threads and put messages in Priority queue - sorted on sequence # and process id in case of ties

			}

			//Read the order of mutual exclusion requests from the shared file one line at a time
			//Read log file to check which message was processed last
			//Read the line from the shared file for the next message to be sent - If it is for the current process id then send the message else wait
			while(//Condition - requests are pending and log file does not have "TERMINATE" in it)
			{

				case:
				//Read log file and search for the process id of the current process
				//If message has been processed then search for next message
				//Else if message type =
				//0 - REQUEST
				//Get the process id from the log file
				requestToken(iTempProcessId);

				//1 - SEND
				//Get the process id from the log file
				sendToken(iTempProcessId);


			}

			disInputData.close();
			disLogData.close();

		}
		catch(Exception e)
		{

			System.err.println("Error reading the input file");

		}

	}

	public void executeCS()
	{

		if (bToken == true and bRequestingCS == true) //If the current process has the token and is requesting toj=ken then enter CS
		{

			//Enter CS
			writeLogFile(iProcessID + " I am in CS !!");

		}

		//sendRelease();
		bCS = false;

		if (alChannel not empty)
		{

			iHolder = alChannel.pop(); //Get the first process which should get the token next
			bAsked = false;

			sendToken(iHolder); //Forward the token to the next process that should get it
			if(alChannel not empty)
			{

				requestToken(iHolder);
				bAsked = true;

			}

		}

	}

	public void sortChannel()
	{

		//Use quick sort to sort the queue by sequence id and then process id

		//Update log file
		writeLogFile(iProcessID + " FIFO channel sorted");

	}//End of sortChannel()

	public void updateSequenceId(int iTempSequenceId)//Update the sequence id of the current process based on the message received by it
	{

		if (iSequenceId >= iTempSequenceId)
		{

			//Do Nothing

		}
		else
		{

			iSequenceId = iTempSequenceId;

		}


	}//End of updateSequenceId()

	public void readLogFile()
	{

		//Access log file and search for the text pattern needed for the next action
		//Eg:
		//Search for "Quorums created for all processes" pattern to call method - initializeQuorums() for each process

	} //End of readLogFile()

	public void renameLogFiles()
	{

		//Rename log file
		//Rename bad file

	} //End of renameLogFiles()

	public void writeLogFile(String sTempMessage)
	{

		//Append to the log file the message passed to it.

	} //End of writeLogFile()

	public void writeBadFile(String sTempMessage)
	{

		//Append to the bad file the message passed to it.

	} //End of writeBadFile()

	public void sendRequest() //The current process is requesting for the critical section
	{

		//Set requesting CS variable to true
		bRequestingCS = true;

		//Update log file
		writeLogFile(iProcessID + " Requesting Criical Section at local sequence id " + iSequenceId);

		//Increment current process's sequence id
		iSequenceID = iSequenceID + 1;

		//Add iSequenceId, iProcessId to the Arraylist alChannel
		receiveRequest(iSequenceId,iProcessId);

		//Send request to all the quorum members with parameters iSequenceId and iProcessId
		sendMessage(iProcessId, iHolder, 0, iSequenceId); //Sending message with current process id, receiver's process id, Message, type of message and current process's sequence id

	}//End of sendRequest()

	public void sendMessage(int iTempSenderProcessId, int iTempReceiverProcessId, int iTempMessageType, int iTempSenderSequenceId)
	{

		//Write to the shared file where the processes will be able to read the messages intended for them

	}//End of sendMessage()

	public void receiveRequest(int iTempSequenceId, int iTempProcessId)
	{

		updateSequenceId(iTempProcessId);

		//Update log file
		writeLogFile(iProcessID + " Received Request for Critical Section by process " + iTempProcessId + " with sender's sequence id " + iTempSequenceId + " at local sequence id " + iSequenceId);

		//Create new channel arraylist member
		alChannel.add();

		//Sort Arraylist alChannel
		sortChannel();

		if (bToken == true && bCS == false)
		{

			if(alChannel is not empty)
			{

				iHolder = alChannel.pop();
				bAsked = false;

				if (iHolder == iProcessId && bRequesting == true)
				{

					executeCS();

				}
				else
				{

					sendToken(iHolder);

				}

			}

		}
		else if (bToken == false && bAsked == false)
		{

			sendRequest(iProcessId, iSequenceId);
			bAsked = true;

		}

	}//End of receiveRequest()

	public void receiveToken()
	{

		bToken = true;

		//Sort the channel by sequence number and if there is a tie then use process id
		sortChannel();

		if (alChannel not empty)
		{

			iHolder = alChannel.pop(); //Get the first process which should get the token next
			bAsked = false;

			if (iHolder == iProcessId)
			{

				executeCS(); //Enter into current process CS


			}
			else
			{

				sendToken(iHolder); //Forward the token to the next process that should get it

				if(alChannel not empty)
				{

					requestToken(iHolder);
					bAsked = true;

				}

			}

		}

	}

	public void sendToken(int iTempProcessId)
	{

		bToken = false;
		sendMessage(iProcessId, iHolder, 1, iSequenceId);

	}

}//End of class Process