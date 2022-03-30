package com.gidi.bio_console.bean;

/**学期采样**/
public class BloodGasSamplingBean {

	private String liverNum;
	
	private String samplingTime;
	
	private int isChecked = 0;

	public String getLiverNum() {
		return liverNum;
	}

	public void setLiverNum(String liverNum) {
		this.liverNum = liverNum;
	}

	public String getSamplingTime() {
		return samplingTime;
	}

	public void setSamplingTime(String samplingTime) {
		this.samplingTime = samplingTime;
	}

	public int getIsChecked() {
		return isChecked;
	}

	public void setChecked(int isChecked) {
		this.isChecked = isChecked;
	}

	
	
	
	
}
