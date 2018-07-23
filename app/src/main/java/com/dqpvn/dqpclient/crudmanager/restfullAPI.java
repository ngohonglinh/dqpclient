package com.dqpvn.dqpclient.crudmanager;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.dqpvn.dqpclient.NavDrawerActivity;
import com.dqpvn.dqpclient.models.BanHSDetail;
import com.dqpvn.dqpclient.models.Chi;
import com.dqpvn.dqpclient.models.ChiDetail;
import com.dqpvn.dqpclient.models.ChuyenBien;
import com.dqpvn.dqpclient.models.DMHaiSan;
import com.dqpvn.dqpclient.models.DSTV;
import com.dqpvn.dqpclient.models.DebtBook;
import com.dqpvn.dqpclient.models.DiemDD;
import com.dqpvn.dqpclient.models.DoiTac;
import com.dqpvn.dqpclient.models.ImgStore;
import com.dqpvn.dqpclient.models.KhachHang;
import com.dqpvn.dqpclient.models.ResponseFromServer;

import com.dqpvn.dqpclient.models.Thu;
import com.dqpvn.dqpclient.models.ThuDetail;
import com.dqpvn.dqpclient.models.Ticket;
import com.dqpvn.dqpclient.models.TicketDetail;
import com.dqpvn.dqpclient.models.Users;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.dqpvn.dqpclient.utils.utils.getCurrentTimeMiliS;

/**
 * Created by linh3 on 05/12/2017.
 */

public class restfullAPI {
    private static OkHttpClient okHttpClient = OkHttpSingleton.getInstance().getClient();
    static final private String TAG = "restfull API";

