package io.ucoin.client.core.model;

/**
 * Blockwhain parameters.
 * 
 * @author Benoit Lavenier <benoit.lavenier@e-is.pro>
 * @since 1.0
 */
public class BlockchainParameter {

    private String currency;
    
    /**
     * The %growth of the UD every [dt] period
     */
    private Double c;
    
    /**
     * Time period between two UD
     */
    private Integer dt;
    
    /**
     * UD(0), i.e. initial Universal Dividend
     */
    private Integer ud0;
    
    /**
     * Minimum delay between 2 identical certifications (same pubkeys)
     */
    private Integer sigDelay;
    
    /* TODO : 
     
    "sigValidity": 2629800,
  "sigQty": 3,
  "stepMax": 3,
  "powZeroMin": 4,
  "powPeriod": 0.05,
  "incDateMin": 3,
  "dtDateMin": 10800
    */
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("currency: ").append(currency).append("\n")
        .append("c: ").append(c).append("\n")
        .append("dt: ").append(dt).append("\n")
        .append("ud0: ").append(ud0).append("\n")
        .append("sigDelay: ").append(sigDelay);
        return sb.toString();
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getC() {
        return c;
    }

    public void setC(Double c) {
        this.c = c;
    }

    public Integer getDt() {
        return dt;
    }

    public void setDt(Integer dt) {
        this.dt = dt;
    }

    public Integer getUd0() {
        return ud0;
    }

    public void setUd0(Integer ud0) {
        this.ud0 = ud0;
    }

    public Integer getSigDelay() {
        return sigDelay;
    }

    public void setSigDelay(Integer sigDelay) {
        this.sigDelay = sigDelay;
    }
}
