var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);

const port = 3131;
let connectedClients = 0;
const maxClients = 2;
let currentTurn = 1;

server.listen(port, function() {
    console.log("Server running on port " + port);
});

io.on('connection', function(socket) {

    if(connectedClients + 1 > maxClients) {
        console.log("Max client number reached - disconnecting");
        socket.emit('info-max-client-number-reached', { info: "This client will be disconnected as the max number of clients (" + maxClients + ") " + "was reached." })
        socket.disconnect();
        return;
    } 

    connectedClients++;

    console.log('Client connected. (' + connectedClients + ')');
    socket.emit('playerID', { id: connectedClients });

    if(connectedClients == maxClients) {
        socket.broadcast.emit("room-full");
        socket.emit('room-full');
    }

    
    socket.on('turn-changed', (arg) => {
        if(connectedClients == maxClients) {
            console.log('Turn changed, new turn: ' + arg);
            currentTurn = arg;
        } else {
            console.log('Tried to change turn but the game is NOT started yet. No changes made.')
        }
    });

    socket.on('get-turn', function() {
        socket.emit('receive-turn', { turn: currentTurn });
    });

    socket.on('movement', (arg) => {
        socket.broadcast.emit('update-board', arg);
    });

    socket.on('disconnect', function() {
        connectedClients--;
        socket.broadcast.emit('player-left');
        resetGame();
        console.log('Client disconnected. Number of clients left: ' + connectedClients);
    })
});

function resetGame() {
    currentTurn = 1;
}