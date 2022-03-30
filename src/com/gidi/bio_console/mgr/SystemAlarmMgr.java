package com.gidi.bio_console.mgr;

import android.content.Context;
import com.gidi.bio_console.BioConsoleApplication;
import com.gidi.bio_console.R;
import com.gidi.bio_console.utils.StringUtil;

/***
 * system alarm/alert manager
 * @author Administrator
 *
 */
public class SystemAlarmMgr {
	
	public boolean isSysInterrput = false;

	/**门静脉压力警报状态 0:normal;1:low;2:high**/
	private int veinWarnPreStatus = 0;
	/**门静脉流量警报状态 0:normal;1:low;2:high**/
	private int veinWarnFlowStatus = 0;
	/**肝动脉压力警报状态  0:normal;1:low;2:high**/
	private int artWarnPreStatus = 0;
	/**门静脉流量警报状态 0:normal;1:low;2:high**/
	private int artWarnFlowStatus = 0;
	/** 反流报警 **/
	private boolean isArteryBackFlow = false;
	private boolean isVeinBackFlow = false;

	/**肝动脉压力报警状态 0：normal 1：disconnect**/
	private int artPreAlarmStatus = 1;
	/**门静脉压力报警状态 0：normal 1：disconnect**/
	private int veinPreAlarmStatus = 1;//

	/**是否肝动脉气泡报警**/
	private boolean isArteryBubAlert = false;
	/**门静脉气泡报警**/
	private boolean isVeinBubAlert = false;

	/**肝动脉流量报警状态 0：normal 1：disconnect 2：abnormal **/
	private int artFlowAlarmStatus = 1;
	/**门静脉流量报警状态 0：normal 1：disconnect 2：abnormal **/
	private int veinFlowAlarmStatus = 1;//
	/**肝动脉泵速报警状态**/
	private boolean artSpeedAlarm = false;
	private boolean veinSpeedAlarm = false;
	/**电池报警**/
	private boolean batteryAlarm = false;

	
		
	private Context mContext;
	private BioConsoleApplication mApplication;
	private static  SystemAlarmMgr mInstance;
	
	public static SystemAlarmMgr getInstance(BioConsoleApplication Application){
		if(null == mInstance){
			mInstance = new SystemAlarmMgr(Application);
		}
		return mInstance;
	}

	public SystemAlarmMgr( BioConsoleApplication Application){
		this.mContext = BioConsoleApplication.getInstance().getApplicationContext();
		this.mApplication = Application;
		
	}
	
	
	
	/*****************************警报信息*******************************/

	public int getVeinWarnPreStatus() {
		return veinWarnPreStatus;
	}

	public void setVeinWarnPreStatus(int veinWarnPreStatus) {
		this.veinWarnPreStatus = veinWarnPreStatus;
	}



	public int getVeinWarnFlowStatus() {
		return veinWarnFlowStatus;
	}

	public void setVeinWarnFlowStatus(int veinWarnFlowStatus) {
		this.veinWarnFlowStatus = veinWarnFlowStatus;
	}

	public int getArtWarnPreStatus() {
		return artWarnPreStatus;
	}

	public void setArtWarnPreStatus(int artWarnPreStatus) {
		this.artWarnPreStatus = artWarnPreStatus;
	}


	
	public int getArtWarnFlowStatus() {
		return artWarnFlowStatus;
	}

	public void setArtWarnFlowStatus(int artWarnFlowStatus) {
		this.artWarnFlowStatus = artWarnFlowStatus;
	}

	/**获取门静脉压力警报信息**/
	private String getVeinPreWarningMsg(){
		String veinPreAlertMsg = "";
		switch (veinWarnPreStatus) {
		case 0:
			veinPreAlertMsg = "";
			break;
		case 1:
			veinPreAlertMsg = mContext.getResources().getString(R.string.error_pre_vein_low_pre);
			break;
		case 2:
			veinPreAlertMsg = mContext.getResources().getString(R.string.error_pre_vein_high_pre);
		default:
			break;
		}
		return veinPreAlertMsg;
	}
	
	/**获取门静脉流量警报信息**/
	private String getVeinFlowWarningMsg(){
		String veinFlowWarningMsg = "";
		switch (veinWarnFlowStatus) {
		case 0:
			veinFlowWarningMsg = "";
			break;
		case 1:
			veinFlowWarningMsg = mContext.getResources().getString(R.string.error_flow_vein_low_flow);
			break;
		case 2:
			veinFlowWarningMsg = mContext.getResources().getString(R.string.error_flow_vein_high_flow);
		default:
			break;
		}
		return veinFlowWarningMsg;
	}
	
	
	/**获取肝动脉压力警报信息**/
	private String getArtPreWarningMsg(){
		String artPreWarningMsg = "";
		switch (artWarnPreStatus) {
		case 0:
			artPreWarningMsg = "";
			break;
		case 1:
			artPreWarningMsg = mContext.getResources().getString(R.string.error_pre_artery_low_pre);
			break;
		case 2:
			artPreWarningMsg = mContext.getResources().getString(R.string.error_pre_artery_high_pre);
		default:
			break;
		}
		return artPreWarningMsg;
	}
	
