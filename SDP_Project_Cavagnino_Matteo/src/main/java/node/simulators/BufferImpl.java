package node.simulators;

import java.util.Arrays;

public class BufferImpl implements Buffer {
    private static final int BUFFER_SIZE = 24;
    private static final int WINDOW_SIZE = 12;

    private final Measurement[] buffer;
    private int upperBound, lowerBound, next;
    private Measurement mean;
    private volatile boolean meanReady;

    public BufferImpl(){
        this.buffer = new Measurement[BUFFER_SIZE];
        this.upperBound = 0; this.lowerBound = 0;
        this.next = WINDOW_SIZE-1;
    }

    @Override
    public synchronized void addMeasurement(Measurement m) {
        buffer[upperBound] = m;
        //System.out.println("Buffer: " + Arrays.toString(buffer));
        //System.out.println(upperBound + " " + lowerBound);
        if (upperBound == next){
            next = (next + (WINDOW_SIZE/2)) % BUFFER_SIZE;
            double sum = 0;
            int count = 0;
            for(int e = 0; e <= WINDOW_SIZE; e++){
                if(buffer[(e + lowerBound) % BUFFER_SIZE] != null){
                    sum += buffer[(e + lowerBound) % BUFFER_SIZE].getValue();
                    count++;
                }
            }
            //if another mean is ready before it has been polled, make the mean of the 2
            if (meanReady){
                double partialAvg = sum/count;
                setMean(new Measurement(buffer[0].getId(), buffer[0].getType(), (partialAvg + this.getMeanM().getValue()) / 2, m.getTimestamp()));
            }
            setMean(new Measurement(buffer[0].getId(), buffer[0].getType(), sum / count, m.getTimestamp()));
            meanReady = true;
            lowerBound = (lowerBound + (WINDOW_SIZE/2)) % BUFFER_SIZE;
            System.out.println("INFO: Mean ready");
        }
        upperBound = (upperBound + 1) % BUFFER_SIZE;
    }

    public boolean isReady() {
        return meanReady;
    }

    public synchronized void setMean(Measurement mean){
        this.mean = mean;
    }
    private synchronized Measurement getMeanM(){
        return this.mean;
    }
    public synchronized String getMean(){
        meanReady = false;
        return "{\"id\":" + mean.getId() + ", \"type\":" + mean.getType() + ", \"value\":" + mean.getValue() + ", \"timestamp\":" + mean.getTimestamp() + "}";
    }
}
