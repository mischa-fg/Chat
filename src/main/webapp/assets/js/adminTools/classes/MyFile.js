"use strict";
class MyFile extends IDParent {
    constructor(id, fileName, fileType) {
        super(id);
        this._fileName = fileName;
        this._fileType = fileType;
    }
    get fileName() {
        return this._fileName;
    }
    set fileName(value) {
        this._fileName = value;
    }
    get fileType() {
        return this._fileType;
    }
    set fileType(value) {
        this._fileType = value;
    }
}
