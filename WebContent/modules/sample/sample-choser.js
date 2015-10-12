(function(){
'use strict';


function sampleChoser($translate,$scope,restfactory,types,sampleService) {

	this.samples=[];
	this.selectedTypesVar=[]
	this.selectorTypes=[];
	var thisController=this;
		
	
	$scope.$watch('sampleChoserCtrl.selectedtypes', function (seltypes){
		var typeList=[];
		if (seltypes==undefined || seltypes.length==0){  // Nothing selected => Everything selected
			angular.forEach(types,function(type) {
				typeList.push(type.id);			
			});
		} else {
		if (seltypes[0].id==0){  // all types field selected => Everything selected
			angular.forEach(types,function(type) {
				typeList.push(type.id);
			});
		}else{										// make list of selected types
			angular.forEach(thisController.selectedtypes,function(type) {
				typeList.push(type.id);	
			})
		}}
		thisController.selectedTypesVar = typeList;
		
		//check if there was change
		var oldUserinput="";
		if (thisController.userinput!=undefined) {
			oldUserinput=thisController.userinput;
		}
		var oldTypeIDs = thisController.selectedTypesVar;
		  setTimeout(function() {
			  	var newUserinput="";
				if (thisController.userinput!=undefined){
					newUserinput=thisController.userinput;
				}
			    if (oldUserinput==newUserinput){
			    	if( thisController.selectedTypesVar.equals(oldTypeIDs)){
			    		if (!thisController.firsttime){
				    	thisController.loadSamples();
			    		}
			    	}
			    }
			   
		  }, 250);
	});	
	
	
	
	$scope.$watch('sampleChoserCtrl.userinput', function (tmpStr){
		//check if there was change
		var oldUserinput="";
		if (thisController.userinput!=undefined) {
			oldUserinput=thisController.userinput;
		}
		var oldTypeIDs = thisController.selectedTypesVar;
		  setTimeout(function() {
			  	var newUserinput="";
				if (thisController.userinput!=undefined){
					newUserinput=thisController.userinput;
				}
			    if (oldUserinput==newUserinput){
			    	if( thisController.selectedTypesVar.equals(oldTypeIDs)){
				    	thisController.loadSamples();
			    	}
			    }			    
		  }, 250);
	});	
	

	
	// get a bunch of fitting samples
	this.loadSamples=function(){
		var details={}
		details.sampletypes=this.selectedTypesVar;	
		var name="";
		if (thisController.userinput!=undefined){name=thisController.userinput}
			var promise=sampleService.getSamplesByName(name,details);
			promise.then(function(data){
				thisController.samples=data.data;
				if (thisController.firsttime) {
					thisController.init();
					thisController.firsttime=false;
				}
			});		
	}
	
	
	
	// return the translated name string of a type for a sample
	this.getType=function(sample){
		var typeName
		angular.forEach(types,function(type) {
			if (sample.typeid==type.id){
				typeName=type.namef();
			}
		})
		return typeName;
	}
	
	
	// Check if the sampletype is selected
	this.typeSelected=function(sample){
		var found=false;
		angular.forEach(thisController.selectedTypesVar,function(type) {
			if (sample.typeid==type){
				found=true;
			}
		});
		return found;
	}
	

	// build a new array of selectable processtypes, with an "all-types" option
	this.init=function(lang){
		var selectorTypesTemp=[];
		var allTypesString=$translate.instant("all types");
		selectorTypesTemp.push({namef:function(){return allTypesString},'id':0});
		angular.forEach(types,function(type) {
			selectorTypesTemp.push(type);
		})
		this.selectorTypes=selectorTypesTemp;
//		 funzt nicht:
//		this.selectedtypes={trname:allTypesString,'id':0}
	}

	
	
	//activate function
	this.init();
	this.firsttime=true;
};

        
angular.module('unidaplan').controller('sampleChoser',['$translate','$scope','restfactory','types','sampleService',sampleChoser]);

})();