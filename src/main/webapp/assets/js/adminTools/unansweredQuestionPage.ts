document.title = adminToolPageTitleName + ' - Unbeantwortete Fragen';

async function loadStuffUnansweredQuestions(timeRange: string, elementOfLoaderID: string, buttonElement: HTMLSpanElement, initialization: boolean = false) {
    if (!initialization) {
        $('.timeRangeButton.active').removeClass('active'); // Remove the "active" class there where we had it now
        buttonElement.classList.add('active'); // Add the active class to the new element
    }
    initLoadingAnimation(elementOfLoaderID);

    const page = pageCheck;
    try {
        let response = await fetch(`${server}/services/get/allUnansweredQuestions?timeRange=${timeRange}`);
        let json = await response.json();
        checkPageExecute(() => saveUnansweredQuestions(json), page);
    } catch (e) {

        checkPageExecute(() => popup(false, 'Diese Seite konnte leider nicht geladen werden!'), page);
    }

    disableLoader();
}

function saveUnansweredQuestions(questions: any) {
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