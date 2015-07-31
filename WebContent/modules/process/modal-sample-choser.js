(function(){
'use strict';


function modalSampleChoser(avSampleTypeService,$translate,$scope,$modalInstance,restfactory,types,samples) {

	this.chosenSamples=samples;
	this.oldChosenSamples=samples;
	this.samples=[];
	this.selectedTypesVar=[]
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
	
	
	$scope.$watch('mSampleChoserCtrl.selectedtypes', function (seltypes){
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
	
	
	
	$scope.$watch('mSampleChoserCtrl.userinput', function (tmpStr){
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
			var promise=restfactory.POST('/samples_by_name.json?name='+name,details);
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
		return avSampleTypeService.getType(sample,types);
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
	
	
	
	this.close=function(){
	    $modalInstance.close(this.oldChosenSamples);
	}
	
	
	
	this.init=function(){
        var selectorTypesTemp=[];
        var allTypesString="all types";
        if ($translate.use()=="de"){
            allTypesString='alle Typen';
        }
        selectorTypesTemp.push({trname:allTypesString,'id':0});

		angular.forEach(types,function(type) {
			selectorTypesTemp.push(type);
		})
        this.selectortypes=selectorTypesTemp;
		
	}
	
	
	this.assignSamples=function(){
	    $modalInstance.close(this.chosenSamples);
	}
	
	
	this.removeSample=function(sample){
		for (var i=0;i<this.chosenSamples.length;i++){
			if (this.chosenSamples[i].name==sample.name){
				this.chosenSamples.splice(i,1);
			}
		}
	}

	
	this.choseSample=function(sample){
		var i;
		var found=false;
		for (i=0;i<this.chosenSamples.length;i++){
			if (this.chosenSamples[i].name==sample.name){
				found=true			
			}
		}
		if (!found) {
			this.chosenSamples.push(sample);
		}
	}
	
	
	var thisController = this;
	$scope.$on('language changed', function(event, args) {
		thisController.translate(args.language);
	});
	
	
	
	//activate function
	this.firsttime=true;
	this.init();
};

        
angular.module('unidaplan').controller('modalSampleChoser',['avSampleTypeService','$translate','$scope','$modalInstance','restfactory','types','samples',modalSampleChoser]);

})();