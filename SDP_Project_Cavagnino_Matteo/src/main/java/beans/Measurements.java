package beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Measurements {
    @XmlElement(name="measurement")
    private final List<Measurement> measurementList;
    private static Measurements instance;

    public Measurements(){
        measurementList = new ArrayList<>();
    }

    //singleton getInstance
    public synchronized static Measurements getInstance(){
        if(instance==null)
            instance = new Measurements();
        return instance;
    }

    public synchronized void add(Measurement m){
        measurementList.add(m);
    }

    public synchronized Measurement[] getLastMeasurements(int n) {
        if (n > measurementList.size()){
            n = measurementList.size();
        }
        Measurement[] result = new Measurement[n];
        int j = n - 1;
        for(int i = measurementList.size() - 1; i >= measurementList.size() - n; i--){
            result[j--] = measurementList.get(i);
        }
        return result;
    }

    public synchronized String[] getSDMean(int n) {
        if (n > measurementList.size()){
            n = measurementList.size();
        }
        Measurement[] measurements = getLastMeasurements(n);
        double[] values = new double[n];
        int j = 0;
        for (Measurement value : measurements){
            values[j++] = value.getValue();
        }
        double[] result = calculateSDMean(values);
        return new String[]{String.valueOf(result[0]), String.valueOf(result[1])};
    }


    private static double[] calculateSDMean(double[] numArray) {
        double standardDeviation = 0.0;
        int length = numArray.length;
        double mean = calculateMean(numArray);
        for(double num: numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }
        return new double[]{Math.sqrt(standardDeviation/length), mean};
    }

    private static double calculateMean(double[] numArray){
        double sum = 0.0;
        for (double num : numArray){
            sum = sum + num;
        }
        return sum/numArray.length;
    }
}
