class AnswerParent extends IDParent {
    constructor(id: number, title: string, isHidden: boolean, answerType: AnswerType, averageUsefulness: number, views: number) {
        super(id);
        this._title = title;
        this._isHidden = isHidden;
        this._answerType = answerType;
        this._averageUsefulness = +averageUsefulness;
        this._views = +views;
    }

    private _title: string;

    get title(): string {
        return this._title;
    }

    set title(value: string) {
        this._title = value;
    }

    private _isHidden: boolean;

    get isHidden(): boolean {
        return this._isHidden;
    }

    set isHidden(value: boolean) {
        this._isHidden = value;
    }

    private _answerType: any;

    get answerType(): any {
        return this._answerType;
    }

    set answerType(value: any) {
        this._answerType = value;
    }

    private _averageUsefulness: number;

    get averageUsefulness(): number {
        return this._averageUsefulness;
    }

    set averageUsefulness(value: number) {
        this._averageUsefulness = value;
    }

    private _views: number;

    get views(): number {
        return this._views;
    }

    set views(value: number) {
        this._views = value;
    }
}