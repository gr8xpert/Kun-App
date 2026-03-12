package com.example.kunworld.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ImageUtils {

    private static final int MAX_IMAGE_SIZE = 512; // Max width/height in pixels
    private static final int COMPRESSION_QUALITY = 85;

    /**
     * Copy and compress an image from a Uri to internal storage
     * @param context Application context
     * @param sourceUri Source image Uri
     * @param fileName Name for the saved file (without extension)
     * @return Path to the saved file, or null if failed
     */
    public static String copyImageToInternalStorage(Context context, Uri sourceUri, String fileName) {
        try {
            // Open input stream from Uri
            InputStream inputStream = context.getContentResolver().openInputStream(sourceUri);
            if (inputStream == null) return null;

            // Decode bitmap with inJustDecodeBounds to get dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            // Calculate sample size for efficient loading
            int originalWidth = options.outWidth;
            int originalHeight = options.outHeight;
            int sampleSize = 1;

            while (originalWidth / sampleSize > MAX_IMAGE_SIZE * 2 ||
                   originalHeight / sampleSize > MAX_IMAGE_SIZE * 2) {
                sampleSize *= 2;
            }

            // Decode bitmap with sample size
            options.inJustDecodeBounds = false;
            options.inSampleSize = sampleSize;
            inputStream = context.getContentResolver().openInputStream(sourceUri);
            if (inputStream == null) return null;

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            if (bitmap == null) return null;

            // Scale to max size if needed
            bitmap = scaleBitmap(bitmap, MAX_IMAGE_SIZE);

            // Save to internal storage
            File outputDir = new File(context.getFilesDir(), "avatars");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            File outputFile = new File(outputDir, fileName + ".jpg");
            FileOutputStream fos = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, fos);
            fos.flush();
            fos.close();

            // Recycle bitmap to free memory
            bitmap.recycle();

            return outputFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Scale a bitmap to fit within maxSize while maintaining aspect ratio
     */
    private static Bitmap scaleBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width <= maxSize && height <= maxSize) {
            return bitmap;
        }

        float ratio = Math.min((float) maxSize / width, (float) maxSize / height);
        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

        // Recycle original if different
        if (scaledBitmap != bitmap) {
            bitmap.recycle();
        }

        return scaledBitmap;
    }

    /**
     * Delete a profile image file
     */
    public static boolean deleteImage(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        try {
            File file = new File(filePath);
            return file.exists() && file.delete();
        } catch (Exception e) {
            return false;
        }
    }
}
