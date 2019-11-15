package org.techtown.yonseibus;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements onTabItemSelectedListener{


    static final String ROOT_DIR = "/data/data/org.techtown.yonseibus/";

    Fragment1 fragment1;
    Fragment2 fragment2;

    BottomNavigationView bottomNavigationView;

    SQLiteDatabase database;


    SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm");
    int nowTime;
    public int fastest30,fastest34;

    int beforeBottomPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        beforeBottomPosition=0;



        final Context mContext = this;
        boolean bResult = isCheckDB();
        if(!bResult){
            copyDB(mContext);
        }

        createDatabase();



        fragment1 = new Fragment1();
        fragment2 = new Fragment2();

        getFastestTime();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.tab1:
                                getFastestTime();
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();
                                beforeBottomPosition=0;

                                return true;
                            case R.id.tab2:
                                getFastestTime();
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment2).commit();
                                beforeBottomPosition=1;

                                return true;
                            case R.id.tab3:
                                getFastestTime();
                                if(beforeBottomPosition==0){
                                    getSupportFragmentManager().beginTransaction().detach(fragment1).attach(fragment1).commit();
                                    beforeBottomPosition=0;
                                }
                                else if(beforeBottomPosition==1){
                                    getSupportFragmentManager().beginTransaction().detach(fragment2).attach(fragment2).commit();
                                    beforeBottomPosition=1;
                                }
                        }
                        return false;
                    }
                }

        );


    }

    public void onTabSelected(int position){
        if(position == 0){
            bottomNavigationView.setSelectedItemId(R.id.tab1);
        }
        else if(position == 1){
            bottomNavigationView.setSelectedItemId(R.id.tab2);
        }
        else if(position == 2){
            bottomNavigationView.setSelectedItemId(R.id.tab3);
        }
    }

    public boolean isCheckDB(){
        String filePath = ROOT_DIR + "databases/busData.db";
        File file = new File(filePath);

        if(file.exists()){
            return true;

        }
        return false;
    }

    public void copyDB(Context mContext){
        AssetManager manager = mContext.getAssets();
        String folderPath = ROOT_DIR + "databases";
        String filePath = folderPath + "/busData.db";
        File folder = new File(folderPath);
        File file = new File(filePath);

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try{
            InputStream is = manager.open("busData.db");
            BufferedInputStream bis = new BufferedInputStream(is);

            if(folder.exists()){

            }else{
                folder.mkdirs();
            }

            if(file.exists()){
                file.delete();
                file.createNewFile();
            }

            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            int read=-1;
            byte[] buffer = new byte[1024];
            while((read = bis.read(buffer,0,1024)) != -1){
                bos.write(buffer,0,read);
            }

            bos.flush();;

            bos.close();
            fos.close();
            bis.close();
            is.close();

        }catch(IOException e){

        }
    }


    public void createDatabase(){

        database=openOrCreateDatabase("busData.db",MODE_PRIVATE,null);

    }

    public void getNowTime(){
        Date date = new Date(System.currentTimeMillis());

        nowTime = Integer.parseInt(timeFormat.format(date));

    }

    public void getFastestTime(){
        getNowTime();

        Cursor cursor = database.rawQuery("select MIN(busTime) from busTime where ( busNum = 30 ) and ( busTime >= "+ nowTime + ")", null);
        cursor.moveToNext();

        fastest30 = cursor.getInt(0);


        cursor = database.rawQuery("select MIN(busTime) from busTime where ( busNum = 34 ) and ( busTime >= "+ nowTime + " )", null);
        cursor.moveToNext();

        fastest34 = cursor.getInt(0);

        cursor.close();
    }

    public void setTimeList(ViewGroup viewGroup, Context context){
        getNowTime();
        Cursor cursor = database.rawQuery("select COUNT(busTime) from busTime where ( busNum = 30 ) ", null);
        cursor.moveToNext();
        int num = cursor.getInt(0);
        boolean setFastestBus = false;

        cursor = database.rawQuery("select busTime, alarmOnOff from busTime where ( busNum = 30 ) ", null);

        TableLayout buslistView = (TableLayout)viewGroup.findViewById(R.id.busTable30);

        for(int i = 1; i < num; i++) {
            cursor.moveToNext();
            int getBusTime = cursor.getInt(0);
            String getBusTimeToString = changeToString(getBusTime);
            String getAlarm = cursor.getString(1);

            TextView busTime = new TextView(context);
            TableRow.LayoutParams bt = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT, 4f);
            busTime.setLayoutParams(bt);
            busTime.setText(getBusTimeToString);
            busTime.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);


            final ToggleButton busAlarm = new ToggleButton(context);
            TableRow.LayoutParams ba = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT, 1f);
            busAlarm.setLayoutParams(ba);
            busAlarm.setId(getBusTime*100+30);
            busAlarm.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            if(getAlarm.equals("ON")){
                busAlarm.setChecked(true);
                setAlarm(busAlarm.getId());
            }
            busAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int searchTime = v.getId();
                    searchTime /= 100;
                    Cursor searchCursor = database.rawQuery("select busTime, alarmOnOff from busTime where ( busNum = 30 ) and (busTime = " + searchTime +")", null);
                    searchCursor.moveToNext();

                    if(searchCursor.getString(1).equals("ON")){
                        database.execSQL("update busTime set alarmOnOff = 'OFF' where (busNum = 30) and (busTime = " + searchTime + ")");
                        cancelAlarm(v.getId());
                    }
                    else{
                        database.execSQL("update busTime set alarmOnOff = 'ON' where ( busNum = 30 ) and (busTime = " + searchTime +")");
                        setAlarm(v.getId());
                    }

                }
            });

            TableRow tr = new TableRow(context);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            if(nowTime <= getBusTime && !setFastestBus){
                tr.setBackgroundColor(0xff33ff33);
                setFastestBus = true;
            }
            tr.setLayoutParams(lp);
            tr.addView(busTime);
            tr.addView(busAlarm);

            buslistView.addView(tr);
        }

        cursor = database.rawQuery("select COUNT(busTime) from busTime where ( busNum = 34) ", null);
        cursor.moveToNext();
        num = cursor.getInt(0);

        cursor = database.rawQuery("select busTime, alarmOnOff from busTime where ( busNum = 34 ) ", null);
        buslistView = (TableLayout)viewGroup.findViewById(R.id.busTable34);
        setFastestBus = false;

        for(int i = 1; i < num; i++) {
            cursor.moveToNext();
            int getBusTime = cursor.getInt(0);
            String getBusTimeToString = changeToString(getBusTime);
            String getAlarm = cursor.getString(1);

            TextView busTime = new TextView(context);
            TableRow.LayoutParams bt = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT, 4f);
            busTime.setLayoutParams(bt);
            busTime.setText(getBusTimeToString);
            busTime.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);


            final ToggleButton busAlarm = new ToggleButton(context);
            TableRow.LayoutParams ba = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT, 1f);
            busAlarm.setLayoutParams(ba);
            busAlarm.setId(getBusTime*100+34);
            busAlarm.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            if(getAlarm.equals("ON")){
                busAlarm.setChecked(true);
                setAlarm(busAlarm.getId());
            }
            busAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int searchTime = v.getId();
                    searchTime /= 100;
                    Cursor searchCursor = database.rawQuery("select busTime, alarmOnOff from busTime where ( busNum = 34 ) and (busTime = " + searchTime +")", null);
                    searchCursor.moveToNext();

                    if(searchCursor.getString(1).equals("ON")){
                        database.execSQL("update busTime set alarmOnOff = 'OFF' where (busNum = 34) and (busTime = " + searchTime + ")");
                        cancelAlarm(v.getId());
                    }
                    else{
                        database.execSQL("update busTime set alarmOnOff = 'ON' where ( busNum = 34 ) and (busTime = " + searchTime +")");

                        setAlarm(v.getId());
                    }

                }
            });


            TableRow tr = new TableRow(context);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            if(nowTime <= getBusTime && !setFastestBus){
                tr.setBackgroundColor(0xff33ff33);
                setFastestBus = true;
            }
            tr.setLayoutParams(lp);
            tr.addView(busTime);
            tr.addView(busAlarm);

            buslistView.addView(tr);
        }
    }

    public String changeToString(int Time){
        int high = Time/100;
        int low = Time%100;
        String highString = Integer.toString(high);
        String lowString = Integer.toString(low);

        if(high < 10){
            highString = "0" + highString;
        }
        if(low < 10){
            lowString = "0" + lowString;
        }

        return highString+":"+lowString;
    }


    public void setAlarm(int hourMinBus){
        int hour = hourMinBus/10000;
        int minute = (hourMinBus%10000)/100;

        // 현재 지정된 시간으로 알람 시간 설정
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.add(Calendar.MINUTE,-10);
        calendar.set(Calendar.SECOND, 0);

        // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
        if(calendar.before(Calendar.getInstance())){
            calendar.add(Calendar.DATE,1);
        }


        diaryNotification(calendar, hourMinBus);
    }

    public void diaryNotification(Calendar calendar, int hourMinBus) {

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("hourMinBus", hourMinBus);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, hourMinBus, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        if (alarmManager != null) {

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
        }



    }

    public void cancelAlarm(int hourMinBus){
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("hourMinBus", hourMinBus);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, hourMinBus, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if(PendingIntent.getBroadcast(this,hourMinBus,alarmIntent,0) != null && alarmManager != null){
            alarmManager.cancel(pendingIntent);
        }
    }

}

