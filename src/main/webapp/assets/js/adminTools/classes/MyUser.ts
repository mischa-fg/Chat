class MyUser extends IDParent {
    private _email: string;
    private _canCreateUser: boolean;
    private _lastTimeLoggedIn: string;
    private _isCurrentUser: boolean;

    constructor(id: number, email: string, canCreateUser: boolean, lastTimeLoggedIn: string, isCurrentUser: boolean) {
        super(id);
        this._email = email;
        this._canCreateUser = canCreateUser;
        this._lastTimeLoggedIn = lastTimeLoggedIn;
        this._isCurrentUser = isCurrentUser;
    }

    get canCreateUser(): boolean {
        return this._canCreateUser;
    }

    set canCreateUser(value: boolean) {
        this._canCreateUser = value;
    }

    get email(): string {
        return this._email;
    }

    set email(value: string) {
        this._email = value;
    }

    get lastTimeLoggedIn(): string {
        return this._lastTimeLoggedIn;
    }

    set lastTimeLoggedIn(value: string) {
        this._lastTimeLoggedIn = value;
    }

    get isCurrentUser(): boolean {
        return this._isCurrentUser;
    }

    set isCurrentUser(value: boolean) {
        this._isCurrentUser = value;
    }

    toUserHTML(): HTMLDivElement {
        let userId = "userId-" + this.id;
        let row = document.createElement("tr");
        row.classList.add("single-user");
        row.classList.add("big");
        row.setAttribute("id", `${userId}`);
        let idHTMl = document.createElement("td");
        idHTMl.innerHTML = String(this.id);
        idHTMl.classList.add("id-hidden");
        //idHTMl.setAttribute("onmouseover", 'style="background: rgba(0, 0, 0, 0.1);"');
        //idHTMl.setAttribute("onmouseout", 'style="background: rgb(255, 255, 255);"');

        let email = document.createElement("td");
        email.innerHTML = (this.isCurrentUser) ? this.email + " (Du)" : this.email;
        email.classList.add('left');
        email.setAttribute("onclick", `loadPage("userDetails.jsp","settingsButton",true,${this.id})`);

        const canCreateUserColumn = document.createElement("td");
        canCreateUserColumn.innerHTML = (this.canCreateUser) ? "Ja" : "Nein";
        canCreateUserColumn.setAttribute("onclick", `loadPage("userDetails.jsp","settingsButton",true,${this.id})`);

        const lastLoginColumn = document.createElement("td");
        lastLoginColumn.innerHTML = this.lastTimeLoggedIn;
        lastLoginColumn.setAttribute("onclick", `loadPage("userDetails.jsp","settingsButton",true,${this.id})`);

        const deleteButtonContainer = document.createElement('td') as HTMLTableDataCellElement;
        const deleteButton = document.createElement('button') as HTMLButtonElement;
        deleteButton.classList.add('button');
        deleteButton.innerHTML = 'Löschen';

        
        if (!this.isCurrentUser) {
            deleteButton.setAttribute('onclick', `deleteUser(${this.id},this)`);
        } else {
            deleteButton.setAttribute('onClick', 'popup(false, \'Du kannst dich selber nicht löschen!\')');
        }
        deleteButtonContainer.append(deleteButton);

        row.append(idHTMl, email, canCreateUserColumn, lastLoginColumn, deleteButtonContainer);

        return row;
    }
}