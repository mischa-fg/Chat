"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
class MyAnsweredQuestion extends QuestionParent {
    constructor(id, question, upvotes, downvotes, views, myAnswers = null) {
        super(id, question, views);
        this._myAnswers = myAnswers;
        this._upvotes = upvotes;
        this._downvotes = downvotes;
    }
    get myAnswers() {
        return this._myAnswers;
    }
    set myAnswers(value) {
        this._myAnswers = value;
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
    toAnsweredQuestionHTML(canBeDeleted = true) {
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
function sortAnsweredQuestions(tableID = null, canBeDeleted = true) {
    let unsortedAnsweredQuestions = Array.prototype.slice.call(answeredQuestions);
    answeredQuestions = unsortedAnsweredQuestions.sort(function (answeredQuestion1, answeredQuestion2) {
        if (answeredQuestion1.upvotes != answeredQuestion2.upvotes) {
            return ((answeredQuestion1.upvotes - answeredQuestion1.downvotes) > (answeredQuestion2.upvotes - answeredQuestion2.downvotes)) ? -1 : 1;
        }
        if (answeredQuestion1.question != answeredQuestion2.question) {
            return (answeredQuestion1.question < answeredQuestion2.question) ? -1 : 1;
        }
        return (answeredQuestion1.id < answeredQuestion2.id) ? -1 : 1;
    });
    if (tableID != null) {
        createAnsweredQuestionString(answeredQuestions, tableID, canBeDeleted);
    }
}
function createAnsweredQuestionString(answeredQuestions, tableID, canBeDeleted = true) {
    let value;
    if (answeredQuestions != null && answeredQuestions.length > 0) {
        value = '';
        for (let answeredQuestion of answeredQuestions) {
            value += answeredQuestion.toAnsweredQuestionHTML(canBeDeleted).outerHTML;
        }
    }
    else {
        if (answeredQuestions == null) {
            value = "couldn't load question suggestions!";
        }
        else {
            value = "Es wurden keine Fragen gefunden!";
        }
    }
    $("." + tableID).empty();
    document.getElementById(tableID).innerHTML = value;
}
function deleteAnsweredQuestion(id) {
    return __awaiter(this, void 0, void 0, function* () {
        if (confirm("Wollen sie diese Frage wirklich löschen? Alle dazugehörigen Statistiken werden gelöscht.\nDies kann nicht mehr rückgängig gemacht werden!")) {
            const page = pageCheck;
            try {
                yield fetch(`${server}/services/adminTool/deleteAnsweredQuestion`, {
                    method: 'post',
                    body: `questionID=${id}`
                }).then((response => {
                    var _a;
                    const ids = document.getElementsByClassName('id-hidden');
                    for (const cid of ids) {
                        if (cid.innerHTML === String(id)) {
                            (_a = cid.parentElement) === null || _a === void 0 ? void 0 : _a.remove();
                        }
                    }
                }));
            }
            catch (e) {
                checkPageExecute(() => popup(false, 'Frage konnte nicht gelöscht werden!'), page);
            }
        }
    });
}
