async function loadTag() {
    const page = pageCheck;

    tags = [];
    let fullSearchParams: string = window.location.search;
    const urlParams: URLSearchParams = new URLSearchParams(fullSearchParams);
    let tagID: number = +urlParams.get("objectID")!;

    if (tagID == null || isNaN(tagID) || tagID <= 0) {
        initialiseAddTagPage();
    } else {
        await initialiseTagEditPage(tagID);
    }

    await initMaxLengths(page, [{elem: document.getElementById("tagName") as HTMLInputElement, name: textInputs.TAG}])
}

async function updateTag() {
    let tagId = document.getElementsByClassName('id-hidden');
    let tagName = (document.getElementById("tagName") as HTMLInputElement).value;
    const page = pageCheck
    if (tagName != '' && tagName != null) {
        try {
            let response = await fetch(server + '/services/adminTool/editTag', {
                method: 'post',
                body: `tagID=${tagId.item(0)!.id}&tagContent=${encodeURIComponent(tagName)}`,
            });
            if (response.ok) {
                await loadPage('tagsDetails.html', 'tagButton', true, -1, true);
            } else {
                popup(false, 'Dieser Tag konnte nicht aktualisiert werden!');
            }
        } catch (e) {
            checkPageExecute(() => popup(false, 'Dieser Tag konnte aus unerwarteten Gründen nicht aktualisiert werden!'), 'tagsDetails')

        }
    } else {
        document.getElementById('errorTag')!.innerHTML = 'Es wurde kein Tag erkannt';
    }
}

async function deleteTag() {
    let tagId = document.getElementsByClassName('id-hidden');
    let tagName = (document.getElementById("tagName") as HTMLInputElement).value;
    let amountAnswers: number = +(document.getElementById("amountAnswers") as HTMLElement).innerHTML;
    const page = pageCheck;
    if (tagId != null) {
        let confirmMessage: string = `Willst du den Tag "${tagName}" wirklich löschen?`;
        if (amountAnswers > 0) {
            confirmMessage += `\nEr wird von ${amountAnswers} Antworten entfernt`
        }
        let success = true;
        if (confirm(confirmMessage)) {
            try {
                let response = await fetch(`${server}/services/adminTool/deleteTag`, {
                    method: 'post',
                    body: `tagID=${tagId.item(0)!.id}`
                });
                if (response.ok) {
                    await loadPage("tags.html", "tagButton", false, null, true);
                } else {
                    checkPageExecute(() => {
                        popup(false, 'Dieser Tag konnte nicht gelöscht werden');
                    }, 'tagsDetails')
                    success = false;
                }
            } catch (e) {
                checkPageExecute(() => {
                    popup(false, 'Dieser Tag konnte aus unerwarteten Gründen nicht gelöscht werden!');
                }, 'tagsDetails')
                success = false;
            }
        }
        if (!success) popup(true, 'Der Tag wurde erfolgreich gelöscht!')
    } else {
        checkPageExecute(() => {
            popup(false, 'Der Tag wurde nicht nicht gelöscht!');
        }, 'tagsDetails')
    }

}

//Add Tag page
function initialiseAddTagPage() {
    document.title = adminToolPageTitleName + ' - Tag hinzufügen';
    let divInput = document.getElementById('input-group');
    let createButton = document.createElement('button');
    createButton.setAttribute('id', 'tagFormSubmitButton');
    createButton.setAttribute('type', 'button');
    createButton.classList.add('form-control');
    createButton.name = 'createTag';
    createButton.innerHTML = 'Hinzufügen';
    createButton.setAttribute('onClick', 'addTag()');
    divInput?.appendChild(createButton);
    document.getElementById("tagName")?.addEventListener("keyup", async function (event) {
        if (event.keyCode === 13) {
            await addTag();
        }
    });
    disableLoader();
}

async function addTag() {
    // TODO: handle errors
    let inputTitle: string = (document.getElementById("tagName") as HTMLInputElement).value;

    tags = [];
    if (inputTitle != '' && inputTitle != null) {
        try {
            let response = await fetch(`${server}/services/adminTool/addTag`, {
                method: 'post',
                body: `tag=${encodeURIComponent(inputTitle)}`
            });
            if (!response.ok) {
                popup(false, 'Dieser Tag existiert bereits');
            }
            let json = await response.json();
            await loadPage("tagsDetails.html", "tagButton", true, +json.tag.id, true);
        } catch (e) {
            popup(false, 'Dieser Tag konnte aus unerwarteten Gründen nicht hinzugefügt werden!');
        }
    } else {
        popup(false, 'Tag konnte nicht hinzugefügt werden!');
    }
}

