package gov.ic.nga.kweb.plugins;

import java.io.ByteArrayOutputStream;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mapsforge.android.maps.StaticTileCache;

import android.graphics.Bitmap;
import android.util.Base64;

/**
 * This class implements a PhoneGap plugin to access the tile cache created by
 * MapsForge. Calls to this plugin should pass a function call of "getTile" with
 * an options array containing x, y and z values that correspond to the slippy
 * map convention:<br>
 * 
 * http://.../{z}/{x}/{y}.png<br>
 * 
 * where:<br>
 * 
 * z - Zoom level<br>
 * x - Calculated x coordinate<br>
 * y - Calculated y coordinate<br>
 * 
 * On success, if a tile is found within the cache this plugin returns a
 * JSONObject with the respective tile retrieved and base64 encoded in HTML
 * image format:<br>
 * 
 * {<br>
 * tile : "data:image/png;base64,{img data}"<br>
 * }<br>
 * 
 * If no tile is found, an empty object is returned (ie {}).
 * 
 * @see <a
 *      href="http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Implementations">slippy
 *      map implementation</a> for more information.
 * 
 * @author Zach Swain
 * 
 */
public class TileCache extends Plugin {

	/*
	 * TODO: Implement an in memory tile cache to reduce disk access time.
	 * (non-Javadoc)
	 * 
	 * @see org.apache.cordova.api.Plugin#execute(java.lang.String,
	 * org.json.JSONArray, java.lang.String)
	 */
	@Override
	public PluginResult execute(String action, JSONArray args, String callbackId) {

		try {
			if ("getTile".equalsIgnoreCase(action)) {
				JSONObject object = args.getJSONObject(0);
				long x = Long.parseLong(object.getString("x"));
				long y = Long.parseLong(object.getString("y"));
				byte z = Byte.parseByte(object.getString("z"));

				Bitmap bitmap = StaticTileCache.get(x, y, z);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
				byte[ ] buffer = baos.toByteArray();

				String encodedTile = "data:image/png;base64,"
						+ Base64.encodeToString(buffer, Base64.DEFAULT);

				JSONObject result = new JSONObject();

				if (null != bitmap) {
					result.put("tile", encodedTile);
				}

				return new PluginResult(PluginResult.Status.OK, result);
			}
		} catch (Exception e) {
			return new PluginResult(PluginResult.Status.ERROR, e.getMessage());
		}
		return null;
	}
}
