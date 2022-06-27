class MyFile extends IDParent {
    private _fileName: string;
    private _fileType: string;

    constructor(id: number, fileName: string, fileType: string) {
        super(id);
        this._fileName = fileName;
        this._fileType = fileType;
    }

    get fileName(): string {
        return this._fileName;
    }

    set fileName(value: string) {
        this._fileName = value;
    }

    get fileType(): string {
        return this._fileType;
    }

    set fileType(value: string) {
        this._fileType = value;
    }
}