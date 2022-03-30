package com.gidi.bio_console.bean;

//散热状态
public class HeatDissStatusBean {

	int android_heatdiss;
	int power_heatdiss;
	int semi_heatdiss;
	
	public HeatDissStatusBean(int android_diss,int power_diss, int semi_diss){
		this.android_heatdiss = android_diss;
		this.power_heatdiss = power_diss;
		this.semi_heatdiss = semi_diss;
	}

	public int getAndroid_heatdiss() {
		return android_heatdiss;
	}

	public void setAndroid_heatdiss(int android_heatdiss) {
		this.android_heatdiss = android_heatdiss;
	}

	public int getPower_heatdiss() {
		return power_heatdiss;
	}

	public void setPower_heatdiss(int power_heatdiss) {
		this.power_heatdiss = power_heatdiss;
	}

	public int getSemi_heatdiss() {
		return semi_heatdiss;
	}

	public void setSemi_heatdiss(int semi_heatdiss) {
		this.semi_heatdiss = semi_heatdiss;
	}

	
	
}
