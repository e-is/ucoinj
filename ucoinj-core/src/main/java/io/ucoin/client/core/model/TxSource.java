package io.ucoin.client.core.model;

import java.io.Serializable;

public class TxSource implements Serializable {

	private static final long serialVersionUID = 8084087351543574142L;

	private String type;	
	private int number;	
	private String fingerprint;	
    private double amount;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getFingerprint() {
		return fingerprint;
	}

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}    
}
