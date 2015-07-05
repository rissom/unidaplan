(function(){
'use strict';

function AvProcService(restfactory) {
	// restfactory is a wrapper for $html.

	this.processes = [];
	this.strings = [];


	this.loadProcesses = function(id) {
		var thisProcessesController = this;
		var promise = restfactory.GET("available_processtypes.json?id="+id);
		promise.then(function(rest) {
			thisProcessesController.processes = rest.data.processes;
			thisProcessesController.strings = rest.data.strings;
			thisProcessesController.translate('de');
		}, function(rest) {
			console.log("Error loading processtypes");
		});
	};

	this.translate = function(lang) {
		var strings=this.strings
		angular.forEach(this.processes, function(proc) {
			angular.forEach(strings, function(translation) {
				if (proc.name==translation.string_key && translation.language=="de")
					{proc.trname=translation.value;}
			})
		})
	}

	this.articleClicked = function(process) {
		console.log('Process ' + process + ' clicked');
	};

}
;


angular.module('unidaplan').service('AvProcService', ['restfactory',AvProcService]);

})();