	/**获取门静脉流量警报信息**/
	private String getArtFlowWarningMsg(){
		String artFlowWarningMsg = "";
		switch (artWarnFlowStatus) {
		case 0:
			artFlowWarningMsg = "";
			break;
		case 1:
			artFlowWarningMsg = mContext.getResources().getString(R.string.error_flow_artery_low_flow);
			break;
		case 2:
			artFlowWarningMsg = mContext.getResources().getString(R.string.error_flow_artery_high_flow);
		default:
			break;
		}
		return artFlowWarningMsg;
	}
	

	/****
	 * 流量警报
	 * 1、肝动脉流量警报，门静脉正常
	 * 2、肝动脉流量正常、门静脉流量警报
	 * 3、肝动脉流量警报、门静脉流量警报
	 * 4、肝动脉流量正常、门静脉流量正常
	 * @return
	 */
	private String getSysFlowAlertMsg(){
		StringBuilder flowAlertMsg = new StringBuilder();
		if(null != mApplication){

			//有流量报警信息首先心跳是保持着
			if(mApplication.isArteryHeartConnect() && mApplication.isVeinHeartConnect()){
				if(artWarnFlowStatus != 0 && veinWarnFlowStatus == 0){
					flowAlertMsg.append(getArtFlowWarningMsg());
				}else if(artWarnFlowStatus == 0 && veinWarnFlowStatus != 0){
					flowAlertMsg.append(getVeinFlowWarningMsg());
				}else if(artWarnFlowStatus != 0 && veinWarnFlowStatus != 0){
					flowAlertMsg.append(getArtFlowWarningMsg()).append("/").append(getVeinFlowWarningMsg());
				}else{
					flowAlertMsg.append("");
				}
			}
		}
		
		return flowAlertMsg.toString();
	}
	

	/****
	 * 压力警报
	 * 1、肝动脉压力警报、门静脉压力正常
	 * 2、肝动脉压力正常、门静脉压力警报
	 * 3、肝动脉压力警报、门静脉压力警报
	 * 4、肝动脉压力正常、门静脉压力正常
	 * @return
	 */
	private String getSysPreWarningMsg(){
		StringBuilder mAlertPreMsg = new StringBuilder();
		if(null != mApplication){

			//有流量报警信息首先心跳是保持着
			if(mApplication.isArteryHeartConnect() && mApplication.isVeinHeartConnect()){
				if(artWarnPreStatus != 0 && veinWarnPreStatus == 0){
					mAlertPreMsg.append(getArtPreWarningMsg());
				}else if(artWarnPreStatus == 0 && veinWarnPreStatus != 0){
					mAlertPreMsg.append(getVeinPreWarningMsg());
				}else if(artWarnPreStatus != 0 && veinWarnPreStatus != 0){
					mAlertPreMsg.append(getArtPreWarningMsg()).append("/").append(getVeinPreWarningMsg());
				}else{
					mAlertPreMsg.append("");
				}
			}
		}
		
		return mAlertPreMsg.toString();
	}
	
	
	
	public boolean isSysInterrput() {
		return isSysInterrput;
	}

	public void setSysInterrput(boolean isSysInterrput) {
		this.isSysInterrput = isSysInterrput;
	}

		
	public boolean isArteryBackFlow() {
		return isArteryBackFlow;
	}

	public void setArteryBackFlow(boolean isArteryBackFlow) {
		this.isArteryBackFlow = isArteryBackFlow;
	}

	public boolean isVeinBackFlow() {
		return isVeinBackFlow;
	}

	public void setVeinBackFlow(boolean isVeinBackFlow) {
		this.isVeinBackFlow = isVeinBackFlow;
	}

	



	/**
	 * show the system alert message
	 * 警报信息
	 */
	public String getSysWarningMsg(){
		StringBuffer warningMsg = new StringBuffer();
		if(!StringUtil.isEmpty(getSysPreWarningMsg()) && StringUtil.isEmpty(getSysFlowAlertMsg())){
			warningMsg.append(getSysPreWarningMsg());			
		}else if(StringUtil.isEmpty(getSysPreWarningMsg()) && !StringUtil.isEmpty(getSysFlowAlertMsg())){
			warningMsg.append(getSysFlowAlertMsg());
		}else if(!StringUtil.isEmpty(getSysPreWarningMsg()) && !StringUtil.isEmpty(getSysFlowAlertMsg())){
			warningMsg.append(getSysFlowAlertMsg()).append(" / ").append(getSysPreWarningMsg());
		}
		return warningMsg.toString();
	}
	
	/******************************流量报警**********************************/
	
	
	public int getArtFlowAlarmStatus() {
		return artFlowAlarmStatus;
	}

