class QuestionParent extends IDParent {
    constructor(id: number, question: string, views: number) {
        super(id);
        this._views = views;
        this._question = question;
    }

    private _question: string;

    get question(): string {
        return this._question;
    }

    set question(value: string) {
        this._question = value;
    }

    private _views: number;

    get views(): number {
        return this._views;
    }

    set views(value: number) {
        this._views = value;
    }

    toUnansweredQuestionHTML(): HTMLDivElement {
        let unansweredQuestion = "unansweredQuestion-" + this.id;
        let row = document.createElement("tr");
        row.classList.add("single-unansweredQuestion");
        row.classList.add("big");
        row.setAttribute("id", `${unansweredQuestion}`);
        row.setAttribute("onmouseover", 'style="background: rgba(0, 0, 0, 0.1);"');
        row.setAttribute("onmouseout", 'style="background: rgb(255, 255, 255);"');

        let idHTMl = document.createElement("td");
        idHTMl.innerHTML = String(this.id);
        idHTMl.classList.add("id-hidden");

        let question = document.createElement("td");
        question.innerHTML = this.question;
        question.classList.add('left');

        let viewsElement = document.createElement("td");
        viewsElement.innerHTML = String(this.views);

        let deleteButtonRow = document.createElement("td");
        let deleteButton = document.createElement("button");
        deleteButton.innerHTML = "Löschen";
        deleteButton.classList.add('button');
        deleteButton.setAttribute("onClick", `deleteUnansweredQuestion(${this.id});`);
        deleteButtonRow.appendChild(deleteButton);

        row.append(idHTMl, question, viewsElement, deleteButtonRow);

        return row;
    }
}

function sortUnansweredQuestions(tableID: string | null = null) {
    let unsortedQuestionParent: QuestionParent[] = Array.prototype.slice.call(unansweredQuestions);

    unansweredQuestions = unsortedQuestionParent.sort(function (questionParent1: QuestionParent, questionParent2: QuestionParent) {
            if (questionParent1.views != questionParent2.views) {
                return (questionParent1.views > questionParent2.views) ? -1 : 1;
            }
            if (questionParent1.question != questionParent2.question) {
                return (questionParent1.question < questionParent2.question) ? -1 : 1; // Sort for tag Name
            } // If both have the same text (shouldnt be possible, aber sicher ist sicher)
            return (questionParent1.id < questionParent2.id) ? -1 : 1; // sort for tag id
        }
    )
    if (tableID != null) {
        createUnansweredQuestionString(unansweredQuestions, tableID);
    }
}

function createUnansweredQuestionString(unansweredQuestions: QuestionParent[] | null, tableID: string) {
    // ID of table to fill = allQuestionSuggestionsBody
    let value: string;
    if (unansweredQuestions != null && unansweredQuestions.length > 0) {
        value = '';
        for (let answeredQuestion of unansweredQuestions) {
            value += answeredQuestion.toUnansweredQuestionHTML().outerHTML;
        }
    } else {
        if (unansweredQuestions == null) {
            value = "couldn't load questions!";
        } else {
            value = "Es wurden keine Fragen gefunden!";
        }
    }
    $("." + tableID).empty();
    document.getElementById(tableID)!.innerHTML = value;
}

async function deleteUnansweredQuestion(id: number) {
    if (confirm("Wollen sie diese Frage wirklich löschen? Alle dazugehörigen Statistiken werden gelöscht.\nDies kann nicht rückgängig gemacht werden!")) {
        const page = pageCheck;
        try {
            await fetch(`${server}/services/adminTool/deleteUnansweredQuestion`, {
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