"use strict";
class MyUser extends IDParent {
    constructor(id, email, canCreateUser, lastTimeLoggedIn, isCurrentUser) {
        super(id);
        this._email = email;
        this._canCreateUser = canCreateUser;
        this._lastTimeLoggedIn = lastTimeLoggedIn;
        this._isCurrentUser = isCurrentUser;
    }
    get canCreateUser() {
        return this._canCreateUser;
    }
    set canCreateUser(value) {
        this._canCreateUser = value;
    }
    get email() {
        return this._email;
    }
    set email(value) {
        this._email = value;
    }
    get lastTimeLoggedIn() {
        return this._lastTimeLoggedIn;
    }
    set lastTimeLoggedIn(value) {
        this._lastTimeLoggedIn = value;
    }
    get isCurrentUser() {
        return this._isCurrentUser;
    }
    set isCurrentUser(value) {
        this._isCurrentUser = value;
    }
    toUserHTML() {
        let userId = "userId-" + this.id;
        let row = document.createElement("tr");
        row.classList.add("single-user");
        row.classList.add("big");
        row.setAttribute("id", `${userId}`);
        let idHTMl = document.createElement("td");
        idHTMl.innerHTML = String(this.id);
        idHTMl.classList.add("id-hidden");
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
        const deleteButtonContainer = document.createElement('td');
        const deleteButton = document.createElement('button');
        deleteButton.classList.add('button');
        deleteButton.innerHTML = 'Löschen';
        if (!this.isCurrentUser) {
            deleteButton.setAttribute('onclick', `deleteUser(${this.id},this)`);
        }
        else {
            deleteButton.setAttribute('onClick', 'popup(false, \'Du kannst dich selber nicht löschen!\')');
        }
        deleteButtonContainer.append(deleteButton);
        row.append(idHTMl, email, canCreateUserColumn, lastLoginColumn, deleteButtonContainer);
        return row;
    }
}
