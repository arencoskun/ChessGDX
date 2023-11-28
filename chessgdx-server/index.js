const { exit } = require('process');

var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);

let connectedClients = 0;
const maxClients = 2;
let currentTurn = 1;

var argv = require('minimist')(process.argv.slice(2));

if(argv.port == undefined || argv.port == "") {
    console.log("The port must be specified using the port argument. (--port=1234)");
    exit(1);
}
const port = argv.port;

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
        console.log('Room is now full, the game can start.');
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

        socket.broadcast.emit('turn-changed', arg);
    });

    socket.on('get-turn', function() {
        //console.log('Got turn, updating all clients...');
        socket.emit('receive-turn', { turn: currentTurn });
    });

    socket.on('movement', (arg) => {
        console.log('Got movement, telling clients to update their boards...');
        socket.broadcast.emit('update-board', {originalX: arg.originalX, originalY: 7 - arg.originalY, targetX: arg.targetX, targetY: 7 - arg.targetY});
        
    });

    socket.on('tile-set-enpassantable', (arg) => {
        //console.log('Got tile set enpassantable, telling clients to update...');
        socket.broadcast.emit('tile-receive-enpassantable', { x: arg.x, y: arg.y, enpassantable: arg.enpassantable });
    });

    socket.on('pawn-move-count', (arg) => {
        console.log('Got change in pawn move count, telling clients to update...');
        socket.broadcast.emit('pawn-change-move-count', { x: arg.x, y: 7 - arg.y, moveCount: arg.moveCount });
    });

    socket.on('piece-captured', (arg) => {
        console.log('Got captured piece, telling clients to update...');
        socket.broadcast.emit('update-board-captured', { x: arg.x, y: 7 - arg.y, white: arg.white });
    });

    socket.on('disconnect', function() {
        connectedClients--;
        socket.broadcast.emit('player-left');
        resetGame();
        console.log('Client disconnected. Number of clients left: ' + connectedClients);
    });
});

function resetGame() {
    currentTurn = 1;
}
