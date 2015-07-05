(function(){
'use strict';

function samplecontroller(restfactory) {
	
	
	this.sample = {};
	
	
	this.keyUp = function(keyCode,newValue,parameter) {
		if (keyCode===13) {
			parameter.editing=false; 
			var oldValue=parameter.value;
			parameter.value=newValue;
			var res = restfactory.POST('savesampleparameter.json',parameter);
			res.then(function(data, status, headers, config) {
					 },
					 function(data, status, headers, config) {
						parameter.value=oldValue;
						console.log('verkackt');
						console.log(data);
					 }
					);
		}
		if (keyCode===27) {
			parameter.editing=false;			
		}
	}
	

	this.translate = function(lang) {
		var strings=this.sample.strings
		angular.forEach(this.sample.parameters, function(parameter) {
			angular.forEach(strings, function(translation) {
				if (parameter.stringkeyname==translation.string_key && translation.language=="de")
					{parameter.trname=translation.value;}
			})
		})
	}

	
	this.loadData = function(ID) {
		var promise = restfactory.GET("showsample.json?id="+ID);
		var thisSampleController = this;
	    promise.then(function(rest) {
	    	thisSampleController.sample = rest.data;
	    	thisSampleController.translate();
	    }, function(rest) {
	    	console.log("ERROR");
	    });
	};
	
	
	this.sayHello = function() {
		console.log('Hello')
	};
	
	
	this.saveParameter = function(parameter) {
		var res = restfactory.POST('savesampleparameter.json',parameter);
		res.then(function(data, status, headers, config) {
		},function(data, status, headers, config) {
			console.log('verkackt');
			console.log(data);		
		});
	};
	
	
    this.articleClicked = function(article) {
    	console.log("articleClicked(article):",article);
    }; 
}  


angular.module('unidaplan').controller('samplecontroller',['restfactory',samplecontroller]);

})();