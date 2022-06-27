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
function loadTag() {
    return __awaiter(this, void 0, void 0, function* () {
        const page = pageCheck;
        tags = [];
        let fullSearchParams = window.location.search;
        const urlParams = new URLSearchParams(fullSearchParams);
        let tagID = +urlParams.get("objectID");
        if (tagID == null || isNaN(tagID) || tagID <= 0) {
            initialiseAddTagPage();
        }
        else {
            yield initialiseTagEditPage(tagID);
        }
        yield initMaxLengths(page, [{ elem: document.getElementById("tagName"), name: "TAG" }]);
    });
}
function updateTag() {
    return __awaiter(this, void 0, void 0, function* () {
        let tagId = document.getElementsByClassName('id-hidden');
        let tagName = document.getElementById("tagName").value;
        const page = pageCheck;
        if (tagName != '' && tagName != null) {
            try {
                let response = yield fetch(server + '/services/adminTool/editTag', {
                    method: 'post',
                    body: `tagID=${tagId.item(0).id}&tagContent=${encodeURIComponent(tagName)}`,
                });
                if (response.ok) {
                    yield loadPage('tagsDetails.html', 'tagButton', true, -1, true);
                }
                else {
                    popup(false, 'Dieser Tag konnte nicht aktualisiert werden!');
                }
            }
            catch (e) {
                checkPageExecute(() => popup(false, 'Dieser Tag konnte aus unerwarteten Gründen nicht aktualisiert werden!'), 'tagsDetails');
            }
        }
        else {
            document.getElementById('errorTag').innerHTML = 'Es wurde kein Tag erkannt';
        }
    });
}
function deleteTag() {
    return __awaiter(this, void 0, void 0, function* () {
        let tagId = document.getElementsByClassName('id-hidden');
        let tagName = document.getElementById("tagName").value;
        let amountAnswers = +document.getElementById("amountAnswers").innerHTML;
        const page = pageCheck;
        if (tagId != null) {
            let confirmMessage = `Willst du den Tag "${tagName}" wirklich löschen?`;
            if (amountAnswers > 0) {
                confirmMessage += `\nEr wird von ${amountAnswers} Antworten entfernt`;
            }
            let success = true;
            if (confirm(confirmMessage)) {
                try {
                    let response = yield fetch(`${server}/services/adminTool/deleteTag`, {
                        method: 'post',
                        body: `tagID=${tagId.item(0).id}`
                    });
                    if (response.ok) {
                        yield loadPage("tags.html", "tagButton", false, null, true);
                    }
                    else {
                        checkPageExecute(() => {
                            popup(false, 'Dieser Tag konnte nicht gelöscht werden');
                        }, 'tagsDetails');
                        success = false;
                    }
                }
                catch (e) {
                    checkPageExecute(() => {
                        popup(false, 'Dieser Tag konnte aus unerwarteten Gründen nicht gelöscht werden!');
                    }, 'tagsDetails');
                    success = false;
                }
            }
            if (!success)
                popup(true, 'Der Tag wurde erfolgreich gelöscht!');
        }
        else {
            checkPageExecute(() => {
                popup(false, 'Der Tag wurde nicht nicht gelöscht!');
            }, 'tagsDetails');
        }
    });
}
function initialiseAddTagPage() {
    var _a;
    document.title = adminToolPageTitleName + ' - Tag hinzufügen';
    let divInput = document.getElementById('input-group');
    let createButton = document.createElement('button');
    createButton.setAttribute('id', 'tagFormSubmitButton');
    createButton.setAttribute('type', 'button');
    createButton.classList.add('form-control');
    createButton.name = 'createTag';
    createButton.innerHTML = 'Hinzufügen';
    createButton.setAttribute('onClick', 'addTag()');
    divInput === null || divInput === void 0 ? void 0 : divInput.appendChild(createButton);
    (_a = document.getElementById("tagName")) === null || _a === void 0 ? void 0 : _a.addEventListener("keyup", function (event) {
        return __awaiter(this, void 0, void 0, function* () {
            if (event.keyCode === 13) {
                yield addTag();
            }
        });
    });
    disableLoader();
}
function addTag() {
    return __awaiter(this, void 0, void 0, function* () {
        let inputTitle = document.getElementById("tagName").value;
        tags = [];
        if (inputTitle != '' && inputTitle != null) {
            try {
                let response = yield fetch(`${server}/services/adminTool/addTag`, {
                    method: 'post',
                    body: `tag=${encodeURIComponent(inputTitle)}`
                });
                if (!response.ok) {
                    popup(false, 'Dieser Tag existiert bereits');
                }
                let json = yield response.json();
                yield loadPage("tagsDetails.html", "tagButton", true, +json.tag.id, true);
            }
            catch (e) {
                popup(false, 'Dieser Tag konnte aus unerwarteten Gründen nicht hinzugefügt werden!');
            }
        }
        else {
            popup(false, 'Tag konnte nicht hinzugefügt werden!');
        }
    });
}
function initialiseTagEditPage(tagId) {
    var _a;
    return __awaiter(this, void 0, void 0, function* () {
        const page = pageCheck;
        document.title = adminToolPageTitleName + ' - Tag bearbeiten';
        if (tagId == null || isNaN(tagId)) {
            tagId = dataClass.id;
        }
        try {
            const response = yield fetch(`${server}/services/get/singleTag?tagID=${tagId}`);
            const retrievedTag = yield response.json();
            let myAnswers = [];
            singleTagAnswers = [];
            for (let currentAnswer of retrievedTag.answers) {
                let currentAnswerID = +currentAnswer.id;
                let title = currentAnswer.title;
                let answerViews = +currentAnswer.views;
                let answerHidden = currentAnswer.isHidden;
                let answerType = currentAnswer.answerType[0];
                let answerUsefulness = currentAnswer.averageUsefulness;
                let answerUpvotes = currentAnswer.upvotes;
                let answerDownvotes = currentAnswer.downvotes;
                let answer = new SingleTagAnswer(currentAnswerID, tagId, title, answerViews, answerUpvotes, answerDownvotes, answerHidden, answerUsefulness, answerType);
                singleTagAnswers.push(answer);
                myAnswers.push(answer);
            }
            let tag = new MyTag(retrievedTag.tag.id, retrievedTag.tag.tag, retrievedTag.upvotes, retrievedTag.downvotes, retrievedTag.usage, retrievedTag.amountAnswers, myAnswers);
            dataClass = tag;
            let tagDetailsDetails = document.getElementById('tagDetailsDetails');
            let divName = document.getElementById('tagName');
            divName === null || divName === void 0 ? void 0 : divName.setAttribute('value', tag.tag);
            let idhidden = document.createElement('div');
            idhidden.id = String(tag.id);
            idhidden.classList.add('id-hidden');
            tagDetailsDetails === null || tagDetailsDetails === void 0 ? void 0 : tagDetailsDetails.appendChild(idhidden);
            let amountAnswersHidden = document.createElement('span');
            amountAnswersHidden.id = "amountAnswers";
            amountAnswersHidden.classList.add('hidden');
            amountAnswersHidden.innerHTML = String(tag.amountAnswers);
            tagDetailsDetails === null || tagDetailsDetails === void 0 ? void 0 : tagDetailsDetails.appendChild(amountAnswersHidden);
            (_a = document.getElementById("tagName")) === null || _a === void 0 ? void 0 : _a.addEventListener("keyup", function (event) {
                if (event.keyCode === 13) {
                    updateTag();
                }
            });
            let divInput = document.getElementById('input-group');
            let deleteButton = document.createElement('button');
            deleteButton.setAttribute('id', 'deleteTags');
            deleteButton.setAttribute('type', 'button');
            deleteButton.setAttribute('onClick', 'deleteTag()');
            deleteButton.classList.add('btn', 'btn-outline-danger', 'col-4');
            deleteButton.innerHTML = 'Löschen';
            let updateButton = document.createElement('button');
            deleteButton.setAttribute('id', 'updateTags');
            updateButton.setAttribute('type', 'button');
            updateButton.setAttribute('onClick', 'updateTag()');
            updateButton.classList.add('btn', 'btn-primary', 'col-8');
            updateButton.innerHTML = 'Aktualisieren';
            divInput === null || divInput === void 0 ? void 0 : divInput.append(deleteButton, updateButton);
            let divContainer = document.getElementById('allAnswersContainer');
            let connectedAnswers = document.createElement('h2');
            connectedAnswers.id = "connectedAnswers";
            connectedAnswers.innerHTML = `${tag.amountAnswers} verbundene Antworten`;
            divContainer === null || divContainer === void 0 ? void 0 : divContainer.append(connectedAnswers);
            initialiseAnswerTable(myAnswers);
            disableLoader();
        }
        catch (e) {
            checkPageExecute(() => {
                popup(false, 'Diese Seite konnte leider nicht geladen werden!');
            }, 'tagsDetails');
        }
    });
}
function removeFromTagAnswerBackend(answerId, tagId) {
    let thisTag = dataClass;
    let indexOfAnswer = findSameId(thisTag.answers, answerId);
    let answerTypeOfAnswerToRemove = null;
    if (indexOfAnswer != null) {
        let answerToRemove = thisTag.answers[indexOfAnswer];
        answerTypeOfAnswerToRemove = answerToRemove.answerType;
        if (answerTypeOfAnswerToRemove != null) {
            let warning = "Willst du den Tag wirklich von dieser Antwort entfernen?";
            if (answerTypeOfAnswerToRemove.groupedTags) {
                warning += "\nDer Tag wird von allen Antworten mit dem Typen '" + answerTypeOfAnswerToRemove.name + "' entfernt!";
            }
            if (confirm(warning)) {
                fetch(server + '/services/adminTool/removeTagFromAnswer', {
                    method: 'post',
                    body: `answerId=${answerId}&tagId=${tagId}`,
                }).then((response) => {
                    if (response.ok) {
                        let tag = dataClass;
                        removeFromTagAnswerFrontend(tag, answerId, answerTypeOfAnswerToRemove);
                    }
                    else {
                        popup(false, "Tag konnte nicht von der Antwort enfternt werden!");
                    }
                });
            }
        }
        else {
            popup(false, "Kann den Tag nicht von der Antwort entfernen da der Typ der Antwort leer ist!");
        }
    }
    else {
        popup(false, "Kann den Tag nicht von der Antwort entfernen da die Antwort leer ist!");
    }
}
function removeFromTagAnswerFrontend(tag, answerId, answerTypeOfAnswerToRemove) {
    if (answerTypeOfAnswerToRemove != null && answerTypeOfAnswerToRemove.groupedTags) {
        singleTagAnswers.forEach((currentAnswer) => {
            if (currentAnswer.answerType.value === answerTypeOfAnswerToRemove.value) {
                actualRemoveOfTagFromAnswer(tag, currentAnswer.id);
            }
        });
    }
    else {
        actualRemoveOfTagFromAnswer(tag, answerId);
    }
}
function actualRemoveOfTagFromAnswer(tag, answerId) {
    tag.removeAnswerById(answerId);
    let tagsTableBody = document.getElementById("allAnswers");
    tagsTableBody.removeChild(document.getElementById(`singleTagAnswerID-${answerId}`));
    let connectedAnswersElement = document.getElementById("connectedAnswers");
    tag.amountAnswers = tag.amountAnswers - 1;
    connectedAnswersElement.innerHTML = `${tag.amountAnswers} verbundene Antworten`;
}
