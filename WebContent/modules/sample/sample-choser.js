(function(){
'use strict';


function sampleChoser($translate,$scope,restfactory,types,experiments,sampleService) {

	this.samples = [];
	this.selectedTypesVar = [];
	this.selectorTypes = [];
	this.selectedExperimentsVar = [];
	this.selectorExperiments = [];
	var thisController = this;
		
	
	$scope.$watch('sampleChoserCtrl.selectedtypes', function (seltypes){
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
		}else{										// make list of selected types
			angular.forEach(thisController.selectedtypes,function(type) {
				typeList.push(type.id);	
			});
		}}
		thisController.selectedTypesVar = typeList;
		
		//check if there was change
		var oldUserinput = "";
		if (thisController.userinput !== undefined) {
			oldUserinput = thisController.userinput;
		}
		var oldTypeIDs = thisController.selectedTypesVar;
		    setTimeout(
		        function() {
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
	    }
	);	
	
	
	$scope.$watch('sampleChoserCtrl.selectedexperiments', function (seltypes){
        var experimentList = [];
        if (seltypes === undefined || seltypes.length === 0){  // Nothing selected => Everything selected
            angular.forEach(experiments,function(experiment) {
                experimentList.push(experiment.id);         
            });
        } else {
        if (seltypes[0].id === 0){  // all types field selected => Everything selected
            angular.forEach(experiments,function(experiment) {
                experimentList.push(experiment.id);
            });
        }else{                                      // make list of selected types
            angular.forEach(thisController.selectedexperiments,function(experiment) {
                experimentList.push(experiment.id); 
            });
        }}
        thisController.selectedExperimentsVar = experimentList;
               
        //check if there was change
        var oldUserinput = "";
        if (thisController.userinput !== undefined) {
            oldUserinput = thisController.userinput;
        }
        var oldExperimentIDs = thisController.selectedExperimentsVar;
            setTimeout(
                function() {
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
        }
    );  
	
	
	$scope.$watch('sampleChoserCtrl.userinput', function (tmpStr){
		//check if there was change
		var oldUserinput = "";
		if (thisController.userinput !== undefined) {
			oldUserinput = thisController.userinput;
		}
		var oldTypeIDs = thisController.selectedTypesVar;
		    setTimeout(
		        function() {
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
	    }
	);	
	

	
	// get a bunch of fitting samples
	this.loadSamples = function(){
		var name = thisController.userinput || "";
		var promise = sampleService.getSamplesByName(
						name,this.selectedTypesVar,this.selectedExperimentsVar,'r'); 
		// possible values for privilege: "r"-> read or write; "w"-> write; 
		promise.then(function(data){
			thisController.samples = data.data;
			if (thisController.firsttime) {
				thisController.init();
				thisController.firsttime = false;
			}
		});
	};
	
	
	
	// return the translated name string of a type for a sample
	this.getType = function(sample){
		var typeName;
		angular.forEach(types,function(type) {
			if (sample.typeid == type.id){
				typeName=type.namef();
			}
		});
		return typeName;
	};
	
	
	// Check if the sampletype is selected
	this.typeSelected = function(sample){
		var found = false;
		angular.forEach(thisController.selectedTypesVar,function(type) {
			if (sample.typeid == type){
				found = true;
			}
		});
		return found;
	};
	

	// build two new arrays of selectable processtypes and experiments, with an "all" option
	this.init = function(lang){
		var selectorTypesTemp = [];
		selectorTypesTemp.push({namef:function(){return $translate.instant("all types");},'id':0});
		angular.forEach(types,function(type) {
			selectorTypesTemp.push(type);
		});
		this.selectorTypes = selectorTypesTemp;
//		 funzt nicht:
//		this.selectedtypes={trname:allTypesString,'id':0}
		
        var selectorExperimentsTemp = [];
        selectorExperimentsTemp.push({namef:function(){return $translate.instant("all experiments");},'id':0})
        angular.forEach(experiments,function(experiment) {
            selectorExperimentsTemp.push(experiment);
        });
        this.selectorExperiments = selectorExperimentsTemp;
	};

	
	
	//activate function
	this.init();
	this.firsttime = true;
}

        
angular.module('unidaplan').controller('sampleChoser',['$translate','$scope','restfactory','types','experiments','sampleService',sampleChoser]);

})();