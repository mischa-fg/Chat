document.title = adminToolPageTitleName + ' - Blacklists';

function loadBlackList() {
    const page = pageCheck;
    fetch(`${server}/services/get/allBlackList`)
        .then((response) => {
            return response.json();
        })
        .then((retrievedBlackListEntries: any) => {
            checkPageExecute(() => {
                disableLoader();
                sortBlackList();
                saveBlackList(retrievedBlackListEntries);
            }, page)
        })
}

function saveBlackList(retrievedBlackListEntries: any) {
    if (retrievedBlackListEntries != null) {
        blackListEntries = [];
        for (let blackListEntry in retrievedBlackListEntries) {
            let blackListEntryID: number = retrievedBlackListEntries[blackListEntry].id;
            let blackListEntryWord: string = retrievedBlackListEntries[blackListEntry].word;
            let blackListEntryUsages: number = retrievedBlackListEntries[blackListEntry].usages;
            let blackList: BlackListEntry = new BlackListEntry(blackListEntryID, blackListEntryWord, blackListEntryUsages);
            blackListEntries.push(blackList);
        }

        createBlackListEntriesString(blackListEntries);
    } else {
        createBlackListEntriesString(null);
    }
}

function createBlackListEntriesString(blackListEntries: BlackListEntry[] | null, value: string | null = null) {
    if (blackListEntries != null) {
        value = '';
        for (let i = 0; i < blackListEntries.length; i++) {
            value += blackListEntries[i].toBlackListEntryHTML().outerHTML;
        }

    } else if (value == null) {
        value = "Couln't load BlackList!";
    }
    document.getElementById("allBlackListedTableBody")!.innerHTML = value;
}

async function deleteBlacklistEntry(id: number, element: HTMLButtonElement) {
    if (confirm("Möchtest du wirklich diese Blacklisteintrag löschen?")) {
        element.disabled = true;
        const page = pageCheck;

        try {
            await fetch(`${server}/services/adminTool/deleteBlacklistEntry`, {
                method: 'post',
                body: `entryId=${id}`
            });

                const ids = document.getElementsByClassName('id-hidden');
                for (const cid of ids) {
                    if (cid.innerHTML === String(id)) {
                        cid.parentElement?.remove();
                    }
                }
            }
        catch (e) {
            executePageSpecific(() => popup(false, 'Blacklist Eintrag konnte nicht gelöscht werden!'), page);
        }
    }
}

function blackListSearch(input: string) {

    const startsWith: BlackListEntry[] = [];
    const includes: BlackListEntry[] = [];

    input = input.toLowerCase();

    for (const blacklist of blackListEntries) {
        const blacklistWord = blacklist.word.toLowerCase();
        if (blacklistWord.startsWith(input)) {
            startsWith.push(blacklist);
        } else if (blacklistWord.includes(input)) {
            includes.push(blacklist);
        }
    }

    createBlackListEntriesString(startsWith.concat(includes));
}