	public void setArtFlowAlarmStatus(int artFlowAlarmStatus) {
		this.artFlowAlarmStatus = artFlowAlarmStatus;
	}

	public int getVeinFlowAlarmStatus() {
		return veinFlowAlarmStatus;
	}

	public void setVeinFlowAlarmStatus(int veinFlowAlarmStatus) {
		this.veinFlowAlarmStatus = veinFlowAlarmStatus;
	}
	
	/****
	 * 获取肝动脉流量报警状态
	 * @return
	 */
	private String getArtFlowAlarmMsg(){
		String alarmArtFlowMsg = "";
		switch (artFlowAlarmStatus) {
		case 0:
			alarmArtFlowMsg = "";
			break;
		case 1:
			//disconnect;
			alarmArtFlowMsg = mContext.getResources().getString(R.string.disconnect_pump_artrey_flow);
			break;
		case 2:
			//abnormal;
			alarmArtFlowMsg = mContext.getResources().getString(R.string.abnormal_pump_artery_flow);
			break;
		default:
			break;
		}
		return alarmArtFlowMsg;
	}
	
	/**
	 * 获取门静脉流量报警信息
	 * @return
	 */
	private String getVeinFlowAlarmMsg(){
		String alarmVeinFlowMsg = "";
		switch (veinFlowAlarmStatus) {
		case 0:
			alarmVeinFlowMsg = "";
			break;
		case 1:
			alarmVeinFlowMsg = mContext.getResources().getString(R.string.disconnect_pump_vein_flow);
			break;
		case 2:
			alarmVeinFlowMsg = mContext.getResources().getString(R.string.abnormal_pump_vein_flow);
			break;
		default:
			break;
		}
		return alarmVeinFlowMsg;
	}
	
	/***
	 * 流量报警
	 * 肝动脉流量：反流、disconnect、abnormal
	 * 门静脉流量：反流、disconnect、abnormal
	 * 1、流量报警首先心跳信息保存
	 * 2、肝动脉不正常、门静脉正常,判断门静脉是否反流
	 * 3、门静脉不正常、肝动脉正常，判断肝动脉是否反流
	 * 4、门静脉不正常、肝动脉不正常
	 * 5、门静脉正常、肝动脉正常：肝动脉反流，门静脉不反流;肝动脉不反流，门静脉反流；肝动脉反流，门静脉反流
	 * 
	 * 二、肝动脉断连，门静脉有心跳
	 * （2-1）门静脉正常,反流
	 * （2-2）门静脉不正常
	 * 三、门静脉断连、肝动脉有心跳
	 * (3-1)肝动脉正常，出现反流
	 * (3-2)肝动脉异常
	 * @return
	 */
	private String getSysFlowAlarmMsg(){
		StringBuilder flowAlarmMsg = new StringBuilder();
		if(null != mApplication){
			//1有流量报警信息首先心跳是保持着
			if(mApplication.isArteryHeartConnect() && mApplication.isVeinHeartConnect()){
				if(artFlowAlarmStatus != 0 && veinFlowAlarmStatus == 0){
					//2
					if(isVeinBackFlow){
						flowAlarmMsg.append(getArtFlowAlarmMsg()).append("/")
							.append(mContext.getResources().getString(R.string.error_vein_backflow));
					}else{
						flowAlarmMsg.append(getArtFlowAlarmMsg());
					}
				}else if(artFlowAlarmStatus == 0 && veinFlowAlarmStatus != 0){
					//3
					if(isArteryBackFlow){
						flowAlarmMsg.append(getVeinFlowAlarmMsg()).append("/")
							.append(mContext.getResources().getString(R.string.error_artery_backflow));
					}else{
						flowAlarmMsg.append(getVeinFlowAlarmMsg());
					}
				}else if(artFlowAlarmStatus != 0 && veinFlowAlarmStatus != 0){
					//4
					flowAlarmMsg.append(getArtFlowAlarmMsg()).append("/").append(getVeinFlowAlarmMsg());
				}else if(artFlowAlarmStatus == 0 && veinFlowAlarmStatus == 0){
					if(isArteryBackFlow && !isVeinBackFlow){
						flowAlarmMsg.append(mContext.getResources().getString(R.string.error_artery_backflow));
					}else if(!isArteryBackFlow && isVeinBackFlow){
						flowAlarmMsg.append(mContext.getResources().getString(R.string.error_vein_backflow));
					}else if(isArteryBackFlow && isVeinBackFlow){
						flowAlarmMsg.append(mContext.getResources().getString(R.string.error_artery_vein_backflow));
					}
				}				
			}else if(!mApplication.isArteryHeartConnect() && mApplication.isVeinHeartConnect()){
				if(veinFlowAlarmStatus == 0){
					if(isVeinBackFlow){
						flowAlarmMsg.append(mContext.getResources().getString(R.string.error_vein_backflow));
					}
				}else if(veinFlowAlarmStatus != 0){
					flowAlarmMsg.append(getVeinFlowAlarmMsg());
				}
			}else if(mApplication.isArteryHeartConnect() && !mApplication.isVeinHeartConnect()){
				if(artFlowAlarmStatus == 0){
					//肝动脉流量正常
					if(isArteryBackFlow){
						flowAlarmMsg.append(mContext.getResources().getString(R.string.error_artery_backflow));
					}
				}else if(artFlowAlarmStatus != 0){
					flowAlarmMsg.append(getArtFlowAlarmMsg());
				}
			}
			
		}	
		return flowAlarmMsg.toString();
	}
	
