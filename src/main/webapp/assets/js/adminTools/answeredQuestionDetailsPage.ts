document.title = adminToolPageTitleName + ' - Beantwortete Frage bearbeiten';

async function loadAnsweredQuestionDetailsPage(elementOfLoaderID: string) {
    initLoadingAnimation(elementOfLoaderID);

    const answeredQuestionInputElement = document.getElementById("answeredQuestionInput") as HTMLInputElement;
    const page = pageCheck;

    answeredQuestions = [];
    let fullSearchParams: string = window.location.search;
    const urlParams: URLSearchParams = new URLSearchParams(fullSearchParams);
    let answeredQuestionID: number = +urlParams.get("objectID")!;

    if (answeredQuestionID == null || isNaN(answeredQuestionID) || answeredQuestionID <= 0) {
        await loadPage("answeredQuestions.html", "", false, -1, true);
    } else {
        try {
            let response = await fetch(`${server}/services/get/singleAnsweredQuestion?questionID=${answeredQuestionID}`);
            let loadedQuestion = await response.json();

            answeredQuestions = [];

            let actualLoadedQuestion = loadedQuestion[0];

            let myAnswers: SingleTagAnswer[] = [];
            for (let currentAnswer of actualLoadedQuestion.answers) {
                let answerTypeJson = currentAnswer.answerType[0];
                let actualAnswerType: AnswerType = new AnswerType(answerTypeJson.value, answerTypeJson.name, answerTypeJson.groupedTags, answerTypeJson.hidden, answerTypeJson.forceHidden);

                let answer: SingleTagAnswer = new SingleTagAnswer(currentAnswer.id, 0, currentAnswer.title, currentAnswer.views, currentAnswer.upvotes, currentAnswer.downvotes, currentAnswer.isHidden, currentAnswer.averageUsefulness, actualAnswerType);
                myAnswers.push(answer);
            }

            let id: number = actualLoadedQuestion.id;
            let question: string = actualLoadedQuestion.question;
            let upvotes: number = actualLoadedQuestion.upvotes;
            let downvotes: number = actualLoadedQuestion.downvotes;
            let views: number = actualLoadedQuestion.views;
            let newAnsweredQuestion: MyAnsweredQuestion = new MyAnsweredQuestion(id, question, upvotes, downvotes, views, myAnswers);
            answeredQuestions.push(newAnsweredQuestion);
            dataClass = newAnsweredQuestion;

            answeredQuestionInputElement.value = question;

            if (newAnsweredQuestion.myAnswers != null && newAnsweredQuestion.myAnswers.length > 0) {
                initialiseAnswerTable(newAnsweredQuestion.myAnswers, false);
            } else {
                let answerContainer = document.getElementById("allAnswersContainer") as HTMLDivElement;
                answerContainer.innerHTML = "Es wurden keine Antworten gefunden! Das sollte nicht passieren...";
            }
        } catch (e) {
            popup(false, 'Die Frage konnte nicht geladen werden!')
            console.error(e);
        }

        await initMaxLengths(page, [{elem: answeredQuestionInputElement, name: textInputs.QUESTION}])
    }

    disableLoader();
}

async function updateAnsweredQuestion() {
    let answeredQuestion = dataClass as MyAnsweredQuestion;
    const page = pageCheck;

    let questionInput: string = ((document.getElementById("answeredQuestionInput")) as HTMLInputElement).value;
    try {
        let response = await fetch(`${server}/services/adminTool/editAnsweredQuestion`, {
            method: "POST",
            body: `questionID=${answeredQuestion.id}&question=${questionInput}`
        })

        if (response.ok) {
            await loadPage('answeredQuestionDetails.html', 'defaultQuestionButton', true, -1, true);
        } else {
            popup(false, 'Diese Frage konnte nicht aktualisiert werden!');
        }
    } catch (e) {

        checkPageExecute(() => {
            popup(false, 'Diese Frage konnte nicht aktualisiert werden!');
        }, page)
    }
}