    private static String getType(String path){
        String extention = MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extention);
    }


    public  static class checkUpdate extends AsyncTask<String, Void, ArrayList<?>> {
        public interface OnUpdateListener {
            void onUpdate(ArrayList<?> obj);
        }
        OnUpdateListener listener;
        public checkUpdate() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ArrayList<?> doInBackground(String... strings) {
            Request request =new Request.Builder()
                    .url(strings[0])
                    .get()
                    .build();
            try {
                Gson gson = new Gson();
                //Response response=okHttpClient.newCall(request).execute();
                Response response=okHttpClient.newCall(request).execute();
                //String[] res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                ArrayList<?> res = gson.fromJson(response.body().string(), ArrayList.class);
                //return response.body().string();
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<?> res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }
    }

    //*************************************LOGIN************************************************

    public static class loginDaiHuuDSF extends AsyncTask<String, String, ResponseFromServer> {

        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj) throws ExecutionException, InterruptedException;
        }
        OnUpdateListener listener;
        public loginDaiHuuDSF() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }

        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {
            Gson gson = new Gson();
            RequestBody formBody = new FormBody.Builder()
                    .add("email", strings[1])
                    .add("password",strings[2])
                    .build();
            Request request = new Request.Builder()
                    .url(strings[0])
                    .post(formBody)
                    .build();
            Response response = null;
            try {
                response = okHttpClient.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ResponseFromServer res = null;
            try {
                res = gson.fromJson(response.body().string(), ResponseFromServer.class);
            } catch (IOException|NullPointerException e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                try {
                    listener.onUpdate(res);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static class postFile extends AsyncTask<String,Void,ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public postFile() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        OkHttpClient okHttpClient=new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

        @Override
        protected ResponseFromServer doInBackground(String... strings) throws NullPointerException {
            File file=new File(strings[1]);
            String conten_type=getType(file.getPath());
            String path=file.getAbsolutePath();
            Gson gson = new Gson();
            try {
                RequestBody file_Body =RequestBody.create(MediaType.parse(conten_type),file);
                RequestBody requestBody= new MultipartBody.Builder()
                        .addFormDataPart("eobitginua",path.substring(path.lastIndexOf("/")+1),file_Body)
                        .setType(MultipartBody.FORM)
                        .build();
                Request request=new Request.Builder()
                        .url(strings[0])
                        .put(requestBody)
                        .build();
                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (Exception e) {
                Log.e(TAG, "POSTFILE Error----------: " + e.toString() );
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }
    }

    public  static class deleteFileOnServer extends AsyncTask<String, Void, ResponseFromServer> {
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {
            Request request =new Request.Builder()
                    .url(strings[0])
                    .delete()
                    .build();
            try {
                Gson gson = new Gson();
                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                //return response.body().string();
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
        }
    }

    public static class downloadFile extends AsyncTask<String, String, String> {

        public interface OnUpdateListener {
            void onUpdate(String obj) throws ExecutionException, InterruptedException;
        }
        OnUpdateListener listener;
        public downloadFile() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }

        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... strings) {
            Request.Builder builder = new Request.Builder();
            builder.url(strings[0]);
            Request request = builder.build();
            String realPhotoPath="";
            try {
                Response response = okHttpClient.newCall(request).execute();
                //File newFile = new File(createImageFile(strings[0],strings[1]));
                realPhotoPath = createImageFile(strings[0],strings[1]);
                try {
                    FileOutputStream fos = new FileOutputStream(realPhotoPath);
                    fos.write(response.body().bytes());
                    fos.close();
                } catch (java.io.IOException e) {
                    Log.e(TAG, "Exception in downloadfile", e);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return realPhotoPath;
        }

        @Override
        protected void onPostExecute(String filepath) {
            super.onPostExecute(filepath);
            if (listener != null) {
                try {
                    listener.onUpdate(filepath);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    private static String createImageFile(String imgurl, String pictute_dir) throws IOException {
        String [] arrImgserver=imgurl.split("/");
        String imageFileName=(arrImgserver[arrImgserver.length-1]);
        String imgFolder=arrImgserver[arrImgserver.length-2];
        File dir = new File(pictute_dir + "/" + imgFolder);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //image = File.createTempFile(imageFileName,".jpg",dir);
        File image =new File(dir+"/"+imageFileName);
        if (image.exists()) {
            image.delete();
        }
        return image.getAbsolutePath();
    }

    //----------------------------------Count on server records had serverkey------------------------------
    public  static class countOnServer extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public countOnServer() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {
            Request request =new Request.Builder()
                    .url(strings[0])
                    .build();
            try {
                Gson gson = new Gson();
                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                //return response.body().string();
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }
    }



    //-----------------------------------------USERS------------------
    public static class getUser extends AsyncTask<String, String, ArrayList<Users>> {

        public interface OnUpdateListener {
            void onUpdate(ArrayList<Users> obj) throws ExecutionException, InterruptedException;
        }
        public getUser() {
        }
        OnUpdateListener listener;
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }

        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ArrayList<Users> doInBackground(String... strings) {
            Request.Builder builder = new Request.Builder();
            builder.url(strings[0]);

            Request request = builder.build();
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Users>>() {}.getType();
            ArrayList<Users>arrUser;

            try {
                Response response = okHttpClient.newCall(request).execute();
                arrUser=gson.fromJson(response.body().charStream(),type);
                return arrUser;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Users> arrUser) {
            super.onPostExecute(arrUser);
            if (listener != null) {
                try {
                    listener.onUpdate(arrUser);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public  static class postUser extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public postUser() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        Users user=new Users();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public postUser(Users user2) {
            user.setServerkey(user2.getServerkey());
            user.setRkey(user2.getRkey());
            user.setFullname(user2.getFullname());
            user.setHonourname(user2.getHonourname());
            user.setEmail(user2.getEmail());
            user.setPassword(user2.getPassword());
            user.setCtyno(user2.getCtyno());
            user.setNocty(user2.getNocty());
            user.setUpdatetime(user2.getUpdatetime());
            user.setAdmin(user2.getAdmin());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<Users>() {}.getType();
            String json = gson.toJson(user, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .post(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }


    public  static class putUser extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public putUser() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        Users user=new Users();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public putUser(Users user2) {
            user=user2;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<Users>() {}.getType();
            String json = gson.toJson(this.user, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .put(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }

    public  static class deleteUser extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public deleteUser() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {
            Request request =new Request.Builder()
                    .url(strings[0])
                    .delete()
                    .build();
            try {
                Gson gson = new Gson();
                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                //return response.body().string();
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }
    }


//****************************CHUYENBIEN*************************************

    public static class getChuyenBien extends AsyncTask<String, String, ArrayList<ChuyenBien>> {

        public interface OnUpdateListener {
            void onUpdate(ArrayList<ChuyenBien> obj) throws ExecutionException, InterruptedException;
        }
        OnUpdateListener listener;
        public getChuyenBien() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }

        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ArrayList<ChuyenBien> doInBackground(String... strings) {
            Request.Builder builder = new Request.Builder();
            builder.url(strings[0]);

            Request request = builder.build();
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ChuyenBien>>() {}.getType();
            ArrayList<ChuyenBien>arrChuyenBien;

            try {
                Response response = okHttpClient.newCall(request).execute();
                arrChuyenBien=gson.fromJson(response.body().charStream(),type);
                return arrChuyenBien;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ChuyenBien> arrChuyenBien) {
            super.onPostExecute(arrChuyenBien);
            if (listener != null) {
                try {
                    listener.onUpdate(arrChuyenBien);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public  static class postChuyenBien extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public postChuyenBien() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        ChuyenBien ChuyenBien=new ChuyenBien();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public postChuyenBien(ChuyenBien chuyenbien) {
            //this.serverChuyenBien.setId(Integer.valueOf(chuyenbien.getmId()));
            this.ChuyenBien.setServerkey(Integer.valueOf(chuyenbien.getServerkey()));
            this.ChuyenBien.setRkey(chuyenbien.getRkey());
            this.ChuyenBien.setUpdatetime(chuyenbien.getUpdatetime());
            this.ChuyenBien.setChuyenbien(chuyenbien.getChuyenbien());
            this.ChuyenBien.setTentau(chuyenbien.getTentau());
            this.ChuyenBien.setNgaykhoihanh(chuyenbien.getNgaykhoihanh());
            if (chuyenbien.getNgayketchuyen()!=null){
                this.ChuyenBien.setNgayketchuyen(chuyenbien.getNgayketchuyen());
            }
            this.ChuyenBien.setTongchi(chuyenbien.getTongchi());
            this.ChuyenBien.setTongthu(chuyenbien.getTongthu());
            this.ChuyenBien.setDachia(chuyenbien.getDachia());
            this.ChuyenBien.setUsername(chuyenbien.getUsername());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<ChuyenBien>() {}.getType();
            String json = gson.toJson(this.ChuyenBien, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .post(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }


    public  static class putChuyenBien extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public putChuyenBien() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        ChuyenBien ChuyenBien=new ChuyenBien();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public putChuyenBien(ChuyenBien chuyenbien) {
            //this.serverChuyenBien.setId(Integer.valueOf(chuyenbien.getmId()));
            this.ChuyenBien.setServerkey(Integer.valueOf(chuyenbien.getServerkey()));
            this.ChuyenBien.setRkey(chuyenbien.getRkey());
            this.ChuyenBien.setUpdatetime(chuyenbien.getUpdatetime());
            this.ChuyenBien.setChuyenbien(chuyenbien.getChuyenbien());
            this.ChuyenBien.setTentau(chuyenbien.getTentau());
            this.ChuyenBien.setNgaykhoihanh(chuyenbien.getNgaykhoihanh());
            if (chuyenbien.getNgayketchuyen()!=null){
                this.ChuyenBien.setNgayketchuyen(chuyenbien.getNgayketchuyen());
            }
            this.ChuyenBien.setTongchi(chuyenbien.getTongchi());
            this.ChuyenBien.setTongthu(chuyenbien.getTongthu());
            this.ChuyenBien.setDachia(chuyenbien.getDachia());
            this.ChuyenBien.setUsername(chuyenbien.getUsername());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<ChuyenBien>() {}.getType();
            String json = gson.toJson(this.ChuyenBien, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .put(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }

    public  static class deleteChuyenBien extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public deleteChuyenBien() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {
            Request request =new Request.Builder()
                    .url(strings[0])
                    .delete()
                    .build();
            try {
                Gson gson = new Gson();
                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                //return response.body().string();
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }
    }

    //**************************************TICKET*******************************************

    public static class getTicket extends AsyncTask<String, String, ArrayList<Ticket>> {

        public interface OnUpdateListener {
            void onUpdate(ArrayList<Ticket> obj) throws ExecutionException, InterruptedException;
        }
        OnUpdateListener listener;
        public getTicket() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }

        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ArrayList<Ticket> doInBackground(String... strings) {
            Request.Builder builder = new Request.Builder();
            builder.url(strings[0]);

            Request request = builder.build();
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Ticket>>() {}.getType();
            ArrayList<Ticket>arrTicket;

            try {
                Response response = okHttpClient.newCall(request).execute();
                arrTicket=gson.fromJson(response.body().charStream(),type);
                return arrTicket;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Ticket> arrTicket) {
            super.onPostExecute(arrTicket);
            if (listener != null) {
                try {
                    listener.onUpdate(arrTicket);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public  static class postTicket extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public postTicket() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        Ticket Ticket=new Ticket();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public postTicket(Ticket Ticket) {
            //this.serverTicket.setId(Integer.valueOf(Ticket.getmId()));
            this.Ticket.setServerkey(Integer.valueOf(Ticket.getServerkey()));
            this.Ticket.setRkey(Ticket.getRkey());
            this.Ticket.setAmount(Ticket.getAmount());
            this.Ticket.setUsed(Ticket.getUsed());
            this.Ticket.setOpendate(Ticket.getOpendate());
            this.Ticket.setLydo(Ticket.getLydo());
            if (Ticket.getClosedate()!=null){
                this.Ticket.setClosedate(Ticket.getClosedate());
            }
            this.Ticket.setFinished(Ticket.getFinished());
            this.Ticket.setComeback(Ticket.getComeback());
            this.Ticket.setUpdatetime(Ticket.getUpdatetime());
            this.Ticket.setUsername(Ticket.getUsername());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<Ticket>() {}.getType();
            String json = gson.toJson(this.Ticket, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .post(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }


    public  static class putTicket extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public putTicket() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        Ticket Ticket=new Ticket();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public putTicket(Ticket rfTicket) {
            //this.serverTicket.setId(Integer.valueOf(Ticket.getmId()));

            this.Ticket.setServerkey(Integer.valueOf(rfTicket.getServerkey()));
            this.Ticket.setRkey(rfTicket.getRkey());
            this.Ticket.setAmount(rfTicket.getAmount());
            this.Ticket.setUsed(rfTicket.getUsed());
            this.Ticket.setOpendate(rfTicket.getOpendate());
            this.Ticket.setLydo(rfTicket.getLydo());
            if (Ticket.getClosedate()!=null){
                this.Ticket.setClosedate(rfTicket.getClosedate());
            }
            this.Ticket.setFinished(rfTicket.getFinished());
            this.Ticket.setComeback(rfTicket.getComeback());
            this.Ticket.setUpdatetime(rfTicket.getUpdatetime());
            this.Ticket.setUsername(rfTicket.getUsername());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<Ticket>() {}.getType();
            String json = gson.toJson(this.Ticket, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .put(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }

    public  static class deleteTicket extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public deleteTicket() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {
            Request request =new Request.Builder()
                    .url(strings[0])
                    .delete()
                    .build();
            try {
                Gson gson = new Gson();
                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                //return response.body().string();
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }
    }

    //*******************************TICKETDETAIL*******************************************************

    public static class getTicketDetail extends AsyncTask<String, String, ArrayList<TicketDetail>> {

        public interface OnUpdateListener {
            void onUpdate(ArrayList<TicketDetail> obj) throws ExecutionException, InterruptedException;
        }
        public getTicketDetail() {
        }
        OnUpdateListener listener;
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }

        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ArrayList<TicketDetail> doInBackground(String... strings) {
            Request.Builder builder = new Request.Builder();
            builder.url(strings[0]);

            Request request = builder.build();
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<TicketDetail>>() {}.getType();
            ArrayList<TicketDetail>arrTicketDetail;

            try {
                Response response = okHttpClient.newCall(request).execute();
                arrTicketDetail=gson.fromJson(response.body().charStream(),type);
                return arrTicketDetail;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<TicketDetail> arrTicketDetail) {
            super.onPostExecute(arrTicketDetail);
            if (listener != null) {
                try {
                    listener.onUpdate(arrTicketDetail);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public  static class postTicketDetail extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public postTicketDetail() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        TicketDetail ticketdetail=new TicketDetail();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public postTicketDetail(TicketDetail ticketdetail2) {
            ticketdetail.setServerkey(ticketdetail2.getServerkey());
            ticketdetail.setRkey(ticketdetail2.getRkey());
            ticketdetail.setRkeyticket(ticketdetail2.getRkeyticket());
            ticketdetail.setForuser(ticketdetail2.getForuser());
            ticketdetail.setAmount(ticketdetail2.getAmount());
            ticketdetail.setNgayps(ticketdetail2.getNgayps());
            ticketdetail.setNotes(ticketdetail2.getNotes());
            ticketdetail.setUpdatetime(ticketdetail2.getUpdatetime());
            ticketdetail.setUsername(ticketdetail2.getUsername());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<TicketDetail>() {}.getType();
            String json = gson.toJson(ticketdetail, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .post(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }


    public  static class putTicketDetail extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public putTicketDetail() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        TicketDetail ticketdetail=new TicketDetail();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public putTicketDetail(TicketDetail ticketdetail2) {
            ticketdetail=ticketdetail2;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<TicketDetail>() {}.getType();
            String json = gson.toJson(this.ticketdetail, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .put(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }

    public  static class deleteTicketDetail extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public deleteTicketDetail() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {
            Request request =new Request.Builder()
                    .url(strings[0])
                    .delete()
                    .build();
            try {
                Gson gson = new Gson();
                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                //return response.body().string();
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }
    }



    //**************************************DOITAC******************************************

    public static class getDoiTac extends AsyncTask<String, String, ArrayList<DoiTac>> {

        public interface OnUpdateListener {
            void onUpdate(ArrayList<DoiTac> obj) throws ExecutionException, InterruptedException;
        }
        OnUpdateListener listener;
        public getDoiTac() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }

        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ArrayList<DoiTac> doInBackground(String... strings) {
            Request.Builder builder = new Request.Builder();
            builder.url(strings[0]);

            Request request = builder.build();
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<DoiTac>>() {}.getType();
            ArrayList<DoiTac>arrDoiTac;

            try {
                Response response = okHttpClient.newCall(request).execute();
                arrDoiTac=gson.fromJson(response.body().charStream(),type);
                return arrDoiTac;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<DoiTac> arrDoiTac) {
            super.onPostExecute(arrDoiTac);
            if (listener != null) {
                try {
                    listener.onUpdate(arrDoiTac);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public  static class postDoiTac extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public postDoiTac() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        DoiTac DoiTac=new DoiTac();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public postDoiTac(DoiTac doitac) {
            //this.serverChuyenBien.setId(Integer.valueOf(chuyenbien.getmId()));
            this.DoiTac.setServerkey(Integer.valueOf(doitac.getServerkey()));
            this.DoiTac.setRkey(doitac.getRkey());
            this.DoiTac.setUpdatetime(doitac.getUpdatetime());
            this.DoiTac.setTendoitac(doitac.getTendoitac());
            this.DoiTac.setSodienthoai(doitac.getSodienthoai());
            this.DoiTac.setDiachi(doitac.getDiachi());
            this.DoiTac.setNocty(doitac.getNocty());
            this.DoiTac.setCtyno(doitac.getCtyno());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<DoiTac>() {}.getType();
            String json = gson.toJson(this.DoiTac, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .post(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }


    public  static class putDoiTac extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public putDoiTac() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        DoiTac DoiTac=new DoiTac();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public putDoiTac(DoiTac doitac) {
            //this.serverChuyenBien.setId(Integer.valueOf(chuyenbien.getmId()));
            this.DoiTac.setServerkey(Integer.valueOf(doitac.getServerkey()));
            this.DoiTac.setRkey(doitac.getRkey());
            this.DoiTac.setUpdatetime(doitac.getUpdatetime());
            this.DoiTac.setTendoitac(doitac.getTendoitac());
            this.DoiTac.setSodienthoai(doitac.getSodienthoai());
            this.DoiTac.setDiachi(doitac.getDiachi());
            this.DoiTac.setNocty(doitac.getNocty());
            this.DoiTac.setCtyno(doitac.getCtyno());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<DoiTac>() {}.getType();
            String json = gson.toJson(this.DoiTac, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .put(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }

    public  static class deleteDoiTac extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public deleteDoiTac() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {
            Request request =new Request.Builder()
                    .url(strings[0])
                    .delete()
                    .build();
            try {
                Gson gson = new Gson();
                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                //return response.body().string();
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }
    }


    //*******************************KHACHHANG*********************************************

    public static class getKhachHang extends AsyncTask<String, String, ArrayList<KhachHang>> {

        public interface OnUpdateListener {
            void onUpdate(ArrayList<KhachHang> obj) throws ExecutionException, InterruptedException;
        }
        OnUpdateListener listener;
        public getKhachHang() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }

        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ArrayList<KhachHang> doInBackground(String... strings) {
            Request.Builder builder = new Request.Builder();
            builder.url(strings[0]);

            Request request = builder.build();
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<KhachHang>>() {}.getType();
            ArrayList<KhachHang>arrKhachHang;

            try {
                Response response = okHttpClient.newCall(request).execute();
                arrKhachHang=gson.fromJson(response.body().charStream(),type);
                return arrKhachHang;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<KhachHang> arrKhachHang) {
            super.onPostExecute(arrKhachHang);
            if (listener != null) {
                try {
                    listener.onUpdate(arrKhachHang);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public  static class postKhachHang extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public postKhachHang() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        KhachHang KhachHang=new KhachHang();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public postKhachHang(KhachHang khachhang) {
            //this.serverChuyenBien.setId(Integer.valueOf(chuyenbien.getmId()));
            this.KhachHang.setServerkey(Integer.valueOf(khachhang.getServerkey()));
            this.KhachHang.setRkey(khachhang.getRkey());
            this.KhachHang.setUpdatetime(khachhang.getUpdatetime());
            this.KhachHang.setTenkhach(khachhang.getTenkhach());
            this.KhachHang.setSodienthoai(khachhang.getSodienthoai());
            this.KhachHang.setDiachi(khachhang.getDiachi());
            this.KhachHang.setNocty(khachhang.getNocty());
            this.KhachHang.setCtyno(khachhang.getCtyno());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<KhachHang>() {}.getType();
            String json = gson.toJson(this.KhachHang, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .post(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }


    public  static class putKhachHang extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public putKhachHang() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        KhachHang KhachHang=new KhachHang();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public putKhachHang(KhachHang khachhang) {
            //this.serverChuyenBien.setId(Integer.valueOf(chuyenbien.getmId()));
            this.KhachHang.setServerkey(Integer.valueOf(khachhang.getServerkey()));
            this.KhachHang.setRkey(khachhang.getRkey());
            this.KhachHang.setUpdatetime(khachhang.getUpdatetime());
            this.KhachHang.setTenkhach(khachhang.getTenkhach());
            this.KhachHang.setSodienthoai(khachhang.getSodienthoai());
            this.KhachHang.setDiachi(khachhang.getDiachi());
            this.KhachHang.setNocty(khachhang.getNocty());
            this.KhachHang.setCtyno(khachhang.getCtyno());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<KhachHang>() {}.getType();
            String json = gson.toJson(this.KhachHang, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .put(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }

    public  static class deleteKhachHang extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public deleteKhachHang() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {
            Request request =new Request.Builder()
                    .url(strings[0])
                    .delete()
                    .build();
            try {
                Gson gson = new Gson();
                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                //return response.body().string();
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }
    }


    //*******************************DSTV*******************************************************

    public static class getDSTV extends AsyncTask<String, String, ArrayList<DSTV>> {

        public interface OnUpdateListener {
            void onUpdate(ArrayList<DSTV> obj) throws ExecutionException, InterruptedException;
        }
        public getDSTV() {
        }
        OnUpdateListener listener;
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }

        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ArrayList<DSTV> doInBackground(String... strings) {
            Request.Builder builder = new Request.Builder();
            builder.url(strings[0]);

            Request request = builder.build();
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<DSTV>>() {}.getType();
            ArrayList<DSTV>arrDSTV;

            try {
                Response response = okHttpClient.newCall(request).execute();
                arrDSTV=gson.fromJson(response.body().charStream(),type);
                return arrDSTV;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<DSTV> arrDSTV) {
            super.onPostExecute(arrDSTV);
            if (listener != null) {
                try {
                    listener.onUpdate(arrDSTV);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public  static class postDSTV extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public postDSTV() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        DSTV dstv=new DSTV();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public postDSTV(DSTV dstv2) {
            dstv.setServerkey(dstv2.getServerkey());
            dstv.setRkey(dstv2.getRkey());
            dstv.setRkeychuyenbien(dstv2.getRkeychuyenbien());
            dstv.setTen(dstv2.getTen());
            dstv.setDiem(dstv2.getDiem());
            dstv.setTienchia(dstv2.getTienchia());
            dstv.setTienmuon(dstv2.getTienmuon());
            dstv.setTiencanca(dstv2.getTiencanca());
            dstv.setConlai(dstv2.getConlai());
            dstv.setNotes(dstv2.getNotes());
            dstv.setUpdatetime(dstv2.getUpdatetime());
            dstv.setUsername(dstv2.getUsername());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<DSTV>() {}.getType();
            String json = gson.toJson(dstv, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .post(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }


    public  static class putDSTV extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public putDSTV() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        DSTV dstv=new DSTV();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public putDSTV(DSTV dstv2) {
            dstv=dstv2;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<DSTV>() {}.getType();
            String json = gson.toJson(this.dstv, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .put(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }

    public  static class deleteDSTV extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public deleteDSTV() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {
            Request request =new Request.Builder()
                    .url(strings[0])
                    .delete()
                    .build();
            try {
                Gson gson = new Gson();
                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                //return response.body().string();
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }
    }



    //*******************************BanHSDetail*******************************************************

    public static class getBanHSDetail extends AsyncTask<String, String, ArrayList<BanHSDetail>> {

        public interface OnUpdateListener {
            void onUpdate(ArrayList<BanHSDetail> obj) throws ExecutionException, InterruptedException;
        }
        public getBanHSDetail() {
        }
        OnUpdateListener listener;
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }

        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ArrayList<BanHSDetail> doInBackground(String... strings) {
            Request.Builder builder = new Request.Builder();
            builder.url(strings[0]);

            Request request = builder.build();
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<BanHSDetail>>() {}.getType();
            ArrayList<BanHSDetail>arrBanHSDetail;

            try {
                Response response = okHttpClient.newCall(request).execute();
                arrBanHSDetail=gson.fromJson(response.body().charStream(),type);
                return arrBanHSDetail;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<BanHSDetail> arrBanHSDetail) {
            super.onPostExecute(arrBanHSDetail);
            if (listener != null) {
                try {
                    listener.onUpdate(arrBanHSDetail);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public  static class postBanHSDetail extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public postBanHSDetail() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        BanHSDetail banhsdetail=new BanHSDetail();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public postBanHSDetail(BanHSDetail banhsdetail2) {
            banhsdetail.setServerkey(banhsdetail2.getServerkey());
            banhsdetail.setRkey(banhsdetail2.getRkey());
            banhsdetail.setRkeythu(banhsdetail2.getRkeythu());
            banhsdetail.setRkeythudetail(banhsdetail2.getRkeythudetail());
            banhsdetail.setTenhs(banhsdetail2.getTenhs());
            banhsdetail.setSoluong(banhsdetail2.getSoluong());
            banhsdetail.setUpdatetime(banhsdetail2.getUpdatetime());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<BanHSDetail>() {}.getType();
            String json = gson.toJson(banhsdetail, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .post(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }


    public  static class putBanHSDetail extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public putBanHSDetail() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        BanHSDetail banhsdetail=new BanHSDetail();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public putBanHSDetail(BanHSDetail banhsdetail2) {
            banhsdetail=banhsdetail2;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<BanHSDetail>() {}.getType();
            String json = gson.toJson(this.banhsdetail, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .put(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }

    public  static class deleteBanHSDetail extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public deleteBanHSDetail() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {
            Request request =new Request.Builder()
                    .url(strings[0])
                    .delete()
                    .build();
            try {
                Gson gson = new Gson();
                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                //return response.body().string();
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }
    }

    //*******************************ThuDetail*******************************************************

    public static class getThuDetail extends AsyncTask<String, String, ArrayList<ThuDetail>> {

        public interface OnUpdateListener {
            void onUpdate(ArrayList<ThuDetail> obj) throws ExecutionException, InterruptedException;
        }
        public getThuDetail() {
        }
        OnUpdateListener listener;
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }

        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ArrayList<ThuDetail> doInBackground(String... strings) {
            Request.Builder builder = new Request.Builder();
            builder.url(strings[0]);

            Request request = builder.build();
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ThuDetail>>() {}.getType();
            ArrayList<ThuDetail>arrThuDetail;

            try {
                Response response = okHttpClient.newCall(request).execute();
                arrThuDetail=gson.fromJson(response.body().charStream(),type);
                return arrThuDetail;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ThuDetail> arrThuDetail) {
            super.onPostExecute(arrThuDetail);
            if (listener != null) {
                try {
                    listener.onUpdate(arrThuDetail);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public  static class postThuDetail extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public postThuDetail() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        ThuDetail thudetail=new ThuDetail();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public postThuDetail(ThuDetail thudetail2) {
            thudetail.setServerkey(thudetail2.getServerkey());
            thudetail.setRkey(thudetail2.getRkey());
            thudetail.setRkeythu(thudetail2.getRkeythu());
            thudetail.setTenhs(thudetail2.getTenhs());
            thudetail.setRkeyhs(thudetail2.getRkeyhs());
            thudetail.setSoluong(thudetail2.getSoluong());
            thudetail.setDongia(thudetail2.getDongia());
            thudetail.setThanhtien(thudetail2.getThanhtien());
            thudetail.setUpdatetime(thudetail2.getUpdatetime());
            thudetail.setUsername(thudetail2.getUsername());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<ThuDetail>() {}.getType();
            String json = gson.toJson(thudetail, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .post(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }


    public  static class putThuDetail extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public putThuDetail() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        ThuDetail thudetail=new ThuDetail();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public putThuDetail(ThuDetail thudetail2) {
            thudetail=thudetail2;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<ThuDetail>() {}.getType();
            String json = gson.toJson(this.thudetail, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .put(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }

    public  static class deleteThuDetail extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public deleteThuDetail() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {
            Request request =new Request.Builder()
                    .url(strings[0])
                    .delete()
                    .build();
            try {
                Gson gson = new Gson();
                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                //return response.body().string();
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }
    }

    //*******************************DMHaiSan*******************************************************

    public static class getDMHaiSan extends AsyncTask<String, String, ArrayList<DMHaiSan>> {

        public interface OnUpdateListener {
            void onUpdate(ArrayList<DMHaiSan> obj) throws ExecutionException, InterruptedException;
        }
        public getDMHaiSan() {
        }
        OnUpdateListener listener;
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }

        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ArrayList<DMHaiSan> doInBackground(String... strings) {
            Request.Builder builder = new Request.Builder();
            builder.url(strings[0]);

            Request request = builder.build();
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<DMHaiSan>>() {}.getType();
            ArrayList<DMHaiSan>arrDMHaiSan;

            try {
                Response response = okHttpClient.newCall(request).execute();
                arrDMHaiSan=gson.fromJson(response.body().charStream(),type);
                return arrDMHaiSan;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<DMHaiSan> arrDMHaiSan) {
            super.onPostExecute(arrDMHaiSan);
            if (listener != null) {
                try {
                    listener.onUpdate(arrDMHaiSan);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public  static class postDMHaiSan extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public postDMHaiSan() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        DMHaiSan dmhaisan=new DMHaiSan();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public postDMHaiSan(DMHaiSan dmhaisan2) {
            dmhaisan.setServerkey(dmhaisan2.getServerkey());
            dmhaisan.setRkey(dmhaisan2.getRkey());
            dmhaisan.setTenhs(dmhaisan2.getTenhs());
            dmhaisan.setPhanloai(dmhaisan2.getPhanloai());
            dmhaisan.setDongia(dmhaisan2.getDongia());
            dmhaisan.setNgayps(dmhaisan2.getNgayps());
            dmhaisan.setNotes(dmhaisan2.getNotes());
            dmhaisan.setUpdatetime(dmhaisan2.getUpdatetime());
            dmhaisan.setUsername(dmhaisan2.getUsername());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<DMHaiSan>() {}.getType();
            String json = gson.toJson(dmhaisan, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .post(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }


    public  static class putDMHaiSan extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public putDMHaiSan() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        DMHaiSan dmhaisan=new DMHaiSan();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public putDMHaiSan(DMHaiSan dmhaisan2) {
            dmhaisan=dmhaisan2;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<DMHaiSan>() {}.getType();
            String json = gson.toJson(this.dmhaisan, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .put(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }

    public  static class deleteDMHaiSan extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public deleteDMHaiSan() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {
            Request request =new Request.Builder()
                    .url(strings[0])
                    .delete()
                    .build();
            try {
                Gson gson = new Gson();
                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                //return response.body().string();
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }
    }

    //*******************************DIEMDD*******************************************************

    public static class getDiemDD extends AsyncTask<String, String, ArrayList<DiemDD>> {

        public interface OnUpdateListener {
            void onUpdate(ArrayList<DiemDD> obj) throws ExecutionException, InterruptedException;
        }
        public getDiemDD() {
        }
        OnUpdateListener listener;
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }

        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ArrayList<DiemDD> doInBackground(String... strings) {
            Request.Builder builder = new Request.Builder();
            builder.url(strings[0]);

            Request request = builder.build();
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<DiemDD>>() {}.getType();
            ArrayList<DiemDD>arrDiemDD;

            try {
                Response response = okHttpClient.newCall(request).execute();
                arrDiemDD=gson.fromJson(response.body().charStream(),type);
                return arrDiemDD;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<DiemDD> arrDiemDD) {
            super.onPostExecute(arrDiemDD);
            if (listener != null) {
                try {
                    listener.onUpdate(arrDiemDD);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public  static class postDiemDD extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public postDiemDD() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        DiemDD diemdd=new DiemDD();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public postDiemDD(DiemDD diemdd2) {
            diemdd.setServerkey(diemdd2.getServerkey());
            diemdd.setRkey(diemdd2.getRkey());
            diemdd.setEater(diemdd2.getEater());
            diemdd.setEatername(diemdd2.getEatername());
            diemdd.setChuyenbien(diemdd2.getChuyenbien());
            diemdd.setDiemeater(diemdd2.getDiemeater());
            diemdd.setLydo(diemdd2.getLydo());
            diemdd.setChucvu(diemdd2.getChucvu());
            diemdd.setFeeder(diemdd2.getFeeder());
            diemdd.setNgayps(diemdd2.getNgayps());
            diemdd.setUpdatetime(diemdd2.getUpdatetime());
            diemdd.setUsername(diemdd2.getUsername());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<DiemDD>() {}.getType();
            String json = gson.toJson(diemdd, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .post(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }


    public  static class putDiemDD extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public putDiemDD() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        DiemDD diemdd=new DiemDD();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public putDiemDD(DiemDD diemdd2) {
            diemdd=diemdd2;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<DiemDD>() {}.getType();
            String json = gson.toJson(this.diemdd, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .put(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }

    public  static class deleteDiemDD extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public deleteDiemDD() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {
            Request request =new Request.Builder()
                    .url(strings[0])
                    .delete()
                    .build();
            try {
                Gson gson = new Gson();
                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                //return response.body().string();
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }
    }

    //********************************DEBTBOOK*************************************************

    public static class getDebtBook extends AsyncTask<String, String, ArrayList<DebtBook>> {

        public interface OnUpdateListener {
            void onUpdate(ArrayList<DebtBook> obj) throws ExecutionException, InterruptedException;
        }
        public getDebtBook() {
        }
        OnUpdateListener listener;
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }

        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ArrayList<DebtBook> doInBackground(String... strings) {
            Request.Builder builder = new Request.Builder();
            builder.url(strings[0]);

            Request request = builder.build();
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<DebtBook>>() {}.getType();
            ArrayList<DebtBook>arrDebtBook;

            try {
                Response response = okHttpClient.newCall(request).execute();
                arrDebtBook=gson.fromJson(response.body().charStream(),type);
                return arrDebtBook;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<DebtBook> arrDebtBook) {
            super.onPostExecute(arrDebtBook);
            if (listener != null) {
                try {
                    listener.onUpdate(arrDebtBook);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public  static class postDebtBook extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public postDebtBook() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        DebtBook debtbook=new DebtBook();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public postDebtBook(DebtBook debtbook2) {
            //bat buoc de loai bo id tranh de server hieu lam
            debtbook.setServerkey(debtbook2.getServerkey());
            debtbook.setRkey(debtbook2.getRkey());
            debtbook.setRkeythuyenvien(debtbook2.getRkeythuyenvien());
            debtbook.setRkeyticket(debtbook2.getRkeyticket());
            debtbook.setChuyenbien(debtbook2.getChuyenbien());
            debtbook.setTen(debtbook2.getTen());
            debtbook.setLydo(debtbook2.getLydo());
            debtbook.setNgayps(debtbook2.getNgayps());
            debtbook.setSotien(debtbook2.getSotien());
            debtbook.setUpdatetime(debtbook2.getUpdatetime());
            debtbook.setUsername(debtbook2.getUsername());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<DebtBook>() {}.getType();
            String json = gson.toJson(debtbook, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .post(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }


    public  static class putDebtBook extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public putDebtBook() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        DebtBook debtbook=new DebtBook();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public putDebtBook(DebtBook debtbook2) {
            debtbook=debtbook2;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<DebtBook>() {}.getType();
            String json = gson.toJson(this.debtbook, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .put(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }

    public  static class deleteDebtBook extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public deleteDebtBook() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {
            Request request =new Request.Builder()
                    .url(strings[0])
                    .delete()
                    .build();
            try {
                Gson gson = new Gson();
                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                //return response.body().string();
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }
    }


    //*******************************CHI*********************************************

    public static class getChi extends AsyncTask<String, String, ArrayList<Chi>> {

        public interface OnUpdateListener {
            void onUpdate(ArrayList<Chi> obj) throws ExecutionException, InterruptedException;
        }
        OnUpdateListener listener;
        public getChi() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }

        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ArrayList<Chi> doInBackground(String... strings) {
            Request.Builder builder = new Request.Builder();
            builder.url(strings[0]);

            Request request = builder.build();
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Chi>>() {}.getType();
            ArrayList<Chi>arrChi;

            try {
                Response response = okHttpClient.newCall(request).execute();
                arrChi=gson.fromJson(response.body().charStream(),type);
                return arrChi;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Chi> arrChi) {
            super.onPostExecute(arrChi);
            if (listener != null) {
                try {
                    listener.onUpdate(arrChi);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public  static class postChi extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public postChi() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        Chi Chi=new Chi();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public postChi(Chi chi) {
            //this.serverChuyenBien.setId(Integer.valueOf(chuyenbien.getmId()));
            this.Chi.setServerkey(Integer.valueOf(chi.getServerkey()));
            this.Chi.setRkey(chi.getRkey());
            this.Chi.setRkeychuyenbien(chi.getRkeychuyenbien());
            this.Chi.setRkeydoitac(chi.getRkeydoitac());
            this.Chi.setRkeyticket(chi.getRkeyticket());
            this.Chi.setLydo(chi.getLydo());
            this.Chi.setNgayps(chi.getNgayps());
            this.Chi.setGiatri(chi.getGiatri());
            this.Chi.setDatra(chi.getDatra());
            this.Chi.setUpdatetime(chi.getUpdatetime());
            this.Chi.setUsername(chi.getUsername());

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<Chi>() {}.getType();
            String json = gson.toJson(this.Chi, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .post(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }


    public  static class putChi extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public putChi() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        Chi chi=new Chi();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public putChi(Chi chi2) {
            //this.serverChuyenBien.setId(Integer.valueOf(chuyenbien.getmId()));
            chi=chi2;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<Chi>() {}.getType();
            String json = gson.toJson(this.chi, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .put(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }

    public  static class deleteChi extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public deleteChi() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {
            Request request =new Request.Builder()
                    .url(strings[0])
                    .delete()
                    .build();
            try {
                Gson gson = new Gson();
                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                //return response.body().string();
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }
    }


    //*******************************CHIDETAIL*********************************************

    public static class getChiDetail extends AsyncTask<String, String, ArrayList<ChiDetail>> {

        public interface OnUpdateListener {
            void onUpdate(ArrayList<ChiDetail> obj) throws ExecutionException, InterruptedException;
        }
        OnUpdateListener listener;
        public getChiDetail() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }

        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ArrayList<ChiDetail> doInBackground(String... strings) {
            Request.Builder builder = new Request.Builder();
            builder.url(strings[0]);

            Request request = builder.build();
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ChiDetail>>() {}.getType();
            ArrayList<ChiDetail>arrChiDetail;

            try {
                Response response = okHttpClient.newCall(request).execute();
                arrChiDetail=gson.fromJson(response.body().charStream(),type);
                return arrChiDetail;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ChiDetail> arrChiDetail) {
            super.onPostExecute(arrChiDetail);
            if (listener != null) {
                try {
                    listener.onUpdate(arrChiDetail);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public  static class postChiDetail extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public postChiDetail() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        ChiDetail ChiDetail=new ChiDetail();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public postChiDetail(ChiDetail ChiDetail) {
            //this.serverChuyenBien.setId(Integer.valueOf(chuyenbien.getmId()));
            this.ChiDetail.setServerkey(Integer.valueOf(ChiDetail.getServerkey()));
            this.ChiDetail.setRkey(ChiDetail.getRkey());
            this.ChiDetail.setRkeychi(ChiDetail.getRkeychi());
            this.ChiDetail.setTenchuyenbien(ChiDetail.getTenchuyenbien());
            this.ChiDetail.setTendoitac(ChiDetail.getTendoitac());
            this.ChiDetail.setSanpham(ChiDetail.getSanpham());
            this.ChiDetail.setSoluong(ChiDetail.getSoluong());
            this.ChiDetail.setDongia(ChiDetail.getDongia());
            this.ChiDetail.setThanhtien(ChiDetail.getThanhtien());
            this.ChiDetail.setUpdatetime(ChiDetail.getUpdatetime());
            this.ChiDetail.setUsername(ChiDetail.getUsername());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<ChiDetail>() {}.getType();
            String json = gson.toJson(this.ChiDetail, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .post(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }


    public  static class putChiDetail extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public putChiDetail() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        ChiDetail ChiDetail=new ChiDetail();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public putChiDetail(ChiDetail ChiDetail2) {
            //this.serverChuyenBien.setId(Integer.valueOf(chuyenbien.getmId()));
            this.ChiDetail=ChiDetail2;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<ChiDetail>() {}.getType();
            String json = gson.toJson(this.ChiDetail, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .put(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }

    public  static class deleteChiDetail extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public deleteChiDetail() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {
            Request request =new Request.Builder()
                    .url(strings[0])
                    .delete()
                    .build();
            try {
                Gson gson = new Gson();
                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                //return response.body().string();
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }
    }


    //*******************************THU*********************************************

    public static class getThu extends AsyncTask<String, String, ArrayList<Thu>> {

        public interface OnUpdateListener {
            void onUpdate(ArrayList<Thu> obj) throws ExecutionException, InterruptedException;
        }
        OnUpdateListener listener;
        public getThu() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }

        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ArrayList<Thu> doInBackground(String... strings) {
            Request.Builder builder = new Request.Builder();
            builder.url(strings[0]);

            Request request = builder.build();
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Thu>>() {}.getType();
            ArrayList<Thu>arrThu;

            try {
                Response response = okHttpClient.newCall(request).execute();
                arrThu=gson.fromJson(response.body().charStream(),type);
                return arrThu;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Thu> arrThu) {
            super.onPostExecute(arrThu);
            if (listener != null) {
                try {
                    listener.onUpdate(arrThu);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public  static class postThu extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public postThu() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        Thu Thu=new Thu();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public postThu(Thu thu) {
            //this.serverChuyenBien.setId(Integer.valueOf(chuyenbien.getmId()));
            this.Thu.setServerkey(Integer.valueOf(thu.getServerkey()));
            this.Thu.setRkey(thu.getRkey());
            this.Thu.setUpdatetime(thu.getUpdatetime());
            this.Thu.setRkeychuyenbien(thu.getRkeychuyenbien());
            this.Thu.setRkeykhachhang(thu.getRkeykhachhang());
            this.Thu.setLydo(thu.getLydo());
            this.Thu.setNgayps(thu.getNgayps());
            this.Thu.setGiatri(thu.getGiatri());
            this.Thu.setDatra(thu.getDatra());
            this.Thu.setUsername(thu.getUsername());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<Thu>() {}.getType();
            String json = gson.toJson(this.Thu, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .post(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }


    public  static class putThu extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public putThu() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        Thu Thu=new Thu();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public putThu(Thu thu) {
            //this.serverChuyenBien.setId(Integer.valueOf(chuyenbien.getmId()));
            this.Thu=thu;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<Thu>() {}.getType();
            String json = gson.toJson(this.Thu, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .put(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }

    public  static class deleteThu extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public deleteThu() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {
            Request request =new Request.Builder()
                    .url(strings[0])
                    .delete()
                    .build();
            try {
                Gson gson = new Gson();
                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                //return response.body().string();
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }
    }


    //*******************************IMGSTORE*********************************************

    public static class getImgStore extends AsyncTask<String, String, ArrayList<ImgStore>> {

        public interface OnUpdateListener {
            void onUpdate(ArrayList<ImgStore> obj) throws ExecutionException, InterruptedException;
        }
        OnUpdateListener listener;
        public getImgStore() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }

        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ArrayList<ImgStore> doInBackground(String... strings) {
            Request.Builder builder = new Request.Builder();
            builder.url(strings[0]);

            Request request = builder.build();
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ImgStore>>() {}.getType();
            ArrayList<ImgStore>arrImgStore;

            try {
                Response response = okHttpClient.newCall(request).execute();
                arrImgStore=gson.fromJson(response.body().charStream(),type);
                return arrImgStore;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ImgStore> arrImgStore) {
            super.onPostExecute(arrImgStore);
            if (listener != null) {
                try {
                    listener.onUpdate(arrImgStore);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public  static class postImgStore extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public postImgStore() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        ImgStore ImgStore=new ImgStore();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public postImgStore(ImgStore imgstore) {
            //this.serverChuyenBien.setId(Integer.valueOf(chuyenbien.getmId()));
            this.ImgStore.setServerkey(Integer.valueOf(imgstore.getServerkey()));
            this.ImgStore.setStorekey(imgstore.getStorekey());
            this.ImgStore.setFortable(imgstore.getFortable());
            this.ImgStore.setImgpath(imgstore.getImgpath());
            this.ImgStore.setNgayps(imgstore.getNgayps());
            this.ImgStore.setUpdatetime(imgstore.getUpdatetime());
            this.ImgStore.setUsername(imgstore.getUsername());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<ImgStore>() {}.getType();
            String json = gson.toJson(this.ImgStore, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .post(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }


    public  static class putImgStore extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public putImgStore() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        ImgStore ImgStore=new ImgStore();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        public putImgStore(ImgStore imgstore) {
            //this.serverChuyenBien.setId(Integer.valueOf(chuyenbien.getmId()));
            this.ImgStore=imgstore;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {

            Gson gson = new Gson();
            Type type = new TypeToken<ImgStore>() {}.getType();
            String json = gson.toJson(this.ImgStore, type);
            RequestBody body = RequestBody.create(JSON, json);
            Request request =new Request.Builder()
                    .url(strings[0])
                    .put(body)
                    .build();
            try {

                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }

    }

    public  static class deleteImgStore extends AsyncTask<String, Void, ResponseFromServer> {
        public interface OnUpdateListener {
            void onUpdate(ResponseFromServer obj);
        }
        OnUpdateListener listener;
        public deleteImgStore() {
        }
        public void setUpdateListener(OnUpdateListener listener) {
            this.listener = listener;
        }
        //        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
//                .build();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ResponseFromServer doInBackground(String... strings) {
            Request request =new Request.Builder()
                    .url(strings[0])
                    .delete()
                    .build();
            try {
                Gson gson = new Gson();
                Response response=okHttpClient.newCall(request).execute();
                ResponseFromServer res = gson.fromJson(response.body().string(), ResponseFromServer.class);
                //return response.body().string();
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResponseFromServer res) {
            super.onPostExecute(res);
            if (listener != null) {
                listener.onUpdate(res);
            }
        }
    }
}
