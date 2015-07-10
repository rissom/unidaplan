(function(){

function samplebutton(){
	return {
		restrict: 'E',		
		template: '<button class="btn btn-default" ui-sref="sample({sampleID:buttonsample.id})">'
					+'{{buttonsample.trtypename}} {{buttonsample.name}}</button>',
		scope: { buttonsample : '=' }
	}
};

	
angular.module('unidaplan').directive('samplebutton', samplebutton)

})();
