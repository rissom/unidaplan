(function(){
'use strict';

function samplecontroller(restfactory,lang) {
	
	
	this.sample = {};
	
//	this.lang = 
	
	
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
	

	this.translate = function() {
		var sample=this.sample
		var strings=sample.strings;
		var typestringkey=this.sample.typestringkey;
		angular.forEach(strings, function(translation) {
			if (typestringkey==translation.string_key && translation.language==lang){
				sample.trtype=translation.value;
				if (sample.next) { sample.next.trtypename=translation.value; }
				if (sample.previous) { sample.previous.trtypename=translation.value; }
			}
		})
		angular.forEach(this.sample.parameters, function(parameter) {
			angular.forEach(strings, function(translation) {
				if (parameter.stringkeyname==translation.string_key && translation.language==lang)
					{parameter.trname=translation.value;}
			})
		})
		angular.forEach(this.sample.children, function(child) {
			angular.forEach(strings, function(translation) {
				if (child.typestringkey==translation.string_key && translation.language==lang)
					{child.trtypename=translation.value;}
			})
		})	
		angular.forEach(this.sample.ancestors, function(ancestor) {
			angular.forEach(strings, function(translation) {
				if (ancestor.typestringkey==translation.string_key && translation.language==lang)
					{ancestor.trtypename=translation.value;}
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


angular.module('unidaplan').controller('samplecontroller',['restfactory','lang',samplecontroller]);

})();