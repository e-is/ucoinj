package io.ucoin.ucoinj.core.client.model.bma;

/*
 * #%L
 * UCoin Java :: Core Client API
 * %%
 * Copyright (C) 2014 - 2016 EIS
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

import io.ucoin.ucoinj.core.client.model.BasicIdentity;

import java.io.Serializable;

public class BlockchainMemberships extends BasicIdentity {
	private static final long serialVersionUID = -5631089862725952431L;

	private long sigDate;
	private Membership[] memberships;

	public long getSigDate() {
		return sigDate;
	}
	public void setSigDate(long sigDate) {
		this.sigDate = sigDate;
	}
	public Membership[] getMemberships() {
		return memberships;
	}
	public void setMemberships(Membership[] memberships) {
		this.memberships = memberships;
	}

	public class Membership implements Serializable {
		private static final long serialVersionUID = 1L;

		private String version;
		private String currency;
		private String membership;
		private long blockNumber;
		private String blockHash;

		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}
		public String getCurrency() {
			return currency;
		}
		public void setCurrency(String currency) {
			this.currency = currency;
		}
		public String getMembership() {
			return membership;
		}
		public void setMembership(String membership) {
			this.membership = membership;
		}
		public long getBlockNumber() {
			return blockNumber;
		}
		public void setBlockNumber(long blockNumber) {
			this.blockNumber = blockNumber;
		}
		public String getBlockHash() {
			return blockHash;
		}
		public void setBlockHash(String blockHash) {
			this.blockHash = blockHash;
		}
	}
}
