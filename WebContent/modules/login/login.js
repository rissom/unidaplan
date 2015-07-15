(function(){
'use strict';

var loginController=function($state){
		this.userLogin = function(){
			var data = {
				name : this.userinput,
				pw : this.pwinput
			}
		console.log('Login in: '+name+' with pw: '+pw);	
		};
	}

angular.module('unidaplan').controller('loginController',['$state','restfactory','$translate',loginController])
	
})();