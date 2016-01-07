package io.ucoin.ucoinj.core.client.model.bma;

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
