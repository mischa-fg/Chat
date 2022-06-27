document.title = adminToolPageTitleName + ' - Fragen';

async function loadStuff() {

    await getQuestionSuggestions();
    await loadQuestions();
}

async function getQuestionSuggestions() {
    const page = pageCheck;
    try {
        let response = await fetch(`${server}/services/get/topQuestionSuggestions?amountQuestions=${amountQuestions}`);
        let json = await response.json();
        checkPageExecute(() => saveQuestionSuggestions(json), page);
    } catch (e) {

        checkPageExecute(() => popup(false, 'Diese Seite konnte leider nicht geladen werden!'), page);
    }
}

async function loadQuestions() {
    const page = pageCheck;
    try {
        let response = await fetch(`${server}/services/get/allDefaultQuestion?amountQuestions=${amountQuestions}`);
        let json = await response.json();
        checkPageExecute(() => saveDefaultQuestions(json), page);
        disableLoader();
    } catch (e) {
        checkPageExecute(() => popup(false, 'Diese Seite konnte leider nicht geladen werden!'), page);
    }
}

function saveQuestionSuggestions(retrievedQuestionSuggestions: any) {
    if (retrievedQuestionSuggestions != null) {
        answeredQuestions = [];
        for (let question of retrievedQuestionSuggestions) {
            let questionID = question.id;
            let questionQuestion = question.question;
            let upvotes = question.upvotes;
            let downvotes = question.downvotes;
            let views = question.views;

            let questionSuggestion = new MyAnsweredQuestion(questionID, questionQuestion, upvotes, downvotes, views);
            answeredQuestions.push(questionSuggestion);
        }
        sortAnsweredQuestions("allQuestionSuggestionsBody");
    }
}

function saveDefaultQuestions(retrievedDefaultQuestion: any) {
    if (retrievedDefaultQuestion != null) {
        defaultQuestions = [];
        for (let question in retrievedDefaultQuestion) {
            let questionId: number = retrievedDefaultQuestion[question].id;
            let questionQuestion: string = retrievedDefaultQuestion[question].question;
            let defaultQuestionList: DefaultQuestions = new DefaultQuestions(questionId, questionQuestion);
            defaultQuestions.push(defaultQuestionList);
        }
        let canBeDeleted: boolean = false;
        if (defaultQuestions.length > 3) {
            canBeDeleted = true;
        }

        sortDefaultQuestion(canBeDeleted);
    } else {
        createDefaultQuestionString(null);
    }
}

function createDefaultQuestionString(defaultQuestions: DefaultQuestions[] | null, canBeDeleted: boolean = false) {
    let value: string;
    if (defaultQuestions != null && defaultQuestions.length > 0) {
        value = '';
        for (let i = 0; i < defaultQuestions.length; i++) {
            value += defaultQuestions[i].toDefaultQuestionHTML(canBeDeleted).outerHTML;
        }
    } else {
        value = "Couldn't load default Question!";
    }
    $(".allDefaultTableBody").empty();
    document.getElementById("allDefaultTableBody")!.innerHTML = value;
}

/**
 * @author Sarah Ambi
 * @since 27.08.2021
 * @param id
 * @param element
 */
async function deleteQuestion(id: number, element: HTMLButtonElement) {
    if (defaultQuestions.length > 3) {
        if (confirm("Möchtest du diese Frage wirklich löschen? Dies kann nicht rückgängig gemacht werden!")) {
            const page = pageCheck;
            try {
                let response = await fetch(`${server}/services/adminTool/deleteQuestion`, {
                    method: 'post',
                    body: `questionId=${id}`
                });

                if (response.ok) {
                    const ids = document.getElementsByClassName('id-hidden');
                    for (const cid of ids) {
                        if (cid.innerHTML === String(id)) {
                            cid.parentElement?.remove();
                            const questionID = findSameId(defaultQuestions, id);
                            if (questionID != null) {
                                defaultQuestions.splice(questionID, 1);
                            }
                        }
                    }
                } else {
                    popup(false, "Frage konnnte nicht gelöscht werden\n")
                }
            } catch (e) {

                checkPageExecute(() => popup(false, 'Frage konnnte nicht gelöscht werden.'), page);
            }
        }
    } else {
        cantDeleteQuestion();
    }
}

/**
 * @author Tim Irmler
 * @since 27.08.2021
 */
function cantDeleteQuestion() {
    popup(false, 'Es müssen mindestens 3 Fragen vorhanden sein!');
}