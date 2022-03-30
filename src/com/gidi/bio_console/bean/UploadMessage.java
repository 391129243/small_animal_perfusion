package com.gidi.bio_console.bean;

import com.google.gson.annotations.SerializedName;


public class UploadMessage {
	//灌注模式
	@SerializedName("arteryMode")
	private int arteryMode;
	
	@SerializedName("arteryMode")
	private int veinMode;
	
	@SerializedName("ArtPreal")
	private String ArtPreal;
	
	@SerializedName("ArtPMean")
	private String ArtPMean;
	
	@SerializedName("ArtPDias")
	private String ArtPDias;
	
	@SerializedName("ArtPSyst")
	private String ArtPSyst;
	
	@SerializedName("ArtPFreq")
	private String ArtPFreq;
	
	@SerializedName("ArtFlow")
	private String ArtFlow;
	
	@SerializedName("ArtSpeed")
	private String ArtSpeed;
	
	@SerializedName("ArtTemp")
	private String ArtTemp;
	
	@SerializedName("ArtBubble")
	private String ArtBubble;
	
	@SerializedName("VeinPreal")
	private String VeinPreal;
	
	@SerializedName("VeinFlow")
	private String VeinFlow;
	
	@SerializedName("VeinSpeed")
	private String VeinSpeed;
	
	@SerializedName("VeinTemp")
	private String VeinTemp;
	
	@SerializedName("VeinBubble")
	private String VeinBubble;
	
	@SerializedName("isSysPause")
	private String isSysPause;
	
	@SerializedName("isTempCtrl")
	private String isTempCtrl;
	//阻力指数
	private String arteryResitIndex;
	
	private String veinResistIndex;

	
	public int getArteryMode() {
		return arteryMode;
	}
	public void setArteryMode(int arteryMode) {
		this.arteryMode = arteryMode;
	}
	public int getVeinMode() {
		return veinMode;
	}
	public void setVeinMode(int veinMode) {
		this.veinMode = veinMode;
	}
	public String getArtPreal() {
		return ArtPreal;
	}
	public void setArtPreal(String artPreal) {
		ArtPreal = artPreal;
	}
	public String getArtPMean() {
		return ArtPMean;
	}
	public void setArtPMean(String artPMean) {
		ArtPMean = artPMean;
	}
	public String getArtPDias() {
		return ArtPDias;
	}
	public void setArtPDias(String artPDias) {
		ArtPDias = artPDias;
	}
	public String getArtPSyst() {
		return ArtPSyst;
	}
	public void setArtPSyst(String artPSyst) {
		ArtPSyst = artPSyst;
	}
	public String getArtPFreq() {
		return ArtPFreq;
	}
	public void setArtPFreq(String artPFreq) {
		ArtPFreq = artPFreq;
	}
	public String getArtFlow() {
		return ArtFlow;
	}
	public void setArtFlow(String artFlow) {
		ArtFlow = artFlow;
	}
	public String getArtSpeed() {
		return ArtSpeed;
	}
	public void setArtSpeed(String artSpeed) {
		ArtSpeed = artSpeed;
	}
	public String getArtTemp() {
		return ArtTemp;
	}
	public void setArtTemp(String artTemp) {
		ArtTemp = artTemp;
	}
	public String getArtBubble() {
		return ArtBubble;
	}
	public void setArtBubble(String artBubble) {
		ArtBubble = artBubble;
	}
	public String getVeinPreal() {
		return VeinPreal;
	}
	public void setVeinPreal(String veinPreal) {
		VeinPreal = veinPreal;
	}
	public String getVeinFlow() {
		return VeinFlow;
	}
	public void setVeinFlow(String veinFlow) {
		VeinFlow = veinFlow;
	}
	public String getVeinSpeed() {
		return VeinSpeed;
	}
	public void setVeinSpeed(String veinSpeed) {
		VeinSpeed = veinSpeed;
	}
	public String getVeinTemp() {
		return VeinTemp;
	}
	public void setVeinTemp(String veinTemp) {
		VeinTemp = veinTemp;
	}
	public String getVeinBubble() {
		return VeinBubble;
	}
	public void setVeinBubble(String veinBubble) {
		VeinBubble = veinBubble;
	}
	
	
	public String getArteryResitIndex() {
		return arteryResitIndex;
	}
	public void setArteryResitIndex(String arteryResitIndex) {
		this.arteryResitIndex = arteryResitIndex;
	}
	
	public String getVeinResistIndex() {
		return veinResistIndex;
	}
	public void setVeinResistIndex(String veinResistIndex) {
		this.veinResistIndex = veinResistIndex;
	}
	public String getIsSysPause() {
		return isSysPause;
	}
	public void setIsSysPause(String isSysPause) {
		this.isSysPause = isSysPause;
	}
	public String getIsTempCtrl() {
		return isTempCtrl;
	}
	public void setIsTempCtrl(String isTempCtrl) {
		this.isTempCtrl = isTempCtrl;
	}	
}
