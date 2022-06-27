class MyAnswer extends AnswerParent {
    constructor(id: number, title: string, answerPageAnswer: string, views: number, answerType: AnswerType, isHidden: string, tags: MyTag[], files: MyFile[], averageTagsUsefulness: number) {
        super(id, title, (isHidden === "true"), answerType, averageTagsUsefulness, views);

        this._answerPageAnswer = answerPageAnswer;
        this._tags = tags;
        this._files = files;
    }

    private _answerPageAnswer: string;

    get answerPageAnswer(): string {
        return this._answerPageAnswer;
    }

    set answerPageAnswer(value: string) {
        this._answerPageAnswer = value;
    }

    private _files: MyFile[] = [];

    get files(): MyFile[] {
        return this._files;
    }

    set files(value: MyFile[]) {
        this._files = value;
    }

    private _tags: MyTag[] = [];

    get tags(): MyTag[] {
        return this._tags;
    }

    set tags(value: MyTag[]) {
        this._tags = value;
    }

    getTagsAtIndex(index: number): MyTag {
        return this._tags[index];
    }

    removeTag(tag: MyTag) {
        this.removeTagById(tag.id);
    }

    removeTagById(tagId: number) {
        tags.forEach(currentTag => {
            if (currentTag.id === tagId) {
                const index = tags.indexOf(currentTag);
                tags = [...tags.slice(0, index), ...tags.slice(index + 1)];
            }
        })
    }

    toAnswerHTML(details: boolean = true): HTMLElement {
        if (details) {
            let div = document.createElement('div');
            div.classList.add('single-answer', 'no-details');
            let idHTML = document.createElement('span');
            idHTML.classList.add("id-hidden");
            idHTML.innerHTML = String(this.id);
            let title = document.createElement('span');
            title.classList.add("answerTitle");
            title.innerHTML = this.title;
            let views = document.createElement('span');
            views.innerHTML = 'Aufrufe: ' + this.views;
            views.classList.add('answerViews')
            div.append(idHTML, title, views)
            return div;
        }
        let div = document.createElement('div');
        div.classList.add('single-answer', 'details');
        let idHTML = document.createElement('span');
        idHTML.classList.add("id-hidden");
        idHTML.innerHTML = String(this.id);
        let view = document.createElement('span');
        view.classList.add("answerTitle");
        view.innerHTML = this.title + ' Views: ' + this.views;
        return div;
    }
}

function sortAnswersType(selectElement: HTMLSelectElement) {
    let value: number = +selectElement.value;

    if (value > 0) {

        let foundAnswers: MyAnswer[] = [];
        for (let answer of answers) {
            if (answer.answerType.id == (value - 1)) {
                foundAnswers.push(answer);
            }
        }
        createAnswerString(foundAnswers);
    } else {
        createAnswerString(answers);
    }
}

function sortAnswersRange() {
    let rangeInput = document.getElementById('sortByRange') as HTMLInputElement;
    let range: number = parseInt(rangeInput.value);
    let sortNoTag = (document.getElementById('sortByUPDOWN') as HTMLInputElement).value;
    let labelSort = document.getElementById('labelSort') as HTMLElement;
    labelSort.innerHTML = String(range);
    allAnswers = answers;
    answers = [];

    for (let i = 0; i < allAnswers.length; i++) {
        let useful: number = parseInt((Math.round((allAnswers[i].averageUsefulness + Number.EPSILON) * 100)).toFixed(0));

        if (sortNoTag === 'False') {
            if (useful <= range) {
                answers.push(allAnswers[i]);
            }
        } else if (sortNoTag === 'True') {
            if (useful >= range) {
                answers.push(allAnswers[i]);
            }
        }
    }

    createAnswerString(answers);

    answers = allAnswers;
    allAnswers = [];
}

function sortAnswersRangeUPDOWN(up: boolean) {
    let updown = document.getElementById('sortByUPDOWN') as HTMLInputElement;
    if (up) {
        updown.innerHTML = "Darunter";
        updown.value = "False";
        updown.setAttribute('onclick', 'sortAnswersRangeUPDOWN(false)');
        sortAnswersRange();
    } else {
        updown.innerHTML = "Darüber";
        updown.value = "True";
        updown.setAttribute('onclick', 'sortAnswersRangeUPDOWN(true)');
        sortAnswersRange();
    }
}

function sortAnswersNoTag(checked: boolean) {
    let checkbox = document.getElementById('sortByNoTag');
    let range = document.getElementById('sortByRange') as HTMLInputElement;
    let rangeBt = document.getElementById('sortByUPDOWN') as HTMLInputElement;
    range.value = '0';

    if (checked) {
        checkbox?.setAttribute('onclick', 'sortAnswersNoTag(false)');
        range.disabled = true;
        rangeBt.disabled = true;
        allAnswers = answers;
        answers = [];
        allAnswers.forEach((answer: MyAnswer) => {
            if (answer.tags.length === 0) {
                answers.push(answer);
            }
        })
        createAnswerString(answers);
    } else {
        answers = allAnswers;
        allAnswers = [];
        checkbox?.setAttribute('onclick', 'sortAnswersNoTag(true)')
        range.disabled = false;
        range.value = '0';
        rangeBt.disabled = false;
        createAnswerString(answers);
    }
}

