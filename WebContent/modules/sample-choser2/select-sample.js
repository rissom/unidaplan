(function(){

	
function sampleChoser($scope, $modal) {
	
  this.samples = [{id:11},{id:12},{id:13}];
  
  this.stype = 'Solarzelle';
  
  this.open = function (size) {
    var modalInstance = $modal.open({
      animation: false,   // animations suck!
      templateUrl: 'modules/sample-choser/sample-choser.html',
      controller: 'ModalInstanceCtrl',
      size: size,
      
      resolve: [{
      samplenames: function () {
    	  	return $scope.samplename;
        	}
      	},{
      stype:    function () {
      	return $scope.stype;
  		}}]            
    });
    modalInstance.result.then(
    		function (selectedItem) {
    			$scope.selected = selectedItem;
    		}
    		,function () {
    			console.log("error loading samples")
    		});
  }
}; 
	
angular.module('unidaplan').controller('SampleChoserCtrl', ['$scope', '$modal', sampleChoser]); 


angular.module('unidaplan').controller('ModalInstanceCtrl', function ($scope, $modalInstance, samplenames, stype) {

  $scope.samplenames = samplenames;
  $scope.selected = {
    sample: $scope.samplenames[0]
  };

  $scope.ok = function () {
    $modalInstance.close($scope.selected.sample);
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('abbrechen');
  };
});

})();