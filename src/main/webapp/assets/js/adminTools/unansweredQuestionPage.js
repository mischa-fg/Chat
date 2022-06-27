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
document.title = adminToolPageTitleName + ' - Unbeantwortete Fragen';
function loadStuffUnansweredQuestions(timeRange, elementOfLoaderID, buttonElement, initialization = false) {
    return __awaiter(this, void 0, void 0, function* () {
        if (!initialization) {
            $('.timeRangeButton.active').removeClass('active');
            buttonElement.classList.add('active');
        }
        initLoadingAnimation(elementOfLoaderID);
        const page = pageCheck;
        try {
            let response = yield fetch(`${server}/services/get/allUnansweredQuestions?timeRange=${timeRange}`);
            let json = yield response.json();
            checkPageExecute(() => saveUnansweredQuestions(json), page);
        }
        catch (e) {
            checkPageExecute(() => popup(false, 'Diese Seite konnte leider nicht geladen werden!'), page);
        }
        disableLoader();
    });
}
function saveUnansweredQuestions(questions) {
    if (questions != null) {
        unansweredQuestions = [];
        for (let question of questions) {
            let questionID = question.id;
            let questionQuestion = question.question;
            let views = question.views;
            let answeredQuestion = new QuestionParent(questionID, questionQuestion, views);
            unansweredQuestions.push(answeredQuestion);
        }
        sortUnansweredQuestions("allUnansweredQuestionBody");
    }
}
