(function(){

function samplebutton(){
	return {
		restrict: 'E',		
		scope: { getTypeAction : '&',
				 buttonsample : '='
		},
		template: '<button class="btn btn-default" ui-sref="sample({sampleID:buttonsample.id})">'
		+'{{getTypeAction()}} {{buttonsample.name}}</button>'
	}
};

	
angular.module('unidaplan').directive('samplebutton', samplebutton)

})();
