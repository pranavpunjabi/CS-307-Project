var http = require('http');
var express = require('express');
var port = (process.env.PORT || 8000);

var app = express();

console.log("server running");
app.listen(port,'localhost', function() {

	console.log("Connection made");



});