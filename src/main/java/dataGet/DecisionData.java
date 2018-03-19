package dataGet;

public class DecisionData {
  private CP cp = new CP();
  private double time;
  private String[] tickerCodeList;
  private String[] tickerNameList;
  private String segment;
  private MySqlDataGet mySqlDataGet = new MySqlDataGet();

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public CP getCp() {
        return cp;
    }

    public void setCp() {
        double[][] temp = mySqlDataGet.getCpData(segment);
        cp.setData(temp);
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public String[] getTickerCodeList() {
        setTickerCodeList();
        return tickerCodeList;
    }

    public void setTickerCodeList() {
        this.tickerCodeList = mySqlDataGet.getTickerCodeList();
    }

    public String[] getTickerNameList() {
        setTickerNameList();
        return tickerNameList;
    }

    public void setTickerNameList() {
        this.tickerNameList = mySqlDataGet.getTickerNameList();
    }
}
