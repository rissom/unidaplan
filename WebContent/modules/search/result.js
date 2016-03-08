(function(){
'use strict';

function resultController(restfactory,result,$state,$translate,$stateParams,avSampleTypeService,avProcessTypeService,types,pTypes,
		key2string,languages){
		
	var thisController = this;
	
	this.currentSortColumn = -1;
	
	this.currentSortDirection = 1;
	
	this.result = result;
		
	
	this.getType = function(type){
		return avSampleTypeService.getType({typeid:type},types);
	};

	
  
	this.getProcessType = function(type){
		return avProcessTypeService.getProcessType ({processtype:type},pTypes);
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
			for (var j=0; j<result.samples[i].processes.length;j++){
				processedArray.push([{
						id:sample.sampleid,
						name:sample.samplename,
						type: sample.sampletype,
					}])
				processedArray[line]=processedArray[line].concat(result.samples[i].sampledata);
				processedArray[line]=processedArray[line].concat(result.samples[i].processes[j].processdata);
				line++;
			}
		}		
		return processedArray;
	}
	
	
	
	if (result.type==4){
		this.sampleDataLength=result.samples[0].sampledata.length;
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
    
    this.sortBy = function(index){
    	if (thisController.currentSortColumn==index) {thisController.currentSortDirection*=-1}
    	thisController.currentSortColumn=index;
    	if (result.type<4){
    		thisController.result.data.sort(function(a,b){return thisController.currentSortDirection*(a.rowdata[index]>b.rowdata[index]?1:-1)});
    	}else{
    		thisController.processedArray.sort(function(a,b){
    			return thisController.currentSortDirection*((a[index]<b[index])?-1:1)});
    	}
    	
    }
    	
}  


angular.module('unidaplan').controller('resultController',['restfactory','result','$state','$translate','$stateParams',
                             'avSampleTypeService','avProcessTypeService','types','pTypes','key2string','languages',resultController]);

})();