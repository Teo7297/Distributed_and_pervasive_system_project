package analyst;

import analyst.UI.UI;
import beans.DataWrapper;
import beans.Measurement;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Analyst {
    private static final String REQUEST_NODES_NUMBER = "http://localhost:1337/measurements/get/nodenumber";
    private static final String REQUEST_LAST_N_MEASUREMENTS = "http://localhost:1337/measurements/get/last-measurements/";
    private static final String REQUEST_SD_MEAN = "http://localhost:1337/measurements/get/sd-mean/";

    private static boolean active = true;

    public static void main(String[] args) {

        while (active) {
            UI.drawMainPage();

            parseSelection(getUserInput(1));

            System.out.println("\n\nPress Enter to go back to the menu or insert QUIT to quit the application");

            getUserInput(3);

            clearScreen();

        }

    }

    private static void clearScreen() {
        System. out. print("\033[H\033[2J");
        System. out. flush();
    }

    private static void parseSelection(int selection){

        Client client = Client.create();
        WebResource resource;
        ClientResponse response;
        ObjectMapper mapper = new ObjectMapper();
        String JSONResult;
        DataWrapper wrapper;
        int n;

        switch (selection){
            case 1:
                //nodes number
                resource = client.resource(REQUEST_NODES_NUMBER);
                response = resource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
                try (BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntityInputStream()))) {
                    JSONResult = br.readLine();
                    wrapper = mapper.readValue(JSONResult, DataWrapper.class);
                    System.out.println("There are [ " + wrapper.getNumber() + " ] nodes in the network");
                } catch (IOException e) {
                    System.err.println("ANALYST ERROR - An error occurred while mapping server response");
                    e.printStackTrace();
                }
                break;
            case 2:
                //ask n and draw UI with last n measur.
                n = getUserInput(2);
                resource = client.resource(REQUEST_LAST_N_MEASUREMENTS + n);
                response = resource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
                try (BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntityInputStream()))) {
                    JSONResult = br.readLine();
                    wrapper = mapper.readValue(JSONResult, DataWrapper.class);
                    int listLen = wrapper.getList().length;
                    if(n > listLen){
                        System.out.println("There are not enough values on the server, all the values will be shown...");
                    }
                    System.out.println("This is the list of the last " + listLen + " measurements made by the network:\n");
                    for(Measurement m :  wrapper.getList()){
                        System.out.println(m);
                    }
                } catch (IOException e) {
                    System.err.println("ANALYST ERROR - An error occurred while mapping server response");
                    e.printStackTrace();
                }

                break;
            case 3:
                //ask n and draw UI with deviation + mean
                n = getUserInput(2);
                resource = client.resource(REQUEST_SD_MEAN + n);
                response = resource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
                try (BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntityInputStream()))) {
                    JSONResult = br.readLine();
                    wrapper = mapper.readValue(JSONResult, DataWrapper.class);
                    System.out.println("Result from the last [ " + n + " ] measurements made by the network:\n");
                    System.out.println("Standard deviation: " + wrapper.getSdMean()[0]);
                    System.out.println("Mean value: " + wrapper.getSdMean()[1]);

                } catch (IOException e) {
                    System.err.println("ANALYST ERROR - An error occurred while mapping server response");
                    e.printStackTrace();
                }
                break;
            case 4:
                //close application
                System.out.println("Closing ...");
                System.exit(0);
            default:
                System.err.println("ANALYST ERROR - Something went wrong while parsing user selection...");
        }
    }

    private static int getUserInput(int phase) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input = null;
        int value;

        while(input == null) {
            try {

                switch (phase){
                    //checks input in main page (phase 1)
                    case 1:
                        input = br.readLine();
                        try {
                            value = Integer.parseInt(input);
                        } catch (NumberFormatException e){
                            throw new IOException();
                        }

                        if(value < 1 || value > 4){
                            throw new IOException();
                        }
                        return value;

                    //checks input for parameter (phase 2)
                    case 2:
                        System.out.println("Insert parameter:");
                        input = br.readLine();
                        try {
                            value = Integer.parseInt(input);
                        } catch (NumberFormatException e){
                            throw new IOException();
                        }
                        return value;
                    case 3:
                        if((input = br.readLine().toLowerCase()).equals("quit")){
                            active = false;
                        }
                        break;

                    default:
                        System.err.println("ANALYST ERROR - Something went wrong while parsing user input...");
                }

            } catch (IOException e) {
                input = null;
                System.out.println("ANALYST ERROR - Given input is not valid ...\nTry again\n");
            }
        }
        return 0;
    }
}
