"use strict";
class AnswerParent extends IDParent {
    constructor(id, title, isHidden, answerType, averageUsefulness, views) {
        super(id);
        this._title = title;
        this._isHidden = isHidden;
        this._answerType = answerType;
        this._averageUsefulness = +averageUsefulness;
        this._views = +views;
    }
    get title() {
        return this._title;
    }
    set title(value) {
        this._title = value;
    }
    get isHidden() {
        return this._isHidden;
    }
    set isHidden(value) {
        this._isHidden = value;
    }
    get answerType() {
        return this._answerType;
    }
    set answerType(value) {
        this._answerType = value;
    }
    get averageUsefulness() {
        return this._averageUsefulness;
    }
    set averageUsefulness(value) {
        this._averageUsefulness = value;
    }
    get views() {
        return this._views;
    }
    set views(value) {
        this._views = value;
    }
}
