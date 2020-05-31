<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<script type="text/javascript">

	class EventBrokerClient {	    
		constructor(address,parameters) {	        
	        this.webSocket = null;	        
	        this.address = address;
	        this.parameters = parameters;	    
	    }
	    getServerUrl() {return "ws://" + this.address + "/eventbroker" + this.parameters;}
	    connect() {
	    	try {
	    		this.webSocket = new WebSocket(this.getServerUrl());
	            this.webSocket.onopen = function(event) {
	                console.log('onopen::' + JSON.stringify(event, null, 4));
	            }
	            this.webSocket.onmessage = function(event) {
	                console.log('onmessage::' + JSON.stringify(event.data, null, 4));
	                prosessMessage(event.data);	            
	            }
	            this.webSocket.onclose = function(event) {
	                console.log('onclose::' + JSON.stringify(event, null, 4));                
	            }
	            this.webSocket.onerror = function(event) {
	                console.log('onerror::' + JSON.stringify(event, null, 4));
	            }
	        } catch (exception) {
	            console.error(exception);
	        }
	    }
	    getStatus() {return this.webSocket.readyState;}
	    send(message) {
	        if (this.webSocket.readyState == WebSocket.OPEN) {
	            this.webSocket.send(message);
	            
	        } else {
	            console.error('webSocket is not open. readyState=' + this.webSocket.readyState);
	        }
	    }
	    disconnect() {
	        if (this.webSocket.readyState == WebSocket.OPEN) {
	            this.webSocket.close();
	        } else {
	            console.error('webSocket is not open. readyState=' + this.webSocket.readyState);
	        }
	    }
	}	
	
	var client;
	function init(brokeraddress, eventtype, event, user, support) {
		client = new EventBrokerClient(brokeraddress,'?eventtype=' + eventtype + '&event=' + event + ' &user=' + user + '&support=' + support);	
		client.connect();
	}
	
	function sendmessage(message) {client.send("M##"+message);}
	function sendclick(clickid) {client.send("C##"+clickid);}
	
</script>

<script type="text/javascript">	
    function prosessMessage(message) {
    	
    	if(message.substring(0,3)==="T##") {
            
    		var pre = document.createElement("p"); 
            pre.style.wordWrap = "break-word"; 
            pre.innerHTML = '<span style = "color: blue;">R-ALL: ' +
            message+'</span>'
            document.getElementById("output").appendChild(pre);
    	
    	} else if(message.substring(0,3)==="S##") {
    	
    		var pre = document.createElement("p"); 
            pre.style.wordWrap = "break-word"; 
            pre.innerHTML = '<span style = "color: blue;">R-SESSION: ' +
            message+'</span>'
            document.getElementById("output").appendChild(pre);
    		
    	}
    		
    
    }
</script>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>hello</title>
</head>
<body onload="init('127.0.0.1:8080/broker','football','test1','leif','team1')">
	
	<p>Hello everybody</p>	
	<button onclick="sendmessage('hello old chap chap')">click hello</button>
	<button onclick="sendclick('1')">click 1</button>
	<button onclick="sendclick('2')">click 2</button>	
	<div id = "output"></div>
	
</body>
</html>