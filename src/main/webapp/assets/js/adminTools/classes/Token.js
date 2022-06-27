"use strict";
class Token {
    constructor() {
        this._active = true;
    }
    cancel() {
        this._active = false;
    }
    isActive() {
        return this._active;
    }
    executeIfActive(fn) {
        if (this._active)
            fn.call(this);
    }
}
