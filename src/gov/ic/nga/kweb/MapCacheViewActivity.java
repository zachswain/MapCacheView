package gov.ic.nga.kweb;

import org.apache.cordova.DroidGap;
import org.mapsforge.android.maps.StaticTileCache;

import android.os.Bundle;

public class MapCacheViewActivity extends DroidGap {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.loadUrl("file:///android_asset/www/index.html");

		// Initialize the StaticTileCache with the (default) location of the
		// MapCache cache
		StaticTileCache.initialize("/sdcard/UserCache/cache/(backup)cache.ser");
	}
}