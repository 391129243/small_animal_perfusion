package com.gidi.bio_console.db;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;


public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "Bio_Console_db";
	private static final int DATABASE_VERSION = 1;
	private static DatabaseHelper mInstance;
	//通用
	public static final String COLUMN_ID = "id";//自增id列
	public static final String COLUMN_LIVER_NUM = "liver_num";
	public static final String COLUMN_KIDNEY_NUM = "kidney_num";
	
	/**1、报警信息表**/
	public static final String TABLE_ALARM_INFO = "alarm_info_table";	
	/**报警信息表的各项列名**/
	public static final String COLUMN_LIVER_WEIGHT = "liver_weight";	
	public static final String COLUMN_MESSAGE_TYPE = "message_type";//消息类型
	public static final String COLUMN_MESSAGE_TIME = "message_time";//消息时间列
	public static final String COLUMN_MESSAGE_CONTENT = "message_content";//消息内容
	public static final String COLUMN_MESSAGE_PUMP_TYPE = "message_pump_type";
	
	/**2、创建报警信息表**/
	private static final String SQL_CREATE_ALARM_INFO_TABLE =
	            "CREATE TABLE IF NOT EXISTS " + TABLE_ALARM_INFO + " ("
	                    + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
	                    + COLUMN_LIVER_NUM + " TEXT, "
	                    + COLUMN_KIDNEY_NUM + " TEXT, "
	                    + COLUMN_MESSAGE_TYPE + " VARCHAR(20) NOT NULL, "
	                    + COLUMN_MESSAGE_CONTENT + " VARCHAR(20) NOT NULL, "
	                    + COLUMN_MESSAGE_PUMP_TYPE + " INTEGER DEFAULT 0, "
	                    + COLUMN_MESSAGE_TIME + " DATETIME);";
	
	
	/**3、各项指标的数据表的各列名称**/
	public static final String TABLE_SERIAL_INFO = "serial_info_table";
	public static final String SERIAL_INFO_TIME = "time";
	public static final String SERIAL_INFO_TEMP = "temp";
	public static final String SERIAL_INFO_ARTERY_PDIAS = "artery_dias";
	public static final String SERIAL_INFO_ARTERY_PSYST = "artery_psyst"; 
	public static final String SERIAL_INFO_ARTERY_PMEAN = "artery_pmean";
	public static final String SERIAL_INFO_ARTERY_PFREQ = "artery_pfreq";
	public static final String SERIAL_INFO_ARTERY_SPEED = "artery_speed";

	public static final String SERIAL_INFO_ARTERY_FREAL = "artery_freal";
	public static final String SERIAL_INFO_VEIN_FREAL = "vein_freal";
	public static final String SERIAL_INFO_VEIN_PREAL = "vein_preal";
	public static final String SERIAL_INFO_VEIN_SPEED = "vein_speed";
	public static final String SERIAL_INFO_ARTERY_RESISTINDEX = "artery_resistindex";
	public static final String SERIAL_INFO_VEIN_RESISTINDEX = "vein_resistindex";
	public static final String SERIAL_INFO_ARTERY_EHBF = "artery_ehbf";	
	public static final String SERIAL_INFO_VEIN_EHBF = "vein_ehbf";

	
	private static final String SQL_CREATE_SERIAL_INFO_TABLE = "CREATE TABLE IF NOT EXISTS "
						+ TABLE_SERIAL_INFO + " ("
						+ COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
						+ COLUMN_LIVER_NUM + " TEXT, "
						+ SERIAL_INFO_ARTERY_PDIAS + " TEXT, "
						+ SERIAL_INFO_ARTERY_PSYST + " TEXT, "
						+ SERIAL_INFO_ARTERY_PMEAN + " TEXT, "
						+ SERIAL_INFO_ARTERY_PFREQ + " TEXT, "
						+ SERIAL_INFO_ARTERY_SPEED + " TEXT, "
						+ SERIAL_INFO_ARTERY_FREAL + " TEXT, "						
						+ SERIAL_INFO_VEIN_FREAL + " TEXT, "
						+ SERIAL_INFO_VEIN_PREAL + " TEXT,"
						+ SERIAL_INFO_VEIN_SPEED + " TEXT,"
						+ SERIAL_INFO_VEIN_RESISTINDEX + " TEXT,"
						+ SERIAL_INFO_ARTERY_RESISTINDEX + " TEXT,"
						+ SERIAL_INFO_ARTERY_EHBF + " TEXT, "
						+ SERIAL_INFO_VEIN_EHBF + " TEXT,"
						+ SERIAL_INFO_TEMP + " TEXT,"
						+ SERIAL_INFO_TIME + " DATETIME);";
	
	//肾脏灌注信息
	public static final String TABLE_KIDNEY_PERFUSION_INFO = "kidney_perfusion_info_table";
	public static final String KIDNEY_INFO_TIME = "time";
	public static final String KIDNEY_INFO_TEMP = "temp";
	public static final String KIDNEY_INFO_LEFT_ARTERY_PDIAS = "LeftKidneyArtery_Dias";
	public static final String KIDNEY_INFO_LEFT_ARTERY_PSYST = "LeftKidneyArtery_Syst"; 
	public static final String KIDNEY_INFO_LEFT_ARTERY_PMEAN = "LeftKidney_Artery_Pmean";
	public static final String KIDNEY_INFO_LEFT_ARTERY_PFREQ = "LeftKidney_Artery_Freq";
	public static final String KIDNEY_INFO_RIGHT_ARTERY_PDIAS = "RightKidneyArtery_Dias";
	public static final String KIDNEY_INFO_RIGHT_ARTERY_PSYST = "RightKidneyArtery_Syst";
	public static final String KIDNEY_INFO_RIGHT_ARTERY_PFREQ = "RightKidneyArtery_Freq";
	public static final String KIDNEY_INFO_RIGHT_ARTERY_PMEAN = "RightKidneyArtery_Pmean";
	
	public static final String KIDNEY_INFO_LEFT_ARTERY_FREAL = "LeftKidneyArtery_Freal";
	public static final String KIDNEY_INFO_RIGHT_ARTERY_FREAL = "RightKidneyArtery_Freal";
	public static final String KIDNEY_INFO_LEFT_ARTERY_SPEED = "LeftKidneyArtery_Speed";
	public static final String KIDNEY_INFO_RIGHT_ARTERY_SPEED = "RightKidneyArtery_Speed";
	
	public static final String KIDNEY_INFO_LEFT_ARTERY_RESISTINDEX = "LeftKidneyAartery_Resistindex";
	public static final String KIDNEY_INFO_RIGHT_ARTERY_RESISTINDEX = "RightKidneyArtery_Resistindex";
	
	public static final String KIDNEY_LEFT_WEIGHT = "left_kidney_weight";
	public static final String KIDNEY_RIGHT_WEIGHT = "right_kidney_weight";

	private static final String SQL_CREATE_KIDNEY_PERFUSION_INFO_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_KIDNEY_PERFUSION_INFO + " ("
			+ COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_KIDNEY_NUM + " TEXT, "
			+ KIDNEY_INFO_LEFT_ARTERY_PDIAS + " TEXT, "
			+ KIDNEY_INFO_LEFT_ARTERY_PSYST + " TEXT, "
			+ KIDNEY_INFO_LEFT_ARTERY_PFREQ + " TEXT, "
			+ KIDNEY_INFO_RIGHT_ARTERY_PDIAS + " TEXT, "
			+ KIDNEY_INFO_RIGHT_ARTERY_PSYST + " TEXT, "
			+ KIDNEY_INFO_RIGHT_ARTERY_PFREQ + " TEXT, "
			+ KIDNEY_INFO_LEFT_ARTERY_PMEAN + " TEXT, "
			+ KIDNEY_INFO_RIGHT_ARTERY_PMEAN + " TEXT, "
			+ KIDNEY_INFO_LEFT_ARTERY_FREAL + " TEXT, "
			+ KIDNEY_INFO_RIGHT_ARTERY_FREAL + " TEXT, "
			+ KIDNEY_INFO_LEFT_ARTERY_SPEED + " TEXT, "
			+ KIDNEY_INFO_RIGHT_ARTERY_SPEED + " TEXT, "						
			+ KIDNEY_INFO_LEFT_ARTERY_RESISTINDEX + " TEXT, "
			+ KIDNEY_INFO_RIGHT_ARTERY_RESISTINDEX + " TEXT,"
			+ KIDNEY_INFO_TEMP + " TEXT,"
			+ KIDNEY_INFO_TIME + " DATETIME);";
	

	
	
	
	/**5、崩溃日志数据表**/
	public static final String TABLE_CRASH = "crash_table";
	/**崩溃日志数据表的各列**/
	public static final String CRASH_MESSAGE = "crash_message";
	public static final String CRASH_FLAG = "crash_flag";
	
	private static final String SQL_CREATE_CRASH_TABLE ="CREATE TABLE IF NOT EXISTS "
						+ TABLE_CRASH + " ("
						+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
						+ CRASH_MESSAGE + " TEXT, "
		                + CRASH_FLAG + " INTEGER); ";
	
	/**6、历史记录肝脏名称和时间、肝动脉灌注模式，门静脉灌注模式，灌注时长的表**/
	public static final String TABLE_ORGAN_PERFUSION_LOG = "organ_perfusion_log_table";
	public static final String TABLE_KIDNEY_PERFUSION_LOG = "kidney_perfusion_log_table";
	public static final String COLUMN_PERFUION_START_TIME = "perfusion_time";
	public static final String COLUMN_PERFUSION_END_TIME = "perfusion_end_time";
	public static final String COLUMN_ARTERY_PERFUSION_MODE = "artery_perfusion_mode";
	public static final String COLUMN_VEIN_PERFUSION_MODE = "vein_perfusion_mode";
	public static final String COLUMN_LEFT_KIDNEY_ARTERY_MODE = "LeftKidneyArtery_Perfusion_Mode";
	public static final String COLUMN_RIGHT_KIDNEY_ARTERY_MODE = "RightKidneyArtery_Perfusion_Mode";
	public static final String COLUMN_PERFUSION_DURATION = "perusion_duration";

	
	private static final String SQL_CREATE_ORGAN_LOG_TABLE = "CREATE TABLE IF NOT EXISTS "
						+ TABLE_ORGAN_PERFUSION_LOG + " ("
						+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
						+ COLUMN_LIVER_NUM + " TEXT, "
	                    + COLUMN_LIVER_WEIGHT + " TEXT, "
						+ COLUMN_ARTERY_PERFUSION_MODE + " TEXT, "
						+ COLUMN_VEIN_PERFUSION_MODE + " TEXT, "
						+ COLUMN_PERFUSION_DURATION + " TEXT, "
						+ COLUMN_PERFUION_START_TIME + " TEXT, "
			            + COLUMN_PERFUSION_END_TIME + " TEXT);";
	
	private static final String SQL_CREATE_KIDNEY_LOG_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_KIDNEY_PERFUSION_LOG + " ("
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_KIDNEY_NUM + " TEXT, "
            + KIDNEY_LEFT_WEIGHT + " TEXT, "
            + KIDNEY_RIGHT_WEIGHT + " TEXT, "
			+ COLUMN_LEFT_KIDNEY_ARTERY_MODE + " TEXT, "
			+ COLUMN_RIGHT_KIDNEY_ARTERY_MODE + " TEXT, "
			+ COLUMN_PERFUSION_DURATION + " TEXT, "
			+ COLUMN_PERFUION_START_TIME + " TEXT, "
            + COLUMN_PERFUSION_END_TIME + " TEXT);";

	/**7、在主界面添加关于灌注肝脏的血气检测时间根据血气检测时间进行排序**/
	public static final String TABLE_BLOOD_GAS_SAMPLING_TIME = "blood_gas_sampling_time_table";
	public static final String COLUMN_BLOOD_GAS_SAMPLING_TIME = "blood_gas_sampling_time";
	public static final String COLUMN_BLOOD_GAS_CHECK = "time_ischeck";
	private static final String SQL_CREATE_BLOOD_GAS_SAMPLING_TIME_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_BLOOD_GAS_SAMPLING_TIME + " ("
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_LIVER_NUM + " TEXT, "
            + COLUMN_BLOOD_GAS_SAMPLING_TIME + " TEXT,"
            + COLUMN_BLOOD_GAS_CHECK + " INTEGER);";
	
	
	/**血气检测记录值表**/
	public static final String TABLE_BLOOD_GAS = "blood_gas_table";
	public static final String COLUMN_BLOODGAS_PARAM_AST = "blood_gas_asat_got";
	public static final String COLUMN_BLOODGAS_PARAM_ALT = "blood_gas_alat_gpt";
	public static final String COLUMN_BLOODGAS_PARAM_GLU = "blood_gas_glu";
	public static final String COLUMN_BLOODGAS_PARAM_PH = "blood_ph";
	public static final String COLUMN_BLOODGAS_PARAM_PO2 = "blood_po2";
	public static final String COLUMN_BLOODGAS_PARAM_PCO2 = "blood_pco2";
	//碳酸氢盐
	public static final String COLUMN_BLOODGAS_PARAM_BICARBONATE = "blood_bicarbonate";
	public static final String COLUMN_BLOODGAS_PARAM_HCT = "blood_hct";
	//乳酸
	public static final String COLUMN_BLOODGAS_PARAM_LAC = "blood_lac";
	public static final String COLUMN_SAMPLE_TIME = "sample_time";
	//创建血气记录表
	private static final String SQL_CREATE_BLOOD_GAS_TABLE = "CREATE TABLE IF NOT EXISTS "
						+ TABLE_BLOOD_GAS + " ("
						+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
						+ COLUMN_LIVER_NUM + " TEXT, "
						+ COLUMN_BLOODGAS_PARAM_AST + " TEXT, "
						+ COLUMN_BLOODGAS_PARAM_ALT + " TEXT, "
						+ COLUMN_BLOODGAS_PARAM_GLU + " TEXT, "
						+ COLUMN_BLOODGAS_PARAM_PH + " TEXT, "
						+ COLUMN_BLOODGAS_PARAM_PO2 + " TEXT, "
						+ COLUMN_BLOODGAS_PARAM_PCO2 + " TEXT, "
						+ COLUMN_BLOODGAS_PARAM_BICARBONATE + " TEXT, "
						+ COLUMN_BLOODGAS_PARAM_HCT + " TEXT, "
						+ COLUMN_BLOODGAS_PARAM_LAC + " TEXT, "
			            + COLUMN_SAMPLE_TIME + " TEXT);";
	
	
	/**8、胆汁记录表**/
	public static final String TABLE_BILE = "table_bile";
	public static final String COLUMN_BILE_COUNT = "bile_count";
	private static final String SQL_CREATE_BILE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_BILE + " ("
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_LIVER_NUM + " TEXT, "
			+ COLUMN_BILE_COUNT + " INTEGER, "
			+ SERIAL_INFO_TIME + " DATETIME);";
	
	//添加列
	private String ADD_COLUMN_BLOODGAS_LAC = "alter table blood_gas_table add column blood_lac text";	
	//SQLite的alter不支持添加多列
	//在灌注记录中添加列
	private String ADD_COLUMN_SERIAL_INFO_ARTERY_EHBF = "alter table serial_info_table add column artery_ehbf text";
	private String ADD_COLUMN_SERIAL_INFO_VEIN_EHBF = "alter table serial_info_table add column vein_ehbf text";	
	public DatabaseHelper(Context context) {
		super(context, getDatabaseName(DATABASE_NAME), null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	public static DatabaseHelper getInstance(Context context){
		if(null == mInstance){
			mInstance = new DatabaseHelper(context);
		}
		return mInstance;
	}
	
	public static int getVersion(){
		return DATABASE_VERSION;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		db.execSQL(SQL_CREATE_ALARM_INFO_TABLE);
		db.execSQL(SQL_CREATE_SERIAL_INFO_TABLE);
		db.execSQL(SQL_CREATE_CRASH_TABLE);
		db.execSQL(SQL_CREATE_ORGAN_LOG_TABLE);
		db.execSQL(SQL_CREATE_BLOOD_GAS_SAMPLING_TIME_TABLE);
		db.execSQL(SQL_CREATE_BLOOD_GAS_TABLE);
		db.execSQL(SQL_CREATE_BILE_TABLE);
		db.execSQL(SQL_CREATE_KIDNEY_PERFUSION_INFO_TABLE);
		db.execSQL(SQL_CREATE_KIDNEY_LOG_TABLE);

	}

	/**当数据库的version升级时调用*/
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	

	/**查询某表最后一条id**/
	public int queryLastInsertId(String tableName){
		int lastMsgId = 0;
		Cursor c = null;
		SQLiteDatabase db = null;
		try {
			db = getReadableDatabase();
			c = db.rawQuery("select last_insert_rowid() from "
                    + tableName, null);
			if(c.moveToFirst()){
				lastMsgId = c.getInt(0);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			if(null != c){
				c.close();
			}
		}
		return lastMsgId;
	}
	
    private static String getDatabaseName(String name){
        String databasename = name;
        boolean isSdcardEnable = false;
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){//SDCard是否插入
             isSdcardEnable = true;
        }
        String dbPath = null;
        if(isSdcardEnable){
             dbPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Bio_Console/database/";
        }else{//未插入SDCard，建在内存中

        }
        File dbp = new File(dbPath);
        if(!dbp.exists()){
            dbp.mkdirs();
        }
        databasename = dbPath + databasename;
        return databasename;
    }

	
}
