package beans;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;

/**
 * this class represents Node bean objects used to communicate that a node is starting/ending its activity
 */

@XmlRootElement
public class Node {
    private static final String host = "localhost";
    private String ipaddr;
    private String id;
    private int port;

    public Node(){}

    public Node(String id, int port){
        this.id = id;
        this.port = port;
        this.ipaddr = host;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public int getPort() {
        return port;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String toString(){
        return "Node: " + this.getId() + " " + this.getIpaddr()+":"+this.getPort();
    }

    public String toJSONString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (IOException e) {
            System.err.println("BEAN NODE ERROR - Could not translate node to JSON String");
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject toJSONObject(){
        try {
            return new JSONObject().put("id", this.getId()).put("port", this.getPort()).put("ipaddr", this.getIpaddr());
        } catch (JSONException e) {
            System.err.println("BEAN NODE ERROR - Could not translate node to JSONObject");
            e.printStackTrace();
        }
        return null;
    }
}
