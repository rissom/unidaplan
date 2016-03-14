(function(){
'use strict';

function resultController(restfactory,result,$state,$translate,$stateParams,avSampleTypeService,avProcessTypeService,types,pTypes,
		key2string,languages){
		
	var thisController = this;
	
	this.result = result;
	

	
	
	this.getType = function(type){
		return avSampleTypeService.getType({typeid:type},types);
	};

  
	this.getProcessType = function(type){
		return avProcessTypeService.getProcessType ({typeid:type},pTypes);
	};
	
	
    this.saveCSV = function(){   	
    	var csvData=[];
    	var line="";
    	if (result.type==1) {
			line+="Objekt;";
		}
		if (result.type==2) {
			line+="Eigenschaft;";
		}
    	var sep="";
    	angular.forEach (result.headings, function(col){
    		line+=sep;
    		line+=col.namef();
    		if (col.unitf){ 
    			line+="("+col.unitf()+")";
    		}
    		sep=";";
    	});
    	line+="\n";
    	csvData.push(line);
  
    	angular.forEach (result.data,function(row){
    		line="";
    		if (result.type==1) {
    			line+=thisController.getType(row.type);
    		}
    		if (result.type==2) {
    			line+=thisController.getProcessType(row.type);
    		}
			line+=row.name;
			angular.forEach(row.rowdata, function(col){
				line+=";"
				if (col!=null){
					line+=col;
				}
			});
//			<td ng-repeat="col in row.rowdata track by $index">
//			 	<tparameter parameter="col"></tparameter>
        	line+="\n";
    		csvData.push(line)
    	});
    	
    	var b=new Blob(csvData,{encoding:"UTF-8",type : 'text/csv;charset=UTF-8'});
    	var a = document.createElement('a');
    	a.href = window.URL.createObjectURL(b);
        a.download = "searchresult.csv";  // Set to whatever file name you want
        // Now just click the link you created
        // Note that you may have to append the a element to the body somewhere
        // for this to work in Firefox
    	a.click();    	
    };
    	
    	
//    	var params = angular.copy($stateParams);
//    	params.output="csv";   	
//		var xhr = new XMLHttpRequest();
//		xhr.addEventListener('load', function(event) {
//			thisController.importStuff();
//		});
		
//		xhr.open("POST", 'result'); // xhr.open("POST", 'upload-file',true); ???
		
		// formdata
//		var formData = new FormData();
//		console.log (formData);
//		formData.append('searchid',$stateParams.searchParams.searchid);
//		formData.append('output','csv');
//		formData.append('parameters',$stateParams.searchParams.parameters);
//		xhr.send(formData);
	
	
//    var reload=function() {
//    	var current = $state.current;
//    	var params = angular.copy($stateParams);
//    	return $state.transitionTo(current, params, { reload: true, inherit: true, notify: true });
//    };
}  


angular.module('unidaplan').controller('resultController',['restfactory','result','$state','$translate','$stateParams',
                             'avSampleTypeService','avProcessTypeService','types','pTypes','key2string','languages',resultController]);

})();