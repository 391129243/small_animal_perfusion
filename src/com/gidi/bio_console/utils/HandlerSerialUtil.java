package com.gidi.bio_console.utils;

import android.content.Context;

import com.gidi.bio_console.R;
import com.gidi.bio_console.common.ErrorCode;

public class HandlerSerialUtil {

	private Context mContext;
	public HandlerSerialUtil(Context context){
		this.mContext = context;
	}
	
	public String getErrorMsg(int errorCode) {
		switch (errorCode) {
		case ErrorCode.E_BUBBLE_ARTERY_ALERT_MSG:
			return mContext.getString(R.string.error_bubble_one);
		case ErrorCode.E_BUBBLE_VEIN_ALERT_MSG:
			return mContext.getString(R.string.error_bubble_two);
		case ErrorCode.E_FLOW_ONE_MAX_AlARM_MSG:
			return mContext.getString(R.string.error_flow_artery_high_flow);
		case ErrorCode.E_FLOW_ONE_MIN_AlARM_MSG:
			return mContext.getString(R.string.error_flow_artery_low_flow);
		case ErrorCode.E_FLOW_TWO_MAX_AlARM_MSG:
			return mContext.getString(R.string.error_flow_vein_high_flow);
		case ErrorCode.E_FLOW_TWO_MIN_AlARM_MSG:
			return mContext.getString(R.string.error_flow_vein_low_flow);
		case ErrorCode.E_PESSURE_ONE_MAX_AlARM_MSG:
			return mContext.getString(R.string.error_pre_artery_high_pre);
		case ErrorCode.E_PESSURE_ONE_MIN_AlARM_MSG:
			return mContext.getString(R.string.error_pre_artery_low_pre);
		case ErrorCode.E_PESSURE_TWO_MAX_AlARM_MSG:
			return mContext.getString(R.string.error_pre_vein_high_pre);
		case ErrorCode.E_PESSURE_TWO_MIN_AlARM_MSG:
			return mContext.getString(R.string.error_pre_vein_low_pre);
		case ErrorCode.E_DISCONNECT_ARTERY_MSG:
			return mContext.getString(R.string.disconnect_pump_artery);
		case ErrorCode.E_DISCONNECT_VEIN_MSG:
			return mContext.getString(R.string.disconnect_pump_vein);
		case ErrorCode.E_DISCONNECT_PRESSURE_ARTERY_MSG:
			return mContext.getString(R.string.disconnect_pump_artrey_pressure);
		case ErrorCode.E_DISCONNECT_PRESSURE_VEIN_MSG:
			return mContext.getString(R.string.disconnect_pump_vein_pressure);
		case ErrorCode.E_DISCONNECT_FLOW_ARTERY_MSG:
			return mContext.getString(R.string.disconnect_pump_artrey_flow);
		case ErrorCode.E_DISCONNECT_FLOW_VEIN_MSG:
			return mContext.getString(R.string.disconnect_pump_vein_flow);
		case ErrorCode.E_ABNORMAL_FLOW_ARTERY_MSG:
			//肝动脉流量异常
			return mContext.getString(R.string.abnormal_pump_artery_flow);
		case ErrorCode.E_ABNORMAL_FLOW_VEIN_MSG:
			//门静脉流量异常
			return mContext.getString(R.string.abnormal_pump_vein_flow);			
		case ErrorCode.E_FLOW_ONE_BACK_FLOW_MSG:
			return mContext.getString(R.string.error_artery_backflow);
		case ErrorCode.E_FLOW_TWO_BACK_FLOW_MSG:
			return mContext.getString(R.string.error_vein_backflow);
		case ErrorCode.E_ABNORMAL_TEMP:
			//温度异常
			return mContext.getString(R.string.error_temp_abnormal);
		case ErrorCode.E_OVER_TEMP:
			//温度过高
			return mContext.getString(R.string.error_temp_over);
		case ErrorCode.E_INTERRUPT_SYSTEM:
			//系统中断
			return mContext.getString(R.string.error_system_communication_interrupt);
		case ErrorCode.E_SPEED_ARTERY_ABNORMAL:
			return mContext.getString(R.string.alarm_msg_pump_artery_speed);
		case ErrorCode.E_SPEED_VEIN_ABNORMAL:
			return mContext.getString(R.string.alarm_msg_pump_vein_speed);
		case ErrorCode.E_ALARM_LIQUID_LEVEL:
			return mContext.getString(R.string.error_low_water_reservoir);
		case ErrorCode.E_SYSTEM_EMERGENCY:
			return mContext.getString(R.string.error_emergency_stop);
		case ErrorCode.E_LOWER_BATTERY:
			return mContext.getString(R.string.alarm_msg_battery_low);

		default:
			return "";
		}	
	}
	
	public int getErrorCode(String errorMsg){
		if(errorMsg.equals(mContext.getString(R.string.error_bubble_one))){
			return ErrorCode.E_BUBBLE_ARTERY_ALERT_MSG;
		}else if(errorMsg.equals(mContext.getString(R.string.error_bubble_two))){
			return ErrorCode.E_BUBBLE_VEIN_ALERT_MSG;
		}else if(errorMsg.equals(mContext.getString(R.string.disconnect_pump_artery))){
			return ErrorCode.E_DISCONNECT_ARTERY_MSG;
		}else if(errorMsg.equals(mContext.getString(R.string.disconnect_pump_vein))){
			return ErrorCode.E_DISCONNECT_VEIN_MSG;
		}else if(errorMsg.equals(mContext.getString(R.string.disconnect_pump_artrey_pressure))){
			return ErrorCode.E_DISCONNECT_PRESSURE_ARTERY_MSG;
		}else if(errorMsg.equals(mContext.getString(R.string.disconnect_pump_vein_pressure))){
			return ErrorCode.E_DISCONNECT_PRESSURE_VEIN_MSG;
		}else if(errorMsg.equals(mContext.getString(R.string.disconnect_pump_artrey_flow))){
			return ErrorCode.E_DISCONNECT_FLOW_ARTERY_MSG;
		}else if(errorMsg.equals(mContext.getString(R.string.disconnect_pump_vein_flow))){
			return ErrorCode.E_DISCONNECT_FLOW_VEIN_MSG;
		}else if(errorMsg.equals(mContext.getString(R.string.abnormal_pump_artery_flow))){
			return ErrorCode.E_ABNORMAL_FLOW_ARTERY_MSG;
		}else if(errorMsg.equals(mContext.getString(R.string.abnormal_pump_vein_flow))){
			return ErrorCode.E_ABNORMAL_FLOW_VEIN_MSG;
		}else if(errorMsg.equals(mContext.getString(R.string.error_temp_over))){
			return ErrorCode.E_OVER_TEMP;
		}else if(errorMsg.equals(mContext.getString(R.string.error_temp_abnormal))){
			return ErrorCode.E_ABNORMAL_TEMP;
		}else if(errorMsg.equals(mContext.getString(R.string.error_low_water_reservoir))){
			return ErrorCode.E_ALARM_LIQUID_LEVEL;
		}else if(errorMsg.equals(mContext.getString(R.string.error_emergency_stop))){
			return ErrorCode.E_SYSTEM_EMERGENCY;
		}else if(errorMsg.equals(mContext.getString(R.string.alarm_msg_battery_low))){
			return ErrorCode.E_LOWER_BATTERY;
		}
		return ErrorCode.E_INTERRUPT_SYSTEM;
	}
}
