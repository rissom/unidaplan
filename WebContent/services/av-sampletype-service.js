(function(){
'use strict';

function avSampletypeService(restfactory,$translate) {
	// restfactory is a wrapper for $html.

	this.sampletypes = [];
	this.strings = [];


	this.loadSampletypes = function(id) {
		var thisSampletypesController = this;
		var promise = restfactory.GET("available_sampletypes.json?id="+id);
		promise.then(function(rest) {
			thisProcessesController.sampletypes = rest.data.sampletypes;
			thisProcessesController.strings = rest.data.strings;
			thisProcessesController.translate();
		}, function(rest) {
			console.log("Error loading sampletypes");
		});
	};

	
	this.stringFromKey = function(stringkey,strings) {
		var keyfound=false;
		var returnString="@@@ no string! @@@";
		angular.forEach(strings, function(translation) {
			if (!keyfound && stringkey==translation.string_key) {
				returnString = translation.value;
				if (translation.language==$translate.use()) {
					keyfound=true;
				}
			}
		})
		return returnString;
	};
	
	
//	this.getType(sample){}
	
	this.translate = function() {
		var strings=this.strings
		angular.forEach(this.sampletypes, function(sampletype) {
			thisController.stringFromKey(sampletype.string_key,strings);
		})
	}
}


angular.module('unidaplan').service('avSampletypeService', ['restfactory','$translate',avSampletypeService]);

})();