	/******************************电池电量报警*************************/
	public boolean isBatteryAlarm(){
		return batteryAlarm;
	}

	public void setBatteryAlarm(boolean batteryAlarm){
		this.batteryAlarm = batteryAlarm;
	}
	
	private String getAlarmBatteryMsg(){
		String alarmBattery = "";
		if(batteryAlarm){
			alarmBattery =  mContext.getResources().getString(R.string.alarm_msg_battery_low);
		}
		return alarmBattery;
	}	

		
	/******************************泵速报警****************************/
	public boolean isArtSpeedAlarm() {
		return artSpeedAlarm;
	}

	public void setArtSpeedAlarm(boolean artSpeedAlarm) {
		this.artSpeedAlarm = artSpeedAlarm;
	}

	public boolean isVeinSpeedAlarm() {
		return veinSpeedAlarm;
	}

	public void setVeinSpeedAlarm(boolean veinSpeedAlarm) {
		this.veinSpeedAlarm = veinSpeedAlarm;
	}

	/**泵速报警信息
	 * 1、肝动脉没有断连，门静脉没有断连
	 * 1-1、肝动脉泵速报警、门静脉正常
	 * 1-2、肝动脉泵速正常、门静脉泵速报警
	 * 1-3、肝动脉、门静脉泵速异常
	 * 2、g肝动脉断连，门静脉没有断连
	 * 2-1 门静脉泵速报警
	 * 3、肝动脉没有断连，门静脉断连
	 * 3-1 肝动脉泵速报警
	 * **/
	private String getAlarmSpeedMsg(){
		StringBuilder alarmSpeedMsg = new StringBuilder();
		if(null != mApplication){
			//首先心跳是保持着
			if(mApplication.isArteryHeartConnect() && mApplication.isVeinHeartConnect()){
				if(artSpeedAlarm && !veinSpeedAlarm){
					alarmSpeedMsg.append(mContext.getResources().getString(R.string.alarm_msg_pump_artery_speed));
				}else if(!artSpeedAlarm && veinSpeedAlarm){
					alarmSpeedMsg.append(mContext.getResources().getString(R.string.alarm_msg_pump_vein_speed));
				}else if(artSpeedAlarm && veinSpeedAlarm){
					alarmSpeedMsg.append(mContext.getResources().getString(R.string.alarm_msg_pump_artery_vein_speed));
				}else if(!artSpeedAlarm && !veinSpeedAlarm){
					alarmSpeedMsg.append("");
				}
			}else if(!mApplication.isArteryHeartConnect() && mApplication.isVeinHeartConnect()){
				if(veinSpeedAlarm){
					alarmSpeedMsg.append(mContext.getResources().getString(R.string.alarm_msg_pump_vein_speed));
				}else{
					alarmSpeedMsg.append("");
				}
			}else if(mApplication.isArteryHeartConnect() && !mApplication.isVeinHeartConnect()){
				if(artSpeedAlarm){
					alarmSpeedMsg.append(mContext.getResources().getString(R.string.alarm_msg_pump_artery_speed));
				}else{
					alarmSpeedMsg.append("");
				}
			}else{
				alarmSpeedMsg.append("");
			}
		}		
		return alarmSpeedMsg.toString();
	}
	
	/**********************************气泡报警********************************/
	public boolean isArteryBubAlert() {
		return isArteryBubAlert;
	}


	public void setArteryBubAlert(boolean mIsArteryBubAlert) {
		this.isArteryBubAlert = mIsArteryBubAlert;
	}


	public boolean isVeinBubAlert() {
		return isVeinBubAlert;
	}

	public void setVeinBubAlert(boolean mIsVeinBubAlert) {
		this.isVeinBubAlert = mIsVeinBubAlert;
	}
	
