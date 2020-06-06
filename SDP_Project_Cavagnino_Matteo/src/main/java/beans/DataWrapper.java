package beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DataWrapper{
    private String number;
    private Measurement[] list;
    private String[] sdMean;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Measurement[] getList() {
        return list;
    }

    public void setList(Measurement[] list) {
        this.list = list;
    }

    public String[] getSdMean() {
        return sdMean;
    }

    public void setSdMean(String[] sdMean) {
        this.sdMean = sdMean;
    }
}