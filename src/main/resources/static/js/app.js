/*************************************************************************** 
   Copyright 2017 Federico Ricca
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 ***************************************************************************/

var stompClient = null;

function setConnected(connected) {
	$("#connect").prop("disabled", connected);
	$("#disconnect").prop("disabled", !connected);
	if (connected) {
		$("#conversation").show();
	} else {
		$("#conversation").hide();
	}
	$("#greetings").html("");
}

function connect() {
	var socket = new SockJS('/feed-config');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		setConnected(true);
		console.log('Connected: ' + frame);
		stompClient.subscribe('/topic/greetings', function(greeting) {
			showGreeting(JSON.parse(greeting.body).content);
		});
		stompClient.subscribe('/topic/ticker', function(tickerInfo) {
			showTicker(JSON.parse(tickerInfo.body).priceData);
		});
		fetchTicker();
	});

}

function disconnect() {
	if (stompClient !== null) {
		stompClient.disconnect();
	}
	setConnected(false);
	console.log("Disconnected");
}

function sendName() {
	stompClient.send("/feed/hello", {}, JSON.stringify({
		'name' : $("#name").val()
	}));
}

function fetchTicker() {
	let shouldCancel = false;

	var msg = {
		'exchangeId' : 'kraken',
		'base' : 'BTC',
		'counter' : 'EUR'
	}
	stompClient.send("/feed/ticker", {}, JSON.stringify(msg));
	// send messages, do stuff,
	// set shouldCancel to true to stop looping if needed

	if (!shouldCancel) {
		setTimeout(fetchTicker, 1000);
	}
}

function showGreeting(message) {
	$("#greetings").append("<tr><td>" + message + "</td></tr>");
}

function showTicker(ticker) {
	let maxValue = ticker.high;
	let minValue = ticker.low;

	yAxis.domain([ minValue, maxValue ]);
	svg.selectAll(".axis--y").call(d3.axisLeft(yAxis));

	// Push a new data point onto the back.
	askPriceData.push(ticker.askPrice);
	bidPriceData.push(ticker.bidPrice);
	askPriceData.shift();
	bidPriceData.shift();
	// svg.select(".path").attr("d", line).attr("transform", null);
}

$(function() {
	$("form").on('submit', function(e) {
		e.preventDefault();
	});
	$("#connect").click(function() {
		connect();
	});
	$("#disconnect").click(function() {
		disconnect();
	});
	$("#send").click(function() {
		sendName();
	});

	connect();

	stompClient.debug = null
});