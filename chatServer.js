var express = require('express');
var app = express();
var server = require('http').createServer(app).listen(8000);
var io = require('socket.io').listen(server);

var mysql = require('mysql');
var connection = mysql.createConnection({
	host:'localhost',
	user:'sharoons',
	password:'password',
	database:'getguru'
});

connection.connect(function(err) {
});

console.log("server running");

var clients = [];
var clients1 = [
	{"id":0, "client":0}
]; 

io.sockets.on('connection', function(client){
    var senderId;
    var receiverId;
    client.on('sessionMembers', function(data) {
    
    senderId = data;
    //console.log("sender: "+senderId);
    var text = {"id":senderId, "client":client.id}
    console.log(text);
    clients1.push(text);
});

    client.on('sessionMembers1', function(data) {
    
    receiverId = data;
    /*var toClient = 0;
    for(var c in clients1)
    {
	if(c.id == receiverId)
        	toClient = c.client;
    }
    if(toClient == 0)
    {
	var text = {"id":receiverId, "client":toClient}
	//clients1.push(text);
    }*/
	    
    console.log("Receiver is : "+ data);
});

    //clients.push(client.id);
    //var text = {"id":"1", "client":client.id};
    //clients1.push(text);
    //console.log("id = "+clients1[0].id+" client = "+clients1[0].client);
    //console.log("id = "+clients1[1].id+" client = "+clients1[1].client);
    //console.log("client connected: " + client.id);
    client.on('new message', function(data){
	console.log(data);
	//client.broadcast.emit('new message', {
	console.log("sender client iD : " + client.id);
	/*if(client.id == clients[0])
	{
		io.sockets.connected[clients[1]].emit('new message', {'key':data});
	}
	else
		io.sockets.connected[clients[0]].emit('new message', {'key':data});
	*/
	var toClient = 0;
	var fromClient = 0;
	for(i = 0 ; i < clients1.length ; i++)
	{
		if(clients1[i].id == receiverId)
			toClient = clients1[i].client;
		if(clients1[i].id == senderId)
			fromClient = clients1[i].client;
	}
	if(toClient == 0)
	{
		var post = {sender:senderId, reciever:receiverId, message:data};
		connection.query( 'INSERT INTO chat SET ?', post, function(err, result){
		});
	}//history
	else
	{
		console.log("GONNA SEND coz receiver is online\n");
		io.sockets.connected[toClient].emit('new message', {'key':data});
		var post = {sender:senderId, reciever:receiverId, message:data};
                connection.query( 'INSERT INTO chat SET ?', post, function(err, result){
                });
	}
	//console.log("SENT TO FIRST\n");
	//client.emit('new message', {'key':data});
	//'key':data
	//});
	
    });	
	
    client.on('disconnect', function(dat){

	//console.log(dat);
	var current = client.id;
	console.log("trying to delete "+current);
        console.log(clients1[1].client);
	/*for(var c in clients1)
        {
		console.log(c);
		console.log(c.client);
                console.log(c.id);
		if(c.client == current)
        	{
			console.log("FOUND TO DELETE");
			delete c;
		}
	}*/
	for(i = 0 ; i < 2; i++)
	{
		console.log(clients1[i]);
		if(clients1[i].client == current)
		{
			console.log("MATCH")
			delete clients1[i];
		}
		//console.log(c);
                //console.log(c.client);
                //console.log(c.id);
                //if(c.client == current)
                //{
                  //      console.log("FOUND TO DELETE");
                    //    delete c;
                //}
	}
	console.log("deleted");
	console.log(clients1);

    });
    /*client.on('my message', function(client){
	console.log(data);
	client.join(rooms[0]);
	
	});*/
    console.log("BROADCASTED");
    client.on('add user', function (username) {
    // we store the username in the socket session for this client
    client.username = username;
    // add the client's username to the global list
    usernames[username] = username;
    ++numUsers;
    addedUser = true;
    client.emit('login', {
      numUsers: numUsers
    });
    // echo globally (all clients) that a person has connected
    client.broadcast.emit('user joined', {
      username: socket.username,
      numUsers: numUsers
    });
  });

    client.on("sendTo", function(data){
        console.log("Message From: " + chatMessage.fromName);
        console.log("Message To: " + chatMessage.toName);


        io.sockets.socket(chatMessage.toClientID).emit("chatMessage", {"fromName" : chatMessage.fromName,
                                                                    "toName" : chatMessage.toName,
                                                                    "toClientID" : chatMessage.toClientID,
                                                                    "msg" : chatMessage.msg});

    });
});
