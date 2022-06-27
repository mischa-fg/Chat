"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
document.title = adminToolPageTitleName + ' - Blacklists';
function loadBlackList() {
    const page = pageCheck;
    fetch(`${server}/services/get/allBlackList`)
        .then((response) => {
        return response.json();
    })
        .then((retrievedBlackListEntries) => {
        checkPageExecute(() => {
            disableLoader();
            sortBlackList();
            saveBlackList(retrievedBlackListEntries);
        }, page);
    });
}
function saveBlackList(retrievedBlackListEntries) {
    if (retrievedBlackListEntries != null) {
        blackListEntries = [];
        for (let blackListEntry in retrievedBlackListEntries) {
            let blackListEntryID = retrievedBlackListEntries[blackListEntry].id;
            let blackListEntryWord = retrievedBlackListEntries[blackListEntry].word;
            let blackListEntryUsages = retrievedBlackListEntries[blackListEntry].usages;
            let blackList = new BlackListEntry(blackListEntryID, blackListEntryWord, blackListEntryUsages);
            blackListEntries.push(blackList);
        }
        createBlackListEntriesString(blackListEntries);
    }
    else {
        createBlackListEntriesString(null);
    }
}
function createBlackListEntriesString(blackListEntries, value = null) {
    if (blackListEntries != null) {
        value = '';
        for (let i = 0; i < blackListEntries.length; i++) {
            value += blackListEntries[i].toBlackListEntryHTML().outerHTML;
        }
    }
    else if (value == null) {
        value = "Couln't load BlackList!";
    }
    document.getElementById("allBlackListedTableBody").innerHTML = value;
}
function deleteBlacklistEntry(id, element) {
    var _a;
    return __awaiter(this, void 0, void 0, function* () {
        if (confirm("Möchtest du wirklich diese Blacklisteintrag löschen?")) {
            element.disabled = true;
            const page = pageCheck;
            try {
                yield fetch(`${server}/services/adminTool/deleteBlacklistEntry`, {
                    method: 'post',
                    body: `entryId=${id}`
                });
                const ids = document.getElementsByClassName('id-hidden');
                for (const cid of ids) {
                    if (cid.innerHTML === String(id)) {
                        (_a = cid.parentElement) === null || _a === void 0 ? void 0 : _a.remove();
                    }
                }
            }
            catch (e) {
                executePageSpecific(() => popup(false, 'Blacklist Eintrag konnte nicht gelöscht werden!'), page);
            }
        }
    });
}
function blackListSearch(input) {
    const startsWith = [];
    const includes = [];
    input = input.toLowerCase();
    for (const blacklist of blackListEntries) {
        const blacklistWord = blacklist.word.toLowerCase();
        if (blacklistWord.startsWith(input)) {
            startsWith.push(blacklist);
        }
        else if (blacklistWord.includes(input)) {
            includes.push(blacklist);
        }
    }
    createBlackListEntriesString(startsWith.concat(includes));
}
