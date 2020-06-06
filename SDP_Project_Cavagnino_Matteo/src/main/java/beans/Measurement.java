package beans;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Measurement {
    private String id;
    private String type;
    private double value;
    private long timestamp;

    public Measurement(){}

    public Measurement(String id, String type, double value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
        this.id=id;
        this.type=type;
    }

    private String formatMillis(long durationInMillis){
        long millis = durationInMillis % 1000;
        long second = (durationInMillis / 1000) % 60;
        long minute = (durationInMillis / (1000 * 60)) % 60;
        long hour = (durationInMillis / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d.%d", hour, minute, second, millis);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String type) {
        this.id = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toJSONString(){
        return "{\"id\":" + this.getId() + ", \"type\":" + this.getType() + ", \"value\":" + this.getValue() + ", \"timestamp\":" + this.getTimestamp() + "}";
    }

    public String toString(){
        StringBuilder p1 = new StringBuilder("Value: [ " + value);
        while(p1.length() < 27){
            p1.append(" ");
        }
        StringBuilder p2 = new StringBuilder(this.formatMillis(timestamp));
        while(p2.length() < 12){
            p2.append(" ");
        }
        return  p1 + " ] - Timestamp: [ " + p2 + " ]";
    }
}
