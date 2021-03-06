(function(){



angular.module('unidaplan').factory('restfactory', ['$q', '$rootScope','$http', '$state', function($q, $rootScope, $http, $state) {

	$http.defaults.headers.post["Content-Type"] = "application/json";
	$http.defaults.headers.put["Content-Type"] = "application/json";
	
	var rest = {};	// function to save the desired state in case of a failed transition
	
	
	rest.origin   = window.location.origin;
	rest.protocol = window.location.protocol;
	rest.path     = "";	
	
	
	
	
	rest.GET = function(restfunction, config) {
		restfunction = removeLeadingSlashes(restfunction); 
		var uri = rest.path + restfunction;
		
		var defer = $q.defer();
		try {
			var ret = $http.get(uri,config);
			ret.then(
				function(data) {
					try {
						if ( (typeof data.data == 'string') && (data.data.length>0) ) {
							var text = data.data;
							if (text.length>100) {
								text = text.substring(0,100) + "...";
							}
							console.log("restfactory.GET: " + uri + " no JSON warning: ",text);
						}
						console.log("restfactory.GET: from: " + uri + " got: ",data);
						defer.resolve(data);
					} catch (err) {
						console.log("restfactory.GET: "+uri+" parse error: "+data,err);
						defer.reject('parse error occured');
					}
				},
				function(data) {
					console.error("restfactory.GET error data: ",data);
					defer.reject("" + data.status);
			    }
			);
		} catch(err) {
			console.log("restfactory.GET: " + uri + " exception loading data: ", err);
			defer.reject('Oops, try catch!');
		}
		return defer.promise;
	};
	
	removeLeadingSlashes = function(_string) {
		if (_string.substring(0, 1) == "/") {
			return _string.substring(1, _string.length); 
		} else {
			return _string; 
		}
	};

	rest.PUT = function(restfunction, jsonParamObj,config) {
		restfunction = removeLeadingSlashes(restfunction); 
		var uri = rest.path+restfunction;
		
		var defer = $q.defer();
		try {
			console.log("restfactory.PUT: trying to access: "+uri+"  "+angular.toJson(jsonParamObj));
			var ret = $http.put(uri,jsonParamObj,config);
			ret.then(
				function(data) {
					try {
//						var p = new DOMParser();
//						var xml = p.parseFromString(data.data,'application/xml');
						console.log("restfactory.PUT: from: "+uri+" got: ",data);
						defer.resolve(data);
					} catch (err) {
						console.log("restfactory.PUT: parse error: ",err);
						defer.reject('parse error occured');
					}
				},
				function(data) {
					console.log("restfactory.PUT: error: ", data );
					defer.reject("" + data.status);
			    }
			);
		} catch(err) {
			console.log("restfactory.PUT: exception loading data: ",err);
			defer.reject('Oops, try catch!');
		}
		return defer.promise;
	};
	
	
	
	rest.POST = function(restfunction, jsonParamObj,config) {
		restfunction = removeLeadingSlashes(restfunction); 
		var uri = rest.path + restfunction;
		var defer = $q.defer();
		try {
			var text ="";
			if (jsonParamObj !== undefined) {
				text = angular.toJson(jsonParamObj);
				if (text.length>100) {
					text = text.substring(0,100)+"...";
				}
			}
			console.log("restfactory.POST: trying to access: " + uri + "  " + text);
			var ret = $http.post(uri,jsonParamObj,config);
			ret.then(
				function(data, status, headers, config) {
					try {
						console.log("restfactory.POST: from: " + uri + " got: ",data);
						defer.resolve(data);
					} catch (err) {
						console.log("restfactory.POST: parse error: ",err);
						defer.reject('parse error occured');
					}
				},
				function(data) {
					console.error("restfactory.POST error data: ",data);
					defer.reject("" + data.status);
			    }
			);
		} catch(err) {
			console.log("restfactory.POST: exception loading data: ",err);
			defer.reject('Oops, try catch!');
		}
		return defer.promise;
	};
	

	// to add body data, call with config : { data : bodyDataObject }
	rest.DELETE = function(restfunction, config) {
		restfunction = removeLeadingSlashes(restfunction); 
		var uri = rest.path+restfunction;
		console.log("restfactory.DELETE: trying to access: "+uri,config);
		
		var defer = $q.defer();
		try {
			var ret = $http['delete'](uri,config);  // $http.delete is frowned upon by eclipse
			ret.then(
				function(data) {
					defer.resolve(data);
				},
				function(data) {
					console.error("restfactory.DELETE error data: ",data);
					defer.reject("" + data.status);
			    }
			);
		} catch(err) {
			console.log("restfactory.DELETE: exception loading data: ",err);
			defer.reject('Oops, try catch!');
		}
		return defer.promise;
	};

//	rest.filterUTF8canvgSave = function(inString) {
//		if (inString.indexOf("\u00A0")!=-1) {
//			console.log("!!!!!!!!!!!!!!!!!!!!!!!!!!! rest.filterUTF8canvgSave: inString.indexOf(u0020): "+inString.indexOf("\u0020"));
//		}
//		return inString;
//	};
	
	return rest;
}]);


})();