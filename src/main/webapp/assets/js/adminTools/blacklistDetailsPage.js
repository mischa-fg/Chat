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
function initPage() {
    return __awaiter(this, void 0, void 0, function* () {
        const page = pageCheck;
        const fullSearchParams = window.location.search;
        const urlParams = new URLSearchParams(fullSearchParams);
        const entryId = +urlParams.get("objectID");
        const mainButton = document.getElementById('blacklistSubmitButton');
        const contentInput = document.getElementById('blacklistEntryContent');
        if (entryId > 0) {
            try {
                const response = yield fetch(`${server}/services/get/singleBlacklistEntry?id=${entryId}`);
                const json = yield response.json();
                contentInput.value = json.word;
                mainButton.value = 'Aktualisieren';
                mainButton.onclick = () => __awaiter(this, void 0, void 0, function* () {
                    yield fetch(`${server}/services/adminTool/editBlacklistEntry`, {
                        method: 'POST',
                        body: `entryId=${json.id}&content=${contentInput.value}`
                    });
                    yield loadPage("blacklistDetails.html", "blackListButton", true, -1, true);
                });
            }
            catch (e) {
                checkPageExecute(() => popup(false, 'Dieser Blacklist Eintrag konnte leider nicht geladen werden!'), page);
            }
        }
        else {
            checkPageExecute(() => {
                mainButton.value = 'Hinzufügen';
                mainButton.onclick = () => __awaiter(this, void 0, void 0, function* () {
                    const checkResponse = yield fetch(`${server}/services/get/checkNewBlacklist?word=${contentInput.value}`);
                    const check = yield checkResponse.json();
                    const doBlacklist = (check.isTag)
                        ? confirm(`${contentInput.value} ist ein vorhandener Tag, willst du dieses Wort trotzdem Blacklisten?`)
                        : true;
                    if (doBlacklist) {
                        fetch(`${server}/services/adminTool/addBlacklistEntry`, {
                            method: 'POST',
                            body: `content=${contentInput.value}`
                        }).then((response) => {
                            return response.json();
                        }).then((blackListEntry) => {
                            loadPage("blacklistDetails.html", "blackListButton", true, +blackListEntry.id, true);
                        });
                    }
                });
            }, page);
        }
        yield initMaxLengths(page, [{
                elem: document.getElementById('blacklistEntryContent'),
                name: "BLACK_LIST_ENTRY"
            }]);
        contentInput.disabled = false;
        mainButton.disabled = false;
    });
}
$(function () {
    $('#blacklistEntryContent').on('keypress', function (e) {
        if (e.which == 32) {
            popup(false, "Nur ein Wort ist erlaubt.");
            return false;
        }
    });
});
