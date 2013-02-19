package fourth.task.android.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapManager {
	public static byte[] serializeBitmap(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}
	
	public static Bitmap deserializeBitmap(byte[] bitmapArray) {
		if (bitmapArray == null) return null;
		return BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
	}
	
	public static Bitmap downloadBitmap(String url) throws MalformedURLException, IOException {
		return BitmapFactory.decodeStream(new URL(url).openStream());
	}
}