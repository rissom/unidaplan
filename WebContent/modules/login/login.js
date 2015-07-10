(function(){
'use strict';

var LoginCtrl=function($state){
		this.userLogin = function(){
			var data = {
				name : this.userinput,
				pw : this.pwinput
			}
		console.log('Login in: '+name+' with pw: '+pw);	
		};
	}

angular
	.module ('login',[])
	
	.config(['$stateProvider', function($stateProvider){
		$stateProvider
			.state('login', {
				url:'/login',
				templateUrl:'/js/modules/login/login.html',
				controller: 'LoginCtrl',
				controllerAs: 'login'
		});
	}])
	
.LoginCtrl	('LoginCtrl'), ['translate','$state']
})();