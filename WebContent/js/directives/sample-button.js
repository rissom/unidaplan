(function(){

function samplebutton(){
	return {
		restrict: 'E',		
		template: '<button class="btn btn-default" ng-click="action()">'		
			+'{{buttonsample.type}} {{buttonsample.name}} '
		+'</button>',
		scope: { buttonsample : '=' , 
				 action: '&' }
	}
};

	
angular.module('unidaplan').directive('samplebutton', samplebutton)

})();
