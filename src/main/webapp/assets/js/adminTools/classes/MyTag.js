"use strict";
class MyTag extends IDParent {
    constructor(id, tag, upvotes, downvotes, usage, amountAnswers = -1, answers) {
        super(id);
        this._tag = tag;
        this._upvotes = +upvotes;
        this._downvotes = +downvotes;
        this._usage = +usage;
        this._usefulness = +calcPercentage(this._upvotes, this._downvotes);
        this._amountAnswers = +amountAnswers;
        this._answers = answers;
    }
    get tag() {
        return this._tag;
    }
    set tag(value) {
        this._tag = value;
    }
    get upvotes() {
        return this._upvotes;
    }
    set upvotes(value) {
        this._upvotes = value;
    }
    get downvotes() {
        return this._downvotes;
    }
    set downvotes(value) {
        this._downvotes = value;
    }
    get usefulness() {
        return this._usefulness;
    }
    set usefulness(value) {
        this._usefulness = value;
    }
    get usage() {
        return this._usage;
    }
    set usage(value) {
        this._usage = value;
    }
    get amountAnswers() {
        return this._amountAnswers;
    }
    set amountAnswers(value) {
        this._amountAnswers = value;
    }
    get answers() {
        return this._answers;
    }
    set answers(value) {
        this._answers = value;
    }
    removeAnswer(answer) {
        this.removeAnswerById(answer.id);
    }
    removeAnswerById(answerID) {
        singleTagAnswers.forEach((currentAnswer) => {
            if (currentAnswer.id === answerID) {
                const index = singleTagAnswers.indexOf(currentAnswer);
                singleTagAnswers = [...singleTagAnswers.slice(0, index), ...singleTagAnswers.slice(index + 1)];
            }
        });
    }
    toTagHTML(small = false, canBeDeleted = false, linkedToAnswer = false) {
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
        let row = document.createElement("tr");
        row.classList.add("single-tag");
        row.classList.add("big");
        row.setAttribute("id", `${tagStringID}`);
        let idHTML = document.createElement("td");
        idHTML.innerHTML = String(this.id);
        idHTML.classList.add("id-hidden");
        idHTML.setAttribute(`onclick`, `loadPage("tagsDetails.html","tagButton","true",\"${this.id}\")`);
        let tag = document.createElement("td");
        tag.innerHTML = String(this._tag);
        tag.classList.add("tagName", 'left');
        tag.setAttribute(`onclick`, `loadPage("tagsDetails.html","tagButton","true",\"${this.id}\")`);
        let usage = document.createElement("td");
        usage.innerHTML = String(this._usage);
        usage.classList.add("additionalTagInfos");
        usage.setAttribute(`onclick`, `loadPage("tagsDetails.html","tagButton","true",\"${this.id}\")`);
        let amount = document.createElement("td");
        amount.innerHTML = amountAnswersString;
        amount.classList.add("additionalTagInfos");
        amount.setAttribute(`onclick`, `loadPage("tagsDetails.html","tagButton","true",\"${this.id}\")`);
        let usefulnessHTML = document.createElement("td");
        let ratingDiv = document.createElement('div');
        ratingDiv.classList.add('rating');
        ratingDiv.innerHTML = String(Math.round(this._usefulness * 100).toFixed(0)) + "%";
        ratingDiv.style.background = getColor(this.usefulness);
        usefulnessHTML.appendChild(ratingDiv);
        usefulnessHTML.classList.add("additionalTagInfos");
        usefulnessHTML.setAttribute(`onclick`, `loadPage("tagsDetails.html","tagButton","true",\"${this.id}\")`);
        let upvoteTd = document.createElement("td");
        upvoteTd.innerHTML = String(this._upvotes);
        upvoteTd.classList.add("additionalTagInfos");
        upvoteTd.setAttribute(`onclick`, `loadPage("tagsDetails.html","tagButton","true",\"${this.id}\")`);
        let downvoteTd = document.createElement("td");
        downvoteTd.innerHTML = String(this._downvotes);
        downvoteTd.classList.add("additionalTagInfos");
        downvoteTd.setAttribute(`onclick`, `loadPage("tagsDetails.html","tagButton","true",\"${this.id}\")`);
        let deleteButtonTD = document.createElement("td");
        deleteButtonTD.classList.add("additionalTagInfos");
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
function sortTags(choose, first = false) {
    resetTagsSortButtons();
    let unsortedTags = Array.prototype.slice.call(tags);
    tags = unsortedTags.sort(function (tag1, tag2) {
        let dontFind = false;
        if (choose === 'usefulness') {
            let button = document.getElementById('sortByUsefulness');
            button === null || button === void 0 ? void 0 : button.setAttribute('onclick', 'reverseSortTags(\'usefulness\')');
            button.innerHTML = 'Bewertung ↓';
            if (tag1.usefulness != tag2.usefulness) {
                return (tag1.usefulness > tag2.usefulness) ? -1 : 1;
            }
            else
                dontFind = true;
        }
        if (choose === 'usage') {
            let button = document.getElementById('sortByUsage');
            button === null || button === void 0 ? void 0 : button.setAttribute('onclick', 'reverseSortTags(\'usage\')');
            button.innerHTML = 'Verwendung ↓';
            if (tag1.usage != tag2.usage) {
                return (tag1.usage > tag2.usage) ? -1 : 1;
            }
            else {
                dontFind = true;
            }
        }
        if (choose === 'title' || dontFind)
            if (!dontFind) {
                let button = document.getElementById('sortByName');
                button === null || button === void 0 ? void 0 : button.setAttribute('onclick', 'reverseSortTags(\'title\')');
                button.innerHTML = 'Name ↓';
            }
        if (tag1.tag != tag2.tag) {
            return (tag1.tag < tag2.tag) ? -1 : 1;
        }
        return (tag1.id > tag2.id) ? -1 : 1;
    });
    if (!first)
        createTagString(tags);
}
function reverseSortTags(choose) {
    resetTagsSortButtons();
    let unsortedTags = Array.prototype.slice.call(tags);
    tags = unsortedTags.sort(function (tag1, tag2) {
        let dontFind = false;
        if (choose === 'usefulness') {
            let button = document.getElementById('sortByUsefulness');
            button === null || button === void 0 ? void 0 : button.setAttribute('onclick', 'sortTags(\'usefulness\')');
            button.innerHTML = 'Bewertung ↑';
            if (tag1.usefulness != tag2.usefulness) {
                return (tag1.usefulness < tag2.usefulness) ? -1 : 1;
            }
            else
                dontFind = true;
        }
        if (choose === 'usage') {
            let button = document.getElementById('sortByUsage');
            button === null || button === void 0 ? void 0 : button.setAttribute('onclick', 'sortTags(\'usage\')');
            button.innerHTML = 'Verwendung ↑';
            if (tag1.usage != tag2.usage) {
                return (tag1.usage < tag2.usage) ? -1 : 1;
            }
        }
        if (choose === 'title' || dontFind)
            if (!dontFind) {
                let button = document.getElementById('sortByName');
                button === null || button === void 0 ? void 0 : button.setAttribute('onclick', 'sortTags(\'title\')');
                button.innerHTML = 'Name ↑';
            }
        if (tag1.tag != tag2.tag) {
            return (tag1.tag > tag2.tag) ? -1 : 1;
        }
        return (tag1.id > tag2.id) ? -1 : 1;
    });
    createTagString(tags);
}
function resetTagsSortButtons() {
    let title = document.getElementById('sortByName');
    let useful = document.getElementById('sortByUsefulness');
    let usage = document.getElementById('sortByUsage');
    title === null || title === void 0 ? void 0 : title.setAttribute('onclick', 'sortTags(\'title\')');
    useful === null || useful === void 0 ? void 0 : useful.setAttribute('onclick', 'sortTags(\'usefulness\')');
    usage === null || usage === void 0 ? void 0 : usage.setAttribute('onclick', 'sortTags(\'usage\')');
    title.innerHTML = 'Name';
    useful.innerHTML = 'Bewertung';
    usage.innerHTML = 'Verwendung';
}
