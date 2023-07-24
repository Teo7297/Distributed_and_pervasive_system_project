This is the project for the University exam of Distributed and Pervasive Systems.
This project includes a java implementation of a fully flexible token ring architecture.
Communication is executed using gRPC and nodes are able to join and leave the network at any time, granting secure concurrency between processes.


#####################################################################################

Progetto SDP by Cavagnino Matteo (mat. 961707)

Per l'avvio delle varie funzioni:

Gateway: 
	- avviare la classe serverREST.server.StartServer

Nodi:
	- avviare la classe node.Node
	
	P.S

		- è possibile terminare i nodi inserendo un qualsiasi input da tastiera sulla console
		- è possibile settare nelle classi node.services.TokenHandler e node.services.NodeServicesImpl dei campi test per rallentare lo scambio dei token
	
Analyst: 
	- avviare la classe analyst.Analyst e seguire le istruzioni a schermo per l'utilizzo
