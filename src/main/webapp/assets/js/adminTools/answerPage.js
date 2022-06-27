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
document.title = adminToolPageTitleName + ' - Antworten';
function loadAnswers(tagLimit = loadedTagsPerAnswer) {
    return __awaiter(this, void 0, void 0, function* () {
        const page = pageCheck;
        try {
            checkPageExecute(() => __awaiter(this, void 0, void 0, function* () {
                const response = yield fetch(`${server}/services/get/allAnswers?tagLimit=${tagLimit}`);
                const retrievedAnswers = yield response.json();
                yield setAllAnswerTypes(null, "sortByType", false, 1);
                saveAnswersAndTags(retrievedAnswers, tagLimit);
                disableLoader();
            }), 'answers');
        }
        catch (e) {
            console.error(e);
            checkPageExecute(() => {
                disableLoader();
                popup(false, 'Antworten konnten nicht vom Server geladen werden!');
                createAnswerString(null);
            }, page);
            return null;
        }
    });
}
function saveAnswersAndTags(retrievedAnswers, tagLimit) {
    if (retrievedAnswers != null) {
        answers = [];
        for (let retrievedAnswer of retrievedAnswers) {
            let answerTitle = retrievedAnswer.title;
            let answerString = retrievedAnswer.answer;
            let answerId = retrievedAnswer.id;
            let answerViews = retrievedAnswer.views;
            let answerHidden = retrievedAnswer.isHidden;
            let answerType = new AnswerType(retrievedAnswer.answerType[0].value, retrievedAnswer.answerType[0].name, retrievedAnswer.answerType[0].groupedTags, retrievedAnswer.answerType[0].hidden, retrievedAnswer.answerType[0].forceHidden);
            let averageUsefulness = retrievedAnswer.averageUsefulness;
            let myAnswerTags = [];
            for (let retreivedTag of retrievedAnswer.tags) {
                let id = +retreivedTag.tag.id;
                let tagName = retreivedTag.tag.tag;
                let tagUpvotes = retreivedTag.upvotes;
                let tagDownvotes = retreivedTag.downvotes;
                let tagUsage = retreivedTag.usage;
                let tag = new MyTag(id, tagName, tagUpvotes, tagDownvotes, tagUsage, -1, []);
                myAnswerTags.push(tag);
            }
            let myAnswerFiles = [];
            for (let file of retrievedAnswer.files) {
                let id = file.id;
                let name = file.name;
                let type = file.type;
                myAnswerFiles.push(new MyFile(id, name, type));
            }
            let answer = new MyAnswer(answerId, answerTitle, answerString, answerViews, answerType, answerHidden, myAnswerTags, myAnswerFiles, averageUsefulness);
            answers.push(answer);
        }
        sortAnswers('usefulness', true);
        createAnswerString(answers, tagLimit);
    }
    else {
        createAnswerString(null);
    }
}
function createAnswerString(sortedAnswers, tagLimit = loadedTagsPerAnswer, value = null) {
    function getTagBadges(container, tags) {
        for (let j = 0; j < tags.length; j++) {
            container.appendChild(tags[j].toTagHTML(true, false, true));
        }
    }
    function noTagWarning(container) {
        const noTagWarningImage = document.createElement('img');
        noTagWarningImage.src = `${server}/assets/images/warning.svg`;
        noTagWarningImage.alt = 'Warnungs Icon';
        noTagWarningImage.classList.add('noTagWarningImage');
        const noTagWarningText = document.createElement('span');
        noTagWarningText.innerHTML = 'Diese Antwort wird nie gefunden, da sie keine Tags enthält!';
        noTagWarningText.classList.add('noTagWarningText');
        container.append(noTagWarningImage, noTagWarningText);
    }
    function moreTagsIcon(container) {
        const mult = document.createElement('img');
        mult.src = `${server}/assets/images/more.svg`;
        mult.alt = 'Three Dots';
        container.appendChild(mult);
    }
    if (sortedAnswers != null) {
        value = '';
        for (let i = 0; i < sortedAnswers.length; i++) {
            let answerId = "answerId-" + sortedAnswers[i].id;
            let answerContainer = document.createElement("div");
            answerContainer.setAttribute("id", `${answerId}`);
            answerContainer.classList.add("answer-container");
            answerContainer.setAttribute("onclick", `loadPage("answersDetails.html", "answersButton", true, ${sortedAnswers[i].id})`);
            let tagContainer = document.createElement("div");
            tagContainer.classList.add("tag-container");
            let answerTopInfo = sortedAnswers[i].toAnswerHTML();
            answerTopInfo.classList.add('answerTopInfo');
            let tags = sortedAnswers[i].tags;
            if (tags.length > 0) {
                getTagBadges(tagContainer, tags);
                if (tags.length >= tagLimit) {
                    moreTagsIcon(tagContainer);
                }
            }
            else {
                noTagWarning(tagContainer);
            }
            let usefulTypeContainer = document.createElement('div');
            usefulTypeContainer.classList.add('flex');
            let type = document.createElement('span');
            type.innerHTML = 'Typ: ' + sortedAnswers[i].answerType.name;
            let usefulness = document.createElement('div');
            usefulness.classList.add('answerUsefulness');
            usefulness.innerHTML = (Math.round((sortedAnswers[i].averageUsefulness + Number.EPSILON) * 100)).toFixed(0) + '%';
            usefulness.style.background = getColor(sortedAnswers[i].averageUsefulness);
            usefulTypeContainer.append(usefulness, type);
            answerContainer.append(answerTopInfo, usefulTypeContainer, tagContainer);
            value += answerContainer.outerHTML;
        }
    }
    else if (value == null) {
        value = "Couldn't load Answers!";
    }
    checkPageExecute(() => {
        $(".allAnswersContainer").empty();
        document.getElementById("allAnswersContainer").innerHTML = String(value);
    }, 'answers');
}
function search(input) {
    const startsWith = [];
    const contains = [];
    const startsWithTags = [];
    const containsTags = [];
    input = input.toLowerCase();
    for (let answer of answers) {
        const title = answer.title.toLowerCase();
        if (title.startsWith(input)) {
            startsWith.push(answer);
        }
        else if (title.includes(input)) {
            contains.push(answer);
        }
        else {
            for (const tag of answer.tags) {
                const t = tag.tag.toLowerCase();
                if (t.startsWith(input)) {
                    startsWithTags.push(answer);
                    break;
                }
                else if (t.includes(input)) {
                    containsTags.push(answer);
                    break;
                }
            }
        }
    }
    let foundAnswers = startsWith.concat(contains);
    foundAnswers = foundAnswers.concat(startsWithTags);
    foundAnswers = foundAnswers.concat(containsTags);
    createAnswerString(foundAnswers);
}
