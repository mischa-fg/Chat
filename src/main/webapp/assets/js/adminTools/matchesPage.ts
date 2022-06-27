interface ServerTag {
    id: number,
    tag: string
}

interface ServerMatch {
    id: number,
    tag: ServerTag,
    word: string,
    upvote: number,
    downvote: number,
    Status: string
}

document.title = adminToolPageTitleName + ' - Matches';

async function loadMatches() {
    const page = pageCheck;
    try {
        let response = await fetch(`${server}/services/get/allMatches`);
        let json = await response.json();
        disableLoader()
        saveMatches(json)
    } catch (e) {
        checkPageExecute(() => popup(false, 'Diese Seite konnte leider nicht geladen werden'), 'matches');
    }
}

function saveMatches(retrievedMatches: ServerMatch[]) {
    if (retrievedMatches != null) {
        matches = [];
        for (let match of retrievedMatches) {
            let matchID: number = match.id;
            let matchWord: string = match.word;
            let matchTag: ServerTag = match.tag;
            let matchUpvote: number = match.upvote;
            let matchDownvote: number = match.downvote;
            let matchStatus: string = match.Status;
            let matchList: Matches = new Matches(matchID, matchWord, matchTag, matchStatus, matchUpvote, matchDownvote);
            matches.push(matchList);
        }

        sortMatch('usefulness');
        createMatchesEntriesString(matches);
    } else {
        createTagString(null);
    }
}

function createMatchesEntriesString(matches: Matches[], value: string | null = null) {
    if (matches != null) {
        value = '';
        for (let i = 0; i < matches.length; i++) {
            value += matches[i].toMatchHTML().outerHTML;
        }

    } else if (value == null) {
        value = "Couln't load Matches!";
    }
    document.getElementById("allMatchesTableBody")!.innerHTML = value;
}

async function blacklistMatch(id: number, word: string) {
    const page = pageCheck;
    try {
        const checkResponse = await fetch(`${server}/services/get/checkNewBlacklist?word=${word}`)
        const check = await checkResponse.json();


        const doBlacklist = (check.isTag)
            ? confirm(`${word} ist ein vorhandener Tag, willst du dieses Wort trotzdem Blacklisten?`)
            : true;

        if (!doBlacklist) return;

        const response = await fetch(`${server}/services/adminTool/blacklistMatch`, {
            method: 'POST',
            body: `matchId=${id}`
        });

        if (response.status === 403) {
            checkPageExecute(() => {
                popup(false, 'Dieses Wort konnte nicht geblacklistet werden, da du nicht eingelogged bist!');
            }, 'matches')
        } else {
            const json = await response.json();
            if (json.success) {
                checkPageExecute(() => {
                    popup(true, `${word} wurde erfolgreich geblacklistet!`)
                    document.getElementById(`matchId-${id}`)?.remove();
                }, 'matches');
            } else {
                checkPageExecute(() => popup(false, `${word} wurde nicht geblacklistet!`), 'matches');
            }
        }
    } catch (e) {
        checkPageExecute(() => popup(false, 'Dieses Wort konnte nicht geblacklistet werden!'), 'matches');
    }
}

async function resetMatchRating(id: number) {
    const page = pageCheck;
    try {
        const response = await fetch(`${server}/services/adminTool/resetMatchRating`, {
            method: 'POST',
            body: `matchID=${id}`
        })
        const json = await response.json();

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
            }, 'matches')
        } else {
            checkPageExecute(() => {
                popup(false, 'Die Bewertung konnte nicht zurückgesetzt werden!');
            }, 'matches')
        }
    } catch (e) {
        checkPageExecute(() => popup(false, 'Die Bewertungen konnten nicht zurückgesetzt werden!'), 'matches');
    }
}

async function noTranslate(id: number) {
    const page = pageCheck;
    try {
        const response = await fetch(`${server}/services/adminTool/noTranslate`, {
            method: 'POST',
            body: `matchID=${id}`
        })
        const json = await response.json();

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
            }, 'matches')
        } else {
            checkPageExecute(() => {
                popup(false, 'Der Status konnte nicht geändert werden!');
            }, 'matches')
        }
    } catch (e) {
        checkPageExecute(() => popup(false, 'Der Status konnte nicht geändert werden!'), 'matches');
    }
}

function matchesSearch(input: string) {
    const startsWith: Matches[] = [];
    const includes: Matches[] = [];
    const startsWithTags: Matches[] = [];
    const containsTags: Matches[] = [];

    input = input.toLowerCase();

    for (const match of matches) {
        const word = match.word.toLowerCase();
        if (word.startsWith(input)) {
            startsWith.push(match);
        } else if (word.includes(input)) {
            includes.push(match);
        } else {
            const t = match.tag.tag.toLowerCase();
            if (t.startsWith(input)) {
                startsWithTags.push(match);
            } else if (t.includes(input)) {
                containsTags.push(match);
            }
        }
    }

    let foundMatches = startsWith.concat(includes);
    foundMatches = foundMatches.concat(startsWithTags);
    foundMatches = foundMatches.concat(containsTags);

    createMatchesEntriesString(foundMatches);
}