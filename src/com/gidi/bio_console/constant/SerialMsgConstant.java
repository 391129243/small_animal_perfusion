package com.gidi.bio_console.constant;

/****
 * 用于上报信息参数名称
 * @author Administrator
 *
 */
public class SerialMsgConstant {
	public static final String ARTERY_MODE = "ArtMode";
	public static final String ARTERY_PREAL = "ArtPreal";
	public static final String ARTERY_PMEAN = "ArtPMean";
	public static final String ARTERY_PDIAS = "ArtPDias";
	public static final String ARTERY_PSYST = "ArtPSyst";
	public static final String ARTERY_PFREQ = "ArtPFreq";
	public static final String ARTERY_FREAL = "ArtFlow";
	public static final String ARTERY_SPEED = "ArtSpeed";
	public static final String ARTERY_TEMP = "ArtTemp";
	public static final String ARTERY_BUBBLE = "ArtBubble";
	public static final String ARTERY_FMEAN = "ArtFmean";
	public static final String ARTERY_RESISTINDEX = "ArteryResistIndex";
	public static final String ARTERY_CNECT = "ArtCnect";
	public static final String ARTERY_SPEED_ALARM = "ArtAlarmSpeed";
	public static final String ARTERY_TARGET_PRESSURE = "ArtTarPressure";
	
	public static final String VEIN_MODE = "VeinMode";
	public static final String VEIN_PREAL = "VeinPreal";
	public static final String VEIN_FREAL = "VeinFlow";
	public static final String VEIN_SPEED ="VeinSpeed";
	public static final String VEIN_BUBBLE = "VeinBubble";
	public static final String VEIN_PMEAN = "VeinPmean";
	public static final String VEIN_PDIAS = "VeinPDias";
	public static final String VEIN_PSYST = "VeinPSyst";
	public static final String VEIN_PFREQ = "VeinPFreq";	
	
	public static final String VEIN_FMEAN = "VeinFmean";
	public static final String VEIN_CNECT = "VeinCnect";
	public static final String VEIN_RESISTINDEX = "VeinResistIndex";
	public static final String VEIN_SPEED_ALARM = "VeinAlarmSpeed";
	public static final String VEIN_TARGET = "VeinTarget";
	//胆汁
	public static final String EBILE = "ebile";
	public static final String TARGET_TEMP = "target_temp";
	
	public static final String ISSYSPAUSE = "isSysPause";
	public static final String ISTEMPCTRL = "isTempCtrl";
	public static final String CTRL_ARTERY_TARSPEED = "CtrlArtPumpSpeed";
	public static final String CTRL_VEIN_TARSPEED = "CtrlVeinPumpSpeed";
	public static final String MSGTIME = "msgTime";
	public static final String STSTEM_ITERRUPT = "system_interrupt";
		
	//电池电压 
	public static final String BATTERY_VOLTAGE = "battery_voltage";
	//电池电量
	public static final String BATTERY_LEVEL = "battery_level";
	//液位状态：0：没有水，1：有水
	public static final String LIQUID_LEVEL = "liquid_level";
	//电池状态 0:正常，1：异常
	public static final String BATTERY_STATE = "battery_state";
	//充电状态 0没有充电 1充电完成  2正在充电
	public static final String CHARGING_STATE = "charging_state";
	// 电源 使用电源 1表示AC 0表示 电池
	public static final String POWER_STATE = "power_state";
	// 急停开关 0急停中  1正常使用 
	public static final String EMERGENCY_STOP_SWITCH = "emergency_stop_switch";
	//1是表示安卓散热风扇状态 0异常 1正常  
	public static final String ANDROID_HEATDISS = "android_heatdiss";
	//2是表示电源散热风扇状态 0异常 1正常
	public static final String POWER_HEATDISS = "power_heatrdiss";
	//3是表示半导体散热风道状态 0表示闭合 1表示打开 
	public static final String SEMI_HEATDISS = "semi_heatdiss";
	
}
