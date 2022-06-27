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
function initialiseAddDQuestionPage() {
    document.title = adminToolPageTitleName + ' - Fragen hinzufügen';
    let submitButton = document.getElementById("dQuestionFormSubmitButton");
    submitButton.innerHTML = "Hinzufügen";
    submitButton.setAttribute("onClick", "addQuestion()");
    disableLoader();
}
function initialiseEditDQuestionPage(defaultQuestion) {
    const dQuestionInput = document.getElementById("defaultQuestion");
    if (defaultQuestion != null) {
        document.title = adminToolPageTitleName + ' - Fragen bearbeiten';
        let submitButton = document.getElementById("dQuestionFormSubmitButton");
        submitButton.innerHTML = "Aktualisieren";
        submitButton.setAttribute("onClick", "updateQuestion();");
        dQuestionInput.value = defaultQuestion.question;
    }
    else {
        document.getElementById("loadingError").innerHTML = "Couldn't load question!";
    }
    disableLoader();
}
function addQuestion() {
    return __awaiter(this, void 0, void 0, function* () {
        let inputQuestion = document.getElementById("defaultQuestion").value;
        try {
            let response = yield fetch(`${server}/services/adminTool/addQuestion`, {
                method: 'post',
                body: `defaultQuestion=${inputQuestion}`
            });
            let json = yield response.json();
            yield loadPage("defaultQuestionsDetails.html", "defaultQuestionButton", true, +json[0].id, true);
        }
        catch (e) {
            popup(false, 'Diese Standardfrage konnte aus unerwarteten Gründen nicht hinzugefügt werden!');
        }
    });
}
function updateQuestion() {
    return __awaiter(this, void 0, void 0, function* () {
        const page = pageCheck;
        let fullSearchParams = window.location.search;
        const urlParams = new URLSearchParams(fullSearchParams);
        let dQuestionId = +urlParams.get("objectID");
        let inputQuestion = document.getElementById("defaultQuestion").value;
        try {
            let response = yield fetch(`${server}/services/adminTool/editQuestion`, {
                method: 'post',
                body: `defaultQuestionId=${dQuestionId}&defaultQuestion=${inputQuestion}`
            });
            yield loadPage("defaultQuestionsDetails.html", "defaultQuestionButton", true, -1, true);
        }
        catch (e) {
            checkPageExecute(() => popup(false, 'Diese Standardfrage konnte aus unerwarteten Gründen nicht bearbeitet werden!'), page);
        }
    });
}
function loadDefaultQuestion() {
    return __awaiter(this, void 0, void 0, function* () {
        defaultQuestions = [];
        let fullSearchParams = window.location.search;
        const urlParams = new URLSearchParams(fullSearchParams);
        const page = pageCheck;
        let dQuestionId = +urlParams.get("objectID");
        if (dQuestionId == null || isNaN(dQuestionId) || dQuestionId <= 0) {
            initialiseAddDQuestionPage();
        }
        else {
            try {
                let response = yield fetch(`${server}/services/get/singleDefaultQuestion?defaultQuestionId=${dQuestionId}`);
                let json = yield response.json();
                let defaultQuestion = new DefaultQuestions(json[0].id, json[0].question);
                defaultQuestions.push(defaultQuestion);
                checkPageExecute(() => {
                    initialiseEditDQuestionPage(defaultQuestion);
                }, page);
            }
            catch (e) {
                checkPageExecute(() => popup(false, 'Standard Frage konnte nicht geladen werden!'), 'defaultQuestionsDetails');
            }
        }
        yield initMaxLengths(page, [{
                elem: document.getElementById('defaultQuestion'),
                name: "USER_QUESTION_INPUT"
            }]);
    });
}
function checkInput() {
    let input = document.getElementById('defaultQuestion');
    input.addEventListener('input', (event) => {
        input.value = input.value.replace(/^\s*(.*)$/, (wholeString, captureGroup) => captureGroup);
    });
}
function exist() {
    let input = document.getElementById('defaultQuestion');
    let button = document.getElementById('dQuestionFormSubmitButton');
    let error = document.getElementById('error');
    for (let i = 0; i < defaultQuestions.length; i++) {
        if (defaultQuestions[i].question == input.value)
            if (input.value == defaultQuestions[i].question) {
                error.textContent = "Bereits vorhanden!";
                button.disabled = true;
                setTimeout(function () {
                    error.innerHTML = '';
                }, 2000);
            }
            else {
                error.textContent = "";
            }
    }
}
