(function(){
'use strict';


function sampleChoser(sampleService,$translate,$scope,restfactory) {

	this.samples=[];
	this.types=[];
	this.strings=[];
	this.selectortypes=[];
	
	
	this.loadTypes=function(){
		var thisController=this;
		var promise=restfactory.GET('/sampletypes.json');
		promise.then(function(data){
			thisController.types=data.data.samplestypes;
			thisController.types=data.data.sampletypes;
			thisController.strings=data.data.strings;
		});
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
		var thisController=this;
		var details={}
		details.sampletypes=this.getSelectedSampleTypeIDs();
		var promise=restfactory.POST('/samples_by_name.json?type=1&name=1',details);
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
		if (this.selectedtypes==undefined||this.selectedtypes[0].id==0){
			return true
		}
		var thisController=this;
		var found=false;
		angular.forEach(thisController.selectedtypes,function(type) {
			if (sample.typeid==type.id){
				found=true;
			}
		});
		return found;
	}
	
	
	
	this.translate=function(lang){
	var thisController=this;
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
	this.loadSamples();
};

        
angular.module('unidaplan').controller('sampleChoser',['sampleService','$translate','$scope','restfactory',sampleChoser]);

})();