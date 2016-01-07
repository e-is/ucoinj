package io.ucoin.ucoinj.core.client.model.bma;

public class TxHistory {

    private String currency;

    private String pubkey;

    private History history;

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

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

	public class History {

		private Movement[] sent;

		private Movement[] received;

		private Movement[] sending;

		private Movement[] receiving;

		public Movement[] getSent() {
			return sent;
		}

		public void setSent(Movement[] sent) {
			this.sent = sent;
		}

		public Movement[] getReceived() {
			return received;
		}

		public void setReceived(Movement[] received) {
			this.received = received;
		}

		public Movement[] getSending() {
			return sending;
		}

		public void setSending(Movement[] sending) {
			this.sending = sending;
		}

		public Movement[] getReceiving() {
			return receiving;
		}

		public void setReceiving(Movement[] receiving) {
			this.receiving = receiving;
		}
	}

	public static class Movement {

		private String version;

		private String[] issuers;

		private String[] inputs;

		private String[] outputs;

		private String comment;

		private String[] signatures;

		private String hash;

		private int block_number;

		private long time;

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String[] getIssuers() {
			return issuers;
		}

		public void setIssuers(String[] issuers) {
			this.issuers = issuers;
		}

		public String[] getInputs() {
			return inputs;
		}

		public void setInputs(String[] inputs) {
			this.inputs = inputs;
		}

		public String[] getOutputs() {
			return outputs;
		}

		public void setOutputs(String[] outputs) {
			this.outputs = outputs;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public String[] getSignatures() {
			return signatures;
		}

		public void setSignatures(String[] signatures) {
			this.signatures = signatures;
		}

		public String getHash() {
			return hash;
		}

		public String getFingerprint() {
			return hash;
		}

		public void setHash(String hash) {
			this.hash = hash;
		}

		/**
		 * @deprecated use getBlockNumber() instead
		 * @return
		 */
		@Deprecated
		public int getBlock_number() {
			return block_number;
		}

		/**
		 * @deprecated use setBlockNumber() instead
		 * @return
		 */
		@Deprecated
		public void setBlock_number(int block_number) {
			this.block_number = block_number;
		}

		public int getBlockNumber() {
			return block_number;
		}

		public void setNumber(int block_number) {
			this.block_number = block_number;
		}

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
		}
	}
}