// Edit Tage Page
async function initialiseTagEditPage(tagId: number) {
    const page = pageCheck;

    document.title = adminToolPageTitleName + ' - Tag bearbeiten';

    if (tagId == null || isNaN(tagId)) {
        tagId = dataClass!.id;
    }

    try {
        const response = await fetch(`${server}/services/get/singleTag?tagID=${tagId}`);

        const retrievedTag = await response.json();

        let myAnswers: SingleTagAnswer[] = [];
        singleTagAnswers = [];
        for (let currentAnswer of retrievedTag.answers) {
            let currentAnswerID: number = +currentAnswer.id;
            let title: string = currentAnswer.title;
            let answerViews: number = +currentAnswer.views;
            let answerHidden: boolean = currentAnswer.isHidden;
            let answerType: AnswerType = currentAnswer.answerType[0];
            let answerUsefulness: number = currentAnswer.averageUsefulness;
            let answerUpvotes: number = currentAnswer.upvotes;
            let answerDownvotes: number = currentAnswer.downvotes;

            let answer: SingleTagAnswer = new SingleTagAnswer(currentAnswerID, tagId, title, answerViews, answerUpvotes, answerDownvotes, answerHidden, answerUsefulness, answerType);
            singleTagAnswers.push(answer);
            myAnswers.push(answer);
        }

        let tag: MyTag = new MyTag(retrievedTag.tag.id, retrievedTag.tag.tag, retrievedTag.upvotes, retrievedTag.downvotes, retrievedTag.usage, retrievedTag.amountAnswers, myAnswers)
        dataClass = tag;

        let tagDetailsDetails = document.getElementById('tagDetailsDetails');
        let divName = document.getElementById('tagName');
        divName?.setAttribute('value', tag.tag);
        let idhidden = document.createElement('div');
        idhidden.id = String(tag.id);
        idhidden.classList.add('id-hidden');
        tagDetailsDetails?.appendChild(idhidden);

        let amountAnswersHidden = document.createElement('span');
        amountAnswersHidden.id = "amountAnswers";
        amountAnswersHidden.classList.add('hidden');
        amountAnswersHidden.innerHTML = String(tag.amountAnswers);
        tagDetailsDetails?.appendChild(amountAnswersHidden);

        document.getElementById("tagName")?.addEventListener("keyup", function (event) {
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
        divInput?.append(deleteButton, updateButton);
        let divContainer = document.getElementById('allAnswersContainer');
        let connectedAnswers = document.createElement('h2');
        connectedAnswers.id = "connectedAnswers";
        connectedAnswers.innerHTML = `${tag.amountAnswers} verbundene Antworten`;
        divContainer?.append(connectedAnswers);

        initialiseAnswerTable(myAnswers);
        disableLoader();
    } catch (e) {
        checkPageExecute(() => {
            popup(false, 'Diese Seite konnte leider nicht geladen werden!');
        }, 'tagsDetails')
    }
}

function removeFromTagAnswerBackend(answerId: number, tagId: number) {
    let thisTag = dataClass as MyTag;
    let indexOfAnswer = findSameId(thisTag.answers, answerId);
    let answerTypeOfAnswerToRemove: AnswerType | null = null;
    if (indexOfAnswer != null) {
        let answerToRemove: AnswerParent = thisTag.answers[indexOfAnswer];
        answerTypeOfAnswerToRemove = answerToRemove.answerType;

        if (answerTypeOfAnswerToRemove != null) {
            let warning: string = "Willst du den Tag wirklich von dieser Antwort entfernen?";
            if (answerTypeOfAnswerToRemove.groupedTags) {
                warning += "\nDer Tag wird von allen Antworten mit dem Typen '" + answerTypeOfAnswerToRemove.name + "' entfernt!";
            }
            if (confirm(warning)) {
                fetch(server + '/services/adminTool/removeTagFromAnswer', {
                    method: 'post',
                    body: `answerId=${answerId}&tagId=${tagId}`,
                }).then((response: Response) => {
                    if (response.ok) {
                        let tag = dataClass as MyTag;
                        removeFromTagAnswerFrontend(tag, answerId, answerTypeOfAnswerToRemove);
                    } else {
                        popup(false, "Tag konnte nicht von der Antwort enfternt werden!");
                    }
                })
            }
        } else {
            popup(false, "Kann den Tag nicht von der Antwort entfernen da der Typ der Antwort leer ist!");
        }
    } else {
        popup(false, "Kann den Tag nicht von der Antwort entfernen da die Antwort leer ist!");
    }
}

function removeFromTagAnswerFrontend(tag: MyTag, answerId: number, answerTypeOfAnswerToRemove: any | null) {
    if (answerTypeOfAnswerToRemove != null && answerTypeOfAnswerToRemove.groupedTags) {
        singleTagAnswers.forEach((currentAnswer) => {
            if (currentAnswer.answerType.value === answerTypeOfAnswerToRemove.value) {
                actualRemoveOfTagFromAnswer(tag, currentAnswer.id);
            }
        });
    } else {
        actualRemoveOfTagFromAnswer(tag, answerId);
    }
}

function actualRemoveOfTagFromAnswer(tag: MyTag, answerId: number) {
    tag.removeAnswerById(answerId);
    let tagsTableBody = document.getElementById("allAnswers") as HTMLTableElement;
    tagsTableBody.removeChild(document.getElementById(`singleTagAnswerID-${answerId}`)!);

    let connectedAnswersElement = document.getElementById("connectedAnswers") as HTMLElement;
    tag.amountAnswers = tag.amountAnswers - 1;
    connectedAnswersElement.innerHTML = `${tag.amountAnswers} verbundene Antworten`;
}