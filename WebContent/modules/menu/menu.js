(function(){
'use strict';

function menuf() {

  this.status = {
    isopen: false
  };

//  $scope.toggleDropdown = function($event) {
//    $event.preventDefault();
//    $event.stopPropagation();
//    $scope.status.isopen = !$scope.status.isopen;
//  };
    
  this.activeSample={'ID':'2'};
  
  this.select=function(selected) {
	this.currentPage=selected; 
  }
};
  


angular.module('unidaplan').controller('menu',menuf);
  
})();