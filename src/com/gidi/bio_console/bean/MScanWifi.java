package com.gidi.bio_console.bean;

import android.net.wifi.ScanResult;

public class MScanWifi {
	private int wifiLevel;
	private ScanResult scanResult;
	private String securityMode;
	public MScanWifi(ScanResult scanresult, String securitymode, int wifilevel){
		this.scanResult = scanresult;
		this.securityMode = securitymode;
		this.wifiLevel = wifilevel;
	}
	public int getWifiLevel() {
		return wifiLevel;
	}
	public void setWifiLevel(int wifiLevel) {
		this.wifiLevel = wifiLevel;
	}
	public ScanResult getScanResult() {
		return scanResult;
	}
	public void setScanResult(ScanResult scanResult) {
		this.scanResult = scanResult;
	}
	public String getSecurityMode() {
		return securityMode;
	}
	public void setSecurityMode(String securityMode) {
		this.securityMode = securityMode;
	}
	
	

}
