package com.example.loveextra.microblog.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

public class Tools {
	/**
	 * 转换时间格式
	 * @param s
	 * @return
	 */
	public static String recodeTime(String s) {
		SimpleDateFormat sm = new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
		SimpleDateFormat sm2 = new SimpleDateFormat("MM-dd mm:ss");
		try {
			Date date = sm.parse(s);
			String time = sm2.format(date);
			return time;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 计算时间间隔
	 * 
	 * @param s
	 */
	public static String subTime(String s) {
		SimpleDateFormat sm = new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
		try {
			Date date = sm.parse(s);

			Date dateNow = new Date();
			long dateL = date.getTime();
			long dateNowL = dateNow.getTime();
			long time = dateNowL - dateL;
			int mins = (int) (time / 60 / 1000);
			if (mins == 0)
				return "刚刚";
			if (mins < 60) {
				return mins + "分钟前";
			} else {
				int hours = mins / 60;
				if (hours < 24) {
					return hours + "小时前";
				} else {
					int day = hours / 24;
					if (day < 7) {
						return day + "天前";
					} else {
						return "一周前";
					}
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";

	}

	/**
	 * source解析
	 * 
	 * @param s
	 * @return
	 */
	public static String codeSource(String s) {
		if (TextUtils.isEmpty(s)) {
			return s;
		}
		if (!s.contains(">")) {
			return s;
		}
		String[] str = s.split(">");
		str = str[1].split("<");
		return str[0];

	}

	/**
	 * 设置缓存
	 */
	/*
	 * private static LruCache<String, Bitmap> cache = new LruCache<String,
	 * Bitmap>( (int) (Runtime.getRuntime().totalMemory() / 4)) {
	 * 
	 * @Override protected int sizeOf(String key, Bitmap value) {
	 * 
	 * return value.getRowBytes() * value.getHeight();
	 * 
	 * }
	 * 
	 * };
	 * 
	 * public static void getBitmapNet(Context context, String url,
	 * NetworkImageView netImg) { RequestQueue rqQueue =
	 * Volley.newRequestQueue(context); ImageLoader imgLoader = new
	 * ImageLoader(rqQueue, new ImageCache() {
	 * 
	 * @Override public void putBitmap(String url, Bitmap bitmap) {
	 * Log.i("Test", url); if (bitmap != null) { File file = new
	 * File(Environment .getExternalStorageDirectory().getAbsoluteFile() +
	 * "/lianqing", getName(url)); Log.i("Test", getName(url));
	 * 
	 * try { file.createNewFile(); } catch (IOException e) {
	 * e.printStackTrace(); } FileOutputStream fos = null; try { fos = new
	 * FileOutputStream(file); } catch (FileNotFoundException e) {
	 * e.printStackTrace(); } Log.i("Test", file.getAbsolutePath());
	 * bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); try { fos.flush();
	 * } catch (IOException e) { e.printStackTrace(); } try { fos.close(); }
	 * catch (IOException e) { e.printStackTrace(); } } }
	 * 
	 * @Override public Bitmap getBitmap(String url) { String path =
	 * Environment.getExternalStorageDirectory() .getAbsoluteFile() +
	 * "/lianqing/" + getName(url); Bitmap bitmap =
	 * BitmapFactory.decodeFile(path); return bitmap; } }); imgLoader.get(url,
	 * ImageLoader.getImageListener(netImg, R.drawable.loading_01,
	 * R.drawable.loading_06)); netImg.setImageUrl(url, imgLoader);
	 * rqQueue.cancelAll(imgLoader);
	 * 
	 * }
	 * 
	 * 
	 * public static void getDrawablePic(Context context, String url, ImageView
	 * imageView) { RequestQueue rqQueue = Volley.newRequestQueue(context);
	 * ImageLoader imageLoader = new ImageLoader(rqQueue, new ImageCache() {
	 * 
	 * @Override public void putBitmap(String url, Bitmap bitmap) {
	 * cache.put(url, bitmap); }
	 * 
	 * @Override public Bitmap getBitmap(String url) { return cache.get(url); }
	 * });//新建一个ImageLoader,传入requestQueue和图片缓存类 ImageListener listener =
	 * ImageLoader.getImageListener(imageView, R.drawable.loading_01,
	 * R.drawable.loading_06);//参数分别为要显示的图片控件，默认显示的图片（用于图片未下载完时显示），下载图片失败时显示的图片
	 * imageLoader.get(url, listener,150 ,98 );//开始请求网络图片
	 * 
	 * }
	 */
	/**
	 * 判断网络状态
	 * @param con
	 * @return
	 */
	public static boolean isNetworkAvailable(Context con)
	{
	ConnectivityManager cm = (ConnectivityManager)con.getSystemService(Context.CONNECTIVITY_SERVICE);
	if( cm == null )
	return false;
	NetworkInfo netinfo = cm.getActiveNetworkInfo();
	if (netinfo == null )
	{
	return false;
	}
	if(netinfo.isConnected())
	{
	return true;
	}
	return false;
	}
	/**
	 * sharePreference
	 */
	public static void sharePre(Context context,String s,String key,String value){
		SharedPreferences sp=context.getSharedPreferences(s, Context.MODE_PRIVATE);
		Editor editor=sp.edit();
		editor.putString(key, value);
		editor.commit();
		
	}
	/**
	 * 图片文件名
	 * 
	 * @param url
	 * @return
	 */
	public static String getName(String url) {
		StringBuilder sb = new StringBuilder();
		if (url.endsWith(".jpg")) {
			String[] u = url.split("/");
			return u[4];
		} else {
			String[] u = url.split("/");
			for (String i : u) {
				sb.append(i);
			}
			sb.append(".jpg");
			return sb.toString();
		}
	}
}
