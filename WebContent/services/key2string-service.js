(function(){
'use strict';

var key2stringService = function($translate){

	
	// get the translated string for a string key
	this.key2string = function(stringkey,strings) {
		return this.key2stringWithLang(stringkey,strings,$translate.use());
	};
	
	
	this.key2stringWithLang = function(stringkey,strings,lang) {
		var keyfound=false;
		var returnString="@@@ no string! @@@";
		angular.forEach(strings, function(translation) {
			if (!keyfound && stringkey==translation.string_key) {
				returnString = translation.value;
				if (translation.language==lang) {
					keyfound=true;
				}
			}
		})
		return returnString;
	};
	
	this.replace = function(string){
		if (string=="@@@ no string! @@@"){
			return "-"
		}
		return string
	}
	
	this.key2stringWithLangStrict = function(stringkey,strings,lang) {
		var keyfound=false;
		var returnString="@@@ no string! @@@";
		angular.forEach(strings, function(translation) {
			if (!keyfound && stringkey==translation.string_key &&
				translation.language==lang) {
					returnString = translation.value;
			}})
		return returnString;
	}
}


angular.module('unidaplan').service('key2string', ['$translate',key2stringService]);

})();