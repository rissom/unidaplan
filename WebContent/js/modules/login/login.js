(function(){
'use strict';

angular
	.module ('login',[])
	
	.config(['$stateProvider', function($stateProvider){
		$stateProvider
			.state('login', {
				url:'/view/login'
		})
	}])
})();