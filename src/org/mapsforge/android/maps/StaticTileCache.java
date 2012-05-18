package org.mapsforge.android.maps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedHashMap;

import android.graphics.Bitmap;
import android.util.Log;

public class StaticTileCache {
	public static short TILE_SIZE = Tile.TILE_SIZE;
	private static String mapFile = null;
	private static LinkedHashMap<MapGeneratorJob, File> map = null;
	private static MapGeneratorJob defaultJob;

	public static boolean initialize(String locationOfMapFile) {
		mapFile = locationOfMapFile;

		Log.d("StaticTileCache", "loading " + mapFile);

		return deserializeCache();
	}

	private synchronized static boolean deserializeCache() {
		try {
			// check if the serialization file exists and is readable
			File file = new File(mapFile);

			if (!file.exists()) {
				return false;
			} else if (!file.isFile()) {
				return false;
			} else if (!file.canRead()) {
				return false;
			}

			// create the input streams
			FileInputStream inputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream = new ObjectInputStream(
					inputStream);

			// restore the serialized cache map (the compiler warning cannot be
			// fixed)
			map = (LinkedHashMap<MapGeneratorJob, File>) objectInputStream
					.readObject();

			// close the input streams
			objectInputStream.close();
			inputStream.close();

			defaultJob = (MapGeneratorJob) map.keySet().toArray()[0];

			// debug
//			for (Iterator<MapGeneratorJob> iterator = map.keySet()
//					.iterator(); iterator.hasNext();) {
//
//				MapGeneratorJob job = iterator.next();
//				Log.d("test", "file=" + job.mapFile + ",x=" + job.tile.x
//						+ ",y=" + job.tile.y + ",zoom=" + job.tile.zoomLevel);
//			}

			return true;
		} catch (IOException e) {
			Logger.e(e);
			return false;
		} catch (ClassNotFoundException e) {
			Logger.e(e);
			return false;
		}
	}
	
	public static Bitmap get(long x, long y, byte zoom) {
		Tile tile = new Tile(x, y, zoom);
		
		MapGeneratorJob job = new MapGeneratorJob(tile, defaultJob.mapViewMode,
				defaultJob.mapFile,
				defaultJob.textScale, defaultJob.drawTileFrames,
				defaultJob.drawTileCoordinates, defaultJob.highlightWater);
		
		ByteBuffer tileBuffer = ByteBuffer.allocate(Tile.TILE_SIZE_IN_BYTES);
		
		Bitmap bitmap = get(job, tileBuffer);
		
		Log.d("test", "success = " + (bitmap!=null));
		
		return bitmap;
	}
	
	private static Bitmap get(MapGeneratorJob mapGeneratorJob, ByteBuffer buffer) {
		try {
			File inputFile;
			
			synchronized (map) {
				inputFile = map.get(mapGeneratorJob);
			}
			
			if( null==inputFile ) return null;
			
			FileInputStream fileInputStream = new FileInputStream(inputFile);
			if (fileInputStream.read(buffer.array()) == buffer.array().length) {
				// the complete bitmap has been read successfully
				buffer.rewind();
			}
			fileInputStream.close();
			
			Bitmap tileBitmap = Bitmap.createBitmap(Tile.TILE_SIZE, Tile.TILE_SIZE,
					Bitmap.Config.RGB_565);
			
			tileBitmap.copyPixelsFromBuffer(buffer);
			
			return tileBitmap;
		} catch (FileNotFoundException e) {
			synchronized (map) {
				map.remove(mapGeneratorJob);
			}
			return null;
		} catch (IOException e) {
			Logger.e(e);
			return null;
		}
	}
}
