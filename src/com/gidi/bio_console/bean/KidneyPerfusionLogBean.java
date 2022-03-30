package com.gidi.bio_console.bean;

public class KidneyPerfusionLogBean {
	private int index;
	private String kidneyName;
	private String startTime;
	private String leftKidneyWeight;
	private String rightKidneyWeight;
//	/**肝动脉灌注模式 0是恒压,1是搏动**/
	private String leftKidneyMode;
	/**2:恒压 3：恒流**/
	private String rightKidneyMode;
	public boolean ischeck = false;
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getKidneyName() {
		return kidneyName;
	}
	public void setKidneyName(String kidneyName) {
		this.kidneyName = kidneyName;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getLeftKidneyWeight() {
		return leftKidneyWeight;
	}
	public void setLeftKidneyWeight(String leftKidneyWeight) {
		this.leftKidneyWeight = leftKidneyWeight;
	}
	public String getRightKidneyWeight() {
		return rightKidneyWeight;
	}
	public void setRightKidneyWeight(String rightKidneyWeight) {
		this.rightKidneyWeight = rightKidneyWeight;
	}
	public String getLeftKidneyMode() {
		return leftKidneyMode;
	}
	public void setLeftKidneyMode(String leftKidneyMode) {
		this.leftKidneyMode = leftKidneyMode;
	}
	public String getRightKidneyMode() {
		return rightKidneyMode;
	}
	public void setRightKidneyMode(String rightKidneyMode) {
		this.rightKidneyMode = rightKidneyMode;
	}
	public boolean isCheck() {
		return ischeck;
	}
	public void setCheck(boolean ischeck) {
		this.ischeck = ischeck;
	}
	
	
	
}
