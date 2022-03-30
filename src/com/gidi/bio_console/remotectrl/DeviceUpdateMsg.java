package com.gidi.bio_console.remotectrl;

import java.util.List;


/**设备更新信息**/
public class DeviceUpdateMsg{
	private int pro;
	private String uuidkey;
	private int did;
	private List<StatusObject> StatusList;
	public int getPro() {
		return pro;
	}
	public void setPro(int pro) {
		this.pro = pro;
	}
	public String getUuidkey() {
		return uuidkey;
	}
	public void setUuidkey(String uuidkey) {
		this.uuidkey = uuidkey;
	}
	public int getDid() {
		return did;
	}
	public void setDid(int did) {
		this.did = did;
	}
	
	public List<StatusObject> getStatusList() {
		return StatusList;
	}
	public void setStatusList(List<StatusObject> statusList) {
		StatusList = statusList;
	}
	public DeviceUpdateMsg(int pro, String uuidkey, int did,
			List<StatusObject> list) {
		super();
		this.pro = pro;
		this.uuidkey = uuidkey;
		this.did = did;
		this.StatusList = list;
	}
}
