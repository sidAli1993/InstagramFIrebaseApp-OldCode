package com.myvuapp.socialapp.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;

import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*******************************************************************************
 * Copyright (c) 2020. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 * Written by alimirza00 , 04 2020
 *
 *  Programmer Info:
 *  FullName: Mirza ALi
 *  E-Mail: alimirza00@gmail.com
 ******************************************************************************/


public class Utils {

    private Context context;
     ProgressDialog dialog;
    public Utils(Context context){
        this.context=context;
    }
    public void showWarningToast(String message){
        FancyToast.makeText(context,message, FancyToast.LENGTH_LONG, FancyToast.WARNING,false).show();
    }
    public void showSuccessToast(String message){
        FancyToast.makeText(context,message, FancyToast.LENGTH_LONG, FancyToast.SUCCESS,false).show();
    }
    public void showInfoToast(String message){
        FancyToast.makeText(context,message, FancyToast.LENGTH_LONG, FancyToast.INFO,false).show();
    }
    public void showErrorToast(String message){
        FancyToast.makeText(context,message, FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show();
    }
    public void showConfusingToast(String message){
        FancyToast.makeText(context,message, FancyToast.LENGTH_LONG, FancyToast.CONFUSING,false).show();
    }
    public void showDefaultToast(String message){
        FancyToast.makeText(context,message, FancyToast.LENGTH_LONG, FancyToast.DEFAULT,false).show();
    }
    public boolean isEmpty(String value){
        boolean empty=false;
        if (TextUtils.isEmpty(value)){
            empty=true;
        }else {
            empty=false;
        }
        return empty;
    }
    public String date(String datee) throws ParseException {
        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date newDate = spf.parse(datee);
        spf = new SimpleDateFormat("yyyy-MM-dd");
        String newDateString = spf.format(newDate);
        return newDateString;
    }
    public String gettime(){
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }
    public String getdate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }
}
