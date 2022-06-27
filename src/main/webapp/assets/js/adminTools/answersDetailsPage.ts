async function initLengths(page: string) {
    const answerTitleInput = document.getElementById("answerTitle") as HTMLInputElement;
    const answerTextInput = document.getElementById("answerText") as HTMLTextAreaElement;
    const tagInput = document.getElementById('tag-container-input') as HTMLInputElement;

    await initMaxLengths(page, [{elem: answerTitleInput, name: textInputs.ANSWER_TITLE}, {
        elem: answerTextInput,
        name: textInputs.ANSWER_TEXT
    }, {elem: tagInput, name: textInputs.TAG}])
}

async function initialiseAnswerEditPage(answer: MyAnswer, page: string) {
    answerFiles = [];
    document.title = adminToolPageTitleName + ' - Antwort bearbeiten';
    const token = pageCheck

    const answerTitleInput = document.getElementById("answerTitle") as HTMLInputElement;
    const answerTextInput = document.getElementById("answerText") as HTMLTextAreaElement;
    const answerHiddenInput = document.getElementById("isHiddenInput") as HTMLInputElement;
    const fileTable = document.getElementById('files') as HTMLTableSectionElement;
    const fileSelector = document.getElementById('fileSelector') as HTMLSelectElement;

    if (answer != null) {
        initialiseTagsTable();

        let submitButton = document.getElementById("answerFormSubmitButton") as HTMLButtonElement;
        submitButton.value = "Antwort aktualisieren";
        submitButton.setAttribute("onClick", "updateAnswer()");

        answerTitleInput.value = answer.title;

        answerTextInput.value = answer.answerPageAnswer;

        // Add files to table
        for (let file of answer.files) {
            answerFiles.push(file);
            fileTable.append(createFileTableRow(file))
        }

        await setAllAnswerTypes(answer.answerType.id);
        answerHiddenInput.checked = answer.isHidden;
        await loadFilesToSelect(false, answer, fileTable, fileSelector);

        let myTags: MyTag[] = answer.tags;
        if (!answer.answerType.groupedTags) {
            tempTagData = myTags;
        }
        fillTableWithTags(myTags, true, page);
    } else {
        // TODO: error handling
    }

    await initLengths(token);

    disableLoader();
}

async function initialiseAddAnswerPage() {
    $("#topNavForEditAnswer").remove();

    const token = pageCheck;

    answerFiles = [];
    document.title = adminToolPageTitleName + ' - Antwort hinzufügen';
    let title = document.getElementById("title") as HTMLHeadElement;
    title.innerHTML = "Antwort hinzufügen";

    let deleteAnswerButton = document.getElementById("deleteAnswerButton");
    deleteAnswerButton?.parentNode?.removeChild(deleteAnswerButton);

    let submitButton = document.getElementById("answerFormSubmitButton") as HTMLButtonElement;
    submitButton.value = "Neue Antwort hinzufügen";
    submitButton.setAttribute("onClick", "addAnswer()");

    let div = document.getElementById('answersDetailsContent');
    let error = document.createElement('p');
    error.id = 'errorAnswers';
    error.style.color = 'red';
    div?.appendChild(error);

    await setAllAnswerTypes();

    const fileTable = document.getElementById('files') as HTMLTableSectionElement;
    const fileSelector = document.getElementById('fileSelector') as HTMLSelectElement;
    await loadFilesToSelect(true, null, fileTable, fileSelector);

    initLengths(token);

    disableLoader();
}

