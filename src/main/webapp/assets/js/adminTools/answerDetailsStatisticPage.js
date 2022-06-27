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
document.title = adminToolPageTitleName + ' - Antwort Statistiken';
function loadAnsweredQuestionsOfThisAnswer(elementOfLoaderID) {
    return __awaiter(this, void 0, void 0, function* () {
        initLoadingAnimation(elementOfLoaderID);
        let fullSearchParams = window.location.search;
        const urlParams = new URLSearchParams(fullSearchParams);
        const answerID = +urlParams.get("objectID");
        const page = pageCheck;
        try {
            let response = yield fetch(`${server}/services/get/allAnsweredQuestionsPerAnswer?answerID=${answerID}`);
            let json = yield response.json();
            checkPageExecute(() => saveAnsweredQuestionsPerAnswer(json), page);
        }
        catch (e) {
            checkPageExecute(() => popup(false, 'Diese Seite konnte leider nicht geladen werden!'), page);
        }
        disableLoader();
    });
}
function saveAnsweredQuestionsPerAnswer(questions) {
    if (questions != null) {
        answeredQuestions = [];
        for (let question of questions) {
            let questionID = question.id;
            let questionQuestion = question.question;
            let upvotes = question.upvotes;
            let downvotes = question.downvotes;
            let views = question.views;
            let answers = [];
            let answeredQuestion = new MyAnsweredQuestion(questionID, questionQuestion, upvotes, downvotes, views, answers);
            answeredQuestions.push(answeredQuestion);
        }
        sortAnsweredQuestions("allAnsweredQuestionBody", false);
    }
}
