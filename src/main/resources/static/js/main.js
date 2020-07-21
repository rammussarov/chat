'use strict';

const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const userList = document.getElementById('users');
const chatMessages = document.querySelector('.chat-messages');

const CHAT_INFO = 'CHAT INFO';

var stompClient = null;
var username = null;

function connect() {
    username = document.querySelector('#username').innerText.trim();

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnected, onError);
}

// Connect to WebSocket Server.
connect();

function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/publicChat', onMessageReceived);
    stompClient.subscribe('/topic/userList', onUserListReceived);

    // Tell your username to the server
    stompClient.send("/app/chat/auth",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    )
}

function onError(error) {
    const sender = CHAT_INFO;
    const content = 'Could not connect to WebSocket server. Please refresh this page to try again!';

    const div = document.createElement('div');
    div.classList.add('message');
    div.innerHTML = `<p style="font-style: italic" class="meta">${sender}</span></p><p style="font-weight: bold; color: red" class="text">${content}</p>`;

    chatMessages.appendChild(div);
}

function sendMessage(event) {
    const messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        const chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        stompClient.send("/app/chat/sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);

    if (message.type === 'JOIN') {
        message.content = message.sender + ' joined chat!';
        message.sender = CHAT_INFO;
        message.type = 'info';
    } else if (message.type === 'LEAVE') {
        message.content = message.sender + ' left chat!';
        message.sender = CHAT_INFO;
        message.type = 'info';
    }

    const div = document.createElement('div');
    div.classList.add('message');

    if (message.type === 'info') {
        div.innerHTML = `<p style="font-style: italic" class="meta">${message.sender}</span></p><p style="font-weight: bold" class="text">${message.content}</p>`;
    } else {
        div.innerHTML = `<p class="meta">${message.sender}</span></p><p class="text">${message.content}</p>`;
    }

    chatMessages.appendChild(div);
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

function onUserListReceived(payload) {
    const users = JSON.parse(payload.body);
    userList.innerHTML = `${users.map(user => `<li>${user}</li>`).join('')}`;
}

messageForm.addEventListener('submit', sendMessage, true);