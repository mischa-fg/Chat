"use strict";
class IDParent {
    constructor(id) {
        this._id = +id;
    }
    get id() {
        return this._id;
    }
    set id(value) {
        this._id = value;
    }
}
