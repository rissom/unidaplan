(function(){
'use strict';

angular.module('unidaplan').controller('menu', function () {

  this.status = {
    isopen: false
  };

//  $scope.toggleDropdown = function($event) {
//    $event.preventDefault();
//    $event.stopPropagation();
//    $scope.status.isopen = !$scope.status.isopen;
//  };
  
  this.currentPage='sample';
  this.activeSample={'ID':'2'};
  
  this.select=function(selected) {
	this.currentPage=selected; 
  };
});
})();