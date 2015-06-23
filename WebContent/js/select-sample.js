angular

.controller('SampleChoserCtrl', ['$scope', '$modal', '$log', function ($scope, $modal, $log) {

  $scope.samplenames = ['2224', '2231', '1221'];
  
  $scope.stype = 'Solarzelle';
  
  $scope.loadSamples = function(ID) {
	    console.log("button pressed");
		var promise = restfactory.GET("samples-by-name?name="+nameinputD);
	    promise.then(function(rest) {
	    	$scope.samplenames = rest.data;
	    }, function(rest) {
	    	console.log("ERROR");
	    });
	};

  $scope.open = function (size) {
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


}]);


myApp.controller('ModalInstanceCtrl', function ($scope, $modalInstance, samplenames, stype) {

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