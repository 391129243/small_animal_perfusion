package com.gidi.bio_console.bean;
/**
 * 仪器状态
 * @author 80657
 * @param liquid_level 水箱液位状态
 * @param battery_state 电池状态 0：异常，1：正常
 * @param charging_state 充电状态  0：没有充电，1：充电完成 2:正在充电
 * @param power_state 电源状态  0:电池,1:AC
 * @param emergency_stop_switch 急停开关
 */
public class DeviceStatusBean {

	int liquid_level;
	int battery_state;
	int charging_state;
	int power_state;
	int emergency_stop_switch;
	
	public DeviceStatusBean(int liquid_level, int battery_state,int charging_state,
			 int power_state, int emergency_stop_switch){
		this.liquid_level = liquid_level;
		this.battery_state = battery_state;
		this.charging_state = charging_state;
		this.power_state = power_state;
		this.emergency_stop_switch = emergency_stop_switch;
	}

	public int getLiquid_level() {
		return liquid_level;
	}

	public void setLiquid_level(int liquid_level) {
		this.liquid_level = liquid_level;
	}

	public int getBattery_state() {
		return battery_state;
	}

	public void setBattery_state(int battery_state) {
		this.battery_state = battery_state;
	}

	public int getCharging_state() {
		return charging_state;
	}

	public void setCharging_state(int charging_state) {
		this.charging_state = charging_state;
	}

	public int getPower_state() {
		return power_state;
	}

	public void setPower_state(int power_state) {
		this.power_state = power_state;
	}

	public int getEmergency_stop_switch() {
		return emergency_stop_switch;
	}

	public void setEmergency_stop_switch(int emergency_stop_switch) {
		this.emergency_stop_switch = emergency_stop_switch;
	}

	
	
}
