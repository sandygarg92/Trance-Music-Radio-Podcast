package com.onlineradiofm.trancemusicradio.ypylibs.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;


public class ImageProcessingUtils {

	public static final String TAG = ImageProcessingUtils.class.getSimpleName();
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	public static Bitmap decodePortraitBitmap(InputStream mInputStream, int desireW, int desireH) {
		try {
			if (mInputStream != null) {
				byte[] mByteArray = convertInputStreamToArray(mInputStream);

				BitmapFactory.Options option = new BitmapFactory.Options();
				option.inJustDecodeBounds = true;
				BitmapFactory.decodeByteArray(mByteArray, 0, mByteArray.length, option);
				option.inJustDecodeBounds = false;
				option.inSampleSize = calculateInSampleSize(option, desireW, desireH);
				return BitmapFactory.decodeByteArray(mByteArray, 0, mByteArray.length, option);
			}
		} catch (Exception e) {
			YPYLog.e(TAG, "--------->decodeBitmap error=" + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	private static byte[] convertInputStreamToArray(InputStream mInputStream) {
		if (mInputStream != null) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
				int len = 0;
				while ((len = mInputStream.read(buffer)) > -1) {
					baos.write(buffer, 0, len);
				}
				baos.flush();
				return baos.toByteArray();
			}
			catch (Exception e) {
				YPYLog.e(TAG, "--------->cloneInputStream error=" + e.getMessage());
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Bitmap getRotatedBitmap(Bitmap originalBitmap) {
		Matrix matrix = new Matrix();
		matrix.postRotate(90f);

		Bitmap rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
		if (rotatedBitmap != null) {
			originalBitmap.recycle();
			originalBitmap = null;
			return rotatedBitmap;
		}
		return rotatedBitmap;
	}

	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} 
			else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

}
