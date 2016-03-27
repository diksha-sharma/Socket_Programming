README.txt

1. The folder contains following sub folders:
	src: Thsi folder contains all the source code for the project
	
	AOSTokens.java - Starts program execution - main method
	MA.java - contains Maekawa's algorithm implementation
	TQ.java - Contains Test bed implementation
	Quorum.java - Contains quorum structure
	RequestQueue.java - Contains request queue member structure
	ListenerThread.java - Contains code for ListenerThread for TQ algo
	ListenerThread_MA.java - Contains code for ListenerThread for Maekawa algo
	TalkerThread.java - Contains code for TalkerThread for TQ algo
	TalkerThread_MA.java - Contains code for TalkerThread for Maekawa algo
	DatabaseThread.java - Contains code for DatabaseThread for TQ algo
	DatabaseThread_MA.java - Contains code for DatabaseThread for Maekawa algo
	
	setup: This folder contains the shared file AOS.txt
	
	doc: This folder contains project report.
	
2. The location and name of source file is passed as the argument and so is the algorithm# and process id of the current process
Eg:


3. To compile all the java files run:
	javac *.java

4. Place the input shared file in the location and pass that location as argument.
5. Login to terminals for the testing.
6. Initiate connections to oracle server and start tomcat/csoracle server
7. Start database server from eclipse
8. Set the classpath to the directory where the .class files will be generated for the source files
9. To execute program run the following commands: (Eg using nodes 4,5,6,7)

java -cp .:ojdbc7.jar AOSTokens "/people/cs/d/dxs134530/AOS/AOS.txt" "4" "2"
java -cp .:ojdbc7.jar AOSTokens "/people/cs/d/dxs134530/AOS/AOS.txt" "5" "2"
java -cp .:ojdbc7.jar AOSTokens "/people/cs/d/dxs134530/AOS/AOS.txt" "6" "2"
java -cp .:ojdbc7.jar AOSTokens "/people/cs/d/dxs134530/AOS/AOS.txt" "7" "2"
