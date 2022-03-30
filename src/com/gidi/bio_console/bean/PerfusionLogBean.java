package com.gidi.bio_console.bean;

public class PerfusionLogBean {

	private int index;
	private String liverName;
	private String startTime;
	private String liverWeight;
//	/**肝动脉灌注模式 0是恒压,1是搏动**/
	private String artery_mode;
	/**2:恒压 3：恒流**/
	private String vein_mode;
	public boolean ischeck = false;
	
	
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public boolean isIscheck() {
		return ischeck;
	}
	public void setIscheck(boolean ischeck) {
		this.ischeck = ischeck;
	}
	public String getLiverName() {
		return liverName;
	}
	public void setLiverName(String liverName) {
		this.liverName = liverName;
	}
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getArtery_mode() {
		return artery_mode;
	}
	public void setArtery_mode(String artery_mode) {
		this.artery_mode = artery_mode;
	}
	public String getVein_mode() {
		return vein_mode;
	}
	public void setVein_mode(String vein_mode) {
		this.vein_mode = vein_mode;
	}
	public String getLiverWeight() {
		return liverWeight;
	}
	public void setLiverWeight(String liverWeight) {
		this.liverWeight = liverWeight;
	}
	public boolean isCheck() {
		return ischeck;
	}
	public void setCheck(boolean ischeck) {
		this.ischeck = ischeck;
	}
	
}
