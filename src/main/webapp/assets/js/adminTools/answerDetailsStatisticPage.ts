document.title = adminToolPageTitleName + ' - Antwort Statistiken';

async function loadAnsweredQuestionsOfThisAnswer(elementOfLoaderID: string) {
    initLoadingAnimation(elementOfLoaderID);

    let fullSearchParams: string = window.location.search;
    const urlParams: URLSearchParams = new URLSearchParams(fullSearchParams);
    const answerID: number = +urlParams.get("objectID")!;

    const page = pageCheck
    try {
        let response = await fetch(`${server}/services/get/allAnsweredQuestionsPerAnswer?answerID=${answerID}`);
        let json = await response.json();
        checkPageExecute(() => saveAnsweredQuestionsPerAnswer(json), page);
    } catch (e) {

        checkPageExecute(() => popup(false, 'Diese Seite konnte leider nicht geladen werden!'), page);
    }

    disableLoader();
}

function saveAnsweredQuestionsPerAnswer(questions: any) {
    if (questions != null) {
        answeredQuestions = [];
        for (let question of questions) {
            let questionID = question.id;
            let questionQuestion = question.question;
            let upvotes = question.upvotes;
            let downvotes = question.downvotes;
            let views = question.views;

            let answers: SingleTagAnswer[] = [];

            let answeredQuestion = new MyAnsweredQuestion(questionID, questionQuestion, upvotes, downvotes, views, answers);
            answeredQuestions.push(answeredQuestion);
        }
        sortAnsweredQuestions("allAnsweredQuestionBody", false);
    }
}