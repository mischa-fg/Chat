class SingleTagAnswer extends AnswerParent {
    private _upvotes: number;
    private _downvotes: number;
    private _tagId: number;

    constructor(id: number, tagId: number, title: string, views: number, upvotes: number, downvotes: number, isHidden: boolean, averageUsefulness: number, answerType: AnswerType) {
        super(id, title, isHidden, answerType, averageUsefulness, views);
        this._tagId = tagId;
        this._upvotes = upvotes;
        this._downvotes = downvotes;
    }

    get tagId(): number {
        return this._tagId;
    }

    set tagId(value: number) {
        this._tagId = value;
    }

    get upvotes(): number {
        return this._upvotes;
    }

    set upvotes(value: number) {
        this._upvotes = value;
    }

    get downvotes(): number {
        return this._downvotes;
    }

    set downvotes(value: number) {
        this._downvotes = value;
    }

    toSingleTagAnswerHTML(connectedToTag: boolean = true): HTMLDivElement {
        let answerIDString = "singleTagAnswerID-" + this.id;
        let row = document.createElement("tr");
        row.classList.add("single-tagAnswer");
        row.setAttribute("id", `${answerIDString}`);

        // ID
        let idHTML = document.createElement("td");
        idHTML.innerHTML = String(this.id);
        idHTML.classList.add("hidden");
        idHTML.setAttribute(`onClick`, `loadPage("answersDetails.html","answersButton","true",\"${this.id}\")`);

        // title
        let answerTitle = document.createElement("td");
        answerTitle.innerHTML = this.title;
        answerTitle.classList.add("answerTitle", "left", "additionalTagInfos");
        answerTitle.setAttribute(`onClick`, `loadPage("answersDetails.html","answersButton","true",\"${this.id}\")`);

        // answer type
        let answerTypeTd = document.createElement("td");
        answerTypeTd.innerHTML = this.answerType.name;
        answerTypeTd.classList.add("additionalTagInfos");
        answerTypeTd.setAttribute(`onClick`, `loadPage("answersDetails.html","answersButton","true",\"${this.id}\")`);

        // upvotes
        let answerTagUpvotes = document.createElement("td");
        answerTagUpvotes.innerHTML = String(this.upvotes);
        answerTagUpvotes.classList.add("additionalTagInfos");
        answerTagUpvotes.setAttribute(`onClick`, `loadPage("answersDetails.html","answersButton","true",\"${this.id}\")`);

        // downvotes
        let answerTagDownvotes = document.createElement("td");
        answerTagDownvotes.innerHTML = String(this.downvotes);
        answerTagDownvotes.classList.add("additionalTagInfos");
        answerTagDownvotes.setAttribute(`onClick`, `loadPage("answersDetails.html","answersButton","true",\"${this.id}\")`);

        row.append(idHTML, answerTitle, answerTypeTd, answerTagUpvotes, answerTagDownvotes);

        if (connectedToTag) {
            let tdDelete = document.createElement('td');
            let deleteButton = document.createElement('button');
            deleteButton.innerHTML = 'Löschen';
            deleteButton.classList.add('button');
            deleteButton.setAttribute('onClick', `removeFromTagAnswerBackend(${this.id}, ${this.tagId})`);
            tdDelete.appendChild(deleteButton);

            row.appendChild(tdDelete);
        }

        return row;
    }
}

function initialiseAnswerTable(myAnswers: SingleTagAnswer[], connectedToTag: boolean = true) {
    let contentContainer = document.getElementById('allAnswersContainer');
    let table = document.createElement('table');
    let thead = document.createElement('thead');
    let thAnswer = document.createElement('th');
    thAnswer.innerHTML = 'Antwort';
    let thAnswerType = document.createElement("th");
    thAnswerType.innerHTML = 'Typ';
    let thAnswerUpvotes = document.createElement("th");
    thAnswerUpvotes.innerHTML = 'Upvotes';
    let thAnswerDownvotes = document.createElement("th");
    thAnswerDownvotes.innerHTML = 'Downvotes';
    let thDelete = document.createElement('th');
    thDelete.innerHTML = 'Löschen';

    thead.append(thAnswer, thAnswerType, thAnswerUpvotes, thAnswerDownvotes);

    if(connectedToTag) {
        thead.appendChild(thDelete);
    }

    let tbody = document.createElement('tbody');
    tbody.id = 'allAnswers';

    for (let answer of myAnswers) {
        tbody.appendChild(answer.toSingleTagAnswerHTML(connectedToTag))
    }

    table.append(thead, tbody);
    contentContainer?.appendChild(table);
}