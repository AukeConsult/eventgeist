<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta charset = "utf-8" />
<style type="text/css">
body {
	background-image: url('https://cdn.crunchify.com/bg.png');
}
</style>

<script type="text/javascript" charset="UTF-8">

	class EventBrokerClient {	    
		constructor(address,parameters) {	        
	        this.webSocket = null;	        
	        this.address = address;
	        this.parameters = parameters;	    
	    }
	    getServerUrl() {return "ws://" + this.address + "/evsocket" + this.parameters;}
	    isopen() {
	    	return this.webSocket.readyState == WebSocket.OPEN;
	    }
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
	                //console.log('onclose::' + JSON.stringify(event, null, 4));                
	            }
	            this.webSocket.onerror = function(event) {
	                console.log('onerror::' + JSON.stringify(event, null, 4));
	            }
	        } catch (exception) {
	            console.error(exception);
	        	alert(exception);
	        }
	    }
	    
	    getStatus() {return this.webSocket.readyState;}
	    send(message) {
	        if (this.webSocket.readyState == WebSocket.OPEN) {
	            this.webSocket.send(message);
	            
	        } else {
	            console.error('webSocket is not open. readyState=' + this.webSocket.readyState);
	            alert('webSocket is not open. readyState=' + this.webSocket.readyState);
	        }
	    }
	    disconnect() {
	        if (this.webSocket.readyState == WebSocket.OPEN) {
	            this.webSocket.close();
	        } else {
	            console.error('webSocket is not open. readyState=' + this.webSocket.readyState);
	            alert('webSocket is not open. readyState=' + this.webSocket.readyState);
	        }
	    }
	}	
	
	var client;
	function init(brokeraddress, eventtype, event, user, support) {
		
		if(client!=null && client.isopen()) {
			client.disconnect();
		}
		
		client = new EventBrokerClient(brokeraddress,'?eventtype=' + eventtype.trim() + '&event=' + event.trim() + ' &user=' + user.trim() + '&support=' + support.trim());	
		client.connect();
	
	}
	
	function sendmessage(message) {
		if(client!=null) {
			client.send("M#"+message);
		}
	}
	function sendclick(clickid) {
		if(client!=null) {
			client.send("C#"+clickid);		
		}
	}
	
</script>

<script type="text/javascript">	
    
    function prosessMessage(message) {
    	
   		var pre = document.createElement("p"); 
           pre.style.wordWrap = "break-word"; 
           pre.innerHTML = '<span style = "color: blue;">' +
           message+'</span>'
           document.getElementById("output").appendChild(pre);    		
    
    }
    
    
</script>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>test event</title>
</head>

<%
String uri = request.getServerName() +       
        ":" +                           
        request.getServerPort();                
%>

<body>

<div style="font-family: verdana; padding: 10px; border-radius: 10px; font-size: 12px; text-align: center;">
	
	<h2>Test event response</h2>

	<div id="match" visible=true>
		
		<form>
			<input type="text" placeholder="Legg inn eventid" id="eventid">
			<input type="text" placeholder="Legg inn brukernick" id="userid">
			<input type="radio" name="choice" value="team1"> team 1
	        <input type="radio" name="choice" value="team2"> team 2
	        <input type="button" id="btn" value="Start event">				
		</form>
		
		<br>

	</div>
	
	<div id="play">
		
		
		<button onclick="sendclick('jubel')">click jubel</button>
		<button onclick="sendclick('buu')">click buu</button>	
		<br>
		<br>Melding: 
		<input type="text" id="message"> <button onclick="sendmessage(document.getElementById('message').value)">Send</button><br><br>	
		<br>
		<div id = "output"></div>
	</div>

		<script type="text/javascript">
		
			const btn = document.getElementById("btn");
			
			btn.onclick = function () {
	            const rbs = document.querySelectorAll('input[name="choice"]');
	            let selectedTeam;
	            for (const rb of rbs) {
	                if (rb.checked) {
	                	selectedTeam = rb.value;
	                    break;
	                }
	            }
	
				init('<%=uri%>/broker','football',
						document.getElementById("eventid").value,
						document.getElementById("userid").value,
						selectedTeam
			
				)		
				
				const output = document.getElementById("output");
  				while (output.firstChild) {
  					output.removeChild(output.lastChild);
 				}
			
			};
		
		
		</script>


</div>
</body>
</html>