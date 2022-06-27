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
var _a;
function initLengths(page) {
    return __awaiter(this, void 0, void 0, function* () {
        const answerTitleInput = document.getElementById("answerTitle");
        const answerTextInput = document.getElementById("answerText");
        const tagInput = document.getElementById('tag-container-input');
        yield initMaxLengths(page, [{ elem: answerTitleInput, name: "ANSWER_TITLE" }, {
                elem: answerTextInput,
                name: "ANSWER_TEXT"
            }, { elem: tagInput, name: "TAG" }]);
    });
}
function initialiseAnswerEditPage(answer, page) {
    return __awaiter(this, void 0, void 0, function* () {
        answerFiles = [];
        document.title = adminToolPageTitleName + ' - Antwort bearbeiten';
        const token = pageCheck;
        const answerTitleInput = document.getElementById("answerTitle");
        const answerTextInput = document.getElementById("answerText");
        const answerHiddenInput = document.getElementById("isHiddenInput");
        const fileTable = document.getElementById('files');
        const fileSelector = document.getElementById('fileSelector');
        if (answer != null) {
            initialiseTagsTable();
            let submitButton = document.getElementById("answerFormSubmitButton");
            submitButton.value = "Antwort aktualisieren";
            submitButton.setAttribute("onClick", "updateAnswer()");
            answerTitleInput.value = answer.title;
            answerTextInput.value = answer.answerPageAnswer;
            for (let file of answer.files) {
                answerFiles.push(file);
                fileTable.append(createFileTableRow(file));
            }
            yield setAllAnswerTypes(answer.answerType.id);
            answerHiddenInput.checked = answer.isHidden;
            yield loadFilesToSelect(false, answer, fileTable, fileSelector);
            let myTags = answer.tags;
            if (!answer.answerType.groupedTags) {
                tempTagData = myTags;
            }
            fillTableWithTags(myTags, true, page);
        }
        else {
        }
        yield initLengths(token);
        disableLoader();
    });
}
function initialiseAddAnswerPage() {
    var _a;
    return __awaiter(this, void 0, void 0, function* () {
        $("#topNavForEditAnswer").remove();
        const token = pageCheck;
        answerFiles = [];
        document.title = adminToolPageTitleName + ' - Antwort hinzufügen';
        let title = document.getElementById("title");
        title.innerHTML = "Antwort hinzufügen";
        let deleteAnswerButton = document.getElementById("deleteAnswerButton");
        (_a = deleteAnswerButton === null || deleteAnswerButton === void 0 ? void 0 : deleteAnswerButton.parentNode) === null || _a === void 0 ? void 0 : _a.removeChild(deleteAnswerButton);
        let submitButton = document.getElementById("answerFormSubmitButton");
        submitButton.value = "Neue Antwort hinzufügen";
        submitButton.setAttribute("onClick", "addAnswer()");
        let div = document.getElementById('answersDetailsContent');
        let error = document.createElement('p');
        error.id = 'errorAnswers';
        error.style.color = 'red';
        div === null || div === void 0 ? void 0 : div.appendChild(error);
        yield setAllAnswerTypes();
        const fileTable = document.getElementById('files');
        const fileSelector = document.getElementById('fileSelector');
        yield loadFilesToSelect(true, null, fileTable, fileSelector);
        initLengths(token);
        disableLoader();
    });
}
function loadFilesToSelect(isNewAnswer, answer, fileTable, fileSelector) {
    return __awaiter(this, void 0, void 0, function* () {
        const fileResponse = yield fetch(`${server}/services/get/files`);
        const json = yield fileResponse.json();
        const files = json.files;
        for (const file of files) {
            let alreadyAdded = false;
            if (!isNewAnswer && answer !== null) {
                answer.files.forEach(value => {
                    if (value.id == file.id)
                        alreadyAdded = true;
                    return;
                });
            }
            if (alreadyAdded) {
                continue;
            }
            const option = document.createElement('option');
            option.innerHTML = file.name;
            option.value = String(file.id);
            option.onclick = () => {
                answerFiles.push(file);
                fileTable.append(createFileTableRow(new MyFile(file.id, file.name, file.type)));
                option.remove();
                fileSelector.children[0].selected = true;
            };
            fileSelector.append(option);
        }
        fileSelector.onclick = () => {
            const selected = fileSelector.children[fileSelector.selectedIndex];
            for (const f of files) {
                if (selected.value == f.id) {
                    answerFiles.push(f);
                    fileTable.append(createFileTableRow(new MyFile(f.id, f.name, f.type)));
                    selected.remove();
                    fileSelector.children[0].selected = true;
                    break;
                }
            }
        };
    });
}
function createFileTableRow(file) {
    const row = document.createElement('tr');
    const titleContainer = document.createElement('td');
    const title = document.createElement('a');
    const removeContainer = document.createElement('td');
    const remove = document.createElement('button');
    const answerId = dataClass === null || dataClass === void 0 ? void 0 : dataClass.id;
    title.innerHTML = file.fileName;
    title.href = `../../../file?id=${file.id}`;
    title.target = '_blank';
    titleContainer.append(title);
    remove.innerHTML = 'Entfernen';
    remove.classList.add('button');
    remove.onclick = (e) => {
        e.preventDefault();
        if (confirm("Möchtest du wirklich diese Datei von dieser Antwort entfernen? Dies kann nicht rückgängig gemacht werden!")) {
            fetch(`${server}/services/adminTool/removeFileFromAnswer`, {
                method: 'post',
                body: `answerId=${answerId}&fileId=${file.id}`
            }).then((response) => {
                if (response.ok) {
                    answerFiles.forEach(((value, index) => {
                        if (value.id == file.id) {
                            answerFiles.splice(index, 1);
                            row.remove();
                        }
                    }));
                }
                else {
                    popup(false, "File konnte nicht von der Antwort enfternt werden!");
                }
            });
        }
    };
    removeContainer.append(remove);
    row.append(titleContainer, removeContainer);
    return row;
}
function updateAnswer() {
    return __awaiter(this, void 0, void 0, function* () {
        createTagByElementID("tag-container-input");
        putTagsInInput();
        let answerId = dataClass === null || dataClass === void 0 ? void 0 : dataClass.id;
        let inputTags = generateTagString();
        let inputTitle = document.getElementById("answerTitle").value;
        let inputAnswer = document.getElementById("answerText").value;
        let isHidden = document.getElementById("isHiddenInput").checked;
        let answerTypeValue = document.getElementById("answerTypesSelect").value;
        const fileParam = answerFiles.reduce((previousValue, currentValue) => {
            return previousValue + currentValue.id + ',';
        }, '');
        inputAnswer = encodeURIComponent(inputAnswer);
        inputTitle = encodeURIComponent(inputTitle);
        inputTags = encodeURIComponent(inputTags);
        answerTypeValue = encodeURIComponent(answerTypeValue);
        const encodedBody = `aTitle=${inputTitle}&aText=${inputAnswer}&tags=${inputTags}&isHidden=${isHidden}&answerTypeOrdinal=${+answerTypeValue}&files=${fileParam}`;
        let response = yield fetch(`${server}/services/adminTool/editAnswer`, {
            method: 'post',
            body: encodedBody
        });
        yield loadPage("answersDetails.html", "answersButton", true, -1, true);
    });
}
function addAnswer() {
    return __awaiter(this, void 0, void 0, function* () {
        createTagByElementID("tag-container-input");
        putTagsInInput();
        let inputTags = generateTagString();
        let inputTitle = document.getElementById("answerTitle").value;
        let inputAnswer = document.getElementById("answerText").value;
        let isHidden = document.getElementById("isHiddenInput").checked;
        let answerTypeValue = document.getElementById("answerTypesSelect").value;
        const fileParam = answerFiles.reduce((previousValue, currentValue) => {
            return previousValue + currentValue.id + ',';
        }, '');
        if (inputTitle !== '' && inputTitle !== null) {
            if (inputAnswer === '' || inputAnswer === null) {
                inputAnswer = inputTitle;
            }
            answers = [];
            inputAnswer = encodeURIComponent(inputAnswer);
            inputTitle = encodeURIComponent(inputTitle);
            inputTags = encodeURIComponent(inputTags);
            answerTypeValue = encodeURIComponent(answerTypeValue);
            const encodedBody = `aTitle=${inputTitle}&aText=${inputAnswer}&tags=${inputTags}&isHidden=${isHidden}&answerTypeOrdinal=${+answerTypeValue}&files=${fileParam}`;
            try {
                const response = yield fetch(`${server}/services/adminTool/addAnswer`, {
                    method: 'post',
                    body: encodedBody,
                });
                const json = yield response.json();
                yield loadPage("answersDetails.html", "answersButton", true, +json[0].id, true);
            }
            catch (e) {
                popup(false, 'Antwort konnte nicht hinzugefügt werden!');
            }
        }
        else {
            popup(false, "Der Titel der Antwort darf nicht leer sein!");
        }
    });
}
(_a = document.getElementById("answerTypesSelect")) === null || _a === void 0 ? void 0 : _a.addEventListener("change", function () {
    return __awaiter(this, void 0, void 0, function* () {
        let selectElement = document.getElementById("answerTypesSelect");
        let index = findSameId(answerTypes, +selectElement.value);
        if (index != null) {
            if (answerTypes[index].groupedTags) {
                yield getSpecialTags(+selectElement.value);
                clearAndFillTable(specialTags);
            }
            else {
                clearAndFillTable(tempTagData);
            }
            let currentAnswer = dataClass;
            if (currentAnswer != null) {
                checkIsHiddenStatus(answerTypes[index], (currentAnswer.answerType.id == answerTypes[index].id), currentAnswer.isHidden);
            }
            else {
                checkIsHiddenStatus(answerTypes[index]);
            }
        }
    });
});
function fillTableWithTags(myTags, canBeDeleted = true, page = null) {
    myTags = sortDetailAnswer(myTags);
    let value = '';
    for (let i = 0; i < myTags.length; i++) {
        value += myTags[i].toTagHTML(false, canBeDeleted, true).outerHTML;
    }
    checkPageExecute(() => {
        document.getElementById("allTagsTableBody").innerHTML = value;
    }, page);
}
function clearTagsTable() {
    let table = document.getElementById("tagContainerTable");
    if (table != null) {
        table.remove();
    }
}
function clearAndFillTable(myTags) {
    let fullSearchParams = window.location.search;
    const urlParams = new URLSearchParams(fullSearchParams);
    let answerID = +urlParams.get("objectID");
    let canBeDeleted = false;
    if (answerID > 0) {
        canBeDeleted = true;
    }
    clearTagsTable();
    initialiseTagsTable(canBeDeleted);
    fillTableWithTags(myTags, canBeDeleted);
}
function removeTagFromAnswerBackend(tagId) {
    return __awaiter(this, void 0, void 0, function* () {
        if (confirm("Willst du den Tag wirklich von dieser Antwort entfernen?\nDies kann nicht rückgängig gemacht werden!")) {
            let answer = dataClass;
            try {
                fetch(`${server}/services/adminTool/removeTagFromAnswer`, {
                    method: `post`,
                    body: `answerId=${answer.id}&tagId=${tagId}`
                }).then((response) => {
                    if (response.ok) {
                        removeTagFromAnswerFrontend(answer, tagId);
                    }
                    else {
                        popup(false, "Tag konnte nicht von der Antwort enfternt werden!");
                    }
                });
            }
            catch (e) {
                popup(false, 'Tag konnte nicht von der Antwort entfernt werden!');
            }
        }
    });
}
function deleteAnswer() {
    return __awaiter(this, void 0, void 0, function* () {
        if (confirm("Willst du wirklich diese Antwort löschen?\nDies kann nicht rückgängig gemacht werden!")) {
            let answer = dataClass;
            try {
                let response = yield fetch(`${server}/services/adminTool/deleteAnswer`, {
                    method: `post`,
                    body: `answerID=${answer.id}`
                });
            }
            catch (e) {
                popup(false, 'Die Antwort konnte leider nicht gelöscht werden!');
            }
            yield loadPage("answers.html", "", false, -1, true);
        }
    });
}
function removeTagFromAnswerFrontend(answer, tagId) {
    answer.removeTagById(tagId);
    let tagsTableBody = document.getElementById("allTagsTableBody");
    tagsTableBody.removeChild(document.getElementById(`tagId-${tagId}`));
}
function addTagToAnswer(answer, tag) {
    answer.tags.push(tag);
    let tagsTableBody = document.getElementById("allTagsTableBody");
    tagsTableBody.append(tag.toTagHTML(false, true, true));
}
function loadAnswer(returnAnswer = false, answerIDParam = null) {
    return __awaiter(this, void 0, void 0, function* () {
        answers = [];
        tempTagData = [];
        let fullSearchParams = window.location.search;
        const urlParams = new URLSearchParams(fullSearchParams);
        const page = pageCheck;
        let answerID = +urlParams.get("objectID");
        if (answerID == null || isNaN(answerID) || answerID <= 0) {
            yield initialiseAddAnswerPage();
        }
        else {
            try {
                let response = yield fetch(`${server}/services/get/singleAnswer?answerID=${answerID}`);
                let loadedAnswer = yield response.json();
                let answerTags = [];
                tags = [];
                for (let currentTag of loadedAnswer[0].tags) {
                    let id = +currentTag.tag.id;
                    let tagName = currentTag.tag.tag;
                    let tagUpvotes = currentTag.upvotes;
                    let tagDownvotes = currentTag.downvotes;
                    let tagUsage = currentTag.usage;
                    let tag = new MyTag(id, tagName, tagUpvotes, tagDownvotes, tagUsage, -1, []);
                    tags.push(tag);
                    answerTags.push(tag);
                }
                let answerFiles = [];
                files = [];
                for (let currentFile of loadedAnswer[0].files) {
                    let id = currentFile.id;
                    let name = currentFile.name;
                    let type = currentFile.type;
                    let file = new MyFile(id, name, type);
                    files.push(file);
                    answerFiles.push(file);
                }
                let newAnswerType = new AnswerType(loadedAnswer[0].answerType[0].value, loadedAnswer[0].answerType[0].name, loadedAnswer[0].answerType[0].groupedTags, loadedAnswer[0].answerType[0].hidden, loadedAnswer[0].answerType[0].forceHidden);
                let answer = new MyAnswer(loadedAnswer[0].id, loadedAnswer[0].title, loadedAnswer[0].answer, loadedAnswer[0].views, newAnswerType, loadedAnswer[0].isHidden, answerTags, answerFiles, loadedAnswer[0].averageUsefulness);
                dataClass = answer;
                if (answer.isHidden) {
                    $("#topNavForEditAnswer").remove();
                }
                if (returnAnswer) {
                    return answer;
                }
                else {
                    yield initialiseAnswerEditPage(answer, page);
                    return null;
                }
            }
            catch (e) {
                popup(false, 'Die Antwort konnte leider nicht geladen werden!');
                console.error(e);
            }
        }
        return null;
    });
}
function initialiseEditForm() {
    var _a;
    (_a = document.getElementById("answerForm")) === null || _a === void 0 ? void 0 : _a.setAttribute("onsubmit", "updateAnswer()");
}
function initialseAddForm() {
    var _a;
    (_a = document.getElementById("answerForm")) === null || _a === void 0 ? void 0 : _a.setAttribute("onsubmit", "addAnswer()");
}
function initialiseTagsTable(canBeDeleted = true) {
    var _a;
    let title = document.createElement("h2");
    title.innerText = "Bereits vorhandene Tags";
    let table = document.createElement("table");
    table.id = "allTagsTable";
    let thead = document.createElement("thead");
    let tr = document.createElement("tr");
    let th1 = document.createElement("th");
    th1.innerText = "Tag";
    tr.appendChild(th1);
    let th2 = document.createElement("th");
    th2.innerText = "Verwendung";
    tr.appendChild(th2);
    let th3 = document.createElement("th");
    th3.innerText = "Upvotes";
    tr.appendChild(th3);
    let th4 = document.createElement("th");
    th4.innerText = "Downvotes";
    tr.appendChild(th4);
    let th5 = document.createElement("th");
    th5.innerText = "Bewertung";
    tr.appendChild(th5);
    if (canBeDeleted) {
        let th6 = document.createElement("th");
        th6.innerText = "Tag löschen";
        tr.appendChild(th6);
    }
    thead.appendChild(tr);
    let tbody = document.createElement("tbody");
    tbody.id = "allTagsTableBody";
    table.append(thead, tbody);
    let container = document.createElement("div");
    container.id = "tagContainerTable";
    container.appendChild(title);
    container.appendChild(table);
    (_a = document.getElementById("allTagsTableContent")) === null || _a === void 0 ? void 0 : _a.appendChild(container);
}
function sortDetailAnswer(tags) {
    let unsortedTags = Array.prototype.slice.call(tags);
    tags = unsortedTags.sort(function (tag1, tag2) {
        if (tag1.usefulness != tag2.usefulness) {
            return (tag1.usefulness > tag2.usefulness) ? -1 : 1;
        }
        if (tag1.tag != tag2.tag) {
            return (tag1.tag < tag2.tag) ? -1 : 1;
        }
        return (tag1.id > tag2.id) ? -1 : 1;
    });
    return tags;
}
