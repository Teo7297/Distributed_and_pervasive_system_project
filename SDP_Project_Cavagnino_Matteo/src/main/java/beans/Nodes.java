package beans;

import org.codehaus.jackson.map.ObjectMapper;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * bean class used by the REST server to store Node beans.
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Nodes {
    @XmlElement(name="node")
    private final List<Node> nodesList;
    private static Nodes instance;

    public Nodes(){
        nodesList = new ArrayList<>();
    }

    public synchronized static Nodes getInstance(){
        if(instance==null)
            instance = new Nodes();
        return instance;
    }

    public int nodeNumber(){  //sync perchè potrebbero esserci inserimenti ed è meglio mandare all'analista il dato piu aggiornato
        return nodesList.size();
    }

    public synchronized Nodes add(beans.Node n){

        for(Node e : nodesList){
            if (e.getId().equals(n.getId())){
                System.err.println("INFO: Process tried to insert a node already registered - " + n);
                return null;
            }
        }
        System.out.println("adding new node => { " + n + " }" );
        nodesList.add(n);
        try {
            return new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(instance), Nodes.class);
        } catch (IOException e) {
            System.err.println("SERVER ERROR - Error occurred while making a node.Nodes instance deep copy");
            e.printStackTrace();
            return null;
        }
    }


    //removes node from list (if not present nothing happens)
    public synchronized void remove(Node n) {               //sync perche add scorre la lista non posso togliere un nodo mentre add lo cerca
        nodesList.removeIf(nb -> nb.getId().equals(n.getId()));
        System.out.println("removed node => { " + n + " }" );

    }

    public synchronized List<Node> getNodesList() {
        return nodesList;
    }
}
