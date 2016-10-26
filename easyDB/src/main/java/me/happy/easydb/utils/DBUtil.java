package me.happy.easydb.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.text.TextUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
/**
 * Created by Administrator on 2016/10/10 0010.
 */
public class DBUtil {

    public static String getDataBaseFilePath(Context context,String dbName,String dbDirName,int rawResource){
        //dbDirName 为null时使用默认路径/data/data/packagename/database下面
        String dbFilePath = "";
        if (TextUtils.isEmpty(dbDirName) || !hasSdcard()) {
            dbFilePath = context.getDatabasePath(dbName).getAbsolutePath();
        } else {
            dbFilePath = Environment.getExternalStorageDirectory().getPath()
                    + File.separator + dbDirName + File.separator + dbName;
        }
        File file = new File(dbFilePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if(rawResource!=0&&!checkDataBase(dbFilePath)){
            FileOutputStream os = null;
            try{
                os = new FileOutputStream(dbFilePath);//得到数据库文件的写入流
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }
            InputStream is = context.getResources().openRawResource(rawResource);//得到数据库文件的数据流
            byte[] buffer = new byte[1024];
            int count = 0;
            try{
                while((count=is.read(buffer))>0){
                    os.write(buffer, 0, count);
                    os.flush();
                }
            }catch(IOException e){
                e.printStackTrace();
            }finally {
                try{
                    is.close();
                    os.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        return dbFilePath;
    }


    /**
     * 检查数据库是否创建
     * @param dbFilePath 数据库路径
     */
    public static boolean checkDataBase(String dbFilePath){
        SQLiteDatabase checkDB = null;
        try{
            checkDB = SQLiteDatabase.openDatabase(dbFilePath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            e.printStackTrace();
        }
        if(checkDB!=null){
            checkDB.close();
        }
        return checkDB !=null?true:false;
    }


    /**
     * 检测Sdcard是否存在
     */
    private static boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }
}