async function loadFilesToSelect(isNewAnswer: boolean, answer: MyAnswer | null, fileTable: HTMLTableSectionElement, fileSelector: HTMLSelectElement) {
    const fileResponse = await fetch(`${server}/services/get/files`);
    const json = await fileResponse.json();
    const files = json.files;

    for (const file of files) {
        // Add to select
        let alreadyAdded: boolean = false;
        if (!isNewAnswer && answer !== null) {
            answer.files.forEach(value => {
                if (value.id == file.id) alreadyAdded = true;
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
            (fileSelector.children[0] as HTMLOptionElement).selected = true;
        }
        fileSelector.append(option);
    }

    fileSelector.onclick = () => {
        const selected = fileSelector.children[fileSelector.selectedIndex] as HTMLOptionElement;
        for (const f of files) {
            if (selected.value == f.id) {
                answerFiles.push(f);
                fileTable.append(createFileTableRow(new MyFile(f.id, f.name, f.type)));
                selected.remove();
                (fileSelector.children[0] as HTMLOptionElement).selected = true;
                break;
            }
        }
    }
}

function createFileTableRow(file: MyFile): HTMLTableRowElement {
    const row = document.createElement('tr') as HTMLTableRowElement;
    const titleContainer = document.createElement('td') as HTMLTableCellElement;
    const title = document.createElement('a') as HTMLAnchorElement;
    const removeContainer = document.createElement('td') as HTMLTableCellElement;
    const remove = document.createElement('button') as HTMLButtonElement;
    const answerId: number = dataClass?.id as number;

    // Title with link to file
    title.innerHTML = file.fileName;
    title.href = `../../../file?id=${file.id}`;
    title.target = '_blank';
    titleContainer.append(title);

    // Button to remove File from answer
    remove.innerHTML = 'Entfernen';
    remove.classList.add('button');

    remove.onclick = (e) => {
        e.preventDefault();
        if (confirm("Möchtest du wirklich diese Datei von dieser Antwort entfernen? Dies kann nicht rückgängig gemacht werden!")) {
            fetch(`${server}/services/adminTool/removeFileFromAnswer`, {
                method: 'post',
                body: `answerId=${answerId}&fileId=${file.id}`
            }).then((response: Response) => {
                if (response.ok) {
                    answerFiles.forEach(((value, index) => {
                        if (value.id == file.id) {
                            answerFiles.splice(index, 1);
                            row.remove();
                        }
                    }));
                } else {
                    popup(false, "File konnte nicht von der Antwort enfternt werden!");
                }
            })
        }
    }
    removeContainer.append(remove);

    row.append(titleContainer, removeContainer);

    return row;
}

async function updateAnswer() {
    createTagByElementID("tag-container-input");
    putTagsInInput();

    let answerId: number = dataClass?.id as number;
    let inputTags: string = generateTagString();
    let inputTitle: string = (document.getElementById("answerTitle") as HTMLInputElement).value;
    let inputAnswer: string = (document.getElementById("answerText") as HTMLTextAreaElement).value;
    let isHidden: boolean = (document.getElementById("isHiddenInput") as HTMLInputElement).checked;
    let answerTypeValue: string = (document.getElementById("answerTypesSelect") as HTMLSelectElement).value;

    const fileParam = answerFiles.reduce((previousValue, currentValue) => {
        return previousValue + currentValue.id + ',';
    }, '');

    inputAnswer = encodeURIComponent(inputAnswer);
    inputTitle = encodeURIComponent(inputTitle);
    inputTags = encodeURIComponent(inputTags);
    answerTypeValue = encodeURIComponent(answerTypeValue);

    const encodedBody = `aTitle=${inputTitle}&aText=${inputAnswer}&tags=${inputTags}&isHidden=${isHidden}&answerTypeOrdinal=${+answerTypeValue}&files=${fileParam}`;

    let response = await fetch(`${server}/services/adminTool/editAnswer`, {
        method: 'post',
        body: encodedBody
    });

    await loadPage("answersDetails.html", "answersButton", true, -1, true);
}

async function addAnswer() {
    createTagByElementID("tag-container-input");
    putTagsInInput();

    let inputTags: string = generateTagString();
    let inputTitle: string = (document.getElementById("answerTitle") as HTMLInputElement).value;
    let inputAnswer: string = (document.getElementById("answerText") as HTMLTextAreaElement).value;
    let isHidden: boolean = (document.getElementById("isHiddenInput") as HTMLInputElement).checked;
    let answerTypeValue: string = (document.getElementById("answerTypesSelect") as HTMLSelectElement).value;

    const fileParam = answerFiles.reduce((previousValue, currentValue) => {
        return previousValue + currentValue.id + ',';
    }, '');

    if (inputTitle !== '' && inputTitle !== null) {
        if (inputAnswer === '' || inputAnswer === null) {
            inputAnswer = inputTitle;
        }
        answers = [];

        // encodeUriComponent all of them
        inputAnswer = encodeURIComponent(inputAnswer);
        inputTitle = encodeURIComponent(inputTitle);
        inputTags = encodeURIComponent(inputTags);
        answerTypeValue = encodeURIComponent(answerTypeValue);

        const encodedBody = `aTitle=${inputTitle}&aText=${inputAnswer}&tags=${inputTags}&isHidden=${isHidden}&answerTypeOrdinal=${+answerTypeValue}&files=${fileParam}`;

        try {
            const response = await fetch(`${server}/services/adminTool/addAnswer`, {
                method: 'post',
                body: encodedBody,
            });
            const json = await response.json();
            await loadPage("answersDetails.html", "answersButton", true, +json[0].id, true);
        } catch (e) {
            popup(false, 'Antwort konnte nicht hinzugefügt werden!');
        }
    } else {
        popup(false, "Der Titel der Antwort darf nicht leer sein!");
    }
}

/**
 * add event listener to the select element
 */
document.getElementById("answerTypesSelect")?.addEventListener("change", async function () {
    let selectElement = document.getElementById("answerTypesSelect") as HTMLSelectElement;

    let index = findSameId(answerTypes, +selectElement.value)
    if (index != null) {
        if (answerTypes[index].groupedTags) {
            await getSpecialTags(+selectElement.value);
            clearAndFillTable(specialTags);
        } else {
            clearAndFillTable(tempTagData);
        }
        let currentAnswer = dataClass as MyAnswer;
        if (currentAnswer != null) {
            checkIsHiddenStatus(answerTypes[index], (currentAnswer.answerType.id == answerTypes[index].id), currentAnswer.isHidden);
        } else {
            checkIsHiddenStatus(answerTypes[index]);
        }

    }
});

function fillTableWithTags(myTags: MyTag[], canBeDeleted: boolean = true, page: string | null = null) {
    myTags = sortDetailAnswer(myTags);
    let value: string = '';
    for (let i = 0; i < myTags.length; i++) {
        value += myTags[i].toTagHTML(false, canBeDeleted, true).outerHTML;
    }

    checkPageExecute(() => {
        document.getElementById("allTagsTableBody")!.innerHTML = value;
    }, page)
}

function clearTagsTable() {
    let table = document.getElementById("tagContainerTable") as HTMLElement;
    if (table != null) {
        table.remove();
    }
}

function clearAndFillTable(myTags: MyTag[]) {
    let fullSearchParams: string = window.location.search;
    const urlParams: URLSearchParams = new URLSearchParams(fullSearchParams);
    let answerID: number = +urlParams.get("objectID")!;
    let canBeDeleted: boolean = false;
    if (answerID > 0) {
        canBeDeleted = true;
    }

    clearTagsTable();
    initialiseTagsTable(canBeDeleted);
    fillTableWithTags(myTags, canBeDeleted);
}

/**
 * delete the given tag in backend
 * @param tagId the id of the tag we want to remove
 */
async function removeTagFromAnswerBackend(tagId: number) {
    if (confirm("Willst du den Tag wirklich von dieser Antwort entfernen?\nDies kann nicht rückgängig gemacht werden!")) {
        let answer: MyAnswer = dataClass as MyAnswer;
        try {
            fetch(`${server}/services/adminTool/removeTagFromAnswer`, {
                method: `post`,
                body: `answerId=${answer.id}&tagId=${tagId}`
            }).then((response: Response) => {
                if (response.ok) {
                    removeTagFromAnswerFrontend(answer, tagId);
                } else {
                    popup(false, "Tag konnte nicht von der Antwort enfternt werden!");
                }
            })
        } catch (e) {
            popup(false, 'Tag konnte nicht von der Antwort entfernt werden!');
        }
    }

}

/**
 * function for deleting answer
 */
async function deleteAnswer() {
    if (confirm("Willst du wirklich diese Antwort löschen?\nDies kann nicht rückgängig gemacht werden!")) {
        let answer: MyAnswer = dataClass as MyAnswer;

        try {
            let response = await fetch(`${server}/services/adminTool/deleteAnswer`, {
                method: `post`,
                body: `answerID=${answer.id}`
            });
        } catch (e) {
            popup(false, 'Die Antwort konnte leider nicht gelöscht werden!');
        }
        await loadPage("answers.html", "", false, -1, true);
    }
}

/**
 * remove the given tag from the given answer in frontend
 * @param answer the answer where we want to remove the tag
 * @param tagId the tag we want to remove from the answer
 */
function removeTagFromAnswerFrontend(answer: MyAnswer, tagId: number) {
    answer.removeTagById(tagId);
    let tagsTableBody = document.getElementById("allTagsTableBody") as HTMLTableElement;
    tagsTableBody.removeChild(document.getElementById(`tagId-${tagId}`)!);
}

/**
 * function to add tag to the answer in the frontend
 */
function addTagToAnswer(answer: MyAnswer, tag: MyTag) {
    answer.tags.push(tag);
    let tagsTableBody = document.getElementById("allTagsTableBody") as HTMLTableElement;
    tagsTableBody.append(tag.toTagHTML(false, true, true));
}

/**
 * loads the answer with all its tags, files and answerType
 */
async function loadAnswer(returnAnswer: boolean = false, answerIDParam: number | null = null): Promise<MyAnswer | null> {
    answers = [];
    tempTagData = [];
    let fullSearchParams: string = window.location.search;
    const urlParams: URLSearchParams = new URLSearchParams(fullSearchParams);
    const page = pageCheck;
    let answerID: number = +urlParams.get("objectID")!;

    if (answerID == null || isNaN(answerID) || answerID <= 0) {
        await initialiseAddAnswerPage();
    } else {
        try {
            let response = await fetch(`${server}/services/get/singleAnswer?answerID=${answerID}`);
            let loadedAnswer = await response.json();

            let answerTags: MyTag[] = [];
            tags = [];
            for (let currentTag of loadedAnswer[0].tags) {
                let id: number = +currentTag.tag.id;
                let tagName: string = currentTag.tag.tag;
                let tagUpvotes: number = currentTag.upvotes;
                let tagDownvotes: number = currentTag.downvotes;
                let tagUsage: number = currentTag.usage;

                let tag: MyTag = new MyTag(id, tagName, tagUpvotes, tagDownvotes, tagUsage, -1, []);
                tags.push(tag);
                answerTags.push(tag);
            }

            let answerFiles: MyFile[] = [];
            files = [];
            for (let currentFile of loadedAnswer[0].files) {
                let id: number = currentFile.id;
                let name: string = currentFile.name;
                let type: string = currentFile.type;
                let file = new MyFile(id, name, type);
                files.push(file);
                answerFiles.push(file);
            }

            let newAnswerType: AnswerType = new AnswerType(loadedAnswer[0].answerType[0].value, loadedAnswer[0].answerType[0].name, loadedAnswer[0].answerType[0].groupedTags, loadedAnswer[0].answerType[0].hidden, loadedAnswer[0].answerType[0].forceHidden);
            let answer: MyAnswer = new MyAnswer(loadedAnswer[0].id, loadedAnswer[0].title, loadedAnswer[0].answer, loadedAnswer[0].views, newAnswerType, loadedAnswer[0].isHidden, answerTags, answerFiles, loadedAnswer[0].averageUsefulness);
            dataClass = answer;

            if (answer.isHidden) {
                $("#topNavForEditAnswer").remove();
            }

            if (returnAnswer) {
                return answer;
            } else {
                await initialiseAnswerEditPage(answer, page);
                return null;
            }
        } catch (e) {
            popup(false, 'Die Antwort konnte leider nicht geladen werden!')
            console.error(e);
        }
    }
    return null;
}

/**
 * init the edit form
 */
function initialiseEditForm() {
    document.getElementById("answerForm")?.setAttribute("onsubmit", "updateAnswer()");
}

/**
 * init the add form
 */
function initialseAddForm() {
    document.getElementById("answerForm")?.setAttribute("onsubmit", "addAnswer()");
}

function initialiseTagsTable(canBeDeleted: boolean = true) {
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

    document.getElementById("allTagsTableContent")?.appendChild(container);
}

function sortDetailAnswer(tags: MyTag[]) {
    let unsortedTags: MyTag[] = Array.prototype.slice.call(tags);
    tags = unsortedTags.sort(function (tag1: MyTag, tag2: MyTag) {
        if (tag1.usefulness != tag2.usefulness) {
            return (tag1.usefulness > tag2.usefulness) ? -1 : 1; // If answer1 is more useful, return -1, if answer2 is more useful return 1
        } // If both have the same usefulness (would return 0)
        if (tag1.tag != tag2.tag) {
            return (tag1.tag < tag2.tag) ? -1 : 1; // Sort for tag Name
        } // If both have the same text (shouldnt be possible, aber sicher ist sicher)

        return (tag1.id > tag2.id) ? -1 : 1; // sort for tag id
    })
    return tags;
}