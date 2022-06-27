// Type Definitions
type Sender = 'none' | 'bot' | 'human';

// Interfaces
interface Message {
    msg: string,
    sender: string,
    date: string
}

interface Match {
    id: number;
}

interface Tag {
    id: number;
}

interface _Answer {
    answer: string,
    id: number,
}

interface ResponseFile {
    id: number,
    fileName: string,
    mimeType: string
}

interface ServerResponse {
    answer: _Answer,
    matches: Match[],
    foundTags: Tag[],
    files: ResponseFile[];
}

// NOTE: Declare variables
const inputField: HTMLInputElement = document.getElementById("userMessageInput") as HTMLInputElement; // NOTE: Get the user input field
const chatbotAreaDiv: HTMLInputElement = document.getElementById('chatbotArea') as HTMLInputElement; // NOTE: Get the chatbotArea div
const messageAreaDiv: HTMLInputElement = document.getElementById('message-area') as HTMLInputElement; // NOTE: Get the message-area div
const messageLoadingDiv: HTMLInputElement = document.getElementById('messageLoading') as HTMLInputElement; // NOTE: Get the messageLoading div
const NOANSWERFOUND = "Ich habe dich leider nicht verstanden. Bitte frag mich erneut mit einer anderen Fragestellung oder wende dich an deinen Junior Talent Manager.";
const suggestionTable = document.getElementById('suggestions') as HTMLTableSectionElement;
let wasFeedbackMessageSent: boolean = false;
let inputMessageFromUser: string = "";

// NOTE: Keep track of the last senders
enum senders {
    NONE = 'none',
    BOT = 'bot',
    HUMAN = 'human'
}

let lastSender: Sender = senders.NONE;

function setLastSender(value: Sender) {
    lastSender = value;
}

// NOTE: USER
// NOTE: Catch enter button
inputField.addEventListener("keydown", async function (e) { // NOTE: adds an EventListener to the input field
    if ((suggestionTable.children.length <= 0 || selectedSuggestion == -1) && e.key === 'Enter') {
        await sendMessage(); // NOTE: call the function and send the message
    }
});

async function sendMessage(input: null | string = null, serverResponse = true, htmlEscaped: boolean = true, customMessageId = '') {
    suggestionTable.innerHTML = '';

    let message: string = (input === null) ? inputField.value : input;
    inputMessageFromUser = message;
    let messageToDisplayInFrontend: string = (htmlEscaped) ? escapeHtml(message) : message;

    if (!isEmptyOrWhiteSpace(message)) {
        let count: string = "firstMessage";
        switch (lastSender) {
            case senders.HUMAN:
                count = "notFirstMessage";
                break;
            default:
                setLastSender(senders.HUMAN);
                break;
        }

        messageAreaDiv.innerHTML += // NOTE: Add the message to the message area
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
            '</div>'
        ;

        // NOTE: After we send a message disable(remove) feedback container
        // await removeFeedbackContainer();

        if (serverResponse) {
            inputField.disabled = true; // Disable the input field
            inputField.placeholder = "Waiting for response..."; // Set the placeholder of the input field to tell the user whats happening
            toggleMessageLoading();
            scrollDown();
            await getAnswerFromServer(message);
        }

        scrollDown();
    }

    // NOTE: Clear the input field
    inputField.value = '';
    triggerInputEvent(inputField)

    generateChatHistory();
}

// NOTE: BOT
let welcomeMessage: string = "Hallo, wie kann ich dir behilflich sein?"; // NOTE: Define the first/welcome message
let question: string[];
let textoption: string = "Hier sind ein paar Suchvorschläge. Nicht das, was du suchst? Dann kannst du deine Frage im Nachrichtenfeld stellen.\n"


