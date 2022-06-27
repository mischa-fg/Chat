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
let secondsToWaitUntilNextReload = 3;
let secondsSinceLastPageUpdate = secondsToWaitUntilNextReload;
let loggedInUser;
let increaseSeconds = setInterval(incrementSeconds, 500);
function incrementSeconds() {
    if (secondsSinceLastPageUpdate <= secondsToWaitUntilNextReload) {
        secondsSinceLastPageUpdate += 0.5;
    }
}
let firstPageFilename = "overview.html";
let firstPageActiveButton = "homeButton";
const targetPageActiveButtonID = "activeButtonID";
const targetPageParamName = "targetPage";
let objectIDParam = "objectID";
let pageCheck = '';
let tempTagData = [];
let singleTagAnswers = [];
let answers = [];
let allAnswers = [];
let answerFiles = [];
let tags = [];
let specialTags = [];
let tagsInputTags = [];
let tagsInputPressCount = 0;
let matches = [];
let blackListEntries = [];
let defaultQuestions = [];
let unansweredQuestions = [];
let answeredQuestions = [];
let files = [];
let answerTypes = [];
let users = [];
let answerChart;
let answeredQuestionVsUnansweredQuestionChart;
document.addEventListener("DOMContentLoaded", function () {
    checkURLForParamsAndLoadContent();
});
function checkURLForParamsAndLoadContent(replaceState = true, forceReload = false) {
    return __awaiter(this, void 0, void 0, function* () {
        let fullSearchParams = window.location.search;
        const urlParams = new URLSearchParams(fullSearchParams);
        let currentPage = urlParams.get(targetPageParamName);
        let currentActiveButtonID = urlParams.get(targetPageActiveButtonID);
        if (currentPage === null) {
            currentPage = firstPageFilename;
            currentActiveButtonID = firstPageActiveButton;
        }
        let currentObjectID = +urlParams.get(objectIDParam);
        let specificDetailsNeeded = true;
        if (currentObjectID == null || isNaN(currentObjectID) || currentObjectID <= 0) {
            specificDetailsNeeded = false;
        }
        if (replaceState) {
            const url = new URL(window.location.href);
            url.searchParams.set(targetPageParamName, currentPage);
            if (currentActiveButtonID != null) {
                url.searchParams.set(targetPageActiveButtonID, currentActiveButtonID);
            }
            const href = url.toString();
            window.history.replaceState({}, document.title, href);
        }
        yield loadPage(currentPage, currentActiveButtonID, specificDetailsNeeded, currentObjectID, forceReload);
    });
}
$(window).on("popstate", function () {
    return __awaiter(this, void 0, void 0, function* () {
        if (history.state) {
            yield checkURLForParamsAndLoadContent(false, true);
        }
    });
});
let dataClass = null;
function loadPage(fileName, newActiveButtonID, specificDataNeeded = false, dataID = null, forceReload = false) {
    return __awaiter(this, void 0, void 0, function* () {
        pageCheck = removeFileExtension(fileName);
        let fullSearchParams = window.location.search;
        const urlParams = new URLSearchParams(fullSearchParams);
        let currentPage = urlParams.get(targetPageParamName);
        let currentActiveButtonID = urlParams.get(targetPageActiveButtonID);
        if (dataID <= 0 || isNaN(dataID)) {
            dataID = +urlParams.get("objectID");
        }
        if (fileName == "" || fileName == null) {
            fileName = currentPage;
        }
        if (newActiveButtonID == "" || newActiveButtonID == null) {
            newActiveButtonID = currentActiveButtonID;
        }
        dataClass = null;
        if (currentPage != fileName) {
            secondsSinceLastPageUpdate = secondsToWaitUntilNextReload;
            const url = new URL(window.location.href);
            url.searchParams.set(targetPageParamName, fileName);
            url.searchParams.set(targetPageActiveButtonID, newActiveButtonID);
            if (specificDataNeeded) {
                url.searchParams.set(objectIDParam, String(dataID));
            }
            else {
                url.searchParams.delete(objectIDParam);
            }
            const href = url.toString();
            window.history.pushState({}, document.title, href);
        }
        else if (specificDataNeeded) {
            secondsSinceLastPageUpdate = secondsToWaitUntilNextReload;
            const url = new URL(window.location.href);
            url.searchParams.set(targetPageParamName, fileName);
            url.searchParams.set(targetPageActiveButtonID, newActiveButtonID);
            url.searchParams.set(objectIDParam, String(dataID));
            const href = url.toString();
            window.history.replaceState({}, document.title, href);
        }
        if (secondsSinceLastPageUpdate >= secondsToWaitUntilNextReload || forceReload) {
            actuallyLoadPage(fileName, newActiveButtonID, currentPage);
            secondsSinceLastPageUpdate = 0;
        }
        const isLoggedIn = yield checkLoggedIn();
        if (!isLoggedIn) {
            yield logout(urlParams);
        }
    });
}
function loadPageWithUrl(pageName, buttonID) {
    return __awaiter(this, void 0, void 0, function* () {
        let fullSearchParams = window.location.search;
        const urlParams = new URLSearchParams(fullSearchParams);
        const answerID = +urlParams.get("objectID");
        yield loadPage(pageName, buttonID, true, answerID, true);
    });
}
function checkLoggedIn() {
    return __awaiter(this, void 0, void 0, function* () {
        const response = yield fetch(`${server}/services/adminTool/checkLogin`, {
            method: 'POST'
        });
        const json = yield response.json();
        return json.isLoggedIn;
    });
}
function actuallyLoadPage(fileName, activeButtonID, currentPage) {
    let activeButtonElement = document.getElementById(activeButtonID);
    $("#pageContent").load(fileName, function (response, status, xhr) {
        if (status === "error") {
            actuallyLoadPage(firstPageFilename, firstPageActiveButton, currentPage);
        }
        else if (status === "success") {
            $('.sidenavButton.active').removeClass('active');
            activeButtonElement.classList.add('active');
        }
    });
}
function getSpecialTags(type, withAmountAnswers = false) {
    return __awaiter(this, void 0, void 0, function* () {
        specialTags = [];
        yield fetch(`${server}/services/get/allTagsByTypeWithInfos?type=${type}`)
            .then((response) => {
            return response.json();
        })
            .then(response => {
            for (let tag of response) {
                let thisAmountAnswers;
                if (withAmountAnswers) {
                    thisAmountAnswers = tag.amountAnswers;
                }
                else {
                    thisAmountAnswers = -1;
                }
                let newTag = new MyTag(tag.tag.id, tag.tag.tag, tag.upvotes, tag.downvotes, tag.usage, thisAmountAnswers, []);
                specialTags.push(newTag);
            }
        });
    });
}
function randomNumber(min = 1, max = 100) {
    return Math.floor(Math.random() * (max - min) + min);
}
function clamp(number, min, max) {
    return Math.min(Math.max(number, min), max);
}
function calcPercentage(value1, value2) {
    let value3 = (+value1 + +value2);
    let percentage;
    if (value3 != 0) {
        percentage = (+value1 / +value3);
    }
    else {
        percentage = 0.5;
    }
    return percentage;
}
function getColor(percent) {
    percent = 1 - percent;
    let hue = ((1 - percent) * 120).toString(10);
    return ["hsl(", hue, ",80%,70%)"].join("");
}
function findSameId(array, idToFind) {
    for (let i = 0; i < array.length; i++) {
        if (+array[i].id == +idToFind) {
            return i;
        }
    }
    return null;
}
function setAllAnswerTypes(selectedAnswerTypeValue = null, selectID = "answerTypesSelect", needToCheckHidden = true, offsetValue = 0) {
    return __awaiter(this, void 0, void 0, function* () {
        let selectAnswerTypes = document.getElementById(selectID);
        const page = pageCheck;
        try {
            let response = yield fetch(`${server}/services/get/answerTypes`);
            let json = yield response.json();
            answerTypes = [];
            for (let currentAnswerType of json.answerTypes) {
                let newAnswerType = new AnswerType(currentAnswerType.value, currentAnswerType.name, currentAnswerType.groupedTags, currentAnswerType.hidden, currentAnswerType.forceHidden);
                answerTypes.push(newAnswerType);
                let option = document.createElement("option");
                option.value = currentAnswerType.value + +offsetValue;
                option.textContent = currentAnswerType.name;
                selectAnswerTypes.appendChild(option);
                selectAnswerTypes.appendChild(option);
                if (selectedAnswerTypeValue != null && currentAnswerType.value + offsetValue == selectedAnswerTypeValue) {
                    selectAnswerTypes.value = currentAnswerType.value;
                }
            }
            if (needToCheckHidden) {
                for (let currentAnswerType of answerTypes) {
                    checkIsHiddenStatus(currentAnswerType, false, false, selectID);
                }
            }
        }
        catch (e) {
            checkPageExecute(() => popup(false, 'Antwort Typen konnten nicht geladen werden!'), page);
        }
    });
}
function checkIsHiddenStatus(answerType, isSet = false, setValue = false, selectID = "answerTypesSelect") {
    let selectAnswerTypes = document.getElementById("answerTypesSelect");
    if (answerType.id == +selectAnswerTypes.value) {
        let hiddenInput = document.getElementById("isHiddenInput");
        if (!isSet) {
            hiddenInput.checked = answerType.isHiddenDefault;
        }
        else {
            hiddenInput.checked = setValue;
        }
        hiddenInput.disabled = answerType.forceIsHiddenDefault;
    }
}
function logout(urlParams, onPurpose = true) {
    return __awaiter(this, void 0, void 0, function* () {
        yield fetch(`${server}/Logout`, {
            method: 'post'
        });
        if (urlParams === null) {
            window.location.replace(`${server}/pages/login/login.jsp`);
            return;
        }
        const targetPage = urlParams.get(targetPageParamName);
        const activeButtonID = urlParams.get(targetPageActiveButtonID);
        const objectID = urlParams.get(objectIDParam);
        window.location.replace(`${server}/pages/login/login.jsp?${(onPurpose) ? `targetPage=${targetPage}&activeButtonID=${activeButtonID}&objectID=${objectID}` : ''}`);
    });
}
function popup(success, textContent, uptime = 3000) {
    var _a;
    uptime = clamp(uptime, 1050, 10000);
    let msg = document.createElement('div');
    msg.classList.add((success) ? 'successMessage' : 'failMessage');
    let img = document.createElement('img');
    img.src = `${server}/assets/images/${(success) ? 'success' : 'failed'}.svg`;
    img.classList.add('image');
    let content = document.createElement('h3');
    content.innerHTML = textContent;
    content.classList.add('message');
    msg.appendChild(img);
    msg.appendChild(content);
    (_a = document.getElementById('pageContent')) === null || _a === void 0 ? void 0 : _a.appendChild(msg);
    setTimeout(() => {
        msg.style.animation = 'slideOut 1s ease-in-out';
        msg.style.animationPlayState = 'running';
        msg.addEventListener('animationend', () => {
            msg.remove();
        });
    }, uptime);
}
function disableLoader() {
    $("#contentLoaderAnimation").remove();
}
function initLoadingAnimation(idOfDivToPutItIn) {
    var _a;
    let container = document.createElement("div");
    container.id = "contentLoaderAnimation";
    container.classList.add("lds-roller");
    for (let i = 0; i < 8; i++) {
        let div = document.createElement("div");
        container.appendChild(div);
    }
    (_a = document.getElementById(idOfDivToPutItIn)) === null || _a === void 0 ? void 0 : _a.appendChild(container);
}
function removeFileExtension(page) {
    return page.replace(/\.[^/.]+$/, "");
}
function checkPageExecute(fun, specifiedPage) {
    if (specifiedPage === pageCheck || specifiedPage === null || pageCheck === null) {
        fun();
    }
}
