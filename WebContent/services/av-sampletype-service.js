(function(){
'use strict';

function AvSampletypeService(restfactory) {
	// restfactory is a wrapper for $html.

	this.sampletypes = [];
	this.strings = [];


	this.loadSampletypes = function(id) {
		var thisSampletypesController = this;
		var promise = restfactory.GET("available_sampletypes.json?id="+id);
		promise.then(function(rest) {
			thisProcessesController.sampletypes = rest.data.sampletypes;
			thisProcessesController.strings = rest.data.strings;
			thisProcessesController.translate('de');
		}, function(rest) {
			console.log("Error loading sampletypes");
		});
	};

	this.translate = function(lang) {
		var strings=this.strings
		angular.forEach(this.sampletypes, function(sampletype) {
			angular.forEach(strings, function(translation) {
				if (sampletype.name==translation.string_key && translation.language=="de")
					{sampletype.trname=translation.value;}
			})
		})
	}

	this.articleClicked = function(sampletype) {
		console.log('Sampletype ' + sampletype + ' clicked');
	};

}
;


angular.module('unidaplan').service('AvSampletypeService', ['restfactory',AvSampletypeService]);

})();