class MyTag extends IDParent {
    constructor(id: number, tag: string, upvotes: number, downvotes: number, usage: number, amountAnswers: number = -1, answers: AnswerParent[]) {
        super(id);
        this._tag = tag;
        this._upvotes = +upvotes;
        this._downvotes = +downvotes;
        this._usage = +usage;

        //
        this._usefulness = +calcPercentage(this._upvotes, this._downvotes);

        this._amountAnswers = +amountAnswers;
        this._answers = answers;
    }

    private _tag: string;

    get tag(): string {
        return this._tag;
    }

    set tag(value: string) {
        this._tag = value;
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

    private _usefulness: number;

    get usefulness(): number {
        return this._usefulness;
    }

    set usefulness(value: number) {
        this._usefulness = value;
    }

    private _usage: number;

    get usage(): number {
        return this._usage;
    }

    set usage(value: number) {
        this._usage = value;
    }

    private _amountAnswers: number;

    get amountAnswers(): number {
        return this._amountAnswers;
    }

    set amountAnswers(value: number) {
        this._amountAnswers = value;
    }

    private _answers: AnswerParent[];

    get answers(): AnswerParent[] {
        return this._answers;
    }

    set answers(value: AnswerParent[]) {
        this._answers = value;
    }

    removeAnswer(answer: SingleTagAnswer) {
        this.removeAnswerById(answer.id);
    }

    removeAnswerById(answerID: number) {
        singleTagAnswers.forEach((currentAnswer) => {
            if (currentAnswer.id === answerID) {
                const index = singleTagAnswers.indexOf(currentAnswer);
                singleTagAnswers = [...singleTagAnswers.slice(0, index), ...singleTagAnswers.slice(index + 1)];
            }
        });
    }

    toTagHTML(small: boolean = false, canBeDeleted: boolean = false, linkedToAnswer: boolean = false): HTMLDivElement {
        let amountAnswersString = "";
        if (small) {
            let div = document.createElement("div");
            div.classList.add("single-tag");
            div.classList.add("small");
            div.style.background = getColor(this.usefulness);
            let idHTML = document.createElement("span");
            idHTML.innerHTML = String(this.id);
            idHTML.classList.add("id-hidden");
            let tag = document.createElement("span");
            tag.innerHTML = String(this._tag);
            tag.classList.add("tagName");
            div.append(idHTML, tag);

            return div;
        }

        amountAnswersString = String(this._amountAnswers);
        let tagStringID = "tagId-" + this.id;

        // Table Row
        let row = document.createElement("tr");
        row.classList.add("single-tag");
        row.classList.add("big");
        row.setAttribute("id", `${tagStringID}`);
        // row.setAttribute(`onclick`, `loadPage("tagsDetails.html","tagButton","true",\"${this.id}\")`);
        // row.setAttribute("onmouseover", `tagHoverOn(\"${tagStringID}\")`);
        // row.setAttribute("onmouseout", `tagHoverOff(\"${tagStringID}\")`);

        // Tag ID
        let idHTML = document.createElement("td");
        idHTML.innerHTML = String(this.id);
        idHTML.classList.add("id-hidden");
        idHTML.setAttribute(`onclick`, `loadPage("tagsDetails.html","tagButton","true",\"${this.id}\")`);

        // Tag Name
        let tag = document.createElement("td");
        tag.innerHTML = String(this._tag);
        tag.classList.add("tagName", 'left');
        tag.setAttribute(`onclick`, `loadPage("tagsDetails.html","tagButton","true",\"${this.id}\")`);

        // Tag Usage
        let usage = document.createElement("td");
        usage.innerHTML = String(this._usage);
        usage.classList.add("additionalTagInfos");
        usage.setAttribute(`onclick`, `loadPage("tagsDetails.html","tagButton","true",\"${this.id}\")`);

        // Tag Amount Answers
        let amount = document.createElement("td");
        amount.innerHTML = amountAnswersString;
        amount.classList.add("additionalTagInfos");
        amount.setAttribute(`onclick`, `loadPage("tagsDetails.html","tagButton","true",\"${this.id}\")`);

        // Tag Usefulness
        let usefulnessHTML = document.createElement("td");
        let ratingDiv = document.createElement('div');
        ratingDiv.classList.add('rating');
        ratingDiv.innerHTML = String(Math.round(this._usefulness * 100).toFixed(0)) + "%";
        ratingDiv.style.background = getColor(this.usefulness);

        usefulnessHTML.appendChild(ratingDiv)
        usefulnessHTML.classList.add("additionalTagInfos");
        usefulnessHTML.setAttribute(`onclick`, `loadPage("tagsDetails.html","tagButton","true",\"${this.id}\")`);

        // Tag Upvotes
        let upvoteTd = document.createElement("td");
        upvoteTd.innerHTML = String(this._upvotes);
        upvoteTd.classList.add("additionalTagInfos");
        upvoteTd.setAttribute(`onclick`, `loadPage("tagsDetails.html","tagButton","true",\"${this.id}\")`);

        // Tag Downvotes
        let downvoteTd = document.createElement("td");
        downvoteTd.innerHTML = String(this._downvotes);
        downvoteTd.classList.add("additionalTagInfos");
        downvoteTd.setAttribute(`onclick`, `loadPage("tagsDetails.html","tagButton","true",\"${this.id}\")`);

        // Delete Button
        let deleteButtonTD = document.createElement("td");
        deleteButtonTD.classList.add("additionalTagInfos");

        // Tag Delete Button
        let deleteButton = document.createElement("button");
        deleteButton.innerHTML = "Löschen";
        deleteButton.classList.add('button');
        let removeFunctionName = (linkedToAnswer) ? "event.preventDefault(); removeTagFromAnswerBackend" : "deleteTag";
        deleteButton.setAttribute("onClick", `${removeFunctionName}(${this.id})`);
        deleteButtonTD.appendChild(deleteButton);

        row.append(idHTML, tag, usage);
        if (this._amountAnswers >= 0) {
            row.append(amount);
        }
        row.append(upvoteTd, downvoteTd, usefulnessHTML);
        if (canBeDeleted) {
            row.append(deleteButtonTD);
        }

        return row;
    }
}

function sortTags(choose: string, first: boolean = false) {
    resetTagsSortButtons();
    let unsortedTags: MyTag[] = Array.prototype.slice.call(tags);
    // usefulness descending
    tags = unsortedTags.sort(function (tag1: MyTag, tag2: MyTag) {
        let dontFind: boolean = false;
        if (choose === 'usefulness') {
            let button = document.getElementById('sortByUsefulness');
            button?.setAttribute('onclick', 'reverseSortTags(\'usefulness\')');
            button!.innerHTML = 'Bewertung ↓';
            if (tag1.usefulness != tag2.usefulness) {

                return (tag1.usefulness > tag2.usefulness) ? -1 : 1; // If answer1 is more useful, return -1, if answer2 is more useful return 1
            } // If both have the same usefulness (would return 0)
            else
                dontFind = true;
        }
        if (choose === 'usage') {
            let button = document.getElementById('sortByUsage');
            button?.setAttribute('onclick', 'reverseSortTags(\'usage\')');
            button!.innerHTML = 'Verwendung ↓';
            if (tag1.usage != tag2.usage) {

                return (tag1.usage > tag2.usage) ? -1 : 1;
            } else {
                dontFind = true;
            }
        }
        if (choose === 'title' || dontFind)
            if (!dontFind) {
                let button = document.getElementById('sortByName');
                button?.setAttribute('onclick', 'reverseSortTags(\'title\')');
                button!.innerHTML = 'Name ↓';
            }
        if (tag1.tag != tag2.tag) {

            return (tag1.tag < tag2.tag) ? -1 : 1; // Sort for tag Name
        } // If both have the same text (shouldnt be possible, aber sicher ist sicher)

        return (tag1.id > tag2.id) ? -1 : 1; // sort for tag id
    })
    if (!first)
        createTagString(tags);
}

function reverseSortTags(choose: string) {
    resetTagsSortButtons();
    let unsortedTags: MyTag[] = Array.prototype.slice.call(tags);
    // usefulness descending
    tags = unsortedTags.sort(function (tag1: MyTag, tag2: MyTag) {
        let dontFind: boolean = false;
        if (choose === 'usefulness') {
            let button = document.getElementById('sortByUsefulness');
            button?.setAttribute('onclick', 'sortTags(\'usefulness\')');
            button!.innerHTML = 'Bewertung ↑';
            if (tag1.usefulness != tag2.usefulness) {

                return (tag1.usefulness < tag2.usefulness) ? -1 : 1; // If answer1 is more useful, return -1, if answer2 is more useful return 1
            } // If both have the same usefulness (would return 0)
            else
                dontFind = true;
        }
        if (choose === 'usage') {
            let button = document.getElementById('sortByUsage');
            button?.setAttribute('onclick', 'sortTags(\'usage\')');
            button!.innerHTML = 'Verwendung ↑';
            if (tag1.usage != tag2.usage) {

                return (tag1.usage < tag2.usage) ? -1 : 1;
            }
        }
        if (choose === 'title' || dontFind)
            if (!dontFind) {
                let button = document.getElementById('sortByName');
                button?.setAttribute('onclick', 'sortTags(\'title\')');
                button!.innerHTML = 'Name ↑';
            }
        if (tag1.tag != tag2.tag) {

            return (tag1.tag > tag2.tag) ? -1 : 1; // Sort for tag Name
        } // If both have the same text (shouldnt be possible, aber sicher ist sicher)

        return (tag1.id > tag2.id) ? -1 : 1; // sort for tag id
    })
    createTagString(tags);
}

function resetTagsSortButtons() {
    let title = document.getElementById('sortByName');
    let useful = document.getElementById('sortByUsefulness');
    let usage = document.getElementById('sortByUsage');
    title?.setAttribute('onclick', 'sortTags(\'title\')');
    useful?.setAttribute('onclick', 'sortTags(\'usefulness\')');
    usage?.setAttribute('onclick', 'sortTags(\'usage\')');
    title!.innerHTML = 'Name';
    useful!.innerHTML = 'Bewertung';
    usage!.innerHTML = 'Verwendung';
}