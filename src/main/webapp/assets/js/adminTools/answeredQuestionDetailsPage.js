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
document.title = adminToolPageTitleName + ' - Beantwortete Frage bearbeiten';
function loadAnsweredQuestionDetailsPage(elementOfLoaderID) {
    return __awaiter(this, void 0, void 0, function* () {
        initLoadingAnimation(elementOfLoaderID);
        const answeredQuestionInputElement = document.getElementById("answeredQuestionInput");
        const page = pageCheck;
        answeredQuestions = [];
        let fullSearchParams = window.location.search;
        const urlParams = new URLSearchParams(fullSearchParams);
        let answeredQuestionID = +urlParams.get("objectID");
        if (answeredQuestionID == null || isNaN(answeredQuestionID) || answeredQuestionID <= 0) {
            yield loadPage("answeredQuestions.html", "", false, -1, true);
        }
        else {
            try {
                let response = yield fetch(`${server}/services/get/singleAnsweredQuestion?questionID=${answeredQuestionID}`);
                let loadedQuestion = yield response.json();
                answeredQuestions = [];
                let actualLoadedQuestion = loadedQuestion[0];
                let myAnswers = [];
                for (let currentAnswer of actualLoadedQuestion.answers) {
                    let answerTypeJson = currentAnswer.answerType[0];
                    let actualAnswerType = new AnswerType(answerTypeJson.value, answerTypeJson.name, answerTypeJson.groupedTags, answerTypeJson.hidden, answerTypeJson.forceHidden);
                    let answer = new SingleTagAnswer(currentAnswer.id, 0, currentAnswer.title, currentAnswer.views, currentAnswer.upvotes, currentAnswer.downvotes, currentAnswer.isHidden, currentAnswer.averageUsefulness, actualAnswerType);
                    myAnswers.push(answer);
                }
                let id = actualLoadedQuestion.id;
                let question = actualLoadedQuestion.question;
                let upvotes = actualLoadedQuestion.upvotes;
                let downvotes = actualLoadedQuestion.downvotes;
                let views = actualLoadedQuestion.views;
                let newAnsweredQuestion = new MyAnsweredQuestion(id, question, upvotes, downvotes, views, myAnswers);
                answeredQuestions.push(newAnsweredQuestion);
                dataClass = newAnsweredQuestion;
                answeredQuestionInputElement.value = question;
                if (newAnsweredQuestion.myAnswers != null && newAnsweredQuestion.myAnswers.length > 0) {
                    initialiseAnswerTable(newAnsweredQuestion.myAnswers, false);
                }
                else {
                    let answerContainer = document.getElementById("allAnswersContainer");
                    answerContainer.innerHTML = "Es wurden keine Antworten gefunden! Das sollte nicht passieren...";
                }
            }
            catch (e) {
                popup(false, 'Die Frage konnte nicht geladen werden!');
                console.error(e);
            }
            yield initMaxLengths(page, [{ elem: answeredQuestionInputElement, name: "USER_QUESTION_INPUT" }]);
        }
        disableLoader();
    });
}
function updateAnsweredQuestion() {
    return __awaiter(this, void 0, void 0, function* () {
        let answeredQuestion = dataClass;
        const page = pageCheck;
        let questionInput = (document.getElementById("answeredQuestionInput")).value;
        try {
            let response = yield fetch(`${server}/services/adminTool/editAnsweredQuestion`, {
                method: "POST",
                body: `questionID=${answeredQuestion.id}&question=${questionInput}`
            });
            if (response.ok) {
                yield loadPage('answeredQuestionDetails.html', 'defaultQuestionButton', true, -1, true);
            }
            else {
                popup(false, 'Diese Frage konnte nicht aktualisiert werden!');
            }
        }
        catch (e) {
            checkPageExecute(() => {
                popup(false, 'Diese Frage konnte nicht aktualisiert werden!');
            }, page);
        }
    });
}