	/****
	 * 气泡报警
	 * 1、在肝动脉和门静脉没有断连的情况下
	 * 1-1肝动脉有气泡、门静脉没有气泡
	 * 1-2肝动脉没有气泡。门静脉有气泡
	 * 1-3肝动脉有气泡、门静脉有气泡
	 * 1-4，都没有气泡
	 * 
	 * 2、肝动脉断连，门静脉没有断连
	 * 2-1 门静脉有气泡
	 * 
	 * 3、肝动脉没有断连，门静脉断连
	 */
	private String getAlarmBubbleMsg(){
		StringBuilder alarmBubbleMsg = new StringBuilder();
		if(null != mApplication){
			if(mApplication.isArteryHeartConnect() && mApplication.isVeinHeartConnect()){
				if(isArteryBubAlert && !isVeinBubAlert){
					alarmBubbleMsg.append(mContext.getString(R.string.error_bubble_one));
				}else if(!isArteryBubAlert && isVeinBubAlert){
					alarmBubbleMsg.append(mContext.getResources().getString(R.string.error_bubble_two));
				}else if(isArteryBubAlert && isVeinBubAlert){
					alarmBubbleMsg.append(mContext.getResources().getString(R.string.error_bubble_artery_vein));
				}else {
					alarmBubbleMsg.append("");
				}
			}else if(!mApplication.isArteryHeartConnect() && mApplication.isVeinHeartConnect()){
				if(isVeinBubAlert){
					alarmBubbleMsg.append(mContext.getResources().getString(R.string.error_bubble_two));
				}else{
					alarmBubbleMsg.append("");
				}
			}else if(mApplication.isArteryHeartConnect() && !mApplication.isVeinHeartConnect()){
				if(isArteryBubAlert){
					alarmBubbleMsg.append(mContext.getResources().getString(R.string.error_bubble_one));
				}else{
					alarmBubbleMsg.append("");
				}
			}else if(!mApplication.isArteryHeartConnect() && !mApplication.isVeinHeartConnect()){
				alarmBubbleMsg.append("");
			}
		}
		return alarmBubbleMsg.toString();
	}
	
	/***************************压力报警********************************/
	
	
	
	public int getArtPreAlarmStatus() {
		return artPreAlarmStatus;
	}

	public void setArtPreAlarmStatus(int artPreAlarmStatus) {
		this.artPreAlarmStatus = artPreAlarmStatus;
	}

	public int getVeinPreAlarmStatus() {
		return veinPreAlarmStatus;
	}

	public void setVeinPreAlarmStatus(int veinPreAlarmStatus) {
		this.veinPreAlarmStatus = veinPreAlarmStatus;
	}
	
	
	/**
	 * 压力断连报警与压力管路关闭信息
	 * 1、肝动脉、门静脉未断连,心跳信息都在
	 * 1-1、肝动脉压力断连，门静脉压力断连
	 * 1-2、肝动脉压力正常、门静脉压力断连
	 * 1-3、肝动脉压力断连、门静脉压力正常
	 * 2、肝动脉断连，门静脉未断连，门静脉心跳消息不在
	 * 2-1 门静脉压力异常
	 * 3、肝动脉未断连、门静脉断连
	 * 3-1、肝动脉压力异常
	 * @return
	 */
	private String getSysAlarmPreMsg(){
		StringBuilder alarmpreMsg = new StringBuilder();
		if(null != mApplication){
			if(mApplication.isArteryHeartConnect() && mApplication.isVeinHeartConnect()){
				if(veinPreAlarmStatus!= 0 && artPreAlarmStatus != 0){
					alarmpreMsg.append(mContext.getResources().getString(R.string.disconnect_pump_artery_vein_pressure));
				}else if(artPreAlarmStatus !=0  && veinPreAlarmStatus == 0){
					alarmpreMsg.append(mContext.getResources().getString(R.string.disconnect_pump_artrey_pressure));
				}else if(artPreAlarmStatus ==0  && veinPreAlarmStatus != 0){
					alarmpreMsg.append(mContext.getResources().getString(R.string.disconnect_pump_vein_pressure));
				}else{
					alarmpreMsg.append("");
				}
				
			}else if(!mApplication.isArteryHeartConnect() && mApplication.isVeinHeartConnect()){
				if(veinPreAlarmStatus != 0){
					alarmpreMsg.append(mContext.getResources().getString(R.string.disconnect_pump_vein_pressure));
				}
				
			}else if(mApplication.isArteryHeartConnect() && !mApplication.isVeinHeartConnect()){
				if(artPreAlarmStatus !=0 ){
					alarmpreMsg.append(mContext.getResources().getString(R.string.disconnect_pump_artrey_pressure));
				}
				
			}else {
				alarmpreMsg.append("");
			}			
		}	
		return alarmpreMsg.toString();
	}
	
	/********************************温度报警******************************************/
	//0，normal正常；1：abnormal异常；2：overtemperature过高
	private int tempStatus = 0;
	
	
	public int getTempStatus() {
		return tempStatus;
	}

	public void setTempStatus(int tempStatus) {
		this.tempStatus = tempStatus;
	}

	private String getSysAlarmTempMsg(){
		StringBuffer alarmTempMsg = new StringBuffer();
		if(tempStatus == 1){
			alarmTempMsg.append(mContext.getResources().getString(R.string.error_temp_abnormal));
		}		
		if(tempStatus == 2){
			alarmTempMsg.append(mContext.getResources().getString(R.string.error_temp_over));
		}
		
		if(tempStatus == 0){
			alarmTempMsg.append("");
		}
		return alarmTempMsg.toString();
	}
	
