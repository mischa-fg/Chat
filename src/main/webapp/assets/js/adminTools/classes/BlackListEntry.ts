class BlackListEntry extends IDParent {
    private _word: string;
    private _usages: number;

    constructor(id: number, word: string, usages: number) {
        super(id);
        this._word = word;
        this._usages = usages;
    }

    get word(): string {
        return this._word;
    }

    set word(value: string) {
        this._word = value;
    }

    get usages(): number {
        return this._usages;
    }

    set usages(value: number) {
        this._usages = value;
    }

    toBlackListEntryHTML(): HTMLDivElement {
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
        wordTd.setAttribute("onclick", `loadPage('blacklistDetails.html', 'blackListButton', true, ${this.id})`)

        const usagesTd = document.createElement('td');
        usagesTd.innerHTML = String(this.usages);
        usagesTd.classList.add('additionalTagInfos');
        usagesTd.setAttribute("onclick", `loadPage('blacklistDetails.html', 'blackListButton', true, ${this.id})`)

        const deleteButtonContainer = document.createElement('td') as HTMLTableDataCellElement;
        const deleteButton = document.createElement('button') as HTMLButtonElement;
        deleteButton.innerHTML = 'Löschen';
        deleteButton.classList.add('button');
        deleteButton.setAttribute('onclick', `deleteBlacklistEntry(${this.id}, this)`);
        deleteButtonContainer.append(deleteButton);

        row.append(idHTML, wordTd, usagesTd, deleteButtonContainer);

        return row;
    }
}

function sortBlackList() {
    let unsortedBlackList: BlackListEntry[] = Array.prototype.slice.call(blackListEntries);
    // usefulness descending
    let button = document.getElementById('sortByName');
    button?.setAttribute('onclick', 'reverseSortBlackList()');
    button!.innerHTML = 'Name ↓';
    blackListEntries = unsortedBlackList.sort(function (blackListEntry1: BlackListEntry, blackListEntry2: BlackListEntry) {
            if (blackListEntry1.word != blackListEntry2.word) {
                // 
                return (blackListEntry1.word < blackListEntry2.word) ? -1 : 1; // Sort for tag Name
            } // If both have the same text (shouldnt be possible, aber sicher ist sicher)

            // 
            return (blackListEntry1.id < blackListEntry2.id) ? -1 : 1; // sort for tag id
        }
    )
    createBlackListEntriesString(blackListEntries);
}

function reverseSortBlackList() {
    let unsortedBlackList: BlackListEntry[] = Array.prototype.slice.call(blackListEntries);
    // usefulness descending
    let button = document.getElementById('sortByName');
    button?.setAttribute('onclick', 'sortBlackList()');
    button!.innerHTML = 'Name ↑';
    blackListEntries = unsortedBlackList.sort(function (blackListEntry1: BlackListEntry, blackListEntry2: BlackListEntry) {
            if (blackListEntry1.word != blackListEntry2.word) {
                // 
                return (blackListEntry1.word > blackListEntry2.word) ? -1 : 1; // Sort for tag Name
            } // If both have the same text (shouldnt be possible, aber sicher ist sicher)

            // 
            return (blackListEntry1.id < blackListEntry2.id) ? -1 : 1; // sort for tag id
        }
    )
    createBlackListEntriesString(blackListEntries);
}