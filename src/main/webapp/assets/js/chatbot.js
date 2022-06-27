"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
const inputField = document.getElementById("userMessageInput");
const chatbotAreaDiv = document.getElementById('chatbotArea');
const messageAreaDiv = document.getElementById('message-area');
const messageLoadingDiv = document.getElementById('messageLoading');
const NOANSWERFOUND = "Ich habe dich leider nicht verstanden. Bitte frag mich erneut mit einer anderen Fragestellung oder wende dich an deinen Junior Talent Manager.";
const suggestionTable = document.getElementById('suggestions');
let wasFeedbackMessageSent = false;
let inputMessageFromUser = "";
var senders;
(function (senders) {
    senders["NONE"] = "none";
    senders["BOT"] = "bot";
    senders["HUMAN"] = "human";
})(senders || (senders = {}));
let lastSender = senders.NONE;
function setLastSender(value) {
    lastSender = value;
}
inputField.addEventListener("keydown", function (e) {
    return __awaiter(this, void 0, void 0, function* () {
        if ((suggestionTable.children.length <= 0 || selectedSuggestion == -1) && e.key === 'Enter') {
            yield sendMessage();
        }
    });
});
function sendMessage(input = null, serverResponse = true, htmlEscaped = true, customMessageId = '') {
    return __awaiter(this, void 0, void 0, function* () {
        suggestionTable.innerHTML = '';
        let message = (input === null) ? inputField.value : input;
        inputMessageFromUser = message;
        let messageToDisplayInFrontend = (htmlEscaped) ? escapeHtml(message) : message;
        if (!isEmptyOrWhiteSpace(message)) {
            let count = "firstMessage";
            switch (lastSender) {
                case senders.HUMAN:
                    count = "notFirstMessage";
                    break;
                default:
                    setLastSender(senders.HUMAN);
                    break;
            }
            messageAreaDiv.innerHTML +=
                '<div class="row" id="' + customMessageId + '">' +
                    '<div class="col">' +
                    '<!-- NOTE: This is an empty div -->' +
                    '</div>' +
                    '<div class="col">' +
                    '<div class="messageContainer outgoing ' + count + '">' +
                    '<span class="textMessage">' + messageToDisplayInFrontend + '</span>' +
                    '<span class="timeSent hidden">' + getFormattedTime() + '</span>' +
                    '</div>' +
                    '</div>' +
                    '</div>';
            if (serverResponse) {
                inputField.disabled = true;
                inputField.placeholder = "Waiting for response...";
                toggleMessageLoading();
                scrollDown();
                yield getAnswerFromServer(message);
            }
            scrollDown();
        }
        inputField.value = '';
        triggerInputEvent(inputField);
        generateChatHistory();
    });
}
let welcomeMessage = "Hallo, wie kann ich dir behilflich sein?";
let question;
let textoption = "Hier sind ein paar Suchvorschläge. Nicht das, was du suchst? Dann kannst du deine Frage im Nachrichtenfeld stellen.\n";
function getQuestionList() {
    return __awaiter(this, void 0, void 0, function* () {
        let response = yield fetch(`${server}/services/get/questionSuggestions?amountQuestions=3`);
        let json = yield response.json();
        question = json.questions;
        let optionMessage = textoption +
            "<input type='button' class='questionbutton' value='" + question[0] + "' onclick='sendSuggestedMessage(question[0])'/>\n" +
            "<input type='button' class='questionbutton' value='" + question[1] + "' onclick='sendSuggestedMessage(question[1])'/>\n" +
            "<input type='button' class='questionbutton' value='" + question[2] + "' onclick='sendSuggestedMessage(question[2])'/>";
        yield receiveMessage({
            answer: { answer: welcomeMessage, id: -1 },
            matches: [],
            foundTags: [],
            files: []
        }, false, false);
        yield sendMessage(optionMessage, false, false, 'questionSuggestions');
    });
}
function getAnswerFromServer(input) {
    return __awaiter(this, void 0, void 0, function* () {
        try {
            const inputEncoded = encodeURIComponent(input);
            let response = yield fetch(`${server}/services/find2/answer2?q=${inputEncoded}`);
            let json = yield response.json();
            if (json.answer.answer === '404') {
                json.answer.answer = NOANSWERFOUND;
                yield receiveMessage(json, false);
                return;
            }
            else {
                yield receiveMessage(json);
            }
        }
        catch (e) {
            yield receiveMessage({
                answer: {
                    answer: "Bitte überprüfe deine Internetverbindung und versuche es später erneut",
                    id: -1
                },
                matches: [],
                foundTags: [],
                files: []
            }, false);
        }
    });
}
function getFormattedTime() {
    let date = new Date();
    let twoCharValues = [];
    twoCharValues.push((date.getMonth() + 1).toString());
    twoCharValues.push(date.getDate().toString());
    twoCharValues.push(date.getHours().toString());
    twoCharValues.push(date.getMinutes().toString());
    twoCharValues.push(date.getSeconds().toString());
    for (let i = 0; i < twoCharValues.length; i++) {
        if (twoCharValues[i].length < 2)
            twoCharValues[i] = '0' + twoCharValues[i];
    }
    let c = 0;
    return date.getFullYear() + "." + twoCharValues[c++] + "." + twoCharValues[c++] + " " + twoCharValues[c++] + ":" + twoCharValues[c++] + ":" + twoCharValues[c++];
}
let boxCounter = 0;
function receiveMessage(response, feedBack = true, toggleMessageLoader = true, enableInputField = true) {
    return __awaiter(this, void 0, void 0, function* () {
        let count = "firstMessage";
        switch (lastSender) {
            case senders.BOT:
                count = "notFirstMessage";
                break;
            default:
                setLastSender(senders.BOT);
                break;
        }
        let firstMessageDiv = "";
        if (count == "firstMessage") {
            firstMessageDiv = '' +
                '<div id="firstMessageContainer">' +
                `<img class="botProfileImage" src="${server}/assets/images/chatbotImage.png" alt="ChatbotImage" width="50" height="50">` +
                '</div>';
        }
        if (feedBack) {
            yield removeFeedbackContainer();
        }
        wasFeedbackMessageSent = false;
        let feedBackDiv = "";
        if (feedBack) {
            feedBackDiv = '' +
                '<div id="feedBackContainer">' +
                `<img alt="thumbs up feedback image" onclick="upvoteMessage(this)" src="${server}/assets/images/thumbsUp.png" class="feedbackImage hvr-grow" id="thumbsUpImage">` +
                `<img alt="thumbs down feedback image" onclick="downvoteMessage(this)" src="${server}/assets/images/thumbsDown.png" class="feedbackImage hvr-grow" id="thumbsDownImage">` +
                '</div>';
        }
        let matchIDs = "";
        response.matches.forEach((item, index) => {
            matchIDs += item.id;
            if (index < response.matches.length - 1) {
                matchIDs += ',';
            }
        });
        let foundTagIDs = "";
        response.foundTags.forEach((item, index) => {
            foundTagIDs += item.id;
            if (index < response.foundTags.length - 1) {
                foundTagIDs += ',';
            }
        });
        let file = '';
        if (response.files.length > 0) {
            response.files.forEach((responseFile) => {
                file += buildFileView(responseFile);
            });
        }
        let messageAreaContent = '<div class="row">' +
            '<div class="col">' +
            `<div id="messageBox-${boxCounter++}">` +
            firstMessageDiv +
            '<div class="messageContainer incoming ' + count + '">' +
            '<span class="textMessage" style="white-space: pre-wrap">' + response.answer.answer + '</span>';
        if (feedBack) {
            messageAreaContent += '<span class="foundTags hidden">' + foundTagIDs + '</span>' +
                '<span class="foundMatches hidden">' + matchIDs + '</span>' +
                '<span class="answerID hidden">' + response.answer.id + '</span>';
        }
        messageAreaContent += '<span class="timeSent hidden">' + getFormattedTime() + '</span>' +
            '</div>' +
            file +
            feedBackDiv +
            '</div>' +
            '</div>' +
            '<div class="col">' +
            '<!-- NOTE: This is an empty div -->' +
            '</div>' +
            '</div>';
        messageAreaDiv.innerHTML += messageAreaContent;
        if (enableInputField) {
            inputField.disabled = false;
        }
        inputField.focus();
        inputField.placeholder = "Nachricht senden";
        if (toggleMessageLoader) {
            toggleMessageLoading();
        }
        scrollDown();
        generateChatHistory();
    });
}
function buildFileView(file) {
    let out = '';
    let TAGS = {
        URI: {
            OPEN_PREVIEW: `<a target="_blank" href="${server}${'/file?id=' + file.id}">`,
            OPEN_DOWNLOAD: `<a target="_blank" download="${file.fileName}" href="${server}${'/file?id=' + file.id}">`,
            CLOSE: `</a>`,
        },
        CONTAINER_DIV: {
            OPEN: '<div class="attachment-container">',
            CLOSE: '</div>'
        },
        FILENAME_PARAGRAPH: {
            OPEN: `<p>${file.fileName}`,
            CLOSE: '</p>'
        },
        PDF_EMBED: `<embed onload="scrollDown()" src="${server}${'/file?id=' + file.id}">`,
        IMG: `<img onload="scrollDown()" src="${server}${'/file?id=' + file.id}" class="attachment-image" alt="${file.fileName}">`
    };
    switch (file.mimeType) {
        case 'image/png':
        case 'image/jpeg':
        case 'image/gif':
            out += TAGS.URI.OPEN_PREVIEW;
            out += TAGS.IMG;
            out += TAGS.URI.CLOSE;
            break;
        case 'application/pdf':
            out += TAGS.CONTAINER_DIV.OPEN;
            out += TAGS.URI.OPEN_PREVIEW;
            out += TAGS.FILENAME_PARAGRAPH.OPEN + ' in einem neuen Tab öffnen!' + TAGS.FILENAME_PARAGRAPH.CLOSE;
            out += TAGS.URI.CLOSE;
            out += TAGS.PDF_EMBED;
            out += TAGS.CONTAINER_DIV.CLOSE;
            break;
        case 'text/plain':
            out += TAGS.URI.OPEN_PREVIEW;
            out += TAGS.CONTAINER_DIV.OPEN;
            out += TAGS.FILENAME_PARAGRAPH.OPEN;
            out += TAGS.CONTAINER_DIV.CLOSE;
            out += TAGS.URI.CLOSE;
            break;
        case 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet':
        case 'application/vnd.ms-excel':
        case 'application/vnd.openxmlformats-officedocument.wordprocessingml.document':
        case 'application/msword':
        case 'application/vnd.openxmlformats-officedocument.presentationml.presentation':
        case 'application/vnd.ms-powerpoint':
            out += TAGS.CONTAINER_DIV.OPEN;
            out += TAGS.URI.OPEN_DOWNLOAD;
            out += file.fileName;
            out += TAGS.URI.CLOSE;
            out += TAGS.CONTAINER_DIV.CLOSE;
            break;
    }
    return out;
}
function isEmptyOrWhiteSpace(str) {
    return (str.match(/^\s*$/) || []).length > 0;
}
function escapeHtml(str) {
    return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;").replace(/'/g, "&#039;");
}
function scrollDown() {
    chatbotAreaDiv.scrollTop = chatbotAreaDiv.scrollHeight;
}
function randomNum(min, max) {
    return Math.floor(Math.random() * (max - min)) + min;
}
function toggleMessageLoading() {
    if (messageLoadingDiv.style.display === "none") {
        messageLoadingDiv.style.display = "";
    }
    else {
        messageLoadingDiv.style.display = "none";
    }
}
function removeFeedbackContainer() {
    return __awaiter(this, void 0, void 0, function* () {
        let lastFeedBackContainer = document.getElementById('feedBackContainer');
        if (lastFeedBackContainer === null)
            return;
        let isUseless = true;
        for (let i = 0; i < lastFeedBackContainer.children.length; i++) {
            let child = lastFeedBackContainer.children.item(i);
            child.setAttribute('onclick', '');
            if (child.classList.contains('hvr-grow'))
                child.classList.remove('hvr-grow');
            if (child.src.substr(child.src.length - 10, 6) === 'Filled') {
                isUseless = false;
            }
        }
        if (isUseless) {
            lastFeedBackContainer.remove();
            return;
        }
        lastFeedBackContainer.id = '';
        lastFeedBackContainer.classList.add('prevFeedback');
    });
}
function upvoteMessage(origin, reset = false) {
    return __awaiter(this, void 0, void 0, function* () {
        if (reset) {
            sendFeedback(true, true);
        }
        else {
            sendFeedback(true);
        }
        changeVoteView(true);
    });
}
function downvoteMessage(origin, reset = false) {
    return __awaiter(this, void 0, void 0, function* () {
        if (reset) {
            sendFeedback(false, true);
        }
        else {
            sendFeedback(false);
        }
        changeVoteView(false);
    });
}
function changeVoteView(vote) {
    let feedBackContainer = document.getElementById('feedBackContainer');
    let index = (vote) ? 0 : 1;
    let imgPathFilled = (vote) ? 'thumbsUpFilled.png' : 'thumbsDownFilled.png';
    let imgPath = (vote) ? 'thumbsDown.png' : 'thumbsUp.png';
    let onclick = (vote) ? 'thumbsDownImage' : 'thumbsUpImage';
    let method = (vote) ? 'downvoteMessage' : 'upvoteMessage';
    let onclickIndex = (vote) ? 1 : 0;
    let img = feedBackContainer === null || feedBackContainer === void 0 ? void 0 : feedBackContainer.children[index];
    img.src = `${server}/assets/images/${imgPathFilled}`;
    img.removeAttribute('onclick');
    let img2 = feedBackContainer === null || feedBackContainer === void 0 ? void 0 : feedBackContainer.children[onclickIndex];
    img2.setAttribute('onclick', `${method}(document.getElementById(${onclick}), true)`);
    img2.src = `${server}/assets/images/${imgPath}`;
}
function sendFeedback(isPositive, isRevert = false) {
    let foundTagsContainerList = document.getElementsByClassName('foundTags');
    let foundMatchesContainerList = document.getElementsByClassName('foundMatches');
    let answerIDs = document.getElementsByClassName('answerID');
    let answer = answerIDs[answerIDs.length - 1].innerHTML;
    let containers = {
        tags: foundTagsContainerList[foundTagsContainerList.length - 1],
        matches: foundMatchesContainerList[foundMatchesContainerList.length - 1],
    };
    let tags = containers.tags.innerHTML.split(',');
    let matches = containers.matches.innerHTML.split(',');
    fetch(`${server}/services/feedback/vote?result=${isPositive}&revert=${isRevert}&answerID=${answer}&${generateVoteParameter(tags, 'tags')}&${generateVoteParameter(matches, 'matches')}&question=${inputMessageFromUser}`)
        .then(() => {
    });
}
function generateVoteParameter(items, parameter) {
    let string = "";
    for (let i = 0; i < items.length; i++) {
        string += parameter + '=' + items[i];
        if (i < items.length - 1)
            string += '&';
    }
    return string;
}
function generateChatHistory() {
    let messages = document.getElementsByClassName('messageContainer');
    let data = [];
    for (let i = 0; i < messages.length; i++) {
        if (i == 1)
            continue;
        let msg = messages[i];
        if (msg.classList.contains('outgoing')) {
            data.push({
                msg: msg.children[0].innerHTML,
                date: msg.children[msg.children.length - 1].innerHTML,
                sender: 'Nutzer '
            });
        }
        else if (msg.classList.contains('incoming')) {
            data.push({
                msg: msg.children[0].innerHTML,
                date: msg.children[msg.children.length - 1].innerHTML,
                sender: 'Chatbot'
            });
        }
    }
    let content = '';
    for (let i = 0; i < data.length; i++) {
        content += data[i].date + ' | ' + data[i].sender + ' : ' + data[i].msg + '\n';
    }
    download(content, 'ChatHistory_' + randomNum(100000, 999999), 'text/plain');
}
function download(text, name, type) {
    const a = document.getElementById('downloadButton');
    const file = new Blob([text], { type: type });
    a.href = URL.createObjectURL(file);
    a.download = name;
}
function init() {
    return __awaiter(this, void 0, void 0, function* () {
        inputField.disabled = true;
        yield receiveMessage({
            answer: {
                answer: 'Die Verbindung zum Server wird gerade aufgebaut, bitte hab einen Moment Geduld',
                id: -1
            }, matches: [], foundTags: [], files: []
        }, false, false, false);
        let response = yield fetch(`${server}/services/get/status`);
        let json = yield response.json();
        let container = document.getElementById('message-area');
        for (let child of container.children)
            child.remove();
        lastSender = senders.NONE;
        if (json.status) {
            yield getQuestionList().then(() => {
                inputField.disabled = false;
            });
        }
        else {
            yield receiveMessage({
                answer: {
                    answer: 'Ich werde gerade gewartet und kann dir keine Antworten liefern, bitte versuche es später erneut.',
                    id: -1,
                }, files: [], matches: [], foundTags: []
            }, false, false, false);
        }
        yield initMaxLengths(null, [{ elem: inputField, name: "USER_QUESTION_INPUT" }], true);
    });
}
function sendSuggestedMessage(question) {
    var _a;
    return __awaiter(this, void 0, void 0, function* () {
        (_a = document.getElementById('questionSuggestions')) === null || _a === void 0 ? void 0 : _a.remove();
        yield sendMessage(question, true, false);
    });
}
init().then();
getSuggestions('userMessageInput');