	/**************************************设备状态报警*******************************************/
	/**液位报警**/
	private boolean levelAlarm = false;
	/**急停报警**/
	private boolean emergencyStopAlarm = false;
	/**安卓散热报警**/
	private boolean androidDissAlarm = false;
	/**电源散热报警**/
	private boolean powerDissAlarm = false;
	/**半导体散热报警**/
	private boolean semiDissAlarm = false;
	private String getSysDeviceStateAlarmMsg(){
		StringBuffer alarmDeviceMsg = new StringBuffer("");
		if(levelAlarm){
			alarmDeviceMsg.append("/").append(mContext.getResources().getString(R.string.error_low_water_reservoir));
		}
		if(emergencyStopAlarm){
			alarmDeviceMsg.append("/").append(mContext.getResources().getString(R.string.error_emergency_stop));
		}
		if(androidDissAlarm){
			alarmDeviceMsg.append("/").append(mContext.getResources().getString(R.string.error_android_heat_dissipation));
		}
		if(powerDissAlarm){
			alarmDeviceMsg.append("/").append(mContext.getResources().getString(R.string.error_power_heat_dissipation));
		}
		if(semiDissAlarm){
			alarmDeviceMsg.append("/").append(mContext.getResources().getString(R.string.error_semi_heat_dissipation));
		}
		return alarmDeviceMsg.toString();
	}
	
	
	public boolean isLevelAlarm() {
		return levelAlarm;
	}

	public void setLevelAlarm(boolean levelAlarm) {
		this.levelAlarm = levelAlarm;
	}

	public boolean isEmergencyStopAlarm() {
		return emergencyStopAlarm;
	}

	public void setEmergencyStopAlarm(boolean emergencyStopAlarm) {
		this.emergencyStopAlarm = emergencyStopAlarm;
	}
	
	

	public boolean isAndroidDissAlarm() {
		return androidDissAlarm;
	}

	public void setAndroidDissAlarm(boolean androidDissAlarm) {
		this.androidDissAlarm = androidDissAlarm;
	}

	public boolean isPowerDissAlarm() {
		return powerDissAlarm;
	}

	public void setPowerDissAlarm(boolean powerDissAlarm) {
		this.powerDissAlarm = powerDissAlarm;
	}

	public boolean isSemiDissAlarm() {
		return semiDissAlarm;
	}

	public void setSemiDissAlarm(boolean semiDissAlarm) {
		this.semiDissAlarm = semiDissAlarm;
	}

	/**************************************************************************/
	/***
	 * 心跳消息报警
	 * 1、肝动脉断连，门静脉断连
	 * 2、肝动脉没有断连，门静脉断连
	 * 3、肝动脉断连，门静脉没有断连
	 * @return
	 */
	private String getSysCnectMsg(){
		StringBuffer disconnctMsg = new StringBuffer();
		if(null != mApplication){
			if(!mApplication.isArteryHeartConnect() && !mApplication.isVeinHeartConnect()){
				disconnctMsg.append(mContext.getString(R.string.disconnect_pump_artery_vein));
			}else if(mApplication.isArteryHeartConnect() && !mApplication.isVeinHeartConnect()){
				disconnctMsg.append(mContext.getString(R.string.disconnect_pump_vein));
			}else if(!mApplication.isArteryHeartConnect() && mApplication.isVeinHeartConnect()){
				disconnctMsg.append(mContext.getString(R.string.disconnect_pump_artery));
			}else{
				disconnctMsg.append("");
			}
		}	
		return disconnctMsg.toString();
	}
	
	/************************************************************************************/	
	
	
	
