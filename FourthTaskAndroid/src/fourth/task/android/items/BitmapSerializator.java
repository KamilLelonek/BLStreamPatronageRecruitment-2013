package fourth.task.android.items;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapSerializator {
	public static byte[] serializeBitmap(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}
	
	public static Bitmap deserializeBitmap(byte[] bitmapArray) {
		if (bitmapArray == null) return null;
		return BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
	}
}