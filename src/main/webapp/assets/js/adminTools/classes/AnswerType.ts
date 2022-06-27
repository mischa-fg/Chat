class AnswerType extends IDParent {
    private _name: string;
    private _groupedTags: boolean
    private _isHiddenDefault: boolean
    private _forceIsHiddenDefault: boolean

    constructor(value: number, name: string, groupedTags: boolean, isHiddenDefault: boolean, forceIsHiddenDefault: boolean) {
        super(value);
        this._name = name;
        this._groupedTags = groupedTags;
        this._isHiddenDefault = isHiddenDefault;
        this._forceIsHiddenDefault = forceIsHiddenDefault;
    }

    get name(): string {
        return this._name;
    }

    set name(value: string) {
        this._name = value;
    }

    get groupedTags(): boolean {
        return this._groupedTags;
    }

    set groupedTags(value: boolean) {
        this._groupedTags = value;
    }

    get isHiddenDefault(): boolean {
        return this._isHiddenDefault;
    }

    set isHiddenDefault(value: boolean) {
        this._isHiddenDefault = value;
    }

    get forceIsHiddenDefault(): boolean {
        return this._forceIsHiddenDefault;
    }

    set forceIsHiddenDefault(value: boolean) {
        this._forceIsHiddenDefault = value;
    }
}