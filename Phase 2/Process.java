public class Process
{

	//Input file structure (Example of 2 processes):
	//Quorums:
	//1,2
	//2,3
	//3,1

	//Defining class variables
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
	boolean bInquire = false; ; //When true will mean the process has received an inquire message so needs to reply to other process in case of a failed scenario with relinquish message.
					  //If it frees itself from its own lock as other request
					  //preceeds its request then this variable will be false
					  //to indicate that no replies need to be sent to other
					  //processes.
	//Message Types:
	//0 - REQUEST
	//1 - RECEIVE
	//2 - LOCKED
	//3 - FAILED
	//4 - INQUIRE
	//5 - RELINQUISH
	//6 - RELEASE

	//Defining methods
	public void static main(String args[])
	{

		//Renames the log file and bad file used in the program so to track the execution of the program execution in new files every time
		renameLogFiles();

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
				initializeQuorums();
				iOutstandingReplies = # of members; //Initialize this to the # of processes in the quorum. (including itself)

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
				sendRequest();

				//1 - RECEIVE
				//Get the process id and the sequence id from the log file
				receiveRequest(iTempSequenceId, iTempProcessId);

				//2 - LOCKED
				//Get the process id from the log file
				sendLocked(iTempProcessId); OR receiveLocked();

				//3 - FAILED
				//Get the process id from the log file
				sendFailed(iTempProcessId); OR receiveFailed();

				//4 - INQUIRE
				//Get the process id from the log file
				sendInquire(iTempProcessId); OR receiveInquire();

				//5 - RELINQUISH
				//Get the process id from the log file
				sendRelinquish(iTempProcessId); OR receiveRelinquish();

				//6 - RELEASE
				sendRelease(); OR receiveRelease();


			}

			disInputData.close();
			disLogData.close();

		}
		catch(Exception e)
		{

			System.err.println("Error reading the input file");

		}

	}

	public void receiveRelease(int iTempProcessId)
	{

		if (bLocked == true && iTempProcessId == iLockedForProcessId)
		{

			bLocked = false;

			if (alChannel not empty)
			{

				//Send locked to the process at the top of the queue
				sendLocked(iProcessId);

			}

		}

	}

	public void sendRelease()
	{

		bCS = false;
		bLocked = false;
		iOutstandingReplies = //Quorum size;

		//Send request to all the quorum members with parameters iSequenceId and iProcessId
		sendMessage(iProcessId, <quorum process id>, 6, iSequenceId); //Sending message with current process id, receiver's process id, Message, type of message and current process's sequence id

	}

	public void executeCS()
	{

		if (bLocked == true && bInquire == false && iOutstandingReplies == 0)
		{

			//Enter CS
			writeLogFile(iProcessID + " I am in CS !!");

		}

		sendRelease();

	}
	public void initializeQuorums()
	{

		//Read the quorum shared file for the current process and set the arraylist alPQuorum with the quorum members

		//Update log file
		writeLogFile(iProcessID + " Quorums Initialized");

	}//End of initializeQuorums()

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
		sendMessage(iProcessId, alPQuorum[iCounter].iProcessId, 0, iSequenceId); //Sending message with current process id, receiver's process id, Message, type of message and current process's sequence id

	}//End of sendRequest()

	public void sendRelinquish(int iTempProcessId)
	{

		//Send relinquish message to the inquiring process
		sendMessage(iProcessId, iTempProcessId, 5, iSequenceId); //Sending message with current process id, receiver's process id, Message, type of message and current process's sequence id

	}//End of sendRelinquish()

	public void sendLocked(int iTempProcessId)
	{

		//Send failed message to the sender process
		sendMessage(iProcessId, iTempProcessId, 2, iSequenceId); //Sending message with current process id, receiver's process id, Message, type of message and current process's sequence id

	}//End of sendLocked()

	public void sendFailed(int iTempProcessId)
	{

		//Send failed message to the sender process
		sendMessage(iProcessId, iTempProcessId, 3, iSequenceId); //Sending message with current process id, receiver's process id, Message, type of message and current process's sequence id

	}//End of sendFailed()

	public void sendInquire(int iTempProcessId)
	{

		//Send inquire message to the sender process
		sendMessage(iProcessId, iTempProcessId, 4, iSequenceId); //Sending message with current process id, receiver's process id, Message, type of message and current process's sequence id

	}//End of sendInquire()

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

		//If the current process is locked for some process already
		if (bLocked == true)
		{

			if(alChannel[0].ProcessId == iLockedForProcessId) //When the current process is locked for a process that it should be locked for
			{

				if (iLockedForProcessId != iTempProcessId)
				{

					//Send failed message to the sender
					sendFailed(iTempProcessId);

				}

			}
			else if (alChannel[0].ProcessId != iLockedForProcessId) //When current process is locked by a process that may not be the first one who should be granted to access CS
			{

				if ((alChannel[0].ProcessId != iTempProcessId) && (iLockedForProcessId != iTempProcessId)) //A third non priority process is requesting for the CS
				{

					//Send failed message to the sender
					sendFailed(iTempProcessId);

				}

				if (alChannel[0].iSequenceID < iLockedForSequenceId) //The process in the queue should get to CS first than the one for which the current process is locked
				{

					//Send inquire message to the process the current process is locked for
					sendInquire(iLockedForProcessId);

				}
				else if(alChannel[0].iSequenceID > iLockedForSequenceId) //The process in the queue should get to CS later than the one for which the current process is locked
				{

					//Do nothing

				}
				else if (alChannel[0].iSequenceID == iLockedForSequenceId) //The process in the queue and one for which the current process is locked have same sequence id - now we compare process id
				{

					if (alChannel[0].ProcessId < iLockedForProcessId)
					{

						//Send inquire message to the process that the current process is locked for
						sendInquire(iLockedForProcessId);

					}
					else
					{

						//Do nothing
					}

				}

			}

		}
		else if (bLocked == false) //The current process is not locked for any process then it can be locked for the sender
		{

			//Send locked message to the sender process
			sendMessage(iProcessId, iTempProcessId, 2, iSequenceId); //Sending message with current process id, receiver's process id, Message, type of message and current process's sequence id
			bLocked = true;
			iLockedForProcessId = iTempProcessId;
			iLockedForSequenceId = iTempSequenceId;
			iOutstandingReplies = iOutstandingReplies - 1;

		}


	}//End of receiveRequest()

	public void receiveRelinquish()
	{

		if(alChannel[0].iProcessId != null) //If the request queue is not empty then get locked for the process in front of the queue else do nothing
		{

			iLockedForProcessId = alChannel[0].iProcessId;
			iLockedForSequenceId = alChannel[0].iSequenceId;
			SendLocked(iLockedForProcessId);

		}


	}//End of receiveRelinquish()

	public void receiveFailed()
	{

		if (bInquire == true)
		{

			//Send relinquish message to the inquiring process
			sendRelinquish(iInquireProcessId); //Sending message with current process id, receiver's process id, Message, type of message and current process's sequence id
		}

	}//End of receiveFailed()

	public void receiveInquire(int iTempProcessId)
	{

		iInquireProcessId = iTempProcessId;
		bInquire = true;

	}//End of receivingInquire()

	public void receiveLocked()
	{

		iOutstandingReplies = iOutstandingReplies - 1;

		if (iOutstandingReplies == 0)
		{

			executeCS();

		}

	}

}//End of class Process