	/**
	 * show the system alarm message
	 * 报警消息
	 * 1、断连信息为空：表示都没有断连
	 * 1-1 压力报警、气泡断连、泵速报警、流量报警(都不为空)ABCD
	 * 1-2 压力不报警、气泡断连、泵速报警、流量报警BCD
	 * 1-3压力报警、流量不报警、气泡报警、泵速报警、ACD
	 * 1-4压力报警、流量报警、气泡不报警、泵速报警ABD
	 * 1-5压力报警、流量报警、气泡报警、泵速不报警ABC
	 * 1-6 压力不报警、流量不报警、气泡报警、泵速报警CD
	 * 1-7压力不报警、流量报警、气泡不报警、泵速报警BD
	 * 1-8 压力不报警、流量报警、气泡报警、泵速不报警BC
	 * 
	 * 
	 * 1-9压力报警、流量不报警、气泡不报警、泵速报警AD
	 * 1-10压力报警、流量不报警、气泡报警、泵速不报警AC
	 * 1-11压力报警、流量报警、气泡不报警、泵速不报警AB
	 * 
	 * 1-12压力报警、流量不报警、气泡不报警、泵速不报警A
	 * 1-13压力不报警、流量报警、气泡不报警、泵速不报警B
	 * 1-14压力不报警、流量不报警、气泡报警、泵速不报警C
	 * 1-15压力不报警、流量不报警、气泡不报警、泵速报警
	 * @return
	 */
	private String getAllAlarmMsg(){
		StringBuilder msg = new StringBuilder();
		if(!StringUtil.isEmpty(getSysAlarmPreMsg()) && !StringUtil.isEmpty(getSysFlowAlarmMsg())
				&& !StringUtil.isEmpty(getAlarmBubbleMsg()) && !StringUtil.isEmpty(getAlarmSpeedMsg())){
			//1-1ABCD力报警、气泡断连、泵速报警、流量报警、
			msg.append(getSysAlarmPreMsg()).append("/").append(getSysFlowAlarmMsg()).append("/")
				.append(getAlarmBubbleMsg()).append("/").append(getAlarmSpeedMsg());
			
		}else if(StringUtil.isEmpty(getSysAlarmPreMsg()) && !StringUtil.isEmpty(getSysFlowAlarmMsg())
				&& !StringUtil.isEmpty(getAlarmBubbleMsg()) && !StringUtil.isEmpty(getAlarmSpeedMsg())){
			//1-2BCD
			msg.append(getSysFlowAlarmMsg()).append("/")
				.append(getAlarmBubbleMsg()).append("/").append(getAlarmSpeedMsg());
			
		}else if(!StringUtil.isEmpty(getSysAlarmPreMsg()) && StringUtil.isEmpty(getSysFlowAlarmMsg())
				&& !StringUtil.isEmpty(getAlarmBubbleMsg()) && !StringUtil.isEmpty(getAlarmSpeedMsg())){
			//1-3ACD
			msg.append(getSysAlarmPreMsg()).append("/")
				.append(getAlarmBubbleMsg()).append("/").append(getAlarmSpeedMsg());
			
		}else if(!StringUtil.isEmpty(getSysAlarmPreMsg()) && !StringUtil.isEmpty(getSysFlowAlarmMsg())
				&& StringUtil.isEmpty(getAlarmBubbleMsg()) && !StringUtil.isEmpty(getAlarmSpeedMsg())){
			//1-4ABD
			msg.append(getSysAlarmPreMsg()).append("/").append(getSysFlowAlarmMsg()).append("/")
				.append(getAlarmSpeedMsg());
			
		}else if(!StringUtil.isEmpty(getSysAlarmPreMsg()) && !StringUtil.isEmpty(getSysFlowAlarmMsg())
				&& !StringUtil.isEmpty(getAlarmBubbleMsg()) && StringUtil.isEmpty(getAlarmSpeedMsg())){
			//1-5ABC
			msg.append(getSysAlarmPreMsg()).append("/").append(getSysFlowAlarmMsg()).append("/")
				.append(getAlarmBubbleMsg());
		
		}else if(StringUtil.isEmpty(getSysAlarmPreMsg()) && StringUtil.isEmpty(getSysFlowAlarmMsg())
				&& !StringUtil.isEmpty(getAlarmBubbleMsg()) && !StringUtil.isEmpty(getAlarmSpeedMsg())){
			//1-6CD
			msg.append(getAlarmBubbleMsg()).append("/").append(getAlarmSpeedMsg());
			
		}else if(StringUtil.isEmpty(getSysAlarmPreMsg()) && !StringUtil.isEmpty(getSysFlowAlarmMsg())
				&& StringUtil.isEmpty(getAlarmBubbleMsg()) && !StringUtil.isEmpty(getAlarmSpeedMsg())){
			//1-7BD
			msg.append(getSysFlowAlarmMsg()).append("/").append(getAlarmSpeedMsg());
			
		}else if(StringUtil.isEmpty(getSysAlarmPreMsg()) && !StringUtil.isEmpty(getSysFlowAlarmMsg())
				&& !StringUtil.isEmpty(getAlarmBubbleMsg()) && StringUtil.isEmpty(getAlarmSpeedMsg())){
			//1-8BC
			msg.append(getSysFlowAlarmMsg()).append("/").append(getAlarmBubbleMsg());
			
		}else if(!StringUtil.isEmpty(getSysAlarmPreMsg()) && StringUtil.isEmpty(getSysFlowAlarmMsg())
				&& StringUtil.isEmpty(getAlarmBubbleMsg()) && !StringUtil.isEmpty(getAlarmSpeedMsg())){
			//1-9AD
			msg.append(getSysAlarmPreMsg()).append("/").append(getAlarmSpeedMsg());
			
		}else if(!StringUtil.isEmpty(getSysAlarmPreMsg()) && StringUtil.isEmpty(getSysFlowAlarmMsg())
				&& !StringUtil.isEmpty(getAlarmBubbleMsg()) && StringUtil.isEmpty(getAlarmSpeedMsg())){
			//1-10AC
			msg.append(getSysAlarmPreMsg()).append("/").append(getAlarmBubbleMsg());
		}else if(!StringUtil.isEmpty(getSysAlarmPreMsg()) && !StringUtil.isEmpty(getSysFlowAlarmMsg())
				&& StringUtil.isEmpty(getAlarmBubbleMsg()) && StringUtil.isEmpty(getAlarmSpeedMsg())){
			//1-11AB	
			msg.append(getSysAlarmPreMsg()).append("/").append(getSysFlowAlarmMsg());
		}else if(!StringUtil.isEmpty(getSysAlarmPreMsg()) && StringUtil.isEmpty(getSysFlowAlarmMsg())
				&& StringUtil.isEmpty(getAlarmBubbleMsg()) && StringUtil.isEmpty(getAlarmSpeedMsg())){
			//1-12A
			msg.append(getSysAlarmPreMsg());
			
		}else if(StringUtil.isEmpty(getSysAlarmPreMsg()) && !StringUtil.isEmpty(getSysFlowAlarmMsg())
				&& StringUtil.isEmpty(getAlarmBubbleMsg()) && StringUtil.isEmpty(getAlarmSpeedMsg())){
			//1-13B
			msg.append(getSysFlowAlarmMsg());
		}else if(StringUtil.isEmpty(getSysAlarmPreMsg()) && StringUtil.isEmpty(getSysFlowAlarmMsg())
				&& !StringUtil.isEmpty(getAlarmBubbleMsg()) && StringUtil.isEmpty(getAlarmSpeedMsg())){
			//1-14C
			msg.append(getAlarmBubbleMsg());
		}else if(StringUtil.isEmpty(getSysAlarmPreMsg()) && StringUtil.isEmpty(getSysFlowAlarmMsg())
				&& StringUtil.isEmpty(getAlarmBubbleMsg()) && !StringUtil.isEmpty(getAlarmSpeedMsg())){
			//1-15D
			msg.append(getAlarmSpeedMsg());
		}else{
			msg.append("");
		}
		return msg.toString();
	}
	
	
	public String getSysAlarmMsg(){
		StringBuffer alarmMsg = new StringBuffer();
		if(StringUtil.isEmpty(getSysCnectMsg())){
			//断连信息为空，则表示不断连
			if(StringUtil.isEmpty(getSysAlarmTempMsg())){
				if(StringUtil.isEmpty(getAlarmBatteryMsg())){
					alarmMsg.append(getAllAlarmMsg());
				}else{
					if(StringUtil.isEmpty(getAllAlarmMsg())){
						alarmMsg.append(getAlarmBatteryMsg());
					}else{
						alarmMsg.append(getAllAlarmMsg()).append("/").append(getAlarmBatteryMsg());
					}
				}

			}else{
				if(StringUtil.isEmpty(getAlarmBatteryMsg())){
					if(StringUtil.isEmpty(getAllAlarmMsg())){
						alarmMsg.append(getSysAlarmTempMsg());
					}else{
						alarmMsg.append(getAllAlarmMsg()).append("/").append(getSysAlarmTempMsg());
					}
				}else{
					if(StringUtil.isEmpty(getAllAlarmMsg())){
						alarmMsg.append(getSysAlarmTempMsg()).append("/").append(getAlarmBatteryMsg());
					}else{
						alarmMsg.append(getAllAlarmMsg()).append("/").append(getSysAlarmTempMsg())
						.append("/").append(getAlarmBatteryMsg());
					}
				}
				
			}
			
		}else{
			//不为空，则表示断连
			if(getAllAlarmMsg().equals("")){
				if(StringUtil.isEmpty(getAlarmBatteryMsg())){
					alarmMsg.append(getSysCnectMsg());
				}else{
					alarmMsg.append(getSysCnectMsg()).append("/").append(getAlarmBatteryMsg());
				}
				
			}else{
				if(StringUtil.isEmpty(getAlarmBatteryMsg())){
					alarmMsg.append(getSysCnectMsg()).append("/").append(getAllAlarmMsg())
					.append("/").append(getSysAlarmTempMsg());
				}else{
					alarmMsg.append(getSysCnectMsg()).append("/").append(getAllAlarmMsg())
						.append("/").append(getSysAlarmTempMsg()).append("/").append(getAlarmBatteryMsg());
				}
			}
			
		}	
		
		if(!StringUtil.isEmpty(getSysDeviceStateAlarmMsg())){
			alarmMsg.append(getSysDeviceStateAlarmMsg());
		}
		String alarmMessage = "";
		if(alarmMsg.toString().startsWith("/")){
			alarmMessage = alarmMsg.toString().substring(1, alarmMsg.toString().length());			
		}else{
			alarmMessage = alarmMsg.toString();
		}
				
		return alarmMessage;
	}
}