function sortAnswers(choose: string, first: boolean = false) {
    resetAnswerSortButtons();
    let unsortedAnswers: MyAnswer[] = Array.prototype.slice.call(answers);
    // usefulness descending
    checkPageExecute(() => {
        answers = unsortedAnswers.sort(function (answer1: MyAnswer, answer2: MyAnswer) {
            let tempSort = choose;
            let dontFind: boolean = false;
            if (tempSort === 'usefulness') {
                let button = document.getElementById('sortByUsefulness');
                button?.setAttribute('onclick', 'reverseSortAnswers(\'usefulness\')');
                button!.innerHTML = 'Bewertung ↓';
                if (answer1.averageUsefulness != answer2.averageUsefulness) {
                    return (answer1.averageUsefulness > answer2.averageUsefulness) ? -1 : 1; // If answer1 is more useful, return -1, if answer2 is more useful return 1
                } // If both have the same usefulness (would return 0)
                else {
                    tempSort = 'upvotes';
                    dontFind = true;
                }
            }
            if (tempSort === 'upvotes') {
                const upvotes = [
                    answer1.tags.reduce(((previousValue, currentValue) => {
                        return previousValue + currentValue.upvotes
                    }), 0),
                    answer2.tags.reduce(((previousValue, currentValue) => {
                        return previousValue + currentValue.upvotes
                    }), 0)
                ]
                if (upvotes[0] > upvotes[1]) {
                    return -1;
                } else {
                    return 1;
                }
            }
            if (tempSort === 'views') {
                let button = document.getElementById('sortByViews');
                button?.setAttribute('onclick', 'reverseSortAnswers(\'views\')');
                button!.innerHTML = 'Aufrufe ↓';
                if (answer1.views != answer2.views) {
                    return (answer1.views > answer2.views) ? -1 : 1; // If answer1 is more views, return -1, if answer2 is more views return 1
                } // If both have the same views (would return 0)
                else {
                    dontFind = true;
                }
            }
            if (tempSort === 'title' || dontFind)
                if (!dontFind) {
                    let button = document.getElementById('sortByName');
                    button?.setAttribute('onclick', 'reverseSortAnswers(\'title\')');
                    button!.innerHTML = 'Name ↓';
                }
            if (answer1.title != answer2.title) {
                return (answer1.title < answer2.title) ? -1 : 1; // Sort for tag Name
            } // If both have the same text (shouldn't be possible, aber sicher ist sicher)

            return (answer1.id > answer2.id) ? -1 : 1; // sort for tag id
        })
        if (!first) {
            createAnswerString(answers);
        }
    }, 'answers')

}

function reverseSortAnswers(choose: string) {
    resetAnswerSortButtons();
    let unsortedAnswers: MyAnswer[] = Array.prototype.slice.call(answers);
    // usefulness descending
    answers = unsortedAnswers.sort(function (answer1: MyAnswer, answer2: MyAnswer) {
            let dontFind: boolean = false;
            if (choose === 'usefulness') {
                let button = document.getElementById('sortByUsefulness');
                button?.setAttribute('onclick', 'sortAnswers(\'usefulness\')');
                button!.innerHTML = 'Bewertung ↑';
                if (answer1.averageUsefulness != answer2.averageUsefulness) {

                    return (answer1.averageUsefulness < answer2.averageUsefulness) ? -1 : 1;
                } // If both have the same usefulness (would return 0)
                else
                    dontFind = true;
            }
            if (choose === 'views') {
                let button = document.getElementById('sortByViews');
                button?.setAttribute('onclick', 'sortAnswers(\'views\')');
                button!.innerHTML = 'Aufrufe ↑';
                if (answer1.views != answer2.views) {

                    return (answer1.views < answer2.views) ? -1 : 1;
                } // If both have the same views (would return 0)
                else
                    dontFind = true;
            }
            if (choose === 'title' || dontFind) {
                if (!dontFind) {
                    let button = document.getElementById('sortByName');
                    button?.setAttribute('onclick', 'sortAnswers(\'title\')');
                    button!.innerHTML = 'Name ↑';
                }
                if (answer1.title != answer2.title) {

                    return (answer1.title > answer2.title) ? -1 : 1; // Sort for tag Name
                } // If both have the same text (shouldnt be possible, aber sicher ist sicher)
            }

            return (answer1.id < answer2.id) ? -1 : 1; // sort for tag id
        }
    )
    createAnswerString(answers);
}

function resetAnswerSortButtons() {
    checkPageExecute(() => {
        let title = document.getElementById('sortByName');
        let useful = document.getElementById('sortByUsefulness');
        let view = document.getElementById('sortByViews');
        title?.setAttribute('onclick', 'sortAnswers(\'title\')');
        useful?.setAttribute('onclick', 'sortAnswers(\'usefulness\')');
        view?.setAttribute('onclick', 'sortAnswers(\'views\')');
        title!.innerHTML = 'Name';
        useful!.innerHTML = 'Bewertung';
        view!.innerHTML = 'Aufrufe'
    }, 'answers')
}