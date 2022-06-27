"use strict";
class BlackListEntry extends IDParent {
    constructor(id, word, usages) {
        super(id);
        this._word = word;
        this._usages = usages;
    }
    get word() {
        return this._word;
    }
    set word(value) {
        this._word = value;
    }
    get usages() {
        return this._usages;
    }
    set usages(value) {
        this._usages = value;
    }
    toBlackListEntryHTML() {
        const blackListId = "blackListID-" + this.id;
        const row = document.createElement("tr");
        row.classList.add("single-tag");
        row.classList.add("big");
        row.id = blackListId;
        const idHTML = document.createElement("td");
        idHTML.innerHTML = String(this.id);
        idHTML.classList.add("id-hidden");
        const wordTd = document.createElement("td");
        wordTd.innerHTML = this.word;
        wordTd.classList.add("additionalTagInfos", 'left');
        wordTd.setAttribute("onclick", `loadPage('blacklistDetails.html', 'blackListButton', true, ${this.id})`);
        const usagesTd = document.createElement('td');
        usagesTd.innerHTML = String(this.usages);
        usagesTd.classList.add('additionalTagInfos');
        usagesTd.setAttribute("onclick", `loadPage('blacklistDetails.html', 'blackListButton', true, ${this.id})`);
        const deleteButtonContainer = document.createElement('td');
        const deleteButton = document.createElement('button');
        deleteButton.innerHTML = 'Löschen';
        deleteButton.classList.add('button');
        deleteButton.setAttribute('onclick', `deleteBlacklistEntry(${this.id}, this)`);
        deleteButtonContainer.append(deleteButton);
        row.append(idHTML, wordTd, usagesTd, deleteButtonContainer);
        return row;
    }
}
function sortBlackList() {
    let unsortedBlackList = Array.prototype.slice.call(blackListEntries);
    let button = document.getElementById('sortByName');
    button === null || button === void 0 ? void 0 : button.setAttribute('onclick', 'reverseSortBlackList()');
    button.innerHTML = 'Name ↓';
    blackListEntries = unsortedBlackList.sort(function (blackListEntry1, blackListEntry2) {
        if (blackListEntry1.word != blackListEntry2.word) {
            return (blackListEntry1.word < blackListEntry2.word) ? -1 : 1;
        }
        return (blackListEntry1.id < blackListEntry2.id) ? -1 : 1;
    });
    createBlackListEntriesString(blackListEntries);
}
function reverseSortBlackList() {
    let unsortedBlackList = Array.prototype.slice.call(blackListEntries);
    let button = document.getElementById('sortByName');
    button === null || button === void 0 ? void 0 : button.setAttribute('onclick', 'sortBlackList()');
    button.innerHTML = 'Name ↑';
    blackListEntries = unsortedBlackList.sort(function (blackListEntry1, blackListEntry2) {
        if (blackListEntry1.word != blackListEntry2.word) {
            return (blackListEntry1.word > blackListEntry2.word) ? -1 : 1;
        }
        return (blackListEntry1.id < blackListEntry2.id) ? -1 : 1;
    });
    createBlackListEntriesString(blackListEntries);
}
