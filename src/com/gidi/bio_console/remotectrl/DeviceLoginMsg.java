package com.gidi.bio_console.remotectrl;


public class DeviceLoginMsg {

	/**控制码**/
	private int pro;
	private String devicename;
	private String uuidkey;
	private String passwd;
		
	public DeviceLoginMsg(int pro, String devicename, String uuidkey, String passwd){
		this.pro = pro;
		this.devicename = devicename;
		this.uuidkey = uuidkey;
		this.passwd = passwd;
	}

	public int getPro() {
		return pro;
	}

	public void setPro(int pro) {
		this.pro = pro;
	}

	public String getDevicename() {
		return devicename;
	}

	public void setDevicename(String devicename) {
		this.devicename = devicename;
	}

	public String getUuidkey() {
		return uuidkey;
	}

	public void setUuidkey(String uuidkey) {
		this.uuidkey = uuidkey;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
}
