package node.simulators;

public class BufferImpl implements Buffer {
    private static final int BUFFER_SIZE = 12;
    private static final int WINDOW_SIZE = 12;
    private static final int OVERLAPPING = 50;    /* settable % of overlapping */
    private static final int LIMIT = (OVERLAPPING * BUFFER_SIZE) / 100;

    private final Measurement[] buffer, window;
    private int upperBound, lowerBound, overlap;
    private Measurement mean;
    private boolean meanReady;

    public BufferImpl(){
        this.window =  new Measurement[WINDOW_SIZE];
        this.buffer = new Measurement[BUFFER_SIZE];
        this.upperBound = 0; this.lowerBound = 0; this.overlap = -LIMIT -1;
    }

    @Override
    public void addMeasurement(Measurement m) {
        buffer[upperBound] = m;
        window[upperBound % WINDOW_SIZE] = buffer[upperBound];
        overlap = (overlap + 1) % LIMIT;
        upperBound = (upperBound + 1) % (BUFFER_SIZE);
        if(upperBound - lowerBound  >= WINDOW_SIZE || lowerBound > upperBound) {
            lowerBound = (lowerBound + 1) % (BUFFER_SIZE);
        }

        if(overlap == LIMIT - 1){
            double sum = 0;
            int count = 0;
            for (Measurement e : window){
                if(e != null){
                    sum += e.getValue();
                    count++;
                }
            }
            //if another mean is ready before it has been polled, make the mean of the 2
            if (meanReady){
                double partialAvg = sum/count;
                mean = new Measurement(window[0].getId(), window[0].getType(), (partialAvg + mean.getValue()) / 2, m.getTimestamp());
            }
            mean = new Measurement(window[0].getId(), window[0].getType(), sum / count, m.getTimestamp());
            meanReady = true;
            System.out.println("INFO: Mean ready");
        }
    }
    

    public boolean isReady() {
        return meanReady;
    }

    public String getMean(){
        meanReady = false;
        return "{\"id\":" + mean.getId() + ", \"type\":" + mean.getType() + ", \"value\":" + mean.getValue() + ", \"timestamp\":" + mean.getTimestamp() + "}";
    }
}
