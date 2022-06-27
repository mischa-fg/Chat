"use strict";
class Matches {
    constructor(id, word, tag, status, upvote, downvote) {
        this._id = id;
        this._word = word;
        this._tag = tag;
        this._upvote = +upvote;
        this._downvote = +downvote;
        this._status = status;
        this._usefulness = +calcPercentage(this._upvote, this._downvote);
    }
    get id() {
        return this._id;
    }
    set id(value) {
        this._id = value;
    }
    get word() {
        return this._word;
    }
    set word(value) {
        this._word = value;
    }
    get tag() {
        return this._tag;
    }
    set tag(value) {
        this._tag = value;
    }
    get upvote() {
        return this._upvote;
    }
    set upvote(value) {
        this._upvote = value;
    }
    get downvote() {
        return this._downvote;
    }
    set downvote(value) {
        this._downvote = value;
    }
    get usefulness() {
        return this._usefulness;
    }
    set usefulness(value) {
        this._usefulness = value;
    }
    get status() {
        return this._status;
    }
    set status(value) {
        this._status = value;
    }
    toMatchHTML() {
        let matchId = "matchId-" + this.id;
        let row = document.createElement("tr");
        row.classList.add('single-tag', 'c-default', 'big');
        row.setAttribute("id", `${matchId}`);
        let idHTML = document.createElement("td");
        idHTML.innerHTML = String(this.id);
        idHTML.classList.add("id-hidden");
        let word = document.createElement("td");
        word.innerHTML = this._word;
        word.classList.add('left');
        let tag = document.createElement("td");
        tag.innerHTML = this._tag.tag;
        tag.classList.add("tagName");
        tag.setAttribute("onclick", `loadPage('tagsDetails.html', 'tagButton', true, ${this._tag.id})`);
        tag.style.cursor = 'pointer';
        let usefulnessHTML = document.createElement("td");
        let colorDiv = document.createElement('div');
        colorDiv.classList.add('rating');
        colorDiv.innerHTML = String(Math.round(this._usefulness * 100).toFixed(0)) + "%";
        colorDiv.style.background = getColor(this.usefulness);
        usefulnessHTML.classList.add("additionalTagInfos");
        usefulnessHTML.appendChild(colorDiv);
        let upvoteTd = document.createElement("td");
        upvoteTd.innerHTML = String(this._upvote);
        upvoteTd.classList.add("additionalTagInfos");
        let downvoteTd = document.createElement("td");
        downvoteTd.innerHTML = String(this._downvote);
        downvoteTd.classList.add("additionalTagInfos");
        const resetContainer = document.createElement('td');
        const reset = document.createElement('button');
        reset.setAttribute('onclick', `resetMatchRating(${this.id})`);
        reset.classList.add('button');
        reset.innerHTML = 'Zurücksetzen';
        resetContainer.append(reset);
        const noTranslateContainer = document.createElement('td');
        const noTranslateButton = document.createElement('button');
        noTranslateButton.setAttribute('onclick', `noTranslate(${this.id})`);
        noTranslateButton.classList.add('button');
        noTranslateButton.innerHTML = 'Nicht übersetzen';
        noTranslateContainer.append(noTranslateButton);
        const buttonContainer = document.createElement('td');
        const button = document.createElement('button');
        button.setAttribute('onclick', `blacklistMatch(${this.id}, '${this.word}')`);
        button.classList.add('button');
        button.innerHTML = 'Blacklisten';
        buttonContainer.append(button);
        let statusTd = document.createElement('td');
        statusTd.innerHTML = this.status;
        row.append(idHTML, word, tag, upvoteTd, downvoteTd, usefulnessHTML, statusTd, resetContainer, noTranslateContainer, buttonContainer);
        return row;
    }
}
function sortMatch(choose) {
    resetMatchSortButtons();
    let unsortedMatch = Array.prototype.slice.call(matches);
    matches = unsortedMatch.sort(function (matche1, matche2) {
        let dontFind = false;
        if (choose === 'usefulness') {
            let button = document.getElementById('sortByUsefulness');
            button === null || button === void 0 ? void 0 : button.setAttribute('onclick', 'reverseSortMatch(\'usefulness\')');
            button.innerHTML = 'Bewertung ↓';
            if (matche1.usefulness != matche2.usefulness) {
                return (matche1.usefulness > matche2.usefulness) ? -1 : 1;
            }
            else
                dontFind = true;
        }
        if (choose === 'title' || dontFind) {
            if (!dontFind) {
                let button = document.getElementById('sortByName');
                button === null || button === void 0 ? void 0 : button.setAttribute('onclick', 'reverseSortMatch(\'title\')');
                button.innerHTML = 'Names ↓';
            }
            if (matche1.word != matche2.word) {
                return (matche1.word < matche2.word) ? -1 : 1;
            }
        }
        return (matche1.id < matche2.id) ? -1 : 1;
    });
    createMatchesEntriesString(matches);
}
function reverseSortMatch(choose) {
    resetMatchSortButtons();
    let unsortedMatch = Array.prototype.slice.call(matches);
    matches = unsortedMatch.sort(function (matche1, matche2) {
        let dontFind = false;
        if (choose === 'usefulness') {
            let button = document.getElementById('sortByUsefulness');
            button === null || button === void 0 ? void 0 : button.setAttribute('onclick', 'sortMatch(\'usefulness\')');
            button.innerHTML = 'Bewertung ↑';
            if (matche1.usefulness != matche2.usefulness) {
                return (matche1.usefulness < matche2.usefulness) ? -1 : 1;
            }
            else
                dontFind = true;
        }
        if (choose === 'title' || dontFind) {
            if (!dontFind) {
                let button = document.getElementById('sortByName');
                button === null || button === void 0 ? void 0 : button.setAttribute('onclick', 'sortMatch(\'title\')');
                button.innerHTML = 'Name ↑';
            }
            if (matche1.word != matche2.word) {
                return (matche1.word > matche2.word) ? -1 : 1;
            }
        }
        return (matche1.id < matche2.id) ? -1 : 1;
    });
    createMatchesEntriesString(matches);
}
function resetMatchSortButtons() {
    let title = document.getElementById('sortByName');
    let useful = document.getElementById('sortByUsefulness');
    title === null || title === void 0 ? void 0 : title.setAttribute('onclick', 'sortMatch(\'title\')');
    useful === null || useful === void 0 ? void 0 : useful.setAttribute('onclick', 'sortMatch(\'usefulness\')');
    title.innerHTML = 'Name';
    useful.innerHTML = 'Bewertung';
}