async function getQuestionList() {
    let response = await fetch(`${server}/services/get/questionSuggestions?amountQuestions=3`);
    let json = await response.json();


    question = json.questions;

    let optionMessage: string = textoption +
        "<input type='button' class='questionbutton' value='" + question[0] + "' onclick='sendSuggestedMessage(question[0])'/>\n" +
        "<input type='button' class='questionbutton' value='" + question[1] + "' onclick='sendSuggestedMessage(question[1])'/>\n" +
        "<input type='button' class='questionbutton' value='" + question[2] + "' onclick='sendSuggestedMessage(question[2])'/>";

    await receiveMessage({
        answer: {answer: welcomeMessage, id: -1},
        matches: [],
        foundTags: [],
        files: []
    }, false, false); // NOTE: Send the first/welcome message
    await sendMessage(optionMessage, false, false, 'questionSuggestions'); // NOTE: Send the first/welcome message
}

// receiveMessage({
//     answer: "Ich habe im Moment Technische Schwierigkeiten! Ich werde ihnen keine nützlichen Antworten liefern können!",
//     matches: []
// }, false, false);

// NOTE: we send a request to the server and wait for a response, then we show the response
async function getAnswerFromServer(input: string) {
    try {
        const inputEncoded = encodeURIComponent(input);
        let response = await fetch(`${server}/services/find2/answer2?q=${inputEncoded}`);
        let json = await response.json() as ServerResponse;

        if (json.answer.answer === '404') {
            json.answer.answer = NOANSWERFOUND;
            await receiveMessage(json, false)

            return;
        } else {
            await receiveMessage(json);
        }
    } catch (e) {

        await receiveMessage({
            answer: {
                answer: "Bitte überprüfe deine Internetverbindung und versuche es später erneut",
                id: -1
            },
            matches: [],
            foundTags: [],
            files: []
        }, false);
    }
}

function getFormattedTime() {
    let date: Date = new Date();
    let twoCharValues: string[] = [];

    twoCharValues.push((date.getMonth() + 1).toString());
    twoCharValues.push(date.getDate().toString());
    twoCharValues.push(date.getHours().toString());
    twoCharValues.push(date.getMinutes().toString());
    twoCharValues.push(date.getSeconds().toString());

    for (let i = 0; i < twoCharValues.length; i++) {
        if (twoCharValues[i].length < 2)
            twoCharValues[i] = '0' + twoCharValues[i];
    }
    let c: number = 0;
    return date.getFullYear() + "." + twoCharValues[c++] + "." + twoCharValues[c++] + " " + twoCharValues[c++] + ":" + twoCharValues[c++] + ":" + twoCharValues[c++];
}

// NOTE: Message/Response from the bot
let boxCounter = 0;

async function receiveMessage(response: ServerResponse, feedBack = true, toggleMessageLoader = true, enableInputField = true) {
    let count = "firstMessage"; // NOTE: Keep track if its the first message or not

    switch (lastSender) { // NOTE: switch the current lastsender
        case senders.BOT: // NOTE: if the last sender is the bot...
            count = "notFirstMessage"; // NOTE: Its not the first message we sent
            break;
        default: // NOTE: if last sender is not the bot
            setLastSender(senders.BOT); // NOTE: set last sender to bot
            break;
    }
    let firstMessageDiv: string = "";
    if (count == "firstMessage") {
        firstMessageDiv = '' +
            '<div id="firstMessageContainer">' +
            `<img class="botProfileImage" src="${server}/assets/images/chatbotImage.png" alt="ChatbotImage" width="50" height="50">` +
            '</div>';
    }

    // NOTE: check if we already have a "feedbackContainer" element, if so, remove it.
    // We only want to be able to give a feedback to the latest message
    if (feedBack) {
        await removeFeedbackContainer();
    }

    wasFeedbackMessageSent = false;

    // if this is a message we can give a feedback to, add the feedback icons
    let feedBackDiv: string = "";
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
        })
    }

    let messageAreaContent = '<div class="row">' +
        '<div class="col">' +
        `<div id="messageBox-${boxCounter++}">` +
        firstMessageDiv +
        '<div class="messageContainer incoming ' + count + '">' + // NOTE: add the count to the classes, so we can differentiate it in CSS
        '<span class="textMessage" style="white-space: pre-wrap">' + response.answer.answer + '</span>';

    if (feedBack) {
        messageAreaContent += '<span class="foundTags hidden">' + foundTagIDs + '</span>' +
            '<span class="foundMatches hidden">' + matchIDs + '</span>' +
            '<span class="answerID hidden">' + response.answer.id + '</span>'
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
        inputField.disabled = false; // Re-enable the text input field
    }
    inputField.focus(); // Set the focus to the text input field so we directly can start typing the next message again
    inputField.placeholder = "Nachricht senden"; // Set the placeholder back to the default message
    if (toggleMessageLoader) {
        toggleMessageLoading();
    }

    scrollDown();
    generateChatHistory();
}

