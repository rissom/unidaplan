
(function(){
'use strict';

function importController(languages,parameters,restfactory,$translate,$scope,$state,
		$stateParams,$timeout,processService,processTypes,sampleTypes,sampleService) {
	
	var thisController=this;
	
	this.languages=languages;

	this.table = [];
	
	var dummytype={id:0, namef:function(){
			return "<"+$translate.instant('new datatype')+">" 
		}};
		
	this.sampleTypes = sampleTypes;
	
	this.processTypes = processTypes;
	
	this.lang1=$translate.instant(languages[0].name);
	
	this.lang2=$translate.instant(languages[1].name);
	
	this.newPGrpNameL1 = "Daten";
	
	this.newPGrpNameL2 = "data"
	
	this.sampleTypes.unshift(dummytype);
	
	this.processTypes.unshift(dummytype);
	
	this.activeSampleType = 0;
	
	this.activeProcessType = 0;
	
	this.parameters={};
	
	this.newSampleTypeName="new Sample Type";
	
	this.types=[{index:"sample",label:$translate.instant("sample")},{
		index:"process",label:$translate.instant("process")}];
	
	this.type=this.types[0].index;
	
	this.parameters.parameters = parameters;
	
	thisController.parameters.parameters.unshift(
			{id:0,namef:function(){return "<"+$translate.instant("don't import")+">";}}
		);
	
	

	this.getParameters = function(sampletype){
		if (thisController.type=="sample"){ 
			if (thisController.activeSampleType>0){
				var promise = sampleService.getSampleTypeParameters(thisController.activeSampleType);
				promise.then(
					function(rest){
						thisController.parameters=rest;
						thisController.parameters.parameters.unshift(
							{id:0,namef:function(){return $translate.instant("don't import");}}
						);
					},
					function(){
						console.log("Error");
					}
				);
			}else{
				thisController.parameters={};
				thisController.parameters.parameters = parameters;
			}
		} else { // Process
			if (thisController.activeProcessType>0){
				var promise = processService.getProcessTypeParameters(thisController.activeProcessType);
				promise.then(
					function(rest){
						thisController.parameters=rest;
						thisController.parameters.parameters.unshift(
							{id:0,namef:function(){return $translate.instant("don't import");}}
						);
					},
					function(){
						console.log("Error");
					}
				);
			}else{
				thisController.parameters={};
				thisController.parameters.parameters = parameters;
			}
		}
	}
	
	
	
	this.importToDB = function(){
		var parameters= thisController.columns.reduce(function(previousValue,p){
			previousValue.push(p.parameter);
			return previousValue;
		},[]);
		var myDate=new Date();
		var timezone=myDate.getTimezoneOffset();

		var args={
			parameters:parameters,
			file:thisController.file,
			type:thisController.type,
			timezone:timezone
		};
		if (thisController.type=="sample"){
			args.sampletype=this.activeSampleType;
			if (this.activeSampleType==0){ // New sampletype
				var name={};
			  	name[languages[0].key]=this.newTypeNameL1;
			  	name[languages[1].key]=this.newTypeNameL2;
				args.name=name; 
				var paramgrp={};
				paramgrp[languages[0].key]=this.newPGrpNameL1;
				paramgrp[languages[1].key]=this.newPGrpNameL2;
				args.paramgrp=paramgrp; 
			}
		} else{
			args.processtype=this.activeProcessType;
			if (this.activeProcessType==0){ // New processtype
				var name={};
			  	name[languages[0].key]=this.newTypeNameL1;
			  	name[languages[1].key]=this.newTypeNameL2;
				args.name=name; 
				args.paramgrp={de:"Parameter",en:"parameters"};
			}
		}
		
		var promise = restfactory.POST('import-into-db',args);
		promise.then(function(){
			$state.go("importFinished");
		},function(){
			console.log("error");
		});
	}



	this.upload = function(element) {
		thisController.file=element.files[0].name;
		var file=element.files[0].name;
		var xhr = new XMLHttpRequest();
		xhr.addEventListener('load', function(event) {
			thisController.importStuff();
		});
		
		xhr.open("POST", 'upload-file'); // xhr.open("POST", 'upload-file',true); ???
		
		// formdata
		var formData = new FormData();
		formData.append("file", element.files[0]);
		xhr.send(formData);
    };


    
	this.importStuff=function(){
		var promise = restfactory.POST("import-csv",{file:thisController.file})
	    promise.then(function(rest) {
	    	thisController.table=rest.data;	    	
	    	thisController.columns=[];
	    	for (var i=0; i<rest.data.data[0].length;i++){
	    		thisController.columns.push({});
	    	}
	    }, function(rest) {
	    	console.log("ERROR");
	    });
		thisController.stuffImported=true;
	};
}
		
        
angular.module('unidaplan').controller('importController',['languages','parameters',
    'restfactory','$translate','$scope','$state','$stateParams','$timeout',
    'processService','processTypes', 'sampleTypes','sampleService',importController]);

})();