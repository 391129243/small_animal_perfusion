package com.gidi.bio_console.constant;

public class BroadcastActions {
	public static String RECEIVE_REMOTE_CTRL_SPEED = "action_receive_remote_control_speed";
	public static String RECEIVE_REMOTE_CTRL_TEMP = "action_receive_remote_control_temp";

	public static String RECEIVE_REMOTE_CTRL_ARTERY_SPEED = "action_receive_remote_artery_tarspeed";
	public static String RECEIVE_REMOTE_CTRL_VEIN_SPEED = "action_receive_remote_vein_tarspeed";
	/**心跳连接信息**/
	public static String ACTION_RECEIVE_PUMP_ONE_CNECT = "action_receive_pump1_cnect";
	public static String ACTION_RECEIVE_PUMP_TWO_CNECT = "action_receive_pump2_cnect";
	/**通信中断，在心跳连接2min后一直没有收到心跳消息**/
	public static String ACTION_RECEIVE_SYSTEM_ITERRUPT = "action_receive_system_interrupt";
	
	public static String ACTION_RECEIVE_PUMP_ONE_PREAL = "action_receive_pump1_preal";
	public static String ACTION_RECEIVE_PUMP_TWO_PREAL = "action_receive_pump2_preal";
	
	/**压力调零**/
	public static String ACTION_ZERO_PUMP_ONE_PREAL = "action_zero_pump1_preal";
	public static String ACTION_ZERO_PUMP_TWO_PREAL = "action_zero_pump2_preal";
	
	/**流量调零**/
	public static String ACTION_ZERO_PUMP_ONE_FREAL = "action_zero_pump1_freal";
	public static String ACTION_ZERO_PUMP_TWO_FREAL = "action_zero_pump2_freal";
	
	/**瞬时流量**/
	public static String ACTION_RECEIVE_PUMP_ONE_FREAL = "action_receive_pump1_freal";
	public static String ACTION_RECEIVE_PUMP_TWO_FREAL = "action_receive_pump2_freal";
	/**mean flow**/
	public static String ACTION_RECEIVE_PUMP_ONE_FMEAN = "action_receive_pump1_fmean";
	public static String ACTION_RECEIVE_PUMP_TWO_FMEAN = "action_receive_pump2_fmean";
	
	
	/**泵速**/
	public static String ACTION_RECEIVE_PUMP_ONE_SREAL = "action_receive_pump1_sreal";
	public static String ACTION_RECEIVE_PUMP_TWO_SREAL = "action_receive_pump2_sreal";
	
	/**温度**/
	public static String ACTION_RECEIVE_TREAL = "action_receive_treal";

	public static String ACTION_RECEIVE_TARGET_TEMP = "action_receive_target_temp";
	/**气泡**/
	public static String ACTION_RECEIVE_PUMP_ONE_QREAL = "action_receive_pump1_qreal";
	public static String ACTION_RECEIVE_PUMP_TWO_QREAL = "action_receive_pump2_qreal";
	
	/**脉率**/
	public static String ACTION_RECEIVE_PUMP_ONE_PFREQ = "action_receive_pump1_pfreq";
	public static String ACTION_RECEIVE_PUMP_TWO_PFREQ = "action_receive_pump2_pfreq";
	
	/**肝动脉舒张压**/
	public static String ACTION_RECEIVE_PUMP_ONE_PDIAS = "action_receive_pump1_pdias";
	public static String ACTION_RECEIVE_PUMP_TWO_PDIAS = "action_receive_pump2_pdias";
	/**肝动脉收缩压**/
	public static String ACTION_RECEIVE_PUMP_ONE_PSYST = "action_receive_pump1_psyst";
	public static String ACTION_RECEIVE_PUMP_TWO_PSYST = "action_receive_pump2_psyst";
	/**肝动脉平均脉搏**/
	public static String ACTION_RECEIVE_PUMP_ONE_PMEAN = "action_receive_pump1_pmean";
	public static String ACTION_RECEIVE_PUMP_TWO_PMEAN = "action_receive_pump2_pmean";
	
	/**泵速报警**/
	public static String ACTION_ALARM_PUMP_ONE_SPEED = "action_alarm_pump1_speed";
	public static String ACTION_ALARM_PUMP_TWO_SPEED = "action_alarm_pump2_speed";
	
	/**胆汁的数量**/
	public static String ACTION_RECEIVE_EBILE = "action_receive_ebile";
	/**仪器状态**/
	public static String ACTION_RECEIVE_DEVICE_STATE = "action_receive_device_state";
	/**电池状态**/
	public static String ACTION_RECEIVE_BATTERY_STATE = "action_receive_battery_state";
	/**散热状态**/
	public static String ACTION_RECEIVE_HEATDISS_STATE = "action_receive_heatdiss_state";
	
	/**与服务器断连的消息**/
	public static String ACTION_SERVER_DISCONNECTED = "com.gidi.bio_console.entity.action.server_disconnected";
	/**登录服务器结果**/
	public static String ACTION_LOGIN_RESULT = "com.gidi.bio_console.entity.action.login.result";
	
	public static String ACTION_BIOCHEMICAL_INDEX_RESULT = "com.gidi.bio_console.entity.action.biochemicalindex.result";
	
	/****/
	public static String ACTION_UPDATE_SERIAL_MSG_RECORD = "com.gidi.bio_console.entity.action.update_serial_record";
	
	/**结束灌注**/
	public static String ACTION_END_PERFUSION_SYSTEM = "com.gidi.bio_console_entity_action_end_perfusion_system";
	
	/**开机启动结束**/
	public static String ACTION_END_BOOTUP_SYSTEM = "com.gidi.bio_console_entity_action_end_bootup_system";
}
