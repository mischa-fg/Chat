"use strict";
class MyAnswer extends AnswerParent {
    constructor(id, title, answerPageAnswer, views, answerType, isHidden, tags, files, averageTagsUsefulness) {
        super(id, title, (isHidden === "true"), answerType, averageTagsUsefulness, views);
        this._files = [];
        this._tags = [];
        this._answerPageAnswer = answerPageAnswer;
        this._tags = tags;
        this._files = files;
    }
    get answerPageAnswer() {
        return this._answerPageAnswer;
    }
    set answerPageAnswer(value) {
        this._answerPageAnswer = value;
    }
    get files() {
        return this._files;
    }
    set files(value) {
        this._files = value;
    }
    get tags() {
        return this._tags;
    }
    set tags(value) {
        this._tags = value;
    }
    getTagsAtIndex(index) {
        return this._tags[index];
    }
    removeTag(tag) {
        this.removeTagById(tag.id);
    }
    removeTagById(tagId) {
        tags.forEach(currentTag => {
            if (currentTag.id === tagId) {
                const index = tags.indexOf(currentTag);
                tags = [...tags.slice(0, index), ...tags.slice(index + 1)];
            }
        });
    }
    toAnswerHTML(details = true) {
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
            views.classList.add('answerViews');
            div.append(idHTML, title, views);
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
function sortAnswersType(selectElement) {
    let value = +selectElement.value;
    if (value > 0) {
        let foundAnswers = [];
        for (let answer of answers) {
            if (answer.answerType.id == (value - 1)) {
                foundAnswers.push(answer);
            }
        }
        createAnswerString(foundAnswers);
    }
    else {
        createAnswerString(answers);
    }
}
function sortAnswersRange() {
    let rangeInput = document.getElementById('sortByRange');
    let range = parseInt(rangeInput.value);
    let sortNoTag = document.getElementById('sortByUPDOWN').value;
    let labelSort = document.getElementById('labelSort');
    labelSort.innerHTML = String(range);
    allAnswers = answers;
    answers = [];
    for (let i = 0; i < allAnswers.length; i++) {
        let useful = parseInt((Math.round((allAnswers[i].averageUsefulness + Number.EPSILON) * 100)).toFixed(0));
        if (sortNoTag === 'False') {
            if (useful <= range) {
                answers.push(allAnswers[i]);
            }
        }
        else if (sortNoTag === 'True') {
            if (useful >= range) {
                answers.push(allAnswers[i]);
            }
        }
    }
    createAnswerString(answers);
    answers = allAnswers;
    allAnswers = [];
}
function sortAnswersRangeUPDOWN(up) {
    let updown = document.getElementById('sortByUPDOWN');
    if (up) {
        updown.innerHTML = "Darunter";
        updown.value = "False";
        updown.setAttribute('onclick', 'sortAnswersRangeUPDOWN(false)');
        sortAnswersRange();
    }
    else {
        updown.innerHTML = "Darüber";
        updown.value = "True";
        updown.setAttribute('onclick', 'sortAnswersRangeUPDOWN(true)');
        sortAnswersRange();
    }
}
function sortAnswersNoTag(checked) {
    let checkbox = document.getElementById('sortByNoTag');
    let range = document.getElementById('sortByRange');
    let rangeBt = document.getElementById('sortByUPDOWN');
    range.value = '0';
    if (checked) {
        checkbox === null || checkbox === void 0 ? void 0 : checkbox.setAttribute('onclick', 'sortAnswersNoTag(false)');
        range.disabled = true;
        rangeBt.disabled = true;
        allAnswers = answers;
        answers = [];
        allAnswers.forEach((answer) => {
            if (answer.tags.length === 0) {
                answers.push(answer);
            }
        });
        createAnswerString(answers);
    }
    else {
        answers = allAnswers;
        allAnswers = [];
        checkbox === null || checkbox === void 0 ? void 0 : checkbox.setAttribute('onclick', 'sortAnswersNoTag(true)');
        range.disabled = false;
        range.value = '0';
        rangeBt.disabled = false;
        createAnswerString(answers);
    }
}
function sortAnswers(choose, first = false) {
    resetAnswerSortButtons();
    let unsortedAnswers = Array.prototype.slice.call(answers);
    checkPageExecute(() => {
        answers = unsortedAnswers.sort(function (answer1, answer2) {
            let tempSort = choose;
            let dontFind = false;
            if (tempSort === 'usefulness') {
                let button = document.getElementById('sortByUsefulness');
                button === null || button === void 0 ? void 0 : button.setAttribute('onclick', 'reverseSortAnswers(\'usefulness\')');
                button.innerHTML = 'Bewertung ↓';
                if (answer1.averageUsefulness != answer2.averageUsefulness) {
                    return (answer1.averageUsefulness > answer2.averageUsefulness) ? -1 : 1;
                }
                else {
                    tempSort = 'upvotes';
                    dontFind = true;
                }
            }
            if (tempSort === 'upvotes') {
                const upvotes = [
                    answer1.tags.reduce(((previousValue, currentValue) => {
                        return previousValue + currentValue.upvotes;
                    }), 0),
                    answer2.tags.reduce(((previousValue, currentValue) => {
                        return previousValue + currentValue.upvotes;
                    }), 0)
                ];
                if (upvotes[0] > upvotes[1]) {
                    return -1;
                }
                else {
                    return 1;
                }
            }
            if (tempSort === 'views') {
                let button = document.getElementById('sortByViews');
                button === null || button === void 0 ? void 0 : button.setAttribute('onclick', 'reverseSortAnswers(\'views\')');
                button.innerHTML = 'Aufrufe ↓';
                if (answer1.views != answer2.views) {
                    return (answer1.views > answer2.views) ? -1 : 1;
                }
                else {
                    dontFind = true;
                }
            }
            if (tempSort === 'title' || dontFind)
                if (!dontFind) {
                    let button = document.getElementById('sortByName');
                    button === null || button === void 0 ? void 0 : button.setAttribute('onclick', 'reverseSortAnswers(\'title\')');
                    button.innerHTML = 'Name ↓';
                }
            if (answer1.title != answer2.title) {
                return (answer1.title < answer2.title) ? -1 : 1;
            }
            return (answer1.id > answer2.id) ? -1 : 1;
        });
        if (!first) {
            createAnswerString(answers);
        }
    }, 'answers');
}
function reverseSortAnswers(choose) {
    resetAnswerSortButtons();
    let unsortedAnswers = Array.prototype.slice.call(answers);
    answers = unsortedAnswers.sort(function (answer1, answer2) {
        let dontFind = false;
        if (choose === 'usefulness') {
            let button = document.getElementById('sortByUsefulness');
            button === null || button === void 0 ? void 0 : button.setAttribute('onclick', 'sortAnswers(\'usefulness\')');
            button.innerHTML = 'Bewertung ↑';
            if (answer1.averageUsefulness != answer2.averageUsefulness) {
                return (answer1.averageUsefulness < answer2.averageUsefulness) ? -1 : 1;
            }
            else
                dontFind = true;
        }
        if (choose === 'views') {
            let button = document.getElementById('sortByViews');
            button === null || button === void 0 ? void 0 : button.setAttribute('onclick', 'sortAnswers(\'views\')');
            button.innerHTML = 'Aufrufe ↑';
            if (answer1.views != answer2.views) {
                return (answer1.views < answer2.views) ? -1 : 1;
            }
            else
                dontFind = true;
        }
        if (choose === 'title' || dontFind) {
            if (!dontFind) {
                let button = document.getElementById('sortByName');
                button === null || button === void 0 ? void 0 : button.setAttribute('onclick', 'sortAnswers(\'title\')');
                button.innerHTML = 'Name ↑';
            }
            if (answer1.title != answer2.title) {
                return (answer1.title > answer2.title) ? -1 : 1;
            }
        }
        return (answer1.id < answer2.id) ? -1 : 1;
    });
    createAnswerString(answers);
}
function resetAnswerSortButtons() {
    checkPageExecute(() => {
        let title = document.getElementById('sortByName');
        let useful = document.getElementById('sortByUsefulness');
        let view = document.getElementById('sortByViews');
        title === null || title === void 0 ? void 0 : title.setAttribute('onclick', 'sortAnswers(\'title\')');
        useful === null || useful === void 0 ? void 0 : useful.setAttribute('onclick', 'sortAnswers(\'usefulness\')');
        view === null || view === void 0 ? void 0 : view.setAttribute('onclick', 'sortAnswers(\'views\')');
        title.innerHTML = 'Name';
        useful.innerHTML = 'Bewertung';
        view.innerHTML = 'Aufrufe';
    }, 'answers');
}
