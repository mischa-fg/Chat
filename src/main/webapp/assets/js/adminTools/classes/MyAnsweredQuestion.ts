class MyAnsweredQuestion extends QuestionParent {
    constructor(id: number, question: string, upvotes: number, downvotes: number, views: number, myAnswers: SingleTagAnswer[] | null = null) {
        super(id, question, views);
        this._myAnswers = myAnswers;
        this._upvotes = upvotes;
        this._downvotes = downvotes;
    }

    private _myAnswers: SingleTagAnswer[] | null;

    get myAnswers(): SingleTagAnswer[] | null {
        return this._myAnswers;
    }

    set myAnswers(value: SingleTagAnswer[] | null) {
        this._myAnswers = value;
    }

    private _upvotes: number;

    get upvotes(): number {
        return this._upvotes;
    }

    set upvotes(value: number) {
        this._upvotes = value;
    }

    private _downvotes: number;

    get downvotes(): number {
        return this._downvotes;
    }

    set downvotes(value: number) {
        this._downvotes = value;
    }

    toAnsweredQuestionHTML(canBeDeleted: boolean = true): HTMLDivElement {
        let answeredQuestion = "answeredQuestion-" + this.id;
        let row = document.createElement("tr");
        row.classList.add("single-answeredQuestion");
        row.classList.add("big");
        row.setAttribute("id", `${answeredQuestion}`);
        row.setAttribute("onmouseover", 'style="background: rgba(0, 0, 0, 0.1);"');
        row.setAttribute("onmouseout", 'style="background: rgb(255, 255, 255);"');

        let idHTMl = document.createElement("td");
        idHTMl.innerHTML = String(this.id);
        idHTMl.classList.add("id-hidden");

        let question = document.createElement("td");
        question.innerHTML = this.question;
        question.classList.add('left');
        question.setAttribute("onclick", `loadPage("answeredQuestionDetails.html","defaultQuestionButton","true",\"${this.id}\")`);

        let upvotesElement = document.createElement("td");
        upvotesElement.innerHTML = String(this.upvotes);
        upvotesElement.setAttribute("onclick", `loadPage("answeredQuestionDetails.html","defaultQuestionButton","true",\"${this.id}\")`);

        let downvotesElement = document.createElement("td");
        downvotesElement.innerHTML = String(this.downvotes);
        downvotesElement.setAttribute("onclick", `loadPage("answeredQuestionDetails.html","defaultQuestionButton","true",\"${this.id}\")`);

        let viewsElement = document.createElement("td");
        viewsElement.innerHTML = String(this.views);
        viewsElement.setAttribute("onclick", `loadPage("answeredQuestionDetails.html","defaultQuestionButton","true",\"${this.id}\")`);

        let deleteButtonRow = document.createElement("td");
        let deleteButton = document.createElement("button");
        deleteButton.innerHTML = "Löschen";
        deleteButton.classList.add('button');
        deleteButton.setAttribute("onClick", `deleteAnsweredQuestion(${this.id});`);
        deleteButtonRow.appendChild(deleteButton);

        row.append(idHTMl, question, viewsElement, upvotesElement, downvotesElement);

        if (canBeDeleted) {
            row.appendChild(deleteButtonRow);
        }

        return row;
    }

    allAnswersToHTML() {
        if (this.myAnswers != null) {
            initialiseAnswerTable(this.myAnswers);
        }
    }
}

function sortAnsweredQuestions(tableID: string | null = null, canBeDeleted: boolean = true) {
    let unsortedAnsweredQuestions: MyAnsweredQuestion[] = Array.prototype.slice.call(answeredQuestions);

    answeredQuestions = unsortedAnsweredQuestions.sort(function (answeredQuestion1: MyAnsweredQuestion, answeredQuestion2: MyAnsweredQuestion) {
            if (answeredQuestion1.upvotes != answeredQuestion2.upvotes) {
                return ((answeredQuestion1.upvotes - answeredQuestion1.downvotes) > (answeredQuestion2.upvotes - answeredQuestion2.downvotes)) ? -1 : 1;
            }
            if (answeredQuestion1.question != answeredQuestion2.question) {
                return (answeredQuestion1.question < answeredQuestion2.question) ? -1 : 1; // Sort for tag Name
            } // If both have the same text (shouldnt be possible, aber sicher ist sicher)
            return (answeredQuestion1.id < answeredQuestion2.id) ? -1 : 1; // sort for tag id
        }
    )
    if (tableID != null) {
        createAnsweredQuestionString(answeredQuestions, tableID, canBeDeleted);
    }
}

function createAnsweredQuestionString(answeredQuestions: MyAnsweredQuestion[] | null, tableID: string, canBeDeleted: boolean = true) {
    // ID of table to fill = allQuestionSuggestionsBody
    let value: string;
    if (answeredQuestions != null && answeredQuestions.length > 0) {
        value = '';
        for (let answeredQuestion of answeredQuestions) {
            value += answeredQuestion.toAnsweredQuestionHTML(canBeDeleted).outerHTML;
        }
    } else {
        if (answeredQuestions == null) {
            value = "couldn't load question suggestions!";
        } else {
            value = "Es wurden keine Fragen gefunden!";
        }
    }
    $("." + tableID).empty();
    document.getElementById(tableID)!.innerHTML = value;
}

async function deleteAnsweredQuestion(id: number) {
    if (confirm("Wollen sie diese Frage wirklich löschen? Alle dazugehörigen Statistiken werden gelöscht.\nDies kann nicht mehr rückgängig gemacht werden!")) {
        const page = pageCheck;
        try {
            await fetch(`${server}/services/adminTool/deleteAnsweredQuestion`, {
                method: 'post',
                body: `questionID=${id}`
            }).then((response => {
                const ids = document.getElementsByClassName('id-hidden');
                for (const cid of ids) {
                    if (cid.innerHTML === String(id)) {
                        cid.parentElement?.remove();
                    }
                }
            }))
        } catch (e) {
            checkPageExecute(() => popup(false, 'Frage konnte nicht gelöscht werden!'), page);
        }
    }
}