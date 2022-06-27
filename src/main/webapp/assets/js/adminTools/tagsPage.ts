interface ServerTag {
    id: number,
    tag: string
}

interface ServerTempTag {
    tag: ServerTag,
    upvotes: number,
    downvotes: number,
    usage: number,
    amountAnswers: number,
}


async function loadTags() {
    const page = pageCheck;
    try {
        let response = await fetch(`${server}/services/get/allTags`);
        let json = await response.json() as ServerTempTag[];
        disableLoader();
        saveTags(json);
    } catch (e) {
        checkPageExecute(() => {
            popup(false, 'Tags konnten nicht vom Server geladen werden!')
            disableLoader();
        }, 'tags')
    }
}

function removeTags() {
    $(".allTagsTableBody").html('removed tags');
}

function createTagString(sortedTags: MyTag[] | null, value: string | null = null) {
    const page = pageCheck;
    if (sortedTags != null) {
        value = '';
        for (let i = 0; i < sortedTags.length; i++) {
            value += sortedTags[i].toTagHTML().outerHTML;
        }

    } else if (value == null) {
        value = "Couln't load Tags!";
    }
    // $(".allTagsTableBody").html(value);
    checkPageExecute(() => {
        if (typeof value === "string") {
            document.getElementById("allTagsTableBody")!.innerHTML = value;
        }
    }, 'tags')
}

function saveTags(retrievedTags: ServerTempTag[]) {
    if (retrievedTags != null) {
        tags = [];
        for (let retrievedTag of retrievedTags) {
            let tagName: string = retrievedTag.tag.tag;
            let tagID: number = retrievedTag.tag.id;
            let tagUsage: number = retrievedTag.usage;
            let tagUpvotes: number = retrievedTag.upvotes;
            let tagDownvotes: number = retrievedTag.downvotes;
            let tagAmountAnswers: number = retrievedTag.amountAnswers;
            let tag: MyTag = new MyTag(tagID, tagName, tagUpvotes, tagDownvotes, tagUsage, tagAmountAnswers, []);
            tags.push(tag);
        }

        sortTags('usefulness', true);
        createTagString(tags);
    } else {
        createTagString(null);
    }
}

function tagSearch(input: string) {
    const startsWith: MyTag[] = [];
    const includes: MyTag[] = [];

    input = input.toLowerCase();

    for (const tag of tags) {
        const tagName = tag.tag.toLowerCase();
        if (tagName.startsWith(input)) {
            startsWith.push(tag);
        } else if (tagName.includes(input)) {
            includes.push(tag);
        }
    }

    createTagString(startsWith.concat(includes));
}

document.title = 'AdminTool - Tags';