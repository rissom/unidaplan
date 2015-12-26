 (function(){
'use strict';

var selectOnClick = function($window){

    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            element.on('click', function () {
                if (!$window.getSelection().toString()) {
                    // Required for mobile Safari
                    this.setSelectionRange(0, this.value.length)
                }
            });
        }
    };   
};

angular.module('unidaplan').directive('selectOnClick', ['$window',selectOnClick]);

})();