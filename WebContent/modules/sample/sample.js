(function(){
'use strict';

function samplecontroller(restfactory,$translate,$scope) {
	
	
	this.sample = {};
	
	this.sample.empty = true;
	
	this.sample.parameters = [];
	
	this.sample.titleparameters = [];

	this.tKeyUp = function(keyCode,newValue,parameter) {
		if (keyCode===13) {
			parameter.editing=false; 
			var oldValue=parameter.value;
			var res = restfactory.POST('update-sample-parameter.json',parameter);
				res.then(
					function(data, status, headers, config) {
						parameter.value=newValue;
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
	
	$scope.$on('language changed', function(event, args) {
		$scope.ssc.translate(args.language);
	});
	
	this.keyUp = function(keyCode,newValue,parameter) {
		if (keyCode===13) {
			parameter.editing=false; 
			var oldValue=parameter.value;
			parameter.value=newValue;
			 if (parameter.pid) {
				var res = restfactory.POST('update-sample-parameter.json',parameter);
				res.then(function(data, status, headers, config) {
						 },
						 function(data, status, headers, config) {
							parameter.value=oldValue;
							console.log('verkackt');
							console.log(data);
						 }
						);
			 } else {
				var res = restfactory.POST('add-sample-parameter.json?sampleid='+this.sample.id,parameter);
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
		if (keyCode===27) {
			parameter.editing=false;			
		}
	}
	
	
	
	this.deleteSample = function()
	{  
		var id=this.sample.id;
		var res = restfactory.GET("delete-sample?id="+id);
		res.then(function(data, status, headers, config) {   // success
						// gehe zur Experimentseite			
				 },
					function(data, status, headers, config) { 	 // fail
				    console.log("Error deleting Sample");
					console.log("Sample id: ",id);
				 }
		);
	}
		
	

	this.translate = function(lang) {
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
		angular.forEach(this.sample.plans, function(plan) {
			angular.forEach(strings, function(translation) {
				if (plan.name==translation.string_key && translation.language==lang)
					{plan.trname=translation.value;}
			})
		})	
	}

	
	this.loadData = function(ID) {
		var promise = restfactory.GET("showsample.json?id="+ID);
		var thisSampleController = this;
	    promise.then(function(rest) {
	    	var temp = rest.data;
	    	temp.titleparameters=[];
	    	var tempParameters=[];
			angular.forEach(temp.parameters, 
				function(parameter) {
					if (parameter.id_field) {
						temp.titleparameters.push(parameter);						
					} else {
						tempParameters.push(parameter);
					}
				}
			)
			temp.parameters=tempParameters;
			thisSampleController.sample = temp;
			thisSampleController.sample.empty = false;
	    	thisSampleController.translate($translate.use());
	    }, function(rest) {
	    	thisSampleController.sample.error = "Not Found!";
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


angular.module('unidaplan').controller('samplecontroller',['restfactory','$translate','$scope',samplecontroller]);

})();