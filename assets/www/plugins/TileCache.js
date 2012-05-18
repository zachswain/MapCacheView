/*
 * This is the javascript plugin implementation for the NGA MapCache tile cache.<br>
 * 
 * Usage:
 * 
 * TileCache.getTile(successFunction, failureFunction, options)
 * 
 * Where:
 * 	successFunction: Function called on successful execution.  Receives a single result object that has the form:
 * 
 * {
 * 		tile : {tile img data}
 * }
 * 
 * failureFunction: Function called if an error is thrown.  Receives a PhoneGap error object.
 * 
 * options: Object containing the x, y and z values to reference the tile from:
 * 
 * {
 * 		x : {x},
 * 		y : {y},
 * 		z : {z}
 * }
 */
function TileCache() {
}

TileCache.prototype.getTile = function(win, fail, options) {
	return cordova.exec(win, fail, "TileCache", "getTile", [ options ]);
};

cordova.addConstructor(function() {
	cordova.addPlugin("TileCache", new TileCache());
});