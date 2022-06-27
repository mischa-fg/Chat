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
document.title = adminToolPageTitleName + ' - Fragen';
function loadStuff() {
    return __awaiter(this, void 0, void 0, function* () {
        yield getQuestionSuggestions();
        yield loadQuestions();
    });
}
function getQuestionSuggestions() {
    return __awaiter(this, void 0, void 0, function* () {
        const page = pageCheck;
        try {
            let response = yield fetch(`${server}/services/get/topQuestionSuggestions?amountQuestions=${amountQuestions}`);
            let json = yield response.json();
            checkPageExecute(() => saveQuestionSuggestions(json), page);
        }
        catch (e) {
            checkPageExecute(() => popup(false, 'Diese Seite konnte leider nicht geladen werden!'), page);
        }
    });
}
function loadQuestions() {
    return __awaiter(this, void 0, void 0, function* () {
        const page = pageCheck;
        try {
            let response = yield fetch(`${server}/services/get/allDefaultQuestion?amountQuestions=${amountQuestions}`);
            let json = yield response.json();
            checkPageExecute(() => saveDefaultQuestions(json), page);
            disableLoader();
        }
        catch (e) {
            checkPageExecute(() => popup(false, 'Diese Seite konnte leider nicht geladen werden!'), page);
        }
    });
}
function saveQuestionSuggestions(retrievedQuestionSuggestions) {
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
function saveDefaultQuestions(retrievedDefaultQuestion) {
    if (retrievedDefaultQuestion != null) {
        defaultQuestions = [];
        for (let question in retrievedDefaultQuestion) {
            let questionId = retrievedDefaultQuestion[question].id;
            let questionQuestion = retrievedDefaultQuestion[question].question;
            let defaultQuestionList = new DefaultQuestions(questionId, questionQuestion);
            defaultQuestions.push(defaultQuestionList);
        }
        let canBeDeleted = false;
        if (defaultQuestions.length > 3) {
            canBeDeleted = true;
        }
        sortDefaultQuestion(canBeDeleted);
    }
    else {
        createDefaultQuestionString(null);
    }
}
function createDefaultQuestionString(defaultQuestions, canBeDeleted = false) {
    let value;
    if (defaultQuestions != null && defaultQuestions.length > 0) {
        value = '';
        for (let i = 0; i < defaultQuestions.length; i++) {
            value += defaultQuestions[i].toDefaultQuestionHTML(canBeDeleted).outerHTML;
        }
    }
    else {
        value = "Couldn't load default Question!";
    }
    $(".allDefaultTableBody").empty();
    document.getElementById("allDefaultTableBody").innerHTML = value;
}
function deleteQuestion(id, element) {
    var _a;
    return __awaiter(this, void 0, void 0, function* () {
        if (defaultQuestions.length > 3) {
            if (confirm("Möchtest du diese Frage wirklich löschen? Dies kann nicht rückgängig gemacht werden!")) {
                const page = pageCheck;
                try {
                    let response = yield fetch(`${server}/services/adminTool/deleteQuestion`, {
                        method: 'post',
                        body: `questionId=${id}`
                    });
                    if (response.ok) {
                        const ids = document.getElementsByClassName('id-hidden');
                        for (const cid of ids) {
                            if (cid.innerHTML === String(id)) {
                                (_a = cid.parentElement) === null || _a === void 0 ? void 0 : _a.remove();
                                const questionID = findSameId(defaultQuestions, id);
                                if (questionID != null) {
                                    defaultQuestions.splice(questionID, 1);
                                }
                            }
                        }
                    }
                    else {
                        popup(false, "Frage konnnte nicht gelöscht werden\n");
                    }
                }
                catch (e) {
                    checkPageExecute(() => popup(false, 'Frage konnnte nicht gelöscht werden.'), page);
                }
            }
        }
        else {
            cantDeleteQuestion();
        }
    });
}
function cantDeleteQuestion() {
    popup(false, 'Es müssen mindestens 3 Fragen vorhanden sein!');
}
