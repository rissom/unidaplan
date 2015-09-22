(function(){
'use strict';


function modalSampleChoser(avSampleTypeService,$translate,$scope,$modalInstance,restfactory,types,samples,except,buttonLabel,mode) {

	if (samples) {
		this.chosenSamples=samples.slice(0);
	} else {
		this.chosenSamples=[];
	}
	this.oldChosenSamples=this.chosenSamples.slice(0);
	this.samples=[];
	this.selectedTypesVar=[];
	this.selectortypes=[];
	thisController=this;
	this.immediate= (mode=='immediate');
	
	
	
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
	

	
	
	// get a bunch of fitting samples. Remove Samples in except.
	this.loadSamples=function(){
		var details={}
		details.sampletypes=this.selectedTypesVar;	
		var name="";
		if (thisController.userinput!=undefined){name=thisController.userinput}
			var promise=sampleService.getSamplesByName(name,details);
			promise.then(function(data){
				var tempSamples=[];
				for(var i=0; i<data.data.length; i++){
					var inExceptionList=false;
					for(var j=0; j<except.length; j++){
						if (data.data[i].sampleid==except[j].sampleid){
							inExceptionList=true;
						}
							
					}
					if (!inExceptionList){
						tempSamples.push(data.data[i]);
					}					
				}
				thisController.samples=tempSamples;
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
	
	
	
	$scope.isNotSelected=function(sample){
		var chosenSamples=thisController.chosenSamples;
		var found=false;
			angular.forEach(chosenSamples, function(csample){
				if (csample.name==sample.name){
					found=true
				}
			});
		return !found;
	}
	
	
	
	this.close=function(){
	    $modalInstance.close({chosen:this.oldChosenSamples,changed:false});
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
	
	
	this.assignSamples=function(){    // pass the new list of samples and if it changed
		var assignedSamplesChanged=!this.oldChosenSamples.equals(this.chosenSamples)
	    $modalInstance.close({chosen: this.chosenSamples, changed : assignedSamplesChanged});
	}
	
	
	
	this.getButtonLabel = function(){
		return buttonLabel;
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
		if (this.immediate){this.assignSamples()}
	}
	
	
	var thisController = this;
	$scope.$on('language changed', function(event, args) {
		thisController.translate(args.language);
	});
	
	
	
	//activate function
	this.firsttime=true;
	this.init();
};

        
angular.module('unidaplan').controller('modalSampleChoser',['avSampleTypeService','$translate','$scope','$modalInstance','restfactory','types','samples','except','buttonLabel','mode',modalSampleChoser]);

})();