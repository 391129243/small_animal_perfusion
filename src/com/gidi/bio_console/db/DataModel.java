package com.gidi.bio_console.db;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
/***
 * 单例
 * @author Administrator
 *
 */
public class DataModel {

    private static Context mContext = null;
    private SQLiteDatabase mSqLiteDatabase;
    private static DataModel mInstance;
    
    public static DataModel getInstance(Context context){
    	if(null == mInstance){
    		mInstance = new DataModel(context);
    	}
    	return mInstance;
    }
    
    private DataModel (Context context){
    	mContext = context;
    	//获取数据库的读写对象实际打开数据库
    	mSqLiteDatabase = DatabaseHelper.getInstance(mContext).getWritableDatabase();

    }
    
    /**
     * 插入数据
     * @param tablename
     * @param values
     */
    public long insert(String tablename, ContentValues values){
    	long result = -1;
    	if(mSqLiteDatabase.isOpen()){
    		result = mSqLiteDatabase.insert(tablename, null, values);
    	}else{
    		mSqLiteDatabase = DatabaseHelper.getInstance(mContext).getWritableDatabase();
    		result = mSqLiteDatabase.insert(tablename, null, values);
    	}
    	return result;
    }
    
    /***
     * 删除数据
     */
    public int delete(String table, String whereClause, String[] whereArgs){
    	int result = -1;
    	if(mSqLiteDatabase.isOpen()){   
    		result = mSqLiteDatabase.delete(table, whereClause, whereArgs);
    	}else{
    		mSqLiteDatabase = DatabaseHelper.getInstance(mContext).getWritableDatabase();
    		result = mSqLiteDatabase.delete(table, whereClause, whereArgs);
    	}
    	return result;
    }
       
    /**
     * 更新数据
     */
    public int update(String table, ContentValues values, String whereClause, String[] whereArgs){
    	int result = -1;
    	if(mSqLiteDatabase.isOpen()){
    		result = mSqLiteDatabase.update(table, values, whereClause, whereArgs);
    	}
    	return result;
    } 
    
    
    /***
     * 查询数据
     * 返回查询的游标
     */
    
    public Cursor query(String sql, String[] selectionArgs){
    	if(mSqLiteDatabase.isOpen()){
    		Cursor cursor = mSqLiteDatabase.rawQuery(sql, selectionArgs);
    		return cursor;
    	}
    	return null;
    }
    
    public int delete(String sql, String[] selectionArgs){
    	int result = -1;
    	if(mSqLiteDatabase.isOpen()){
    		mSqLiteDatabase.execSQL(sql,selectionArgs);
    	}
    	return result;
    }

	/**
	 * 关闭数据库
	 */
	public void close(){
		try {
			mSqLiteDatabase.close();
			DatabaseHelper.getInstance(mContext).close();
		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
		}
	}
}
