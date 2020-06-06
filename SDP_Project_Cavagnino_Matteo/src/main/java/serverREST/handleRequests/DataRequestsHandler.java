package serverREST.handleRequests;

import beans.DataWrapper;
import beans.Measurements;
import beans.Nodes;

public class DataRequestsHandler {
    public static DataWrapper computeResult(String command, int... n){
        DataWrapper wrapper = new DataWrapper();

        switch (command){
            case "nodes-number":
                wrapper.setNumber(String.valueOf(Nodes.getInstance().nodeNumber()));
                break;
            case "last-n" :
                wrapper.setList(Measurements.getInstance().getLastMeasurements(n[0]));
                break;
            case "SD-mean" :
                wrapper.setSdMean(Measurements.getInstance().getSDMean(n[0]));
        }
        return wrapper;
    }

}
