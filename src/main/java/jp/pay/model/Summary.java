package jp.pay.model;

public class Summary extends PayjpObject {
	Integer chargeCount;
	Integer chargeFees;
	Integer chargeGross;
	Integer net;
	Integer refundAmount;
	Integer refundCount;

	public Integer getChargeFees() {
		return chargeFees;
	}

	public Integer getNet() {
		return net;
	}

	public Integer getRefundCount() {
		return refundCount;
	}

	public Integer getChargeCount() {
		return chargeCount;
	}

	public Integer getChargeGross() {
		return chargeGross;
	}

	public Integer getRefundAmount() {
		return refundAmount;
	}
}
