package com.basewarp.basewarp.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

/**
 * Provides a load of useful functionality for dealing with bitmaps.
 * (Such as converting a "Picture" to a Bitmap & scaling a Bitmap down while saving memory)
 * @author Samuel
 *
 */
public class ImageHelper {
	
	/**
	 * Returns an oversized bitmap in the appropriate bounds
	 * @param path : the file path to the bitmap
	 * @param reqWidth : required width
	 * @param reqHeight : required height
	 * @return : Bitmap bm : resampled bitmap
	 */
	public static Bitmap decodeSampledBitmapFromResource(String path,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(path, options);
	}

	/**
	 * Returns an oversized bitmap in the appropriate bounds
	 * @param data : the byte[] holding the bitmap
	 * @param reqWidth : required width
	 * @param reqHeight : required height
	 * @return : Bitmap bm : resampled bitmap
	 */
	public static Bitmap decodeSampledBitmapFromResource(byte[] data,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(data, 0, data.length, options);
	}


	private static int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}
	
	/**
	 * Converts an image of type Picture, to a bitmap. Neat.
	 * @param picture
	 * @return
	 */
	public static Bitmap pictureDrawable2Bitmap(Picture picture){
		PictureDrawable pictureDrawable = new PictureDrawable(picture);
		Bitmap bitmap = Bitmap.createBitmap(pictureDrawable.getIntrinsicWidth(),pictureDrawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawPicture(pictureDrawable.getPicture());
		return bitmap;
	}

	public static Bitmap fixRotation(String bmPath, Bitmap source) {
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(bmPath);
		} catch (IOException e) {
			Log.e("com.basewarp.basewarp.util.Util", e.getMessage());
			e.printStackTrace();
		}
		int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
		int rotationInDegrees = ImageHelper.exifToDegrees(rotation);
		
		Matrix matrix = new Matrix();
		if (rotation != 0f) {
			matrix.preRotate(rotationInDegrees);
		}
		
		Bitmap adjustedBitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
		return adjustedBitmap;
	}

	public static int exifToDegrees(int exifOrientation) {        
	    if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; } 
	    else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; } 
	    else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }            
	    return 0;    
	}
	
	public static String loadContentPathToEncodedBitmap (String contentPath, int width, int height) {
		
		String content = "";
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Bitmap image = ImageHelper.decodeSampledBitmapFromResource(contentPath, width, height);
			image = ImageHelper.fixRotation(contentPath, image);
			image.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);

			try {
				byte[] ba = outputStream.toByteArray();
				content = Base64.encodeToString(ba, Base64.DEFAULT);
			} catch (OutOfMemoryError e) {
				// TODO: handle exception
				System.gc();
				e.printStackTrace();
			}
			return content;
	}
	
	public static Bitmap getBitmapFromStaticFile(int reqWidth, int reqHeight) {
		return getBitmapFromFile(Constants.photoFilePath + Constants.photoFileName, reqWidth, reqHeight);
	}
	
	public static Bitmap getBitmapFromFile(String path, int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		
		File f = new File(path);
		BitmapFactory.decodeFile(f.getAbsolutePath(), options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		
		Bitmap temp = BitmapFactory.decodeFile(f.getAbsolutePath(), options);
		temp = ImageHelper.fixRotation(f.getAbsolutePath(), temp);


		return temp;
	}
	
	/**
	 * Writes a bitmap with the specified filename in the standard resources directory
	 * @param bm : bitmap to write
	 * @param fileName : filename (NO directories) to write to
	 */
	public static void writeBitmapToCacheFile(Bitmap bm, String fileName, Activity context) {
		File cacheDir = context.getCacheDir();
		if(!cacheDir.exists()) cacheDir.mkdirs();
		if(bm != null) {
			try {
				FileOutputStream fos = context.openFileOutput(fileName, Activity.MODE_PRIVATE);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(new SerialBitmap(bm));
				oos.close();
				Log.w("write image success", cacheDir + "/" + fileName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				Log.w("BubbleListManager","Storing imagecache to file failed!");
			} catch (IOException e) {
				e.printStackTrace();
				Log.w("BubbleListManager","Storing imagecache to file failed!");
			}
		}
	}
	
	public static Bitmap readBitmapFromCacheFile(String fileName, Activity context) {
		File cacheDir = context.getCacheDir();
		if(cacheDir.exists()) {
			File ic = context.getFileStreamPath(fileName);
			FileInputStream fis;
			ObjectInputStream ois;
			if(ic.exists()) {
				try {
					fis = context.openFileInput(fileName);
					ois = new ObjectInputStream(fis);
					SerialBitmap temp  = (SerialBitmap) ois.readObject();
					ois.close();
					Log.w("read image success", cacheDir + "/" + fileName);
					return temp.getBitmap();
				} catch (FileNotFoundException e) {
					Log.w("BubbleListManager", "Could not find " + fileName);
				} catch (StreamCorruptedException e) {
					e.printStackTrace();
					Log.w("BubbleListManager", "Corrupted input stream for " + fileName);
				} catch (IOException e) {
					e.printStackTrace();
					Log.w("BubbleListManager", "IOException for " + fileName);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					Log.w("BubbleListManager", "Cast to object failed for " + fileName);
				}
				ic.delete(); // Remove cache file so we don't accidentally load it with stale data later (or if anything was corrupted and hence didn't load)
			}
		}
		return null;
	}
}


