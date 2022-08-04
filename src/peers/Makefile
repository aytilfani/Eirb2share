
all : socket worker scenario1

file:
	javac FileInfo.java
peers:
	javac PeerClient.java
	javac PeerServer.java

socket: file peers
	javac Socket_t.java
	javac ServerSocket_t.java

scenario1 : socket file peers
	javac Scenario1P1.java
	javac Scenario1P2.java

# test: socket
# 	javac TestConnect.java
# 	javac TestConnectUn.java

worker: socket
	javac Worker.java
clean: 
	rm *.class