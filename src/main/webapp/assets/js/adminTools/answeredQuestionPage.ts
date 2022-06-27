document.title = adminToolPageTitleName + ' - Beantwortete Fragen';

async function loadStuffAnsweredQuestions(timeRange: string, elementOfLoaderID: string, buttonElement: HTMLSpanElement, initialization: boolean = false) {
    if (!initialization) {
        $('.timeRangeButton.active').removeClass('active'); // Remove the "active" class there where we had it now
        buttonElement.classList.add('active'); // Add the active class to the new element
    }
    initLoadingAnimation(elementOfLoaderID);

    const page = pageCheck;
    try {
        let response = await fetch(`${server}/services/get/allAnsweredQuestions?timeRange=${timeRange}&withAnswers=false`);
        let json = await response.json();
        checkPageExecute(() => saveAnsweredQuestions(json), page);
    } catch (e) {
        checkPageExecute(() => popup(false, 'Diese Seite konnte leider nicht geladen werden!'), page);
    }

    disableLoader();
}

function saveAnsweredQuestions(questions: any) {
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
        sortAnsweredQuestions("allAnsweredQuestionBody");
    }
}