package com.gidi.bio_console.constant;

public class SharedConstants {

	
	public static final String LANGUAGE = "language";
	
	public static final String PERFUION_STYSTEM = "perfusion_system";
	//肾脏
	public static final String KIDNEY_NUM = "kidney_number";
	public static final String LEFT_KIDNEY_WEIGHT = "left_kidney_weight";
	public static final String RIGHT_KIDNEY_WEIGHT = "right_kidney_weight";
	public static final String TARGET_LEFT_KIDNED_PRESSURE_MAX = "target_left_pre_max";
	public static final String TARGET_LEFT_KIDNED_PRESSURE_MIN = "target_left_pre_min";

	/**恒压肝动脉目标压力**/
	public static final String TARGET_LEFT_KIDNED_CONST_PRESSURE = "target_left_const_pre";
	public static final String TARGET_LEFT_KIDNED_CONST_FLOW = "target_left_const_flow";
	
	public static final String TARGET_RIGHT_KIDNED_PRESSURE_MAX = "target_right_pre_max";
	public static final String TARGET_RIGHT_KIDNED_PRESSURE_MIN = "target_right_pre_min";
	public static final String TARGET_RIGHT_KIDNED_CONST_PRESSURE = "target_right_const_pre";
	public static final String TARGET_RIGHT_KIDNED_CONST_FLOW = "target_right_const_flow";//右肾动脉和门静脉
	public static final String LEFT_KIDNEY_PERFUSION_MODE = "left_kidney_mode";
	public static final String RIGHT_KIDNEY_PERFUSION_MODE = "right_kidney_mode";
	
	/**左肾动脉压力低限制**/
	/**左肾动脉压力高限制**/
	public static final String PRESSURE_LEFT_KIDNEY_WARN_MIN = "min_warn_left_kidney_pressure";
	public static final String PRESSURE_LEFT_KIDNEY_WARN_MAX = "max_warn_left_kidney_pressure";
	/**右肾脉压力低限制**/
	/**右肾脉压力高限制**/
	public static final String PRESSURE_RIGHT_KIDNEY_WARN_MIN = "min_warn_left_kidney_pressure";
	public static final String PRESSURE_RIGHT_KIDNEY_WARN_MAX = "max_warn_right_kidney_pressure";
	/**左肾动脉流量限制**/
	public static final String FLOW_LEFT_KIDNEY_WARN_MAX = "max_warn_left_kidney_flow";
	public static final String FLOW_LEFT_KIDNEY_WARN_MIN = "min_warn_left_kidney_flow";
	/**右肾动脉流量限制**/
	public static final String FLOW_RIGHT_KIDNEY_WARN_MAX = "max_warn_right_kidney_flow";
	public static final String FLOW_RIGHT_KIDNEY_WARN_MIN = "min_warn_right_kidney_flow";
	
	public static final String SPEED_LEFT_KIDNEY_LIMIT = "left_kidney_limit_speed";
	public static final String SPEED_RIGHT_KIDNEY_LIMIT = "right_kidney_limit_speed";
	
	/**恒流目标流量**/
	
	public static final String TARGET_ARTERY_SPEED = "target_artery_speed";
	
	public static final String TARGET_VEIN_SPEED = "target_vein_speed";
	
	/**自定义设置的肝动脉泵速**/
	public static final String SET_CUSTOM_ARTERY_SPEED = "custom_artery_speed";
	
	/**自定义设置的门静脉泵速**/
	public static final String SET_CUSTOM_VEIN_SPEED = "custom_vein_speed";
	/**肝动脉压力低限制**/
	public static final String PRESSURE_MIN_ONE = "min_pressure_one";
	/**肝动脉压力高限制**/
	public static final String PRESSURE_MAX_ONE = "max_pressure_one";
	/**门静脉压力低限制**/
	public static final String PRESSURE_MIN_TWO = "min_pressure_two";
	/**门静脉压力高限制**/
	public static final String PRESSURE_MAX_TWO = "max_pressure_two";

	/**肝动脉流量限制**/
	public static final String FLOW_MAX_ONE = "flow_max_one";
	public static final String FLOW_MIN_ONE = "flow_min_one";
	/**门静脉流量限制**/
	public static final String FLOW_MAX_TWO = "flow_max_two";
	public static final String FLOW_MIN_TWO = "flow_min_two";
	
	public static final String TARGET_ARTERY_FLOW = "target_artery_flow";
	
	public static final String TARGET_VEIN_FLOW = "target_vein_flow";
	/**肝脏编号**/
	public static final String LIVER_NUMBER = "liver_number";
	/****/
	public static final String LIVER_WEIGHT = "liver_weight";

	/**target temp**/
	public static final String TARGET_TEMP = "target_temp";
	public static final String TEMP_CTRL = "temp_control";
	/**perfusion temp mode 0：normal 1：cold**/
	public static final String TEMP_PERFUSION_MODE = "temp_perfusion_mode";
	public static final String REMOTECTRL_SERVER_URL = "remotectrl_server_url";
	public static final String REMOTECTRL_SERVER_IP = "remotectrl_server_ip";
	public static final String REMOTECTRL_SERVER_PORT = "remotectrl_server_port";
	public static final String REMOTECTRL_SERVER_DID = "remotectrl_server_did";
	
	public static final String ARTERY_PERFUSION_MODE = "artery_perfusion_mode";
	public static final String VEIN_PERFUSION_MODE = "verin_perfusion_mode";
	/**开始灌注的时间**/
	public static final String START_PERFUSION_TIME = "start_perfusion_time";
	/**气泡是否急停**/
	public static final String SYSTEM_SETTING_BUBBLESTOP = "bubble_stop";
	/**肝动脉最新目标的指令**/
	public static final String LASTEST_ARTERY_TARGET_ORDER = "lastest_artery_target_order";
	
	/**门静脉最新目标的指令**/
	public static final String LASTEST_VEIN_TARGET_ORDER = "lastest_vein_target_order";	
	/**泵速限制**/
	public static final String LIMIT_SPEED_ONE = "artery_speed_limit";
	
	public static final String LIMIT_SPEED_TWO = "vein_speed_limit";
	/**是否对压力进行过清零**/
	public static final String IS_PRESSURE_ZERO_FIRST = "isPressureZeroFirst";
	/**判断是否开始灌注**/
	public static final String IS_PERFUSION = "isPerfusion";
	/**是否崩溃**/
	public static final String IS_CRASH = "isCrash";
	/**是否插入usb**/
	public static final String IS_USB_PLUNG = "is_usb_plung";
}
