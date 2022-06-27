class Token {
    private _active: boolean = true;

    cancel() {
        this._active = false;
    }

    isActive() {
        return this._active;
    }

    executeIfActive(fn: () => void) {
        if (this._active) fn.call(this);
    }
}