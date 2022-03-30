package com.gidi.bio_console.db;

import java.util.ArrayList;

import com.gidi.bio_console.bean.AlarmMsgEnity;
import com.gidi.bio_console.bean.BileMsgBean;
import com.gidi.bio_console.bean.BloodGasBean;
import com.gidi.bio_console.bean.BloodGasSamplingBean;
import com.gidi.bio_console.bean.KidneyInfoBean;
import com.gidi.bio_console.bean.KidneyPerfusionLogBean;
import com.gidi.bio_console.bean.PerfusionLogBean;
import com.gidi.bio_console.bean.SerialMessage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class DatabaseMgr {
	
	private DataModel dataModel = null;
	/**使用单例模式的双重锁定**/
	private static final Object LOCK = new Object();
	private static DatabaseMgr mInstance;
	private ArrayList<AlarmMsgEnity> mAlarmMsgList = new ArrayList<AlarmMsgEnity>();
	private static String SQL_QUERY_ERROR_MSG = "select " + "*"
	            + " from "
	            + DatabaseHelper.TABLE_ALARM_INFO + " where " + DatabaseHelper.COLUMN_MESSAGE_TYPE + " =?";
	
	public static DatabaseMgr getInstance(Context context){
		if(null == mInstance){
			synchronized (LOCK) {
				if(null == mInstance){
					mInstance = new DatabaseMgr(context);
				}
			}
		}
		return mInstance;
	}
	
	private DatabaseMgr(Context context) {
	    dataModel = DataModel.getInstance(context);
	    initList();
	}
	
	private void initList(){
		mAlarmMsgList.clear();
	}
	
	/**插入报警信息信息**/
	public void insertAlarmMsg(String liverNum,String errorCode ,String msgType ,String time){
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_LIVER_NUM, liverNum);
		values.put(DatabaseHelper.COLUMN_MESSAGE_CONTENT, errorCode);
		values.put(DatabaseHelper.COLUMN_MESSAGE_TYPE, msgType);		
		values.put(DatabaseHelper.COLUMN_MESSAGE_TIME, time);
		long result = dataModel.insert(DatabaseHelper.TABLE_ALARM_INFO, values);
	}
	
	/**插入报警信息信息**/
	public void insertKidneyAlarmMsg(String kidney,String errorCode ,String msgType ,String time){
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_KIDNEY_NUM, kidney);
		values.put(DatabaseHelper.COLUMN_MESSAGE_CONTENT, errorCode);
		values.put(DatabaseHelper.COLUMN_MESSAGE_TYPE, msgType);		
		values.put(DatabaseHelper.COLUMN_MESSAGE_TIME, time);
		long result = dataModel.insert(DatabaseHelper.TABLE_ALARM_INFO, values);
	}
	
	/**根据日期查询报警信息
	 * @param searchTime
	 * 查询时间的格式是yyyy-MM-dd
	 * */
	public ArrayList<AlarmMsgEnity> getAlarmMsgFromTime(String searchTime){
		Log.i("DatabaseMgr", "---getErrorMsgsFromTime---");
		ArrayList<AlarmMsgEnity> list = null;	
		String[] selectioinArgs = {"%"+ searchTime +"%"};
		if(null != dataModel){
			Cursor cursor = dataModel.query("select * from " + DatabaseHelper.TABLE_ALARM_INFO + " where " + DatabaseHelper.COLUMN_MESSAGE_TIME + " like ? "+ " ORDER BY " + DatabaseHelper.COLUMN_MESSAGE_TIME + " desc ", selectioinArgs);
			if(null != cursor){
				list = new ArrayList<AlarmMsgEnity>();
				AlarmMsgEnity entity;
				while (cursor.moveToNext()){
					entity = new AlarmMsgEnity();
					entity.setLiverNum(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LIVER_NUM)));
					entity.setErrorCode(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MESSAGE_CONTENT)));
					entity.setAlarmTime(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MESSAGE_TIME)));
					list.add(entity);
				}
				
				if(!cursor.isClosed()){
					cursor.close();
				}
			}
			
		}	
		
		return list;
	}
	
	public ArrayList<AlarmMsgEnity> getKidneyAlarmMsgFromTime(String searchTime){
		Log.i("DatabaseMgr", "---getErrorMsgsFromTime---");
		ArrayList<AlarmMsgEnity> list = null;	
		String[] selectioinArgs = {"%"+ searchTime +"%"};
		if(null != dataModel){
			Cursor cursor = dataModel.query("select * from " + DatabaseHelper.TABLE_ALARM_INFO + " where " + DatabaseHelper.COLUMN_MESSAGE_TIME + " like ? "+ " ORDER BY " + DatabaseHelper.COLUMN_MESSAGE_TIME + " desc ", selectioinArgs);
			if(null != cursor){
				list = new ArrayList<AlarmMsgEnity>();
				AlarmMsgEnity entity;
				while (cursor.moveToNext()){
					entity = new AlarmMsgEnity();
					entity.setLiverNum(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_KIDNEY_NUM)));
					entity.setErrorCode(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MESSAGE_CONTENT)));
					entity.setAlarmTime(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MESSAGE_TIME)));
					list.add(entity);
				}
				
				if(!cursor.isClosed()){
					cursor.close();
				}
			}
			
		}	
		
		return list;
	}
	
	
	/**从开始灌注的日期开始查询**/
	/**根据灌注id和灌注时间查询报警消息***/
	public ArrayList<AlarmMsgEnity> getAlarmMsgByLiverAndTime(String liverNum, String starttime){		
		ArrayList<AlarmMsgEnity> list = null;
		String[] selectioinArgs = new String[]{liverNum,starttime};			
		if(null != dataModel){			
			Cursor cursor = dataModel.query("select * from " + DatabaseHelper.TABLE_ALARM_INFO + 
					" where " + DatabaseHelper.COLUMN_LIVER_NUM + " =?" + " and "+ DatabaseHelper.COLUMN_MESSAGE_TIME + " >=? " , selectioinArgs);
			if(null != cursor){
				list = new ArrayList<AlarmMsgEnity>();
				AlarmMsgEnity entity;
				int index = 1;
				while(cursor.moveToNext()){
					entity = new AlarmMsgEnity();
					entity.setLiverNum(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LIVER_NUM)));
					entity.setErrorCode(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MESSAGE_CONTENT)));
					entity.setAlarmTime(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MESSAGE_TIME)));
					list.add(entity);
				}
				if(!cursor.isClosed()){
					cursor.close();
				}
			}
		}
		return list;
	}
	
	
	/**删除报警记录**/
	public void deleteAllAlarmLog(){
		String whereClause = DatabaseHelper.COLUMN_MESSAGE_TYPE + " =?";
		String[] whereArgs =  new String[]{"error"};
		if(null != dataModel){
			dataModel.delete(SQL_QUERY_ERROR_MSG, whereClause, whereArgs);
		}
	}
	
	/**保存灌注的肝脏名称和灌注开始时间**/
	public long insertLiverPerfusionLog(PerfusionLogBean perfusionLogBean){
		long result = -1;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_LIVER_NUM, perfusionLogBean.getLiverName());
		values.put(DatabaseHelper.COLUMN_LIVER_WEIGHT, perfusionLogBean.getLiverWeight());
		values.put(DatabaseHelper.COLUMN_PERFUION_START_TIME, perfusionLogBean.getStartTime());
		values.put(DatabaseHelper.COLUMN_VEIN_PERFUSION_MODE, perfusionLogBean.getVein_mode());
		values.put(DatabaseHelper.COLUMN_ARTERY_PERFUSION_MODE, perfusionLogBean.getArtery_mode());
		result = dataModel.insert(DatabaseHelper.TABLE_ORGAN_PERFUSION_LOG, values);
		return result;
	}
	
	/***有可能会更新灌注的模式或灌注的重量，要有更新操作**/
	public int updatePerfusionLogLiverWeight(String liverweight,String liverNum){
		int updateresult = -1;
		String whereClause = DatabaseHelper.COLUMN_LIVER_NUM +  " =?";
		String[] whereArgs = new String[]{liverNum};
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_LIVER_WEIGHT, liverweight);
		updateresult = dataModel.update(DatabaseHelper.TABLE_ORGAN_PERFUSION_LOG, values, whereClause, whereArgs);
		return updateresult;
	}
	
	public int updatePerfusionArtMode(String arteryMode,String liverNum){
		int updateresult = -1;
		String whereClause = DatabaseHelper.COLUMN_LIVER_NUM +  " =?";
		String[] whereArgs = new String[]{liverNum};
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_ARTERY_PERFUSION_MODE, arteryMode);
		updateresult = dataModel.update(DatabaseHelper.TABLE_ORGAN_PERFUSION_LOG, values, whereClause, whereArgs);
		return updateresult;
	}
	
	/**更新门静脉灌注模式**/
	public int updatePerfusionVeinMode(String veinMode,String liverNum){
		int updateresult = -1;
		String whereClause = DatabaseHelper.COLUMN_LIVER_NUM +  " =?";
		String[] whereArgs = new String[]{liverNum};
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_VEIN_PERFUSION_MODE, veinMode);
		updateresult = dataModel.update(DatabaseHelper.TABLE_ORGAN_PERFUSION_LOG, values, whereClause, whereArgs);
		return updateresult;
	}
	
	
	/**按时间排序所有灌注记录**/
	public ArrayList<PerfusionLogBean> getAllLiverPerfusionLog(){
		ArrayList<PerfusionLogBean> list = null;
		if(null != dataModel){
			Cursor cursor = dataModel.query("select * from " + DatabaseHelper.TABLE_ORGAN_PERFUSION_LOG, null);
			if(null != cursor){
				list = new ArrayList<PerfusionLogBean>();
				PerfusionLogBean perfusionLogBean;
				int index = 1;
				while(cursor.moveToNext()){
					perfusionLogBean = new PerfusionLogBean();
					perfusionLogBean.setLiverName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LIVER_NUM)));
					perfusionLogBean.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PERFUION_START_TIME)));
					perfusionLogBean.setLiverWeight(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LIVER_WEIGHT)));
					perfusionLogBean.setIndex(index);
					perfusionLogBean.setArtery_mode(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ARTERY_PERFUSION_MODE)));
					perfusionLogBean.setVein_mode(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VEIN_PERFUSION_MODE)));
					list.add(perfusionLogBean);
					index++;
				}
				if(!cursor.isClosed()){
					cursor.close();
				}
			}
		}		
		return list;
	}


	
	/**根据开始灌注日期和肝脏名称
	 * 时间必须是格式化的 2008-06-10***/
	public ArrayList<PerfusionLogBean> getLiverPerfusionLogFromName(String start_search_date, String end_search_date){
		ArrayList<PerfusionLogBean> list = null;
		
		if(null != dataModel){
			Cursor cursor = null;
			if(start_search_date.equals(end_search_date)){
				Log.i("PerfusionLogActivity", "true---"+ start_search_date);
				String[] selectioinArgs_1 = new String[]{"%"+ start_search_date +"%"};
				cursor = dataModel.query("select * from " + DatabaseHelper.TABLE_ORGAN_PERFUSION_LOG + " where " + DatabaseHelper.COLUMN_PERFUION_START_TIME+ " like ? " , selectioinArgs_1);
			}else{
				String[] selectionArgs_2 = new String[]{start_search_date ,end_search_date};
				cursor = dataModel.query("select * from " + DatabaseHelper.TABLE_ORGAN_PERFUSION_LOG + " where " + DatabaseHelper.COLUMN_PERFUION_START_TIME+ " >=?" + " and " + DatabaseHelper.COLUMN_PERFUION_START_TIME + " <=? ", selectionArgs_2);
			}
			
			Log.i("PerfusionLogActivity", "cursor---"+ cursor);
			if(null != cursor){
				list = new ArrayList<PerfusionLogBean>();
				PerfusionLogBean perfusionLogBean;
				int index = 1;
				while(cursor.moveToNext()){
					perfusionLogBean = new PerfusionLogBean();
					perfusionLogBean.setLiverName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LIVER_NUM)));
					perfusionLogBean.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PERFUION_START_TIME)));
					perfusionLogBean.setLiverWeight(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LIVER_WEIGHT)));
					perfusionLogBean.setArtery_mode(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ARTERY_PERFUSION_MODE)));
					perfusionLogBean.setVein_mode(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VEIN_PERFUSION_MODE)));				
					perfusionLogBean.setIndex(index);
					list.add(perfusionLogBean);
					index++;
				}
				if(!cursor.isClosed()){
					cursor.close();
					
				}
			}
		}
		Log.i("PerfusionLogActivity", "list---"+ list.size());
		return list;
	}
	
	
	
	
	
	public int delLiverPerfusionLogFromName(String liverNum){
		int result = -1;
		String whereClause = DatabaseHelper.COLUMN_LIVER_NUM + " =?";
		String[] whereArgs = new String[]{liverNum};
		if(null != dataModel){
			 result = dataModel.delete(DatabaseHelper.TABLE_ORGAN_PERFUSION_LOG, whereClause, whereArgs);
		}
		return result;
	}
	
		
	
	/**保存串口上报的数据**/
	public long saveSerialMessage(SerialMessage msg){
		long result = -1;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_LIVER_NUM, msg.getLiverNum());
		values.put(DatabaseHelper.SERIAL_INFO_ARTERY_PDIAS, msg.getArtDiasPre());
		values.put(DatabaseHelper.SERIAL_INFO_ARTERY_PSYST, msg.getArtSystPre());
		values.put(DatabaseHelper.SERIAL_INFO_ARTERY_PMEAN, msg.getArtMeanPre());
		values.put(DatabaseHelper.SERIAL_INFO_ARTERY_SPEED, msg.getArtSpeed());
		values.put(DatabaseHelper.SERIAL_INFO_ARTERY_FREAL, msg.getArtFreal());
		values.put(DatabaseHelper.SERIAL_INFO_ARTERY_PFREQ, msg.getArtFreqPre());
		values.put(DatabaseHelper.SERIAL_INFO_ARTERY_RESISTINDEX, msg.getArtResistIndex());
		values.put(DatabaseHelper.SERIAL_INFO_ARTERY_EHBF, msg.getArtFlowEHBF());
		values.put(DatabaseHelper.SERIAL_INFO_VEIN_FREAL, msg.getVeinFreal());
		values.put(DatabaseHelper.SERIAL_INFO_VEIN_PREAL, msg.getVeinPreal());
		values.put(DatabaseHelper.SERIAL_INFO_VEIN_SPEED, msg.getVeinSpeed());
		values.put(DatabaseHelper.SERIAL_INFO_VEIN_RESISTINDEX, msg.getVeinResistIndex());
		values.put(DatabaseHelper.SERIAL_INFO_VEIN_EHBF, msg.getVeinFlowEHBF());
		values.put(DatabaseHelper.SERIAL_INFO_TEMP, msg.getTemp());
		values.put(DatabaseHelper.SERIAL_INFO_TIME, msg.getMsgTime());
		result = dataModel.insert(DatabaseHelper.TABLE_SERIAL_INFO, values);
		return result;
	}
		
	/**获取开始灌注时间段的灌注数据
	 * @param liverNum 肝脏id
	 * 
	 * **/
	public ArrayList<SerialMessage> getLiverPerfusionFromStarttime(String liverNum, String startTime){
		ArrayList<SerialMessage> list = null;
		String[] selectionArgs = new String[]{liverNum,startTime};
		
		if(null != dataModel){
			Cursor cursor = dataModel.query("select * from " + DatabaseHelper.TABLE_SERIAL_INFO + " where " + DatabaseHelper.COLUMN_LIVER_NUM + " =?" + " and "+ DatabaseHelper.SERIAL_INFO_TIME + " >=? " 
					, selectionArgs);
			if(null != cursor){
				list = new ArrayList<SerialMessage>();
				SerialMessage serialMessage;
				while(cursor.moveToNext()){
					serialMessage = new SerialMessage();
					serialMessage.setLiverNum(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LIVER_NUM)));
					serialMessage.setArtDiasPre(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SERIAL_INFO_ARTERY_PDIAS)));
					serialMessage.setArtSystPre(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SERIAL_INFO_ARTERY_PSYST)));
					serialMessage.setArtMeanPre(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SERIAL_INFO_ARTERY_PMEAN)));
					serialMessage.setArtFreqPre(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SERIAL_INFO_ARTERY_PFREQ)));
					serialMessage.setArtFreal(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SERIAL_INFO_ARTERY_FREAL)));
					serialMessage.setArtSpeed(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SERIAL_INFO_ARTERY_SPEED)));
					serialMessage.setArtResistIndex(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SERIAL_INFO_ARTERY_RESISTINDEX)));
					
					serialMessage.setTemp(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SERIAL_INFO_TEMP)));
					serialMessage.setMsgTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SERIAL_INFO_TIME)));
					serialMessage.setVeinFreal(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SERIAL_INFO_VEIN_FREAL)));
					serialMessage.setVeinPreal(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SERIAL_INFO_VEIN_PREAL)));
					serialMessage.setVeinSpeed(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SERIAL_INFO_VEIN_SPEED)));
					serialMessage.setVeinResistIndex(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SERIAL_INFO_VEIN_RESISTINDEX)));
					
					if(null != cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SERIAL_INFO_ARTERY_EHBF))){
						serialMessage.setArtFlowEHBF(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SERIAL_INFO_ARTERY_EHBF)));
					}else{
						serialMessage.setArtFlowEHBF("--");
					}
					
					if(null != cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SERIAL_INFO_VEIN_EHBF))){
						serialMessage.setVeinFlowEHBF(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SERIAL_INFO_VEIN_EHBF)));
					}else{
						serialMessage.setVeinFlowEHBF("--");
					}
					
					list.add(serialMessage);
				}
				
				if(cursor != null){
					cursor.close();
				}
			}
		}
		
		return list;
	}
	//删除肝脏灌注的数据记录
	public int deleteLiverPerfusionFromStarttime(String liverNum, String startTime){
		int result = -1;
		String whereClause = DatabaseHelper.COLUMN_LIVER_NUM + " =?" + " and "+ DatabaseHelper.SERIAL_INFO_TIME + " >=? ";
		String[] whereArgs = new String[]{liverNum,startTime};
		if(null != dataModel){
			 result = dataModel.delete(DatabaseHelper.TABLE_SERIAL_INFO, whereClause, whereArgs);
		}
		return result;
	}
	
	public int deleteLiverPerfusionLog(String liverNum){
		int result = -1;
		String whereClause = DatabaseHelper.COLUMN_LIVER_NUM + " =?";
		String[] whereArgs = new String[]{liverNum};
		if(null != dataModel){
			 result = dataModel.delete(DatabaseHelper.TABLE_ORGAN_PERFUSION_LOG, whereClause, whereArgs);
		}
		return result;
	}
	
	
	
	/**保存崩溃记录**/
	public long saveOrUpdateCrashMessage(String absolutePath , boolean isUploadServer){
		long result = -1;
		return result;
	}
	
	/**保存血气采样时间数据**/
	public long saveBloodGasSamplingTime(BloodGasSamplingBean bloodgas_sample){
		long result = -1;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_LIVER_NUM, bloodgas_sample.getLiverNum());
		values.put(DatabaseHelper.COLUMN_BLOOD_GAS_CHECK, bloodgas_sample.getIsChecked());
		values.put(DatabaseHelper.COLUMN_BLOOD_GAS_SAMPLING_TIME, bloodgas_sample.getSamplingTime());
		result = dataModel.insert(DatabaseHelper.TABLE_BLOOD_GAS_SAMPLING_TIME, values);
		if(result > 0){
			
		}else{
			
		}
		return result;
	}
	
	/**通过灌注liverId，获取血气采样时间的记录**/
	public ArrayList<BloodGasSamplingBean> getBloodGasSamplingTimes(String liverNum, String perfusionTime){
		ArrayList<BloodGasSamplingBean> list = null;
		String[] selectioinArgs = new String[]{liverNum,"%"+ perfusionTime +"%"};			
		if(null != dataModel){			
			Cursor cursor = dataModel.query("select * from " + DatabaseHelper.TABLE_BLOOD_GAS_SAMPLING_TIME + 
					" where " + DatabaseHelper.COLUMN_LIVER_NUM + " =?" + " and "+ DatabaseHelper.COLUMN_BLOOD_GAS_SAMPLING_TIME + " like ? ", selectioinArgs);
			if(null != cursor){
				list = new ArrayList<BloodGasSamplingBean>();
				BloodGasSamplingBean bean;
				while(cursor.moveToNext()){
					bean = new BloodGasSamplingBean();
					bean.setLiverNum(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LIVER_NUM)));
					bean.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BLOOD_GAS_CHECK)));					
					bean.setSamplingTime(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BLOOD_GAS_SAMPLING_TIME))) ;
					list.add(bean);
				}
				if(!cursor.isClosed()){
					cursor.close();
				}
			}
		}
		return list;
	} 
	
	
	/**保存血气数据**/
	public long saveBloodGasResult(BloodGasBean bloodgasResullt){
		long result = -1;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_LIVER_NUM, bloodgasResullt.getLiverNum());
		values.put(DatabaseHelper.COLUMN_BLOODGAS_PARAM_ALT, bloodgasResullt.getAlt());
		values.put(DatabaseHelper.COLUMN_BLOODGAS_PARAM_AST,bloodgasResullt.getAst());
		values.put(DatabaseHelper.COLUMN_BLOODGAS_PARAM_GLU, bloodgasResullt.getGlu());
		values.put(DatabaseHelper.COLUMN_BLOODGAS_PARAM_PH, bloodgasResullt.getPh());
		values.put(DatabaseHelper.COLUMN_BLOODGAS_PARAM_PO2, bloodgasResullt.getPo2());
		values.put(DatabaseHelper.COLUMN_BLOODGAS_PARAM_PCO2, bloodgasResullt.getPco2());
		values.put(DatabaseHelper.COLUMN_BLOODGAS_PARAM_BICARBONATE, bloodgasResullt.getBicarbonate());
		values.put(DatabaseHelper.COLUMN_BLOODGAS_PARAM_HCT, bloodgasResullt.getHct());
		values.put(DatabaseHelper.COLUMN_BLOODGAS_PARAM_LAC, bloodgasResullt.getLac());
		values.put(DatabaseHelper.COLUMN_SAMPLE_TIME, bloodgasResullt.getSampleTime());
		result = dataModel.insert(DatabaseHelper.TABLE_BLOOD_GAS, values);
		if(result > 0){
			
		}else{
			
		}
		return result;
	}
	
	/**根据采样时间删除血气数据**/
	public int deleteBloodGasResult(String liverNum, String sampleTime){
		int result = -1;
		String whereClause = "liver_num =? and sample_time =?";
		String[] whereArgs = new String[]{liverNum,sampleTime};	
		result = dataModel.delete(DatabaseHelper.TABLE_BLOOD_GAS, whereClause, whereArgs);
		return result;
	}
	
	/**根据采样时间和肝脏id更新采样数据**/
	public int updateBloodGasResult(String liverNum, String sampleTime,BloodGasBean bloodGasBean){
		int result = -1;
		ContentValues updatevalues = new ContentValues();
		String whereClause = "liver_num =? and sample_time =?";
		String[] whereArgs = new String[]{liverNum,sampleTime};	
		updatevalues.put(DatabaseHelper.COLUMN_BLOODGAS_PARAM_ALT, bloodGasBean.getAlt());
		updatevalues.put(DatabaseHelper.COLUMN_BLOODGAS_PARAM_AST,bloodGasBean.getAst());
		updatevalues.put(DatabaseHelper.COLUMN_BLOODGAS_PARAM_GLU, bloodGasBean.getGlu());
		updatevalues.put(DatabaseHelper.COLUMN_BLOODGAS_PARAM_PH, bloodGasBean.getPh());
		updatevalues.put(DatabaseHelper.COLUMN_BLOODGAS_PARAM_PO2, bloodGasBean.getPo2());
		updatevalues.put(DatabaseHelper.COLUMN_BLOODGAS_PARAM_PCO2, bloodGasBean.getPco2());
		updatevalues.put(DatabaseHelper.COLUMN_BLOODGAS_PARAM_BICARBONATE, bloodGasBean.getBicarbonate());
		updatevalues.put(DatabaseHelper.COLUMN_BLOODGAS_PARAM_HCT, bloodGasBean.getHct());	
		updatevalues.put(DatabaseHelper.COLUMN_BLOODGAS_PARAM_LAC, bloodGasBean.getLac());
		result = dataModel.update(DatabaseHelper.TABLE_BLOOD_GAS, updatevalues, whereClause, whereArgs);
		return result;
	} 
	
	/**通过灌注liverId，获取血气记录**/
	public ArrayList<BloodGasBean> getBloodGasData(String liverNum, String searchTime){
		ArrayList<BloodGasBean> list = null;
		String[] selectioinArgs = new String[]{liverNum,"%"+ searchTime +"%"};			
		if(null != dataModel){			
			Cursor cursor = dataModel.query("select * from " + DatabaseHelper.TABLE_BLOOD_GAS + 
					" where " + DatabaseHelper.COLUMN_LIVER_NUM + " =?" + " and "+ DatabaseHelper.COLUMN_SAMPLE_TIME + " like ? " + " ORDER BY " + DatabaseHelper.COLUMN_SAMPLE_TIME, selectioinArgs);
			if(null != cursor){
				list = new ArrayList<BloodGasBean>();
				BloodGasBean bloodGasBean;
				while(cursor.moveToNext()){
					bloodGasBean = new BloodGasBean();
					bloodGasBean.setLiverNum(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LIVER_NUM)));
					bloodGasBean.setAst(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BLOODGAS_PARAM_AST)));
					bloodGasBean.setAlt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BLOODGAS_PARAM_ALT)));
					bloodGasBean.setGlu(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BLOODGAS_PARAM_GLU)));
					bloodGasBean.setHct(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BLOODGAS_PARAM_HCT)));
					bloodGasBean.setLac(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BLOODGAS_PARAM_LAC)));
					bloodGasBean.setPo2(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BLOODGAS_PARAM_PO2)));
					bloodGasBean.setPco2(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BLOODGAS_PARAM_PCO2)));
					bloodGasBean.setPh(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BLOODGAS_PARAM_PH)));
					bloodGasBean.setBicarbonate(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BLOODGAS_PARAM_BICARBONATE)));
					bloodGasBean.setSampleTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SAMPLE_TIME)));
					list.add(bloodGasBean);
				}
				if(!cursor.isClosed()){
					cursor.close();
				}
			}
		}
		return list;
	} 
	
	/**获取所有的灌注的肝脏名称liverName**/
	public ArrayList<String> getAllPerfusionLiverName(){
		ArrayList<String> list = null;
		if(null != dataModel){
			Cursor cursor = dataModel.query("select distinct " + DatabaseHelper.COLUMN_LIVER_NUM +" from " + DatabaseHelper.TABLE_SERIAL_INFO,null);
			if(null != cursor){
				list = new ArrayList<String>();
				while(cursor.moveToNext()){
					String liverName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LIVER_NUM));
					list.add(liverName);
				}
				if(!cursor.isClosed()){
					cursor.close();
				}
			}
		}
		return list;
	}
	
	public ArrayList<String> getAllPerfusionKidneyName(){
		ArrayList<String> list = null;
		if(null != dataModel){
			Cursor cursor = dataModel.query("select distinct " + DatabaseHelper.COLUMN_KIDNEY_NUM +" from " + DatabaseHelper.TABLE_KIDNEY_PERFUSION_INFO,null);
			if(null != cursor){
				list = new ArrayList<String>();
				while(cursor.moveToNext()){
					String liverName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_KIDNEY_NUM));
					list.add(liverName);
				}
				if(!cursor.isClosed()){
					cursor.close();
				}
			}
		}
		return list;
	}
	
	/**插入对应id的对应的胆汁***/
	public void insertBileCountByName(BileMsgBean bilebean){
		
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_LIVER_NUM, bilebean.getLiverName());
		values.put(DatabaseHelper.COLUMN_BILE_COUNT, bilebean.getBileCount());
		values.put(DatabaseHelper.SERIAL_INFO_TIME, bilebean.getTime());
		dataModel.insert(DatabaseHelper.TABLE_BILE, values);
		
	}
	
	/**查询胆汁**/
	public ArrayList<BileMsgBean> getBileCountByName(String liverNum, String searchTime){
		ArrayList<BileMsgBean> list = null;
		String[] selectionArgs = new String[]{liverNum,searchTime};			
		if(null != dataModel){			
			Cursor cursor = dataModel.query("select * from " + DatabaseHelper.TABLE_BILE + 
					" where " + DatabaseHelper.COLUMN_LIVER_NUM + " =?" + " and "+ DatabaseHelper.SERIAL_INFO_TIME + " >=? " + " ORDER BY " + DatabaseHelper.SERIAL_INFO_TIME, selectionArgs);
			if(null != cursor){
				list = new ArrayList<BileMsgBean>();
				BileMsgBean bileMsgBean;
				while (cursor.moveToNext()){
					bileMsgBean = new BileMsgBean();
					bileMsgBean.setTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LIVER_NUM)));
					bileMsgBean.setBileCount(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BILE_COUNT)));
					bileMsgBean.setTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SERIAL_INFO_TIME)));
					list.add(bileMsgBean);
				}
				if(!cursor.isClosed()){
					cursor.close();
				}

			}
		}
		return list;
	}
	
	
	
	/****************************************************kidney*************************************************/
	/**保存串口上报的数据**/
	public long insertKidneyPerfusionInfo(KidneyInfoBean kidneyInfoBean){
		long result = -1;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_KIDNEY_NUM, kidneyInfoBean.getKidneyNum());
		values.put(DatabaseHelper.KIDNEY_INFO_LEFT_ARTERY_PDIAS, kidneyInfoBean.getLeftKidneyArtDiasPre());
		values.put(DatabaseHelper.KIDNEY_INFO_LEFT_ARTERY_PSYST, kidneyInfoBean.getLeftKidneyArtSystPre());
		values.put(DatabaseHelper.KIDNEY_INFO_LEFT_ARTERY_PMEAN, kidneyInfoBean.getLeftKidneyArtMeanPre());
		values.put(DatabaseHelper.KIDNEY_INFO_LEFT_ARTERY_SPEED, kidneyInfoBean.getLeftKidneyArtSpeed());
		values.put(DatabaseHelper.KIDNEY_INFO_LEFT_ARTERY_FREAL, kidneyInfoBean.getLeftKidneyArtFreal());
		values.put(DatabaseHelper.KIDNEY_INFO_LEFT_ARTERY_PFREQ, kidneyInfoBean.getLeftKidneyArtFreqPre());
		values.put(DatabaseHelper.KIDNEY_INFO_LEFT_ARTERY_RESISTINDEX, kidneyInfoBean.getLeftKidneyArtResistIndex());
		
		values.put(DatabaseHelper.KIDNEY_INFO_RIGHT_ARTERY_FREAL, kidneyInfoBean.getRightKidneyArtFreal());
		values.put(DatabaseHelper.KIDNEY_INFO_RIGHT_ARTERY_PDIAS, kidneyInfoBean.getRightKidneyArtDiasPre());
		values.put(DatabaseHelper.KIDNEY_INFO_RIGHT_ARTERY_PSYST, kidneyInfoBean.getRightKidneyArtSystPre());
		values.put(DatabaseHelper.KIDNEY_INFO_RIGHT_ARTERY_PMEAN, kidneyInfoBean.getRightKidneyArtPmean());
		values.put(DatabaseHelper.KIDNEY_INFO_RIGHT_ARTERY_PFREQ, kidneyInfoBean.getRightKidneyArtFreqPre());
		values.put(DatabaseHelper.KIDNEY_INFO_RIGHT_ARTERY_SPEED,kidneyInfoBean.getRightKidneyArtSpeed());
		values.put(DatabaseHelper.KIDNEY_INFO_RIGHT_ARTERY_RESISTINDEX, kidneyInfoBean.getRightKidneyArtResistIndex());
		values.put(DatabaseHelper.SERIAL_INFO_TEMP, kidneyInfoBean.getTemp());
		values.put(DatabaseHelper.SERIAL_INFO_TIME, kidneyInfoBean.getMsgTime());
		result = dataModel.insert(DatabaseHelper.TABLE_KIDNEY_PERFUSION_INFO, values);
		return result;
	}
	
		
	/**获取开始灌注时间段的灌注数据
	 * @param kidneyNum 肾脏id
	 * 
	 * **/
	public ArrayList<KidneyInfoBean> getKidneyPerfusionFromStarttime(String kidneyNum, String startTime){
		ArrayList<KidneyInfoBean> list = null;
		String[] selectionArgs = new String[]{kidneyNum,startTime};
		
		if(null != dataModel){
			Cursor cursor = dataModel.query("select * from " + DatabaseHelper.TABLE_KIDNEY_PERFUSION_INFO 
					+ " where " + DatabaseHelper.COLUMN_KIDNEY_NUM + " =?" 
					+ " and "+ DatabaseHelper.KIDNEY_INFO_TIME + " >=? " 
					, selectionArgs);
			if(null != cursor){
				list = new ArrayList<KidneyInfoBean>();
				KidneyInfoBean kidneyInfo;
				while(cursor.moveToNext()){
					kidneyInfo = new KidneyInfoBean();
					kidneyInfo.setKidneyNum(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_KIDNEY_NUM)));
					kidneyInfo.setLeftKidneyArtDiasPre(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KIDNEY_INFO_LEFT_ARTERY_PDIAS)));
					kidneyInfo.setLeftKidneyArtSystPre(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KIDNEY_INFO_LEFT_ARTERY_PSYST)));
					kidneyInfo.setLeftKidneyArtMeanPre(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KIDNEY_INFO_LEFT_ARTERY_PMEAN)));
					kidneyInfo.setLeftKidneyArtFreqPre(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KIDNEY_INFO_LEFT_ARTERY_PFREQ)));
					kidneyInfo.setLeftKidneyArtFreal(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KIDNEY_INFO_LEFT_ARTERY_FREAL)));
					kidneyInfo.setLeftKidneyArtSpeed(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KIDNEY_INFO_LEFT_ARTERY_SPEED)));
					kidneyInfo.setLeftKidneyArtResistIndex(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KIDNEY_INFO_LEFT_ARTERY_RESISTINDEX)));
					
					kidneyInfo.setTemp(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KIDNEY_INFO_TEMP)));
					kidneyInfo.setMsgTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KIDNEY_INFO_TIME)));
					kidneyInfo.setRightKidneyArtFreal(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KIDNEY_INFO_RIGHT_ARTERY_FREAL)));
					kidneyInfo.setRightKidneyArtDiasPre(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KIDNEY_INFO_RIGHT_ARTERY_PDIAS)));
					kidneyInfo.setRightKidneyArtSystPre(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KIDNEY_INFO_RIGHT_ARTERY_PSYST)));
					kidneyInfo.setRightKidneyArtPmean(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KIDNEY_INFO_RIGHT_ARTERY_PMEAN)));
					kidneyInfo.setRightKidneyArtFreqPre(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KIDNEY_INFO_RIGHT_ARTERY_PFREQ)));
					kidneyInfo.setRightKidneyArtSpeed(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KIDNEY_INFO_RIGHT_ARTERY_SPEED)));
					kidneyInfo.setRightKidneyArtResistIndex(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KIDNEY_INFO_RIGHT_ARTERY_RESISTINDEX)));					
					list.add(kidneyInfo);
				}
				
				if(cursor != null){
					cursor.close();
				}
			}
		}
		
		return list;
	}
	
	/**根据开始灌注日期和肾脏名称
	 * 时间必须是格式化的 2008-06-10***/
	public ArrayList<KidneyPerfusionLogBean> getKidneyPerfusionLogFromName(String start_search_date, String end_search_date){
		ArrayList<KidneyPerfusionLogBean> list = null;
		
		if(null != dataModel){
			Cursor cursor = null;
			if(start_search_date.equals(end_search_date)){
				String[] selectioinArgs_1 = new String[]{"%"+ start_search_date +"%"};
				cursor = dataModel.query("select * from " + DatabaseHelper.TABLE_KIDNEY_PERFUSION_LOG + " where " + DatabaseHelper.COLUMN_PERFUION_START_TIME+ " like ? " , selectioinArgs_1);
			}else{
				String[] selectionArgs_2 = new String[]{start_search_date ,end_search_date};
				cursor = dataModel.query("select * from " + DatabaseHelper.TABLE_KIDNEY_PERFUSION_LOG + " where " + DatabaseHelper.COLUMN_PERFUION_START_TIME+ " >=?" + " and " + DatabaseHelper.COLUMN_PERFUION_START_TIME + " <=? ", selectionArgs_2);
			}
			
			if(null != cursor){
				list = new ArrayList<KidneyPerfusionLogBean>();
				KidneyPerfusionLogBean perfusionLogBean;
				int index = 1;
				while(cursor.moveToNext()){
					perfusionLogBean = new KidneyPerfusionLogBean();
					perfusionLogBean.setKidneyName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_KIDNEY_NUM)));
					perfusionLogBean.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PERFUION_START_TIME)));
					perfusionLogBean.setLeftKidneyWeight(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KIDNEY_LEFT_WEIGHT)));
				    perfusionLogBean.setRightKidneyWeight(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KIDNEY_RIGHT_WEIGHT)));
					perfusionLogBean.setLeftKidneyMode(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LEFT_KIDNEY_ARTERY_MODE)));
					perfusionLogBean.setRightKidneyMode(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RIGHT_KIDNEY_ARTERY_MODE)));				
					perfusionLogBean.setIndex(index);
					list.add(perfusionLogBean);
					index++;
				}
				if(!cursor.isClosed()){
					cursor.close();
					
				}
			}
		}
		Log.i("PerfusionLogActivity", "list---"+ list.size());
		return list;
	}
	
	
	/**获取肾脏灌注按时间排序所有灌注记录**/
	public ArrayList<KidneyPerfusionLogBean> getAllKidneyPerfusionLog(){
		ArrayList<KidneyPerfusionLogBean> list = null;
		if(null != dataModel){
			Cursor cursor = dataModel.query("select * from " + DatabaseHelper.TABLE_KIDNEY_PERFUSION_LOG, null);
			if(null != cursor){
				list = new ArrayList<KidneyPerfusionLogBean>();
				KidneyPerfusionLogBean perfusionLogBean;
				int index = 1;
				while(cursor.moveToNext()){
					perfusionLogBean = new KidneyPerfusionLogBean();
					perfusionLogBean.setKidneyName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_KIDNEY_NUM)));
					perfusionLogBean.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PERFUION_START_TIME)));
					perfusionLogBean.setLeftKidneyWeight(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KIDNEY_LEFT_WEIGHT)));
					perfusionLogBean.setRightKidneyWeight(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KIDNEY_RIGHT_WEIGHT)));
					perfusionLogBean.setIndex(index);
					perfusionLogBean.setLeftKidneyMode(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LEFT_KIDNEY_ARTERY_MODE)));
					perfusionLogBean.setRightKidneyMode(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RIGHT_KIDNEY_ARTERY_MODE)));
					list.add(perfusionLogBean);
					index++;
				}
				if(!cursor.isClosed()){
					cursor.close();
				}
			}
		}		
		return list;
	}
	
	public int deleteKidneyPerfusion(String liverNum, String startTime){
		int result = -1;
		String whereClause = DatabaseHelper.COLUMN_KIDNEY_NUM + " =?" + " and "+ DatabaseHelper.KIDNEY_INFO_TIME + " >=? ";
		String[] whereArgs = new String[]{liverNum,startTime};
		if(null != dataModel){
			 result = dataModel.delete(DatabaseHelper.TABLE_KIDNEY_PERFUSION_INFO, whereClause, whereArgs);
		}
		return result;
	}
	
	public int deleteKidneyPerfusionLog(String liverNum){
		int result = -1;
		String whereClause = DatabaseHelper.COLUMN_KIDNEY_NUM + " =?";
		String[] whereArgs = new String[]{liverNum};
		if(null != dataModel){
			 result = dataModel.delete(DatabaseHelper.TABLE_KIDNEY_PERFUSION_LOG, whereClause, whereArgs);
		}
		return result;
	}
	
	public long insertKidneyPerfusionLog(KidneyPerfusionLogBean perfusionLogBean){
		long result = -1;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_KIDNEY_NUM, perfusionLogBean.getKidneyName());
		values.put(DatabaseHelper.KIDNEY_LEFT_WEIGHT, perfusionLogBean.getLeftKidneyWeight());
		values.put(DatabaseHelper.KIDNEY_RIGHT_WEIGHT, perfusionLogBean.getRightKidneyWeight());
		values.put(DatabaseHelper.COLUMN_PERFUION_START_TIME, perfusionLogBean.getStartTime());
		values.put(DatabaseHelper.COLUMN_RIGHT_KIDNEY_ARTERY_MODE, perfusionLogBean.getRightKidneyMode());
		values.put(DatabaseHelper.COLUMN_LEFT_KIDNEY_ARTERY_MODE, perfusionLogBean.getLeftKidneyMode());
		result = dataModel.insert(DatabaseHelper.TABLE_KIDNEY_PERFUSION_LOG, values);
		return result;
	}

	
	/***
	 * 断开数据库
	 */
	public void stopDB(){
		Log.i("BioConsoleApplication", "--DataBaseMgr stop--");
		try {
			dataModel.close();
			if(null != mAlarmMsgList){
				mAlarmMsgList.clear();
				mAlarmMsgList = null;
			}
			if(null != mInstance){
				mInstance = null;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
