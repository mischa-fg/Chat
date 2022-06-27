"use strict";
class AnswerType extends IDParent {
    constructor(value, name, groupedTags, isHiddenDefault, forceIsHiddenDefault) {
        super(value);
        this._name = name;
        this._groupedTags = groupedTags;
        this._isHiddenDefault = isHiddenDefault;
        this._forceIsHiddenDefault = forceIsHiddenDefault;
    }
    get name() {
        return this._name;
    }
    set name(value) {
        this._name = value;
    }
    get groupedTags() {
        return this._groupedTags;
    }
    set groupedTags(value) {
        this._groupedTags = value;
    }
    get isHiddenDefault() {
        return this._isHiddenDefault;
    }
    set isHiddenDefault(value) {
        this._isHiddenDefault = value;
    }
    get forceIsHiddenDefault() {
        return this._forceIsHiddenDefault;
    }
    set forceIsHiddenDefault(value) {
        this._forceIsHiddenDefault = value;
    }
}
