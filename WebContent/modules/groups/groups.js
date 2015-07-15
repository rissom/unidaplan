(function(){
'use strict';

function groupController(restfactory,$translate,$scope) {
	
	this.groups =  [{"id":1,"name":"Microscopy Group"}
					,{"id":2,"name":"X-Ray Diffraction Group"}
					,{"id":3,"name":"Solar Energy Groups","deletable":true}];			

	this.strings = [];
			
	this.loadData = function() {
		var promise = restfactory.GET("get-groups.json"),
		 	userCtrl=this;
		
		
	    promise.then(function(rest) {
	    	userCtrl.users = rest.data;
	    }, function(rest) {
	    	console.log("ERROR");
	    });
	};
	
	var thisUserCtrl = this;
	$scope.$on('language changed', function(event, args) {
//		thisUserCtrl.translate(args.language);
	});
	
	this.deleteUser = function(user) {
		var promise = restfactory.GET("delete-group?id="+group.id);
	    promise.then(function(rest) {
	    	console.log("group deleted")
	    }, function(rest) {
	    	console.log("ERROR");
	    });
	}
	
	
//	this.getRole = function(user) {
//		if (user.role==undefined){
//			return "horst";		
//		}else{
//			return this.roles[user.role];
//		}
//	};
//	
	
	
//	this.translate = function(lang) {
//		if (lang=='en') {
//			this.roles=["Admins","Technicians","Scientist"];
//		}else{
//			this.roles=["Administrator","Techniker","Wissenschaftler"];
//		}
//	};

};
    
        
angular.module('unidaplan').controller('groupController',['restfactory','$translate','$scope',groupController]);

})();