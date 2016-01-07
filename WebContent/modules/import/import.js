
(function(){
'use strict';

function importController(languages,parameters,restfactory,$translate,$scope,$state,$stateParams,sampleTypes,sampleService) {
	
	var thisController=this;
	
	this.languages=languages;

	this.table = [];
	
	var dummytype={id:0, namef:function(){return "<new datatype>"}};
		
	this.sampleTypes = sampleTypes;
	
	this.sampleTypes.unshift(dummytype);
	
	this.activeSampleType = 0;
	
	this.parameters={};
	
	this.newSampleTypeName="new Sample Type";
	
//	this.regex = new RegExp('[a-zA-Z]{4}[0-9]{6,6}[a-zA-Z0-9]{3}');
	
	
	this.parameters.parameters = parameters;
	
	thisController.parameters.parameters.unshift(
			{id:0,namef:function(){return "<"+$translate.instant("don't import")+">";}}
		);
	
	
	
	this.getParameters = function(sampletype){
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
		} else {
			thisController.parameters={};
			thisController.parameters.parameters = parameters;
		}
	}
	
	
	
	this.importToDB = function(){

		  
		var parameters= thisController.columns.reduce(function(previousValue,p){
			previousValue.push(p.parameter);
			return previousValue;
		},[]);
		var args={
			parameters:parameters,
			sampletype:this.activeSampleType,
			file:thisController.file
		};
		if (this.activeSampleType==0){ // New sampletype
			var name={};
		  	name[languages[0].key]=this.newSampleTypeNameL1;
		  	name[languages[1].key]=this.newSampleTypeNameL2;
			args.name=name; 
			args.paramgrp={de:"Parameter",en:"parameters"};
		}
		var promise = restfactory.POST('import-into-db',args);
		promise.then(function(){
			$state.go("importFinished");
		},function(){
			console.log("error");
		});
	}
	
	
	this.setFiles = function(element) {
		console.log(element.files[0]);
		thisController.file=element.files[0].name;
		thisController.importStuff(element.files[0].name);
    };
	
	
	this.importStuff=function(file){
		var promise = restfactory.POST("import-csv",{file:file})
	    promise.then(function(rest) {
	    	thisController.table=rest.data;	    	
	    	thisController.columns=[];
	    	for (var i=0; i<rest.data.data[0].length;i++){
	    		thisController.columns.push({});
	    	}
	    }, function(rest) {
	    	console.log("ERROR");
	    });
	};
}
	

	
        
angular.module('unidaplan').controller('importController',['languages','parameters','restfactory','$translate','$scope','$state','$stateParams',
                                               'sampleTypes','sampleService',importController]);

})();