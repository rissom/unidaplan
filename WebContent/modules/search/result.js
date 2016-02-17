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
	
	
	
	this.processArray = function(){
		
		//create empty array with length of sampledata
		var emptySampleData=[];
		for (var i=0; i<result.samples[0].sampledata.length+1;i++){
			emptySampleData.push("");
		}
		
		// create a 2 dimensional array for HTML-Table 
		var processedArray=[];
		var line=0;
		for (var i=0; i<result.samples.length; i++){
			var sample=result.samples[i];
			processedArray.push(result.samples[i].sampledata);
			processedArray[line].unshift({
				id:sample.sampleid,
				name:sample.samplename,
				type: sample.sampletype
				}
			);
			for (var j=0; j<result.samples[i].processes.length;j++){
				if (j>0) {
					processedArray.push(emptySampleData);
				}
				processedArray[line]=processedArray[line].concat(result.samples[i].processes[j].processdata); // concat processdata
				line++;
			}
		}
		return processedArray;
	}
	
	
	
	if (result.type==4){
		this.processedArray=this.processArray();
	}
	
	
	
    this.saveCSV = function(){   	
    	var csvData=[];
    	var line="";
    	if (result.type==1 || result.type==4) {
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
    	
    	if (result.type<4){
  
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
	// 			code in result.html: 
	//			<td ng-repeat="col in row.rowdata track by $index">
	//			 	<tparameter parameter="col"></tparameter>
	        	line+="\n";
	    		csvData.push(line)
	    	});
    	} else{
    		line="";
    		var lastObjectName;
    		console.log("processed Array Length: "+thisController.processedArray.length);
    		for (var i=0;i<thisController.processedArray.length;i++){
    			if (thisController.processedArray[i][0].name){
    				line=thisController.processedArray[i][0].name+";";
    				lastObjectName=thisController.processedArray[i][0].name;
    			} else{
    				line=lastObjectName;
    			}
    			for (var j=1;j<thisController.processedArray[i].length;j++){
    				if (thisController.processedArray[i][j]){
    					line+=thisController.processedArray[i][j];
    				}
    				line+=";";
    			}
    			line+="\n";
    			csvData.push(line);
    		}
    	}
    	
    	var b=new Blob(csvData,{encoding:"UTF-8",type : 'text/csv;charset=UTF-8'});
    	var a = document.createElement('a');
    	a.href = window.URL.createObjectURL(b);
    	console.log(window.URL.createObjectURL(b))
        a.download = "searchresult.csv"; 
    	// Set to whatever file name you want
        // Now just click the link you created
        // Note that you may have to append the a element to the body somewhere
        // for this to work in Firefox
    	if(navigator.userAgent.indexOf("Firefox") != -1 ) 
        {
    		  document.body.insertBefore(a,document.body.childNodes[0]);
        }
    	a.click();
    	
    };
    	
}  


angular.module('unidaplan').controller('resultController',['restfactory','result','$state','$translate','$stateParams',
                             'avSampleTypeService','avProcessTypeService','types','pTypes','key2string','languages',resultController]);

})();