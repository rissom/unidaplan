(function(){
'use strict';


function modalSampleChoser(avSampleTypeService,$translate,$scope,$uibModalInstance,types,samples,experiments,except,buttonLabel,mode,sampleService) {

	var thisController = this;
	
	if (samples) {
		this.chosenSamples = samples.slice(0);
	} else {
		this.chosenSamples = [];
	}
	
	this.oldChosenSamples = this.chosenSamples.slice(0);
	
	this.samples = [];
	
	this.selectedTypesVar = [];
	
	this.selectortypes = [];
	
	this.selectedExperimentsVar = [];
	
	this.selectorExperiments = [];
	
	this.immediate = (mode == 'immediate');
	
	
	
	$scope.$watch('mSampleChoserCtrl.selectedtypes', function (seltypes){
		var typeList = [];
		if (seltypes === undefined || seltypes.length === 0){  // Nothing selected => Everything selected
			angular.forEach(types,function(type) {
				typeList.push(type.id);			
			});
		} else {
		if (seltypes[0].id === 0){  // all types field selected => Everything selected
			angular.forEach(types,function(type) {
				typeList.push(type.id);			
			});
		} else {										// make list of selected types
			angular.forEach(thisController.selectedtypes,function(type) {
				typeList.push(type.id);	
			})
		}}
		thisController.selectedTypesVar = typeList;
		
		//check if there was change
		var oldUserinput = "";
		if (thisController.userinput !== undefined) {
			oldUserinput = thisController.userinput;
		}
		var oldTypeIDs = thisController.selectedTypesVar;
	    setTimeout(function() {
		  	var newUserinput = "";
			if (thisController.userinput !== undefined){
				newUserinput = thisController.userinput;
			}
		    if (oldUserinput == newUserinput){
    			    	if( thisController.selectedTypesVar.equals(oldTypeIDs)){
    			    		if (!thisController.firsttime){
    			    		    thisController.loadSamples();
    			    		}
    			    	}
		    }
	    }, 250);
	});	
	
	
	
	$scope.$watch( 'mSampleChoserCtrl.selectedexperiments', function (selexperiments){
        var experimentList = [];
        if (selexperiments === undefined || selexperiments.length === 0 || selexperiments[0].id === 0){  // Nothing selected => Everything selected
            experimentList = ["0"];
        } else {                                      // make list of selected types
            angular.forEach(thisController.selectedexperiments, function(experiment) {
                experimentList.push(experiment.id); 
            });
        }
        thisController.selectedExperimentsVar = experimentList;
               
        //check if there was change
        var oldUserinput = "";
        if (thisController.userinput !== undefined) {
            oldUserinput = thisController.userinput;
        }
        var oldExperimentIDs = thisController.selectedExperimentsVar;
        setTimeout(function() {
            var newUserinput = "";
            if (thisController.userinput !== undefined){
                newUserinput = thisController.userinput;
            }
            if (oldUserinput == newUserinput){
                if( thisController.selectedExperimentsVar.equals(oldExperimentIDs)){
                    if (!thisController.firsttime){
                        thisController.loadSamples();
                    }
                }
            } 
        }, 250);
    });  
	
	
	
	$scope.$watch('mSampleChoserCtrl.userinput', function (tmpStr){
		//check if there was change
		var oldUserinput = "";
		if (thisController.userinput !== undefined) {
			oldUserinput = thisController.userinput;
		}
		var oldTypeIDs = thisController.selectedTypesVar;
		  setTimeout(function() {
			  	var newUserinput = "";
				if (thisController.userinput !== undefined){
					newUserinput = thisController.userinput;
				}
			    if (oldUserinput == newUserinput){
			    	if( thisController.selectedTypesVar.equals(oldTypeIDs)){
				    	thisController.loadSamples();
			    	}
			    }			    
		  }, 250);
	});	
	

  
    this.assignSamples = function(){    // pass the new list of samples and check if it changed
        var assignedSamplesChanged = !this.oldChosenSamples.equals(this.chosenSamples)
        $uibModalInstance.close({chosen: this.chosenSamples, changed : assignedSamplesChanged});
    }
	
    
    
    // return the translated name string of a type for a sample
    this.getType = function(sample){        
        return avSampleTypeService.getType(sample,types);
    }
    
	
	
	// get a bunch of fitting samples. Remove Samples in except.
	this.loadSamples = function(){	
		var name = thisController.userinput || "";
		var promise = sampleService.getSamplesByName(name, this.selectedTypesVar, 
		                thisController.selectedExperimentsVar, 'r');
		// possible values for privilege (3rd parameter): "r"-> read or write; "w"-> write; "a" -> all
		
		promise.then(
			function(data){
				var tempSamples = [];
				for(var i=0; i<data.data.length; i++){
					var inExceptionList = false;
					for(var j=0; j<except.length; j++){
						if (data.data[i].sampleid == except[j].sampleid){
							inExceptionList = true;
						}
							
					}
					if (!inExceptionList){
						tempSamples.push(data.data[i]);
					}					
				}
				thisController.samples = tempSamples;
				if (thisController.firsttime) {
					thisController.init();
					thisController.firsttime = false;
				}
			}
		);	
	}
	
	
	
	// Check if the sampletype is selected
	this.typeSelected = function(sample){
		var found = false;
		angular.forEach(thisController.selectedTypesVar,function(type) {
			if (sample.typeid == type){
				found = true;
			}
		});
		return found;
	}
	
	
	
	$scope.isNotSelected = function(sample){
		var chosenSamples = thisController.chosenSamples;
		var found = false;
			angular.forEach(chosenSamples, function(csample){
				if (csample.name == sample.name){
					found = true;
				}
			});
		return !found;
	}
	
	
	
	this.close = function(){
	    $uibModalInstance.close({chosen:this.oldChosenSamples, changed:false});
	}
	
	
	
	this.init = function(){
        var selectorTypesTemp = [];
        
        var allTypesString = $translate.instant("all types");
		selectorTypesTemp.push({namef:function(){return allTypesString},'id':0});
		angular.forEach(types,function(type) {
			selectorTypesTemp.push(type);
		});
        this.selectortypes = selectorTypesTemp;
		
		var selectorExperimentsTemp = [];
        selectorExperimentsTemp.push({namef:function(){ return $translate.instant("all experiments");},'id':0})
        angular.forEach(experiments,function(experiment) {
            selectorExperimentsTemp.push(experiment);
        });
        this.selectorExperiments = selectorExperimentsTemp;
		
	}
	
	
	
	this.getButtonLabel = function(){
		return $translate.instant(buttonLabel);
	}
	
	
	
	this.removeSample = function(sample){
		for (var i=0; i<this.chosenSamples.length; i++){
			if (this.chosenSamples[i].name==sample.name){
				this.chosenSamples.splice(i,1);
			}
		}
	}

	
	
	this.choseSample = function(sample){
		var i;
		var found = false;
		for (i=0; i<this.chosenSamples.length; i++){
			if (this.chosenSamples[i].name==sample.name){
				found = true			
			}
		}
		if (!found) {
			this.chosenSamples.push(sample);
		}
		if (this.immediate){this.assignSamples()}
	}
	

	//activate function
	this.firsttime = true;
	this.init();
};

        
angular.module('unidaplan').controller('modalSampleChoser',['avSampleTypeService','$translate','$scope','$uibModalInstance',
                            'types','samples','experiments','except','buttonLabel','mode','sampleService',modalSampleChoser]);

})();