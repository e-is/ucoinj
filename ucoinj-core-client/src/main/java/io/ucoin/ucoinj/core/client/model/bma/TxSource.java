package io.ucoin.ucoinj.core.client.model.bma;

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
import java.util.List;

public class TxSource {

	private String currency;
	
	private String pubkey;
	    
    private Source[] sources;

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getPubkey() {
		return pubkey;
	}

	public void setPubkey(String pubkey) {
		this.pubkey = pubkey;
	}

	public Source[] getSources() {
		return sources;
	}

	public void setSources(Source[] sources) {
		this.sources = sources;
	}

	public class Source implements Serializable, Cloneable {

		private static final long serialVersionUID = 8084087351543574142L;

		private String type;
		private int number;
		private String fingerprint;
		private long amount;


		@Override
		public Object clone() throws CloneNotSupportedException {

			Source clone = (Source)super.clone();
			clone.type = type;
			clone.number = number;
			clone.fingerprint = fingerprint;
			clone.amount = amount;
			return clone;
		}

		/**
		 * Source type : <ul>
		 * <li><code>D</code> : Universal Dividend</li>
		 * <li><code>T</code> : Transaction</li>
		 * </ul>
		 * @return
		 */
		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		/**
		 * The block number where the source has been written
		 * @return
		 */
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

		public long getAmount() {
			return amount;
		}

		public void setAmount(long amount) {
			this.amount = amount;
		}
	}


}