function buildFileView(file: ResponseFile): string {
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
    }

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
            out += file.fileName
            out += TAGS.URI.CLOSE;
            out += TAGS.CONTAINER_DIV.CLOSE;
            break;
    }

    return out;
}

// NOTE: Functions
// NOTE: Check if the string is empty
function isEmptyOrWhiteSpace(str: string): boolean {
    return (str.match(/^\s*$/) || []).length > 0; // NOTE: returns true if string is empty
}

// NOTE: escapeHtml
function escapeHtml(str: string): string {
    return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;").replace(/'/g, "&#039;");
}

// NOTE: Auto scroll to the end of the div
function scrollDown(): void {
    chatbotAreaDiv.scrollTop = chatbotAreaDiv.scrollHeight;
}

// NOTE: generate random number
function randomNum(min: number, max: number): number {
    return Math.floor(Math.random() * (max - min)) + min;
}

// NOTE: Toggle message loading gif
function toggleMessageLoading(): void {
    if (messageLoadingDiv.style.display === "none") {
        messageLoadingDiv.style.display = ""; //show
    } else {
        messageLoadingDiv.style.display = "none";  //hide
    }
}

async function removeFeedbackContainer(): Promise<void> {
    let lastFeedBackContainer: HTMLElement | null = document.getElementById('feedBackContainer');

    if (lastFeedBackContainer === null) return;
    let isUseless = true;
    for (let i = 0; i < lastFeedBackContainer.children.length; i++) {
        let child = lastFeedBackContainer.children.item(i) as HTMLImageElement;
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
    lastFeedBackContainer.classList.add('prevFeedback')
}

// NOTE: Upvote the message
async function upvoteMessage(origin: HTMLElement, reset: boolean = false): Promise<void> {
    if (reset) {
        sendFeedback(true, true);
    } else {
        sendFeedback(true);
    }

    changeVoteView(true);
}

// NOTE: Downvote the message
async function downvoteMessage(origin: HTMLElement, reset: boolean = false): Promise<void> {
    if (reset) {
        sendFeedback(false, true);
    } else {
        sendFeedback(false);
    }

    changeVoteView(false);
}

function changeVoteView(vote: boolean) {
    let feedBackContainer = document.getElementById('feedBackContainer');

    let index = (vote) ? 0 : 1;
    let imgPathFilled = (vote) ? 'thumbsUpFilled.png' : 'thumbsDownFilled.png';
    let imgPath = (vote) ? 'thumbsDown.png' : 'thumbsUp.png';
    let onclick = (vote) ? 'thumbsDownImage' : 'thumbsUpImage';
    let method = (vote) ? 'downvoteMessage' : 'upvoteMessage';
    let onclickIndex = (vote) ? 1 : 0;
    let img = feedBackContainer?.children[index] as HTMLImageElement;
    img.src = `${server}/assets/images/${imgPathFilled}`;
    img.removeAttribute('onclick');
    let img2 = feedBackContainer?.children[onclickIndex] as HTMLImageElement;
    img2.setAttribute('onclick', `${method}(document.getElementById(${onclick}), true)`);
    img2.src = `${server}/assets/images/${imgPath}`;
}

// NOTE: Send feedback to the server
function sendFeedback(isPositive: boolean, isRevert: boolean = false): void {
    let foundTagsContainerList: HTMLCollectionOf<Element> = document.getElementsByClassName('foundTags');
    let foundMatchesContainerList: HTMLCollectionOf<Element> = document.getElementsByClassName('foundMatches');
    let answerIDs: HTMLCollectionOf<Element> = document.getElementsByClassName('answerID');
    let answer = answerIDs[answerIDs.length - 1].innerHTML;
    let containers = {
        tags: foundTagsContainerList[foundTagsContainerList.length - 1],
        matches: foundMatchesContainerList[foundMatchesContainerList.length - 1],
    }

    let tags = containers.tags.innerHTML.split(',');
    let matches = containers.matches.innerHTML.split(',');

    fetch(`${server}/services/feedback/vote?result=${isPositive}&revert=${isRevert}&answerID=${answer}&${generateVoteParameter(tags, 'tags')}&${generateVoteParameter(matches, 'matches')}&question=${inputMessageFromUser}`)
        .then(() => {
        });
}

function generateVoteParameter(items: string[], parameter: string): string {
    let string = "";

    for (let i = 0; i < items.length; i++) {
        string += parameter + '=' + items[i];
        if (i < items.length - 1) string += '&';
    }

    return string;
}

// Chat History Download
function generateChatHistory() {
    let messages: HTMLCollectionOf<Element> = document.getElementsByClassName('messageContainer');

    let data: Message[] = [];

    for (let i = 0; i < messages.length; i++) {
        if (i == 1) continue; // Ignore Question Suggestions
        let msg = messages[i];

        if (msg.classList.contains('outgoing')) {
            data.push({
                msg: msg.children[0].innerHTML,
                date: msg.children[msg.children.length - 1].innerHTML,
                sender: 'Nutzer '
            });
        } else if (msg.classList.contains('incoming')) {
            data.push({
                msg: msg.children[0].innerHTML,
                date: msg.children[msg.children.length - 1].innerHTML,
                sender: 'Chatbot'
            });
        }
    }

    let content: string = '';

    for (let i = 0; i < data.length; i++) {
        content += data[i].date + ' | ' + data[i].sender + ' : ' + data[i].msg + '\n';
    }

    download(content, 'ChatHistory_' + randomNum(100000, 999999), 'text/plain');
}

// NOTE: File Download function
function download(text: BlobPart, name: string, type: string) {
    const a = document.getElementById('downloadButton') as HTMLAnchorElement;
    const file: Blob = new Blob([text], {type: type});
    a.href = URL.createObjectURL(file);
    a.download = name;
}

async function init() {
    inputField.disabled = true;

    await receiveMessage({
        answer: {
            answer: 'Die Verbindung zum Server wird gerade aufgebaut, bitte hab einen Moment Geduld',
            id: -1
        }, matches: [], foundTags: [], files: []
    }, false, false, false);

    let response = await fetch(`${server}/services/get/status`);
    let json = await response.json() as { status: boolean };

    let container = document.getElementById('message-area') as HTMLDivElement;
    for (let child of container.children) child.remove();
    lastSender = senders.NONE;

    if (json.status) {
        await getQuestionList().then(() => {
            inputField.disabled = false
        });
    } else {
        await receiveMessage({
            answer: {
                answer: 'Ich werde gerade gewartet und kann dir keine Antworten liefern, bitte versuche es später erneut.',
                id: -1,
            }, files: [], matches: [], foundTags: []
        }, false, false, false);
    }

    await initMaxLengths(null, [{elem: inputField, name: textInputs.QUESTION}], true)
}

async function sendSuggestedMessage(question: string) {
    document.getElementById('questionSuggestions')?.remove();
    await sendMessage(question, true, false);
}

// Execute on load
init().then();
getSuggestions('userMessageInput');
