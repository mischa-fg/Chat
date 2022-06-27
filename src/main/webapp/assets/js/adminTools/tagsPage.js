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
function loadTags() {
    return __awaiter(this, void 0, void 0, function* () {
        const page = pageCheck;
        try {
            let response = yield fetch(`${server}/services/get/allTags`);
            let json = yield response.json();
            disableLoader();
            saveTags(json);
        }
        catch (e) {
            checkPageExecute(() => {
                popup(false, 'Tags konnten nicht vom Server geladen werden!');
                disableLoader();
            }, 'tags');
        }
    });
}
function removeTags() {
    $(".allTagsTableBody").html('removed tags');
}
function createTagString(sortedTags, value = null) {
    const page = pageCheck;
    if (sortedTags != null) {
        value = '';
        for (let i = 0; i < sortedTags.length; i++) {
            value += sortedTags[i].toTagHTML().outerHTML;
        }
    }
    else if (value == null) {
        value = "Couln't load Tags!";
    }
    checkPageExecute(() => {
        if (typeof value === "string") {
            document.getElementById("allTagsTableBody").innerHTML = value;
        }
    }, 'tags');
}
function saveTags(retrievedTags) {
    if (retrievedTags != null) {
        tags = [];
        for (let retrievedTag of retrievedTags) {
            let tagName = retrievedTag.tag.tag;
            let tagID = retrievedTag.tag.id;
            let tagUsage = retrievedTag.usage;
            let tagUpvotes = retrievedTag.upvotes;
            let tagDownvotes = retrievedTag.downvotes;
            let tagAmountAnswers = retrievedTag.amountAnswers;
            let tag = new MyTag(tagID, tagName, tagUpvotes, tagDownvotes, tagUsage, tagAmountAnswers, []);
            tags.push(tag);
        }
        sortTags('usefulness', true);
        createTagString(tags);
    }
    else {
        createTagString(null);
    }
}
function tagSearch(input) {
    const startsWith = [];
    const includes = [];
    input = input.toLowerCase();
    for (const tag of tags) {
        const tagName = tag.tag.toLowerCase();
        if (tagName.startsWith(input)) {
            startsWith.push(tag);
        }
        else if (tagName.includes(input)) {
            includes.push(tag);
        }
    }
    createTagString(startsWith.concat(includes));
}
document.title = 'AdminTool - Tags';
