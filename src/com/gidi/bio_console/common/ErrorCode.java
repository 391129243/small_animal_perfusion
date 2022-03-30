package com.gidi.bio_console.common;

public interface ErrorCode {
	int E_INTERRUPT_SYSTEM = 0;
	int E_BUBBLE_ARTERY_ALERT_MSG = 1;
	int E_BUBBLE_VEIN_ALERT_MSG = 2;
	int E_FLOW_ONE_MIN_AlARM_MSG = 3;
	int E_FLOW_ONE_MAX_AlARM_MSG = 4;
	int E_FLOW_TWO_MIN_AlARM_MSG = 5;
	int E_FLOW_TWO_MAX_AlARM_MSG = 6;
	int E_PESSURE_ONE_MIN_AlARM_MSG = 7;
	int E_PESSURE_ONE_MAX_AlARM_MSG = 8;
	int E_PESSURE_TWO_MIN_AlARM_MSG = 9;
	int E_PESSURE_TWO_MAX_AlARM_MSG = 10;
	//连接泵1和泵2心跳信息
	int E_DISCONNECT_ARTERY_MSG = 11;
	int E_DISCONNECT_VEIN_MSG = 12;
	//压力信息断连
	int E_DISCONNECT_PRESSURE_ARTERY_MSG = 13;
	int E_DISCONNECT_PRESSURE_VEIN_MSG = 14;
	//流量信息断连
	int E_DISCONNECT_FLOW_ARTERY_MSG = 15;
	int E_DISCONNECT_FLOW_VEIN_MSG = 16;
	//流量反流
	int E_FLOW_ONE_BACK_FLOW_MSG = 17;
	int E_FLOW_TWO_BACK_FLOW_MSG = 18;
	//压力传感器旋钮未按，速度一直增长
	int E_PRESSURE_ONE_CLOSE_MSG = 19;
	int E_PRESSURE_TWO_CLOSE_MSG = 20;

	int E_ABNORMAL_FLOW_ARTERY_MSG = 21;
	int E_ABNORMAL_FLOW_VEIN_MSG = 22;
	//温度异常
	int E_ABNORMAL_TEMP = 23;
	//温度过高
	int E_OVER_TEMP = 24;
	//泵速异常
	int E_SPEED_ARTERY_ABNORMAL = 25;
	int E_SPEED_VEIN_ABNORMAL = 26;
	//液位报警
	int E_ALARM_LIQUID_LEVEL = 27;
	int E_SYSTEM_EMERGENCY = 28;
	int E_LOWER_BATTERY = 29;	
	//散热报警
	int E_ANDROID_HEATDISS = 30;
	int E_POWER_HEATDISS = 31;
	int E_SEMI_HEARDIS = 32;
}