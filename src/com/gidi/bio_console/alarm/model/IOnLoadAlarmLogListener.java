package com.gidi.bio_console.alarm.model;

import java.util.ArrayList;

import com.gidi.bio_console.bean.AlarmMsgEnity;

public interface IOnLoadAlarmLogListener {

	void success(ArrayList<AlarmMsgEnity> list);
	void searchFail();
}
