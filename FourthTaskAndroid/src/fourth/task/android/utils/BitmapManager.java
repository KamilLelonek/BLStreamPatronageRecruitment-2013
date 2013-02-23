package fourth.task.android.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * This class performs simple bitmap transformation form and to byte array.
 * Keeping data in byte array which is essentially easy to serialize.
 */
public class BitmapManager {
	/**
	 * Converts bitmap into byte array.
	 * 
	 * @param bitmap bitmap to convert
	 * @return byte array containing bitmap data
	 */
	public static byte[] serializeBitmap(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}
	
	/**
	 * Converts byte array to bitmap.
	 * 
	 * @param bitmapArray containing bitmap data
	 * @return converted bitmap
	 */
	public static Bitmap deserializeBitmap(byte[] bitmapArray) {
		if (bitmapArray == null) return null;
		return BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
	}
	
	/**
	 * Downloads bitmap from specific URL address
	 * 
	 * @param url where bitmap is downloaded from
	 * @return downloaded bitmap
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static Bitmap downloadBitmap(String url) throws MalformedURLException, IOException {
		return BitmapFactory.decodeStream(new URL(url).openStream());
	}
}