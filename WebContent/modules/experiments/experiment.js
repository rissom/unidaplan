(function(){
'use strict';

function experiment(restfactory,$translate,$stateParams,$scope) {
	
	this.experiment =  {};		

	this.strings = [];
	
	this.trname= "empty";
	
	this.ID = function(){
		return $stateParams.experimentID;
	}
		
	var thisExpCtrl = this;
	$scope.$on('language changed', function(event, args) {
		thisExpCtrl.translate(args.language);
	});
	
	
	
	this.loadData = function() {
		var promise = restfactory.GET("experiment.json?id="+$stateParams.experimentID),
			expsCtrl=this;
		
	    promise.then(function(rest) {
	    	expsCtrl.experiment = rest.data.experiment;
	    	expsCtrl.strings = rest.data.strings;
	    	expsCtrl.translate($translate.use());
	    }, function(rest) {
	    	console.log("ERROR");
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
	
	
	
	this.keyUp = function(keyCode,newValue,parameter) {
		if (keyCode===13) {				// Return key pressed
			parameter.editing=false; 
			var oldValue=parameter.value;
			parameter.value=newValue;
			 if (parameter.pid) {
				var res = restfactory.POST('update-experiment-parameter.json',parameter);
				res.then(function(data, status, headers, config) {
						 },
						 function(data, status, headers, config) {
							parameter.value=oldValue;
							console.log('verkackt');
							console.log(data);
						 }
						);
			 } else {
				var res = restfactory.POST('add-experiment-parameter.json?sampleid='+this.experiment.id,parameter);
					res.then(function(data, status, headers, config) {
							 },
							 function(data, status, headers, config) {
								parameter.value=oldValue;
								console.log('verkackt');
								console.log(data);
							 }
							);
			 }
		}
		if (keyCode===27) {		// Escape key pressed
			parameter.editing=false;			
		}
	}
	
	
	
	
	this.translate = function(lang) {
		this.trname=thisExpCtrl.stringFromKey(this.experiment.name,this.strings);
		angular.forEach(this.experiment.parameters, function(parameter) {
			parameter.trname=thisExpCtrl.stringFromKey(parameter.stringkeyname,thisExpCtrl.strings);
		})
	}
};
    
        
angular.module('unidaplan').controller('expController',['restfactory','$translate','$stateParams','$scope',experiment]);

})();