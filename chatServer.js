var mysql      = require('mysql');
var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'ankush',
  password : 'password',
  database : 'getguru'
});

connection.connect(function(err) {
  // connected! (unless `err` is set)
});

/*var post  = {lastname: 'Hello MySQL'};
var query = connection.query('INSERT INTO students SET ?', post, function(err, result) {
  // Neat!
});
connection.query(mysql);*/

/*connection.query(sql, function(err, res) {
 
  // Create row, using the insert id of the first query
  // as the exhibit_id foreign key.
  sql = 'INSERT INTO students (exhibit_id) '+
    'VALUES('+res.insertId+')';
 
  client.query(sql);
 
});*/

var post = {sender:'1', reciever:'2',message:'ggand marao yaar'};
connection.query( 'INSERT INTO chat SET ?', post, function(err, result) {
            //console.log("SELECT * FROM mytable ");

            //for(var i=0; i<rows.length; i++){
              //  console.log(rows[i]);
            //}
        });

var express = require('express');
var app = express();
//var port = (process.env.PORT || 8000);
var server = require('http').createServer(app).listen(5000);
var io = require('socket.io').listen(server);

console.log("server running");
/*app.listen(port,'localhost', function() {

	console.log("Connection made");
});*/

var usernames = {};
var numUsers = 0;
//var rooms = {room1,room2,room3,room4,room5,room6,room7};

io.sockets.on('connection', function(client){

    console.log("client connected: " + client.id);
    //console.log("printing: ");
    client.on('new message', function(data){
	console.log(data);
	//client.broadcast.emit('new message', {
	//if (err) throw err;
	//io.sockets.client(client.id).emit('new message', {'key':'for your legs only'});
	//io.sockets.emit('new message', {'key':'for your eyes only'});
	//client(client.id).emit('new message', {'key':'for your mouth only'});
	client.emit('new message', {'key':data});


	//client[client.id].emit('new message', {'key':'for your eyes only'});
	//username: client.username,
      	//message: data
	//'key':data
	//});
	
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
