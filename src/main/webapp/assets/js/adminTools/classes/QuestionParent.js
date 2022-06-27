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
class QuestionParent extends IDParent {
    constructor(id, question, views) {
        super(id);
        this._views = views;
        this._question = question;
    }
    get question() {
        return this._question;
    }
    set question(value) {
        this._question = value;
    }
    get views() {
        return this._views;
    }
    set views(value) {
        this._views = value;
    }
    toUnansweredQuestionHTML() {
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
function sortUnansweredQuestions(tableID = null) {
    let unsortedQuestionParent = Array.prototype.slice.call(unansweredQuestions);
    unansweredQuestions = unsortedQuestionParent.sort(function (questionParent1, questionParent2) {
        if (questionParent1.views != questionParent2.views) {
            return (questionParent1.views > questionParent2.views) ? -1 : 1;
        }
        if (questionParent1.question != questionParent2.question) {
            return (questionParent1.question < questionParent2.question) ? -1 : 1;
        }
        return (questionParent1.id < questionParent2.id) ? -1 : 1;
    });
    if (tableID != null) {
        createUnansweredQuestionString(unansweredQuestions, tableID);
    }
}
function createUnansweredQuestionString(unansweredQuestions, tableID) {
    let value;
    if (unansweredQuestions != null && unansweredQuestions.length > 0) {
        value = '';
        for (let answeredQuestion of unansweredQuestions) {
            value += answeredQuestion.toUnansweredQuestionHTML().outerHTML;
        }
    }
    else {
        if (unansweredQuestions == null) {
            value = "couldn't load questions!";
        }
        else {
            value = "Es wurden keine Fragen gefunden!";
        }
    }
    $("." + tableID).empty();
    document.getElementById(tableID).innerHTML = value;
}
function deleteUnansweredQuestion(id) {
    return __awaiter(this, void 0, void 0, function* () {
        if (confirm("Wollen sie diese Frage wirklich löschen? Alle dazugehörigen Statistiken werden gelöscht.\nDies kann nicht rückgängig gemacht werden!")) {
            const page = pageCheck;
            try {
                yield fetch(`${server}/services/adminTool/deleteUnansweredQuestion`, {
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
