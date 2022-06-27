/**
 * @author Sarah
 */
function initialiseAddDQuestionPage() {
    document.title = adminToolPageTitleName + ' - Fragen hinzufügen';
    let submitButton = document.getElementById("dQuestionFormSubmitButton") as HTMLButtonElement;
    submitButton.innerHTML = "Hinzufügen";
    submitButton.setAttribute("onClick", "addQuestion()");
    disableLoader();
}

/**
 * @author Sarah
 */
function initialiseEditDQuestionPage(defaultQuestion: DefaultQuestions) {
    const dQuestionInput = document.getElementById("defaultQuestion") as HTMLInputElement;
    if (defaultQuestion != null) {
        document.title = adminToolPageTitleName + ' - Fragen bearbeiten';
        let submitButton = document.getElementById("dQuestionFormSubmitButton") as HTMLButtonElement;
        submitButton.innerHTML = "Aktualisieren";
        submitButton.setAttribute("onClick", "updateQuestion();");
        dQuestionInput.value = defaultQuestion.question;
    } else {
        document.getElementById("loadingError")!.innerHTML = "Couldn't load question!";
    }
    disableLoader();
}

/**
 * @author Sarah
 */
async function addQuestion() {
    //exist();
    let inputQuestion = (document.getElementById("defaultQuestion") as HTMLInputElement).value;


    try {
        let response = await fetch(`${server}/services/adminTool/addQuestion`, {
            method: 'post',
            body: `defaultQuestion=${inputQuestion}`
        });
        let json = await response.json();
        await loadPage("defaultQuestionsDetails.html", "defaultQuestionButton", true, +json[0].id, true);
    } catch (e) {
        popup(false, 'Diese Standardfrage konnte aus unerwarteten Gründen nicht hinzugefügt werden!');
    }
}

/**
 * @author Sarah Ambi
 */
async function updateQuestion() {
    const page = pageCheck;
    let fullSearchParams: string = window.location.search;
    const urlParams: URLSearchParams = new URLSearchParams(fullSearchParams);

    let dQuestionId: number = +urlParams.get("objectID")!;
    let inputQuestion = (document.getElementById("defaultQuestion") as HTMLInputElement).value;

    try {
        let response = await fetch(`${server}/services/adminTool/editQuestion`, {
            method: 'post',
            body: `defaultQuestionId=${dQuestionId}&defaultQuestion=${inputQuestion}`
        });
        await loadPage("defaultQuestionsDetails.html", "defaultQuestionButton", true, -1, true);
    } catch (e) {
        checkPageExecute(() => popup(false, 'Diese Standardfrage konnte aus unerwarteten Gründen nicht bearbeitet werden!'), page);
    }
}

/**
 * @author Sarah Ambi
 */
async function loadDefaultQuestion() {
    defaultQuestions = [];
    let fullSearchParams: string = window.location.search;
    const urlParams: URLSearchParams = new URLSearchParams(fullSearchParams);
    const page = pageCheck;

    let dQuestionId: number = +urlParams.get("objectID")!;
    if (dQuestionId == null || isNaN(dQuestionId) || dQuestionId <= 0) {
        initialiseAddDQuestionPage();
    } else {
        try {
            let response = await fetch(`${server}/services/get/singleDefaultQuestion?defaultQuestionId=${dQuestionId}`);
            let json = await response.json();
            let defaultQuestion: DefaultQuestions = new DefaultQuestions(json[0].id, json[0].question);
            defaultQuestions.push(defaultQuestion);
            checkPageExecute(() => {
                initialiseEditDQuestionPage(defaultQuestion);
            }, page)
        } catch (e) {
            checkPageExecute(() => popup(false, 'Standard Frage konnte nicht geladen werden!'), 'defaultQuestionsDetails')
        }
    }

    await initMaxLengths(page, [{
        elem: document.getElementById('defaultQuestion') as HTMLInputElement,
        name: textInputs.QUESTION
    }])
}

/**
 * Check if the input field is empty or not
 * no whitespace in the beginning
 * @author Sarah Ambi
 */
function checkInput() {
    let input = document.getElementById('defaultQuestion') as HTMLInputElement;

    input.addEventListener('input', (event) => {
        input.value = input.value.replace(/^\s*(.*)$/, (wholeString, captureGroup) => captureGroup);
    });
}

/**
 * Überprüft ob die Frage bereits existiert
 * verhindert wiederholende Frage
 *
 * @note können wir so nicht verwenden
 *
 * @author Sarah Ambi
 */
function exist() {
    let input = document.getElementById('defaultQuestion') as HTMLInputElement;
    let button = document.getElementById('dQuestionFormSubmitButton') as HTMLButtonElement;
    let error = document.getElementById('error');
    for (let i = 0; i < defaultQuestions.length; i++) {
        if (defaultQuestions[i].question == input.value)
            if (input.value == defaultQuestions[i].question) {
                error!.textContent = "Bereits vorhanden!";
                button.disabled = true;
                setTimeout(function () {
                    error!.innerHTML = '';
                }, 2000);

            } else {
                error!.textContent = "";
            }
    }
}