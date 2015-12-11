(function(){
'use strict';


var duration=function($translate) {
	return function(millseconds) {
		var dayStr=$translate.instant('day');
		var daysStr=$translate.instant('days');
		var hourStr=$translate.instant('hour');
		var hoursStr=$translate.instant('hours');
		var minStr=$translate.instant('minutes');		
		var yearStr=$translate.instant('year');
		
	    var seconds = Math.floor(millseconds / 1000);
	    var years = Math.floor(seconds / 31557600);
	    var days = Math.floor((seconds % 31557600) / 86400);
	    var hours = Math.floor((seconds % 86400) / 3600);
	    var minutes = Math.floor(((seconds % 86400) % 3600) / 60);
	    var durationString = '';
	    if(years > 0) durationString += (years > 1) ? (years + " "+yearStr+" ") : (year + " "+yearStr+" ");
	    if(days > 0) durationString += (days > 1) ? (days + " "+daysStr+" ") : (days + " "+dayStr+" ");
	    if(hours > 0 && years<1) durationString += (hours > 1) ? (hours + " "+hoursStr+" ") : (hours + " "+hourStr+" ");
	    if(minutes >= 0  && days<1) durationString += (minutes > 1) ? (minutes + " "+minStr+" ") : (minutes + " "+minStr+" ");
	    return durationString;
	};
};



angular.module('unidaplan').filter('duration',['$translate',duration]);

})();