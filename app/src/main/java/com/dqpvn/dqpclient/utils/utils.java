package com.dqpvn.dqpclient.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.LocaleList;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dqpvn.dqpclient.NavDrawerActivity;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.currentTimeMillis;

/**
 * Created by linh3t on 24/11/2017.
 */

public class utils {
    // variable to hold context
    private Context context;
    final private String TAG = getClass().getSimpleName();

//save the context recievied via constructor in a local variable

    public utils(Context context){
        this.context=context;
    }

    private static Pattern dateRegexPattern = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)");
    public static boolean isDate(String dateString) {
        Matcher dateMatcher = dateRegexPattern.matcher(dateString);

        if (dateMatcher.matches()) {

            dateMatcher.reset();

            if (dateMatcher.find()) {
                String day = dateMatcher.group(1);
                String month = dateMatcher.group(2);
                int year = Integer.parseInt(dateMatcher.group(3));

                if ("31".equals(day) &&
                        ("4".equals(month) || "6".equals(month) || "9".equals(month) ||
                                "11".equals(month) || "04".equals(month) || "06".equals(month) ||
                                "09".equals(month))) {
                    return false; // 1, 3, 5, 7, 8, 10, 12 has 31 days
                } else if ("2".equals(month) || "02".equals(month)) {
                    //leap year
                    if (year % 4 == 0) {
                        return !"30".equals(day) && !"31".equals(day);
                    } else {
                        return !"29".equals(day) && !"30".equals(day) && !"31".equals(day);
                    }
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean isExternalStorageReadable() {
        Boolean readable=false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            readable =true;
        }
        return readable;
    }

    public static Bitmap resizeImage(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    public static byte[] getByteArrayFromImageView(ImageView imgv, int CompressQuality){
        //Lay ra Bitmap tu ImageView
        BitmapDrawable drawable = (BitmapDrawable) imgv.getDrawable();
        Bitmap bmp = drawable.getBitmap();
        //Chuyen Bitmap sang Byte Array de truyen tai qua internet
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, CompressQuality, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public static void imageViewFromRealPath(ImageView imgView, String rPath){
        File file =new File(rPath);
        if(file.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imgView.setImageBitmap(myBitmap);
        }
    }

    public static String getRealPathFromURI(Context context,Uri contentURI) {
        Cursor cursor = context.getContentResolver().query(contentURI,
                null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        }
        cursor.moveToFirst();
        int idx = cursor
                .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public static byte[] getByteArrayFromBitmap(Bitmap bmp,int CompressQuality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, CompressQuality, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public static void checkAndRequestPermissions(Context context) {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission((Activity)context, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) context, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }
    }
    public static Boolean isBad(String CheckStr){
        if (StringUtils.isEmpty(CheckStr) || StringUtils.equals(CheckStr,null)){
            return true;
        }else{
            return false;
        }
    }

    public static long longGet(String str){
        if (isBad(str) || StringUtils.compare(str.toLowerCase(),"null")==0){
            return 0;
        }

        if (StringUtils.contains(str,"e") || StringUtils.contains(str,"E") && NumberUtils.isCreatable(str)){
            try{
                return Double.valueOf(str).longValue();
            }catch (NumberFormatException e){
                return 0;
            }
        }

        String s="";
        DecimalFormatSymbols symbols;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            symbols = new DecimalFormatSymbols(Locale.getDefault(Locale.Category.DISPLAY));
        }else{
            symbols = new DecimalFormatSymbols(Locale.getDefault());
        }
        String groupSep= String.valueOf(symbols.getGroupingSeparator());
        String decimalSep= String.valueOf(symbols.getDecimalSeparator());

        if (StringUtils.contains(str,decimalSep)){
            s=StringUtils.replace(str, String.valueOf(groupSep),"");
            String []arr=StringUtils.split(s,decimalSep);
            s=arr[0];
        }else{
            s=StringUtils.replace(str, String.valueOf(groupSep),"");
        }

        try{
            return Long.valueOf(s).longValue();
        }catch (NumberFormatException e){
            return 0;
        }
    }

    public static long longGet(Context context, EditText edt){
       return longGet(edt.getText()+"");
    }

    public static double doubleGet(String MustISDoubleString){
        if (isBad(MustISDoubleString) || StringUtils.compare(MustISDoubleString.toLowerCase(),"null")==0){
            return 0.0;
        }
        if (StringUtils.contains(MustISDoubleString,"e") || StringUtils.contains(MustISDoubleString,"E") && NumberUtils.isCreatable(MustISDoubleString)){
            try{
                return Double.valueOf(MustISDoubleString).doubleValue();
            }catch (NumberFormatException e){
                return 0.0;
            }
        }
        String s="";
        DecimalFormatSymbols symbols;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            symbols = new DecimalFormatSymbols(Locale.getDefault(Locale.Category.DISPLAY));
        }else{
            symbols = new DecimalFormatSymbols(Locale.getDefault());
        }
        String groupSep= String.valueOf(symbols.getGroupingSeparator());
        String decimalSep= String.valueOf(symbols.getDecimalSeparator());

        if (StringUtils.contains(MustISDoubleString,decimalSep)){
            s=StringUtils.replace(MustISDoubleString, String.valueOf(groupSep),"");
        }else{
            s=StringUtils.replace(MustISDoubleString, String.valueOf(groupSep),"");
            s=StringUtils.left(s+".000",s.length()+3);
        }
        try{
            return round(Double.valueOf(s).doubleValue(),2);
        }catch (NumberFormatException e){
            e.printStackTrace();
            return 0.00;
        }
    }

    public static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    public static int intGet(String str){
        String s=String.valueOf(longGet(str));
        try{
            return Integer.parseInt(s);
        }catch (NumberFormatException e){
            e.printStackTrace();
            return 0;
        }

    }

    public static String getEditText (Context context, EditText edtText){
        try{
            String txt;
            txt=edtText.getText().toString();
            return txt.trim();
        }
        catch(Exception e){
            Log.e("Error: ",e.toString());
            Toast.makeText(context, "Null...", Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    public static String getEditText (Context context, AutoCompleteTextView AutocompleteText){
        try{
            String txt;
            txt=AutocompleteText.getText().toString();
            return txt.trim();
        }
        catch(Exception e){
            Log.e("Error: ",e.toString());
            Toast.makeText(context, "Null...", Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    public static String getNumber(Context context, EditText edt){
        //cai daau dot(.) khon nan trong java that kho doi pho
        String str=edt.getText()+"";
        String s="";
        if (!isBad(str)){
            if (str.contains("\\.")){
                String [] array=str.split("\\.");
                for (int i=0;i<array.length;i++){
                    s+=array[i];
                }
                str=s;
            }
            return str.replaceAll(",","");
        }else{
            return "0";
        }
    }

    public static String getNumber(String str){
        //cai daau dot(.) khon nan trong java that kho doi pho
        String s="";
        if (!isBad(str)){
            if (str.contains("\\.")){
                String [] array=str.split("\\.");
                for (int i=0;i<array.length;i++){
                    s+=array[i];
                }
                str=s;
            }
            return str.replaceAll(",","");
        }else{
            return "0";
        }
    }

    public static String getCurrentTimeMiliS(){
        return String.valueOf(System.currentTimeMillis());
    }

    public static String loaiboDauVN(String str) {
        try {
            String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            String s=pattern.matcher(temp).replaceAll("").toLowerCase().replaceAll(" ", "-").replaceAll("đ", "d");
            String result = s.replaceAll("[-+().^:,]","");
            //thay the cac ky tu dac biet
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


    public static Bitmap XoayImage(String imagePath) throws FileNotFoundException{

        ExifInterface ei = null;
        try {
            ei = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
            Bitmap myBitmap = BitmapFactory.decodeFile(imagePath);
            return myBitmap;
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap = null;
        Bitmap myBitmap = BitmapFactory.decodeFile(imagePath);
        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = utils.rotateImage(myBitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = utils.rotateImage(myBitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = utils.rotateImage(myBitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = myBitmap;
        }
        return rotatedBitmap;
    }

    public static String getStringLeft(String StrToGet, String LeftForChar){
        String str="";
        if (!isBad(LeftForChar) && !isBad(StrToGet)){
            if (StrToGet.contains(LeftForChar)){
                String strArrtmp[];
                strArrtmp = StrToGet.split(LeftForChar);
                str=strArrtmp[0];
            }
        }
        return str;
    }

    public static String formatNumber(String str) {
        DecimalFormat formatter = new DecimalFormat("#,###.##");
        long lv = utils.longGet(str);
        String get_value = formatter.format(lv);
        return get_value;
    }

    public static String  DinhDangNgay(String sdate, String format) {
        try{
            if (sdate != null && !sdate.equals("")) {
                String strArrtmp[];
                if (sdate.contains("/")){
                    strArrtmp = sdate.split("/");
                } else if (sdate.contains("-")){
                    strArrtmp = sdate.split("-");
                } else {
                    return "";
                }
                if (format=="dd/mm/yyyy"){
                    String nam = strArrtmp[0];
                    String thang = strArrtmp[1];
                    String ngay = strArrtmp[2];
                    if (ngay.length()<2){
                        ngay="0"+ngay;
                    }
                    if (thang.length()<2){
                        thang="0"+thang;
                    }
                    return ngay+ "/" + thang + "/" + nam;
                } else if (format=="yyyy/mm/dd") {
                    String ngay = strArrtmp[0];
                    String thang = strArrtmp[1];
                    String nam = strArrtmp[2];
                    if (ngay.length()<2){
                        ngay="0"+ngay;
                    }
                    if (thang.length()<2){
                        thang="0"+thang;
                    }
                    return nam + "/" + thang + "/" + ngay;
                } else
                    return "";
            } else {
                return "";
            }

        }catch (Exception e){
           return null;
        }

    }

    public static String removeUnicode(String string) {
        String result = null;

        if (string != null) {
            result = string.trim();

            result = result.replaceAll("[^\\x00-\\x7F]", "");
        }

        return result;
    }

    public void importDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String currentDBPath = "//data//" + context.getApplicationContext().getPackageName()
                        + "//databases//" + "<database name>";
                String backupDBPath = "<backup db filename>"; // From SD directory.
                File backupDB = new File(data, currentDBPath);
                File currentDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(backupDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(context.getApplicationContext(), "Import Successful!",
                        Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {

            Toast.makeText(context.getApplicationContext(), "Import Failed!", Toast.LENGTH_SHORT)
                    .show();

        }
    }

    public void exportDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + context.getApplicationContext().getPackageName()
                        + "//databases//" + "<db name>";
                String backupDBPath = "<destination>";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(context.getApplicationContext(), "Backup Successful!",
                        Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {

            Toast.makeText(context.getApplicationContext(), "Backup Failed!", Toast.LENGTH_SHORT)
                    .show();

        }
    }

    public static boolean comPare(String str1, String str2) {
        return (str1 == null ? str2 == null : str1.equals(str2));
    }

    public  static String getCurrentDate(){
        Calendar cal =Calendar.getInstance();
        SimpleDateFormat dft=new SimpleDateFormat("yyyy/MM/dd");
        //gan ngay thang hien tai da dc dinh dang cho s
        String s=dft.format(cal.getTime());

        return DinhDangNgay(s,"dd/mm/yyyy");
    }

    public static class isInternetAvailable extends AsyncTask<String, Void, Boolean>{
        public interface OnUpdateListener {
            void onUpdate(Boolean obj);
        }
        OnUpdateListener listener;
        public isInternetAvailable() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String server[]=params[0].split(":");
            String host=server[1].replace("//","");
            int port=Integer.valueOf(server[2]);
            try {
                Socket sock = new Socket();
                sock.connect(new InetSocketAddress(host.trim(), port), 30000);
                return true;
            } catch (IOException e) {
                Log.e("error", "Internet not available: ----------------" + host +"========"+ e.toString() );
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }
    }

    public  static class isServerAvailable extends AsyncTask<String, Void, Boolean> {
        public interface OnUpdateListener {
            void onUpdate(Boolean obj);
        }
        OnUpdateListener listener;
        public isServerAvailable() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String server[]=params[0].split(":");
            String host=server[1].replace("//","");
            int port=Integer.valueOf(server[2]);
            try {
                Socket sock = new Socket();
                sock.connect(new InetSocketAddress(host.trim(), port), 30000);
                return true;
            } catch (IOException e) {
                Log.e("error", "Internet not available: " + host +" ----"+ e.toString() );
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }
    }

    public static void deleteFile(String filePath){
        File file =new File(filePath);
        if (file.exists()){
            file.delete();
        }
    }

    public static void copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void showSoftKeyboard(Activity activity){
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }



    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    public static String getDurationFromMilisecond(long milisecond) {
        int offset = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
        long millis = milisecond + offset;
        if(millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

//        StringBuilder sb = new StringBuilder(64);
//        sb.append(days);
//        sb.append(" Days ");
//        sb.append(hours);
//        sb.append(" Hours ");
//        sb.append(minutes);
//        sb.append(" Minutes ");
//        sb.append(seconds);
//        sb.append(" Seconds");
//
//        return(sb.toString());

        String gio= StringUtils.right("0"+String.valueOf(hours),2)+
                ":"+StringUtils.right("0"+String.valueOf(minutes),2);
        return gio;

    }

    public static String getDateFromMiliSecond(long milisecond) {
        int offset = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
        long millis = milisecond + offset;
        if(millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milisecond);
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

//        StringBuilder sb = new StringBuilder(64);
//        sb.append(days);
//        sb.append(" Days ");
//        sb.append(hours);
//        sb.append(" Hours ");
//        sb.append(minutes);
//        sb.append(" Minutes ");
//        sb.append(seconds);
//        sb.append(" Seconds");
//
//        return(sb.toString());

        String ngay= StringUtils.right("0"+String.valueOf(mDay),2)+
                "/"+StringUtils.right("0"+String.valueOf(mMonth+1),2)+
                "/"+StringUtils.right("000"+String.valueOf(mYear),4);
        return ngay;

    }
    public static void writeToFile(File FilePath, String FileContent){
        try {
            // buoc 1 mo file de gi
            FileOutputStream fOut = new FileOutputStream(FilePath);
            // buoc 2 ghi noi dung vao file
            fOut.write(FileContent.getBytes());
            // buoc 3 dong file
            fOut.close();
            //outputStreamWriter.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static String readFromFile(File FilePath) {
        String result = null;
        try {
            // buoc 1 mo file
            FileInputStream fis = new FileInputStream(FilePath);
            // buoc 2 doc file
            int flength = fis.available();
            if (flength != 0) {
                byte[] buffer = new byte[flength];
                if (fis.read(buffer) != -1) {
                    result = new String(buffer);
                }
            }
            // buoc 3 dong file
            fis.close();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return result;
    }

}
