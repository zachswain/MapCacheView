/*
 * Implementation adapted from the suggestive solution here:
 * 
 * http://stackoverflow.com/questions/9296525/navigate-through-a-pre-known-map-offline-using-phonegap
 */
OSMWithLocalStorage = OpenLayers.Class(OpenLayers.Layer.OSM, {
    initialize: function(options) {
        OpenLayers.Layer.OSM.prototype.initialize.apply(this, ["CachedMap"]);
        this.async = true;
        this.isBaseLayer = true;

        this.url = 'http://tile.openstreetmap.org/${z}/${x}/${y}.png';
    },
    getURLasync: function(bounds, scope, prop, callback) {
        var url = OpenLayers.Layer.OSM.prototype.getURL.apply(this, [bounds]);
        
        var regexp = /(-?\d+)\/(-?\d+)\/(-?\d+)\.png/;
        var match = regexp.exec(url);
        
        if( match ) {
        	var z = match[1];
        	var x = match[2];
        	var y = match[3];
        	
        	if( window.plugins.TileCache ) {
            	window.plugins.TileCache.getTile(function(result) {
            		if( result.tile ) {
            			//console.log(x + "," + y + "," + z + ":success! ");
                		scope[prop] = result.tile;
                		callback.apply(scope);	
            		} else {
            			//console.log(x + "," + y + "," + z + ":success, but no tile");
            			scope[prop] = "img/MapTileUnavailable.png";
                		callback.apply(scope);
            		}
            	}, function(error) {
            		//console.log(x + "," + y + "," + z + ":error: " + error);
            		scope[prop] = "img/MapTileUnavailable.png";
            		callback.apply(scope);
            	}, {
            		x: x,
            		y: y,
            		z: z
            	});
            }
        } else {
        	scope[prop] = "img/MapTileUnavailable.png";
        	callback.apply(scope);
        }
    },
});