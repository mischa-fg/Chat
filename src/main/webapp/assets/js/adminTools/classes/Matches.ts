class Matches {
    constructor(id: number, word: string, tag: ServerTag, status: string, upvote: number, downvote: number) {
        this._id = id;
        this._word = word;
        this._tag = tag;
        this._upvote = +upvote;
        this._downvote = +downvote;
        this._status = status;
        this._usefulness = +calcPercentage(this._upvote, this._downvote);
    }

    private _id: number;

    get id(): number {
        return this._id;
    }

    set id(value: number) {
        this._id = value;
    }

    private _word: string;

    get word(): string {
        return this._word;
    }

    set word(value: string) {
        this._word = value;
    }

    private _tag: ServerTag;

    get tag(): ServerTag {
        return this._tag;
    }

    set tag(value: ServerTag) {
        this._tag = value;
    }

    private _upvote: number;

    get upvote(): number {
        return this._upvote;
    }

    set upvote(value: number) {
        this._upvote = value;
    }

    private _downvote: number;

    get downvote(): number {
        return this._downvote;
    }

    set downvote(value: number) {
        this._downvote = value;
    }

    private _usefulness: number;

    get usefulness(): number {
        return this._usefulness;
    }

    set usefulness(value: number) {
        this._usefulness = value;
    }

    private _status: string;

    get status(): string {
        return this._status;
    }

    set status(value: string) {
        this._status = value;
    }

    toMatchHTML(): HTMLDivElement {
        

        let matchId = "matchId-" + this.id;
        let row = document.createElement("tr");
        row.classList.add('single-tag', 'c-default', 'big');
        row.setAttribute("id", `${matchId}`);

        // Tag ID hidden
        let idHTML = document.createElement("td");
        idHTML.innerHTML = String(this.id);
        idHTML.classList.add("id-hidden");

        // Matched Word
        let word = document.createElement("td");
        word.innerHTML = this._word;
        word.classList.add('left');

        // Matched Tag
        let tag = document.createElement("td");
        tag.innerHTML = this._tag.tag;
        tag.classList.add("tagName");
        tag.setAttribute("onclick", `loadPage('tagsDetails.html', 'tagButton', true, ${this._tag.id})`);
        tag.style.cursor = 'pointer';

        // Usefulness
        let usefulnessHTML = document.createElement("td");
        let colorDiv = document.createElement('div');
        colorDiv.classList.add('rating');
        colorDiv.innerHTML = String(Math.round(this._usefulness * 100).toFixed(0)) + "%";
        colorDiv.style.background = getColor(this.usefulness);
        usefulnessHTML.classList.add("additionalTagInfos");
        usefulnessHTML.appendChild(colorDiv);

        // Tag Upvotes
        let upvoteTd = document.createElement("td");
        upvoteTd.innerHTML = String(this._upvote);
        upvoteTd.classList.add("additionalTagInfos");

        // Tag Downvotes
        let downvoteTd = document.createElement("td");
        downvoteTd.innerHTML = String(this._downvote);
        downvoteTd.classList.add("additionalTagInfos");

        // Reset Rating Button
        const resetContainer = document.createElement('td');
        const reset = document.createElement('button');

        reset.setAttribute('onclick', `resetMatchRating(${this.id})`);
        reset.classList.add('button');
        reset.innerHTML = 'Zurücksetzen';
        resetContainer.append(reset);

        // don't translate Button
        const noTranslateContainer = document.createElement('td');
        const noTranslateButton = document.createElement('button');

        noTranslateButton.setAttribute('onclick', `noTranslate(${this.id})`);
        noTranslateButton.classList.add('button');
        noTranslateButton.innerHTML = 'Nicht übersetzen';
        noTranslateContainer.append(noTranslateButton);

        // Blacklist Word Button
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

function sortMatch(choose: string) {
    resetMatchSortButtons();
    let unsortedMatch: Matches[] = Array.prototype.slice.call(matches);
    // usefulness descending
    matches = unsortedMatch.sort(function (matche1: Matches, matche2: Matches) {
            let dontFind: boolean = false;
            if (choose === 'usefulness') {
                let button = document.getElementById('sortByUsefulness');
                button?.setAttribute('onclick', 'reverseSortMatch(\'usefulness\')');
                button!.innerHTML = 'Bewertung ↓';
                if (matche1.usefulness != matche2.usefulness) {

                    return (matche1.usefulness > matche2.usefulness) ? -1 : 1; // If answer1 is more useful, return -1, if answer2 is more useful return 1
                } // If both have the same usefulness (would return 0)
                else
                    dontFind = true;
            }
            if (choose === 'title' || dontFind) {
                if (!dontFind) {
                    let button = document.getElementById('sortByName');
                    button?.setAttribute('onclick', 'reverseSortMatch(\'title\')');
                    button!.innerHTML = 'Names ↓';
                }
                if (matche1.word != matche2.word) {

                    return (matche1.word < matche2.word) ? -1 : 1; // Sort for tag Name
                } // If both have the same text (shouldnt be possible, aber sicher ist sicher)
            }

            return (matche1.id < matche2.id) ? -1 : 1; // sort for tag id
        }
    )
    createMatchesEntriesString(matches);
}

function reverseSortMatch(choose: string) {
    resetMatchSortButtons();
    let unsortedMatch: Matches[] = Array.prototype.slice.call(matches);
    // usefulness descending
    matches = unsortedMatch.sort(function (matche1: Matches, matche2: Matches) {
            let dontFind: boolean = false;
            if (choose === 'usefulness') {
                let button = document.getElementById('sortByUsefulness');
                button?.setAttribute('onclick', 'sortMatch(\'usefulness\')');
                button!.innerHTML = 'Bewertung ↑';
                if (matche1.usefulness != matche2.usefulness) {

                    return (matche1.usefulness < matche2.usefulness) ? -1 : 1; // If answer1 is more useful, return -1, if answer2 is more useful return 1
                } // If both have the same usefulness (would return 0)
                else
                    dontFind = true;
            }
            if (choose === 'title' || dontFind) {
                if (!dontFind) {
                    let button = document.getElementById('sortByName');
                    button?.setAttribute('onclick', 'sortMatch(\'title\')');
                    button!.innerHTML = 'Name ↑';
                }
                if (matche1.word != matche2.word) {

                    return (matche1.word > matche2.word) ? -1 : 1; // Sort for tag Name
                } // If both have the same text (shouldnt be possible, aber sicher ist sicher)
            }

            return (matche1.id < matche2.id) ? -1 : 1; // sort for tag id
        }
    )
    createMatchesEntriesString(matches);
}

function resetMatchSortButtons() {
    let title = document.getElementById('sortByName');
    let useful = document.getElementById('sortByUsefulness');
    title?.setAttribute('onclick', 'sortMatch(\'title\')');
    useful?.setAttribute('onclick', 'sortMatch(\'usefulness\')');
    title!.innerHTML = 'Name';
    useful!.innerHTML = 'Bewertung';
}
