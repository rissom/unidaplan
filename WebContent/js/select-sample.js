(function(){

	
var sampleChoser = function('$scope', '$modal') {
  this.samplenames = ['2224', '2231', '1221'];
  this.stype = 'Solarzelle';
  this.open = function (size) {
    var modalInstance = $modal.open({
      animation: false,   // animations suck!
      templateUrl: 'view/sample-choser.html',
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
    modalInstance.result.then(function (selectedItem) {
      $scope.selected = selectedItem;
    }, function () {
    });
  };
}; 

	
angular.module('unidaplan')
.controller('SampleChoserCtrl', ['$scope', '$modal', sampleChoser]); 

angular.module('unidaplan').controller('ModalInstanceCtrl', function ($scope, $modalInstance, samplenames, stype) {

  $scope.samplenames = samplenames;
  $scope.selected = {
    sample: $scope.samplenames[0]
  };

  $scope.ok = function () {
    $modalInstance.close($scope.selected.sample);
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});

})();