package io.ucoin.client.core.model;

/*
 * #%L
 * UCoin Java Client :: Core API
 * %%
 * Copyright (C) 2014 - 2015 EIS
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import java.io.Serializable;

/**
 * Blockwhain parameters.
 * 
 * @author Benoit Lavenier <benoit.lavenier@e-is.pro>
 * @since 1.0
 */
public class BlockchainParameter implements Serializable {

	private static final long serialVersionUID = 929951447031659549L;

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
    
    
    /**
     * Maximum age of a valid signature (in seconds) (e.g. 2629800)
     */
    private Integer sigValidity;
 
    /**
     * Minimum quantity of signatures to be part of the WoT (e.g. 3)
     */
    private Integer sigQty;

    /**
     * Minimum quantity of valid made certifications to be part of the WoT for distance rule
     */
    private Integer sigWoT;
    
    /**
     * Maximum age of a valid membership (in seconds)
     */
    private Integer msValidity;
    
    /**
     * Maximum distance between each WoT member and a newcomer
     */
    private Integer stepMax;
    

    /**
     * Number of blocks used for calculating median time.
     */
    private Integer medianTimeBlocks;

    /**
     * The average time for writing 1 block (wished time)
     */
    private Integer avgGenTime;

    /**
     * The number of blocks required to evaluate again PoWMin value
     */
    private Integer dtDiffEval;

    /**
     * The number of previous blocks to check for personalized difficulty
     */
    private Integer blocksRot;

    /**
     * The percent of previous issuers to reach for personalized difficulty
     */
    private Double percentRot;

    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("currency: ").append(currency).append("\n")
        .append("c: ").append(c).append("\n")
        .append("dt: ").append(dt).append("\n")
        .append("ud0: ").append(ud0).append("\n")
        .append("sigDelay: ").append(sigDelay);
        // TODO : display missing fields
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

    public Integer getSigValidity() {
        return sigValidity;
    }

    public void setSigValidity(Integer sigValidity) {
        this.sigValidity = sigValidity;
    }

    public Integer getSigQty() {
        return sigQty;
    }

    public void setSigQty(Integer sigQty) {
        this.sigQty = sigQty;
    }

    public Integer getSigWoT() {
        return sigWoT;
    }

    public void setSigWoT(Integer sigWoT) {
        this.sigWoT = sigWoT;
    }

    public Integer getMsValidity() {
        return msValidity;
    }

    public void setMsValidity(Integer msValidity) {
        this.msValidity = msValidity;
    }

    public Integer getStepMax() {
        return stepMax;
    }

    public void setStepMax(Integer stepMax) {
        this.stepMax = stepMax;
    }

    public Integer getMedianTimeBlocks() {
        return medianTimeBlocks;
    }

    public void setMedianTimeBlocks(Integer medianTimeBlocks) {
        this.medianTimeBlocks = medianTimeBlocks;
    }

    public Integer getAvgGenTime() {
        return avgGenTime;
    }

    public void setAvgGenTime(Integer avgGenTime) {
        this.avgGenTime = avgGenTime;
    }

    public Integer getDtDiffEval() {
        return dtDiffEval;
    }

    public void setDtDiffEval(Integer dtDiffEval) {
        this.dtDiffEval = dtDiffEval;
    }

    public Integer getBlocksRot() {
        return blocksRot;
    }

    public void setBlocksRot(Integer blocksRot) {
        this.blocksRot = blocksRot;
    }

    public Double getPercentRot() {
        return percentRot;
    }

    public void setPercentRot(Double percentRot) {
        this.percentRot = percentRot;
    }
}
