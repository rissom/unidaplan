(function(){
'use strict';


function sampleChoser(sampleService,$translate,$scope,restfactory) {

	this.samples=[];
	this.types=[];
	this.strings=[];
	this.selectortypes=[];
	thisController=this;
	
	
	// attach the .equals method to Array's prototype to call it on any array
	Array.prototype.equals = function (array) {
	    // if the other array is a falsy value, return
	    if (!array)
	        return false;

	    // compare lengths - can save a lot of time 
	    if (this.length != array.length)
	        return false;

	    for (var i = 0, l=this.length; i < l; i++) {
	        // Check if we have nested arrays
	        if (this[i] instanceof Array && array[i] instanceof Array) {
	            // recurse into the nested arrays
	            if (!this[i].equals(array[i]))
	                return false;       
	        }           
	        else if (this[i] != array[i]) { 
	            // Warning - two different object instances will never be equal: {x:20} != {x:20}
	            return false;   
	        }           
	    }       
	    return true;
	}   
	
	
	
	this.loadTypes=function(){
		var promise=restfactory.GET('/sampletypes.json');
		promise.then(function(data){
			thisController.types=data.data.sampletypes;
			thisController.strings=data.data.strings;
			thisController.loadSamples();
		});
	}
	
	
	$scope.$watch('sampleChoserCtrl.userinput', function (tmpStr){
		var oldUserinput="";
		if (tmpStr!=undefined) {oldUserinput=tmpStr;}
		var oldTypeIDs = thisController.getSelectedSampleTypeIDs();
		  setTimeout(function() {
			    // if searchStr is still the same..
			    // go ahead and retrieve the data
			    if (thisController.checkForChange(oldUserinput, oldTypeIDs)==false)			    	
			    {
			    	thisController.loadSamples();
			    }
			  }, 250);
	});			
	
	
	this.checkForChange = function(oldUserinput, oldTypeIDs){
			if (oldUserinput == thisController.userinput){
				var newTypeIDs=thisController.getSelectedSampleTypeIDs();
				 	if (newTypeIDs.equals(oldTypeIDs)){
						return false;				 	
					}
			}
			return true;
	}
	
	this.stringFromKey = function(stringkey,strings) {
		var keyfound=false;
		var returnString="@@@ no string! @@@";
		angular.forEach(strings, function(translation) {
			if (!keyfound && stringkey==translation.string_key) {
				returnString = translation.value;
				if (translation.language==$translate.use()) {
					keyfound=true;
				}
			}
		})
		return returnString;
	};
	
	
	// get a bunch of fitting samples
	this.loadSamples=function(){
		var details={}
		details.sampletypes=this.getSelectedSampleTypeIDs();	
		var name="";
		if (thisController.userinput!=undefined){name=thisController.userinput}
			var promise=restfactory.POST('/samples_by_name.json?name='+name,details);
			promise.then(function(data){
				thisController.samples=data.data;
				thisController.translate();
			});		
	}
	
	
	
	this.getSelectorSize=function(){
		console.log("working");
		return "12";
	}
	
	
	
	this.getType=function(sample){
		var typeName
		angular.forEach(thisController.types,function(type) {
			if (sample.typeid==type.id){
				typeName=type.trname;
			}
		})
		return typeName;
	}
	
	
	
	this.typeSelected=function(sample){
		if (this.selectedtypes==undefined){
			return true;
		}
		if(this.selectedtypes.length>0 && this.selectedtypes[0].id==0){
			return true;
		}
		var found=false;
		angular.forEach(thisController.selectedtypes,function(type) {
			if (sample.typeid==type.id){
				found=true;
			}
		});
		return found;
	}
	
	
	
	this.translate=function(lang){
		this.selectortypes=[];
		angular.forEach(thisController.types,function(type) {
			type.trname=thisController.stringFromKey(type.string_key,thisController.strings);
			thisController.selectortypes.push(type);
		})
		if (lang=="de"){
			thisController.selectortypes.unshift({trname:'alle Typen','id':0});
		}else{
			thisController.selectortypes.unshift({trname:'all types','id':0});
		}
	}
	
	
	
	this.getSelectedSampleTypeIDs=function(){
		var typeList=[]
		if (thisController.selectedtypes==undefined || thisController.selectedtypes[0].id==0){  // all types selected
			angular.forEach(thisController.types,function(type) {
				typeList.push(type.id);			
			});
		}else{
			angular.forEach(thisController.selectedtypes,function(type) {
				typeList.push(type.id);			
			})
		}
		return typeList;
	}
	
	
	
	var thisController = this;
	$scope.$on('language changed', function(event, args) {
		thisController.translate(args.language);
	});
	
	
	
	//activate function
	this.loadTypes();
};

        
angular.module('unidaplan').controller('sampleChoser',['sampleService','$translate','$scope','restfactory',sampleChoser]);

})();