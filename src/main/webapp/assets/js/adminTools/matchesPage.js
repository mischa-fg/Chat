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
document.title = adminToolPageTitleName + ' - Matches';
function loadMatches() {
    return __awaiter(this, void 0, void 0, function* () {
        const page = pageCheck;
        try {
            let response = yield fetch(`${server}/services/get/allMatches`);
            let json = yield response.json();
            disableLoader();
            saveMatches(json);
        }
        catch (e) {
            checkPageExecute(() => popup(false, 'Diese Seite konnte leider nicht geladen werden'), 'matches');
        }
    });
}
function saveMatches(retrievedMatches) {
    if (retrievedMatches != null) {
        matches = [];
        for (let match of retrievedMatches) {
            let matchID = match.id;
            let matchWord = match.word;
            let matchTag = match.tag;
            let matchUpvote = match.upvote;
            let matchDownvote = match.downvote;
            let matchStatus = match.Status;
            let matchList = new Matches(matchID, matchWord, matchTag, matchStatus, matchUpvote, matchDownvote);
            matches.push(matchList);
        }
        sortMatch('usefulness');
        createMatchesEntriesString(matches);
    }
    else {
        createTagString(null);
    }
}
function createMatchesEntriesString(matches, value = null) {
    if (matches != null) {
        value = '';
        for (let i = 0; i < matches.length; i++) {
            value += matches[i].toMatchHTML().outerHTML;
        }
    }
    else if (value == null) {
        value = "Couln't load Matches!";
    }
    document.getElementById("allMatchesTableBody").innerHTML = value;
}
function blacklistMatch(id, word) {
    return __awaiter(this, void 0, void 0, function* () {
        const page = pageCheck;
        try {
            const checkResponse = yield fetch(`${server}/services/get/checkNewBlacklist?word=${word}`);
            const check = yield checkResponse.json();
            const doBlacklist = (check.isTag)
                ? confirm(`${word} ist ein vorhandener Tag, willst du dieses Wort trotzdem Blacklisten?`)
                : true;
            if (!doBlacklist)
                return;
            const response = yield fetch(`${server}/services/adminTool/blacklistMatch`, {
                method: 'POST',
                body: `matchId=${id}`
            });
            if (response.status === 403) {
                checkPageExecute(() => {
                    popup(false, 'Dieses Wort konnte nicht geblacklistet werden, da du nicht eingelogged bist!');
                }, 'matches');
            }
            else {
                const json = yield response.json();
                if (json.success) {
                    checkPageExecute(() => {
                        var _a;
                        popup(true, `${word} wurde erfolgreich geblacklistet!`);
                        (_a = document.getElementById(`matchId-${id}`)) === null || _a === void 0 ? void 0 : _a.remove();
                    }, 'matches');
                }
                else {
                    checkPageExecute(() => popup(false, `${word} wurde nicht geblacklistet!`), 'matches');
                }
            }
        }
        catch (e) {
            checkPageExecute(() => popup(false, 'Dieses Wort konnte nicht geblacklistet werden!'), 'matches');
        }
    });
}
function resetMatchRating(id) {
    return __awaiter(this, void 0, void 0, function* () {
        const page = pageCheck;
        try {
            const response = yield fetch(`${server}/services/adminTool/resetMatchRating`, {
                method: 'POST',
                body: `matchID=${id}`
            });
            const json = yield response.json();
            if (json.success) {
                checkPageExecute(() => {
                    popup(true, 'Die Bewertung wurde erfolgreich zurückgesetzt!');
                    for (let i in matches) {
                        let match = matches[i];
                        if (match.id === id) {
                            match.downvote = 0;
                            match.upvote = 0;
                            match.usefulness = 0.5;
                            match.status = json.status;
                        }
                    }
                    createMatchesEntriesString(matches);
                }, 'matches');
            }
            else {
                checkPageExecute(() => {
                    popup(false, 'Die Bewertung konnte nicht zurückgesetzt werden!');
                }, 'matches');
            }
        }
        catch (e) {
            checkPageExecute(() => popup(false, 'Die Bewertungen konnten nicht zurückgesetzt werden!'), 'matches');
        }
    });
}
function noTranslate(id) {
    return __awaiter(this, void 0, void 0, function* () {
        const page = pageCheck;
        try {
            const response = yield fetch(`${server}/services/adminTool/noTranslate`, {
                method: 'POST',
                body: `matchID=${id}`
            });
            const json = yield response.json();
            if (json.success) {
                checkPageExecute(() => {
                    popup(true, 'Der Status wurde erfolgreich gesetzt!');
                    for (let i in matches) {
                        let match = matches[i];
                        if (match.id === id) {
                            match.downvote = json.downvotes;
                            match.upvote = 0;
                            match.usefulness = json.rating;
                            match.status = json.status;
                        }
                    }
                    createMatchesEntriesString(matches);
                }, 'matches');
            }
            else {
                checkPageExecute(() => {
                    popup(false, 'Der Status konnte nicht geändert werden!');
                }, 'matches');
            }
        }
        catch (e) {
            checkPageExecute(() => popup(false, 'Der Status konnte nicht geändert werden!'), 'matches');
        }
    });
}
function matchesSearch(input) {
    const startsWith = [];
    const includes = [];
    const startsWithTags = [];
    const containsTags = [];
    input = input.toLowerCase();
    for (const match of matches) {
        const word = match.word.toLowerCase();
        if (word.startsWith(input)) {
            startsWith.push(match);
        }
        else if (word.includes(input)) {
            includes.push(match);
        }
        else {
            const t = match.tag.tag.toLowerCase();
            if (t.startsWith(input)) {
                startsWithTags.push(match);
            }
            else if (t.includes(input)) {
                containsTags.push(match);
            }
        }
    }
    let foundMatches = startsWith.concat(includes);
    foundMatches = foundMatches.concat(startsWithTags);
    foundMatches = foundMatches.concat(containsTags);
    createMatchesEntriesString(foundMatches);
}
