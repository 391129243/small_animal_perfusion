package com.gidi.bio_console.alarm.view;

import com.gidi.bio_console.bean.AlarmMsgEnity;

import java.util.ArrayList;


public interface IAlarmLogView {

	void setAlarmLogList(ArrayList<AlarmMsgEnity> list);
	void onSearchFail();
}
