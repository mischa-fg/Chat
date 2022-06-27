class DefaultQuestions extends QuestionParent {
    constructor(id: number, question: string) {
        super(id, question, 0);
    }

    toDefaultQuestionHTML(canBeDeleted: boolean = false): HTMLDivElement {
        let defaultQuestionId = "defaultQuestionId-" + this.id;
        let row = document.createElement("tr");
        row.classList.add("single-defaultQuestion");
        row.classList.add("big");
        row.setAttribute("id", `${defaultQuestionId}`);
        row.setAttribute("onmouseover", 'style="background: rgba(0, 0, 0, 0.1);"');
        row.setAttribute("onmouseout", 'style="background: rgb(255, 255, 255);"');
        let idHTMl = document.createElement("td");
        idHTMl.innerHTML = String(this.id);
        idHTMl.classList.add("id-hidden");
        let word = document.createElement("td");
        word.innerHTML = this.question;
        word.classList.add('left');
        word.setAttribute("onclick", `loadPage("defaultQuestionsDetails.html","defaultQuestionButton","true",\"${this.id}\")`);
        const deleteButtonContainer = document.createElement('td') as HTMLTableDataCellElement;
        const deleteButton = document.createElement('button') as HTMLButtonElement;
        deleteButton.innerHTML = 'Löschen';
        deleteButton.classList.add('button');
        let deleteFunction: string = (canBeDeleted) ? `deleteQuestion(${this.id}, this)` : `cantDeleteQuestion()`;
        deleteButton.setAttribute('onclick', deleteFunction);
        deleteButtonContainer.append(deleteButton);
        row.append(idHTMl, word,deleteButtonContainer);

        return row;
    }
}

function sortDefaultQuestion(canBeDeleted: boolean = false) {
    let unsortedDefaultQuestion: DefaultQuestions[] = Array.prototype.slice.call(defaultQuestions);
    // usefulness descending
    let button = document.getElementById('sortByName');
    button?.setAttribute('onclick', 'reverseSortDefaultQuestion()');
    button!.innerHTML = 'Name ↓';
    defaultQuestions = unsortedDefaultQuestion.sort(function (defaultQuestions1: DefaultQuestions, defaultQuestions2: DefaultQuestions) {
            if (defaultQuestions1.question != defaultQuestions2.question) {
                return (defaultQuestions1.question < defaultQuestions2.question) ? -1 : 1; // Sort for tag Name
            } // If both have the same text (shouldnt be possible, aber sicher ist sicher)
            return (defaultQuestions1.id < defaultQuestions2.id) ? -1 : 1; // sort for tag id
        }
    )
    createDefaultQuestionString(defaultQuestions, canBeDeleted);
}

function reverseSortDefaultQuestion() {
    let unsortedDefaultQuestion: DefaultQuestions[] = Array.prototype.slice.call(defaultQuestions);
    // usefulness descending
    let button = document.getElementById('sortByName');
    button?.setAttribute('onclick', 'sortDefaultQuestion()');
    button!.innerHTML = 'Name ↑';
    defaultQuestions = unsortedDefaultQuestion.sort(function (defaultQuestions1: DefaultQuestions, defaultQuestions2: DefaultQuestions) {
            if (defaultQuestions1.question != defaultQuestions2.question) {
                return (defaultQuestions1.question > defaultQuestions2.question) ? -1 : 1; // Sort for tag Name
            } // If both have the same text (shouldnt be possible, aber sicher ist sicher)
            return (defaultQuestions1.id < defaultQuestions2.id) ? -1 : 1; // sort for tag id
        }
    )
    createDefaultQuestionString(defaultQuestions);
}