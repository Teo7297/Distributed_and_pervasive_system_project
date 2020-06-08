Progetto SDP by Cavagnino Matteo (mat. 961707)

Per l'avvio delle varie funzioni:

Gateway: 
	- avviare la classe serverREST.server.StartServer

Nodi: 3 modalità:
	- avviare la classe node.Node per avviare un singolo nodo con valori prefissati (Consigliato se si vuole testare la terminazione)
	- avviare la classe launchers.NodeLauncher per avviare un numero arbitrario di nodi settabile da stdin (utile per testare avvio in contemporanea di piu nodi)
	- avviare la classe launchers.Launch10Nodes per avviare 10 nodi in contemporanea (utile per avviare i nodi 10 a 10 per testare la rete sotto stress)
	
	P.S
		è possibile terminare i nodi sia inserendo un qualsiasi input da tastiera sulla console sia cliccando il pulsante
		della piccola GUI (se i nodi vengono avviati in bulk terminare un nodo causera il crash degli altri nodi)
	
Analyst: 
	- avviare la classe analyst.Analyst e seguire le istruzioni a schermo per l'utilizzo
