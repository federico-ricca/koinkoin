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
		stompClient.subscribe('/topic/ticker', function(greeting) {
			showTicker(JSON.parse(greeting.body).content);
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
	let
	shouldCancel = false;

	var msg = {
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

function showTicker(message) {
	$("#greetings").append("<tr><td>" + message + "</td></tr>");
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
});