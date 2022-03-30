package com.gidi.bio_console.serialport;


public class SerialPortInstruct {
	//压力调零
	public static final String SET_ZERO_PRESSURE_ONE = "pump1_Pzero_xxxxx";	
	public static final String SET_ZERO_PRESSURE_TWO = "pump2_Pzero_xxxxx";	
	//流量调零
	public static final String SET_ZERO_FLOW_ONE = "pump1_Fzero_xxxxx";	
	public static final String SET_ZERO_FLOW_TWO = "pump2_Fzero_xxxxx";
	//设置泵速
	/**set pump target pump speed**/
	public static final String SET_PUMP_SPEED_ONE = "pump1_Starg_xxxxx";	
	public static final String SET_PUMP_SPEED_TWO = "pump2_Starg_xxxxx";

	//设置目标压力
	public static final String SET_TARGET_PRESSURE_ONE = "pump1_Ptarg_xxxxx";
	public static final String SET_TARGET_PRESSURE_TWO = "pump2_Ptarg_xxxxx";	
	//设置搏动压力
	public static final String SET_TARGET_MINMAX_PRESSURE_ONE = "pump1_PtarB_xxxxx";
	//设置目标流量
	public static final String SET_TARGET_FLOW_ONE = "pump1_Ftarg_xxxxx";
	public static final String SET_TARGET_FLOW_TWO = "pump2_Ftarg_xxxxx";
	
	/**set pump target temp 设置目标温度**/
	public static final String SET_TARGET_NORMAL_TEMPERUTURE = "pump1_TtarN_xxxxx";
	//cold temp
	public static final String SET_TARGET_COLD_TEMPERUTURE = "pump1_TtarC_xxxxx";
		
	/**stop**/
	public static final String STOP_SYSTEM_ONE = "pump1_STOPx_xxxxx_0";
	
	public static final String STOP_SYSTEM_TWO = "pump2_STOPx_xxxxx_0";
	/**close temperature control**/
	public static final String CLOSE_TEMP_CTRL = "pump1_Ttarg_close_1";
	/**转速限制**/
	public static final String LIMIT_SPEED_ONE = "pump1_Slimi_xxxxx";
	
	public static final String LIMIT_SPEED_TWO = "pump2_Slimi_xxxxx";
	/**alarm high priority**/
	public static final String SET_HIGH_PRIORITY_ALARM = "pump1_Alarm_err03_0";
	
	public static final String SET_MEDIUM_PRIORITY_ALARM = "pump1_Alarm_err02_0";
	
	public static final String SET_LOW_PRIORITY_ALARM = "pump1_Alarm_err01_0";
	
	public static final String SET_NORMAL_PRIORITY_ALARM = "pump1_Alarm_err00_0";
	
	public static final String SET_TARGET_PULSE_PRESSURE_ONE = "pump1_PtarB_xxxxx_0";
	public static final String SET_TARGET_PULSE_PRESSURE_TWO = "pump2_PtarB_xxxxx_0";
}
