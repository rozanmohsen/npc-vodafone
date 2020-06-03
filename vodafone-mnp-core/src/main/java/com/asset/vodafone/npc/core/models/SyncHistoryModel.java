package com.asset.vodafone.npc.core.models;

public class SyncHistoryModel extends SyncModel {

	

	public void setSeqNumber(long seqNumber) {
		this.seqNumber = seqNumber;
	}

	public long getSeqNumber() {
		return seqNumber;
	}

	private long seqNumber;
}
