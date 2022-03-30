package com.gidi.bio_console.remotectrl;

/**The message is the device connected to gateway **/
public class DeviceConctMsg {

	private int pro;
	private int did;
	private String uuidkey;
	
	public DeviceConctMsg(int pro, int did, String uuidkey) {
		super();
		this.pro = pro;
		this.did = did;
		this.uuidkey = uuidkey;
	}
	public int getPro() {
		return pro;
	}
	public void setPro(int pro) {
		this.pro = pro;
	}
	public int getDid() {
		return did;
	}
	public void setDid(int did) {
		this.did = did;
	}
	public String getUuidkey() {
		return uuidkey;
	}
	public void setUuidkey(String uuidkey) {
		this.uuidkey = uuidkey;
	}

}
