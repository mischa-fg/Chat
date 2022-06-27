async function initPage() {
    const page = pageCheck;

    const fullSearchParams: string = window.location.search;
    const urlParams: URLSearchParams = new URLSearchParams(fullSearchParams);
    const entryId: number = +urlParams.get("objectID")!;

    const mainButton = document.getElementById('blacklistSubmitButton') as HTMLButtonElement;
    const contentInput = document.getElementById('blacklistEntryContent') as HTMLInputElement;

    if (entryId > 0) {
        try {
            const response = await fetch(`${server}/services/get/singleBlacklistEntry?id=${entryId}`);
            const json = await response.json();


            contentInput.value = json.word;

            mainButton.value = 'Aktualisieren';
            mainButton.onclick = async () => {
                await fetch(`${server}/services/adminTool/editBlacklistEntry`, {
                    method: 'POST',
                    body: `entryId=${json.id}&content=${contentInput.value}`
                })
                await loadPage("blacklistDetails.html", "blackListButton", true, -1, true);
            }
        } catch (e) {
            checkPageExecute(() => popup(false, 'Dieser Blacklist Eintrag konnte leider nicht geladen werden!'), page);
        }
    } else {
        checkPageExecute(() => {
            mainButton.value = 'Hinzufügen';
            mainButton.onclick = async () => {
                const checkResponse = await fetch(`${server}/services/get/checkNewBlacklist?word=${contentInput.value}`)
                const check = await checkResponse.json();


                const doBlacklist = (check.isTag)
                    ? confirm(`${contentInput.value} ist ein vorhandener Tag, willst du dieses Wort trotzdem Blacklisten?`)
                    : true;

                if (doBlacklist) {
                    fetch(`${server}/services/adminTool/addBlacklistEntry`, {
                        method: 'POST',
                        body: `content=${contentInput.value}`
                    }).then((response) => {
                        return response.json();
                    }).then((blackListEntry: any) => {

                        loadPage("blacklistDetails.html", "blackListButton", true, +blackListEntry.id, true);
                    })
                }
            }
        }, page)
    }

    await initMaxLengths(page, [{
        elem: document.getElementById('blacklistEntryContent') as HTMLInputElement,
        name: textInputs.BLACKLIST
    }])

    contentInput.disabled = false;
    mainButton.disabled = false;
}

$(function() {
    $('#blacklistEntryContent').on('keypress', function(e) {
        if (e.which == 32){
            
            popup(false,"Nur ein Wort ist erlaubt.")
            return false;
        }
    });
});