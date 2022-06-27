document.title = adminToolPageTitleName + ' - Antworten';

async function loadAnswers(tagLimit: number = loadedTagsPerAnswer) {
    const page = pageCheck;

    try {
        checkPageExecute(async () => {
            const response = await fetch(`${server}/services/get/allAnswers?tagLimit=${tagLimit}`);
            const retrievedAnswers = await response.json();
            await setAllAnswerTypes(null, "sortByType", false, 1);
            saveAnswersAndTags(retrievedAnswers, tagLimit);
            disableLoader();
        }, 'answers')
    } catch (e) {
        console.error(e);
        checkPageExecute(() => {
            disableLoader();
            popup(false, 'Antworten konnten nicht vom Server geladen werden!')
            createAnswerString(null);
        }, page)
        return null;
    }
}

function saveAnswersAndTags(retrievedAnswers: any, tagLimit: number) {
    if (retrievedAnswers != null) {
        answers = [];
        for (let retrievedAnswer of retrievedAnswers) {
            
            let answerTitle: string = retrievedAnswer.title;
            let answerString: string = retrievedAnswer.answer;
            let answerId: number = retrievedAnswer.id;
            let answerViews: number = retrievedAnswer.views;
            let answerHidden: string = retrievedAnswer.isHidden;
            let answerType: AnswerType = new AnswerType(retrievedAnswer.answerType[0].value, retrievedAnswer.answerType[0].name, retrievedAnswer.answerType[0].groupedTags, retrievedAnswer.answerType[0].hidden, retrievedAnswer.answerType[0].forceHidden);
            let averageUsefulness: number = retrievedAnswer.averageUsefulness;

            let myAnswerTags: MyTag[] = [];
            for (let retreivedTag of retrievedAnswer.tags) {
                let id: number = +retreivedTag.tag.id;
                let tagName: string = retreivedTag.tag.tag;
                let tagUpvotes: number = retreivedTag.upvotes;
                let tagDownvotes: number = retreivedTag.downvotes;
                let tagUsage: number = retreivedTag.usage;

                let tag: MyTag = new MyTag(id, tagName, tagUpvotes, tagDownvotes, tagUsage, -1, []);
                myAnswerTags.push(tag);
            }

            let myAnswerFiles: MyFile[] = [];
            for (let file of retrievedAnswer.files) {
                let id: number = file.id;
                let name: string = file.name;
                let type: string = file.type;

                myAnswerFiles.push(new MyFile(id, name, type));
            }


            let answer: MyAnswer = new MyAnswer(answerId, answerTitle, answerString, answerViews, answerType, answerHidden, myAnswerTags, myAnswerFiles, averageUsefulness);
            answers.push(answer);
        }

        sortAnswers('usefulness', true);
        createAnswerString(answers, tagLimit);
    } else {
        createAnswerString(null);
    }
}

function createAnswerString(sortedAnswers: MyAnswer[] | null, tagLimit: number = loadedTagsPerAnswer, value: string | null = null) {
    function getTagBadges(container: HTMLElement, tags: MyTag[]) {
        for (let j = 0; j < tags.length; j++) {
            container.appendChild(tags[j].toTagHTML(true, false, true));
        }
    }

    function noTagWarning(container: HTMLElement) {
        const noTagWarningImage = document.createElement('img');
        noTagWarningImage.src = `${server}/assets/images/warning.svg`;
        noTagWarningImage.alt = 'Warnungs Icon';
        noTagWarningImage.classList.add('noTagWarningImage');

        const noTagWarningText = document.createElement('span');
        noTagWarningText.innerHTML = 'Diese Antwort wird nie gefunden, da sie keine Tags enthält!'
        noTagWarningText.classList.add('noTagWarningText');

        container.append(noTagWarningImage, noTagWarningText);
    }

    function moreTagsIcon(container: HTMLElement) {
        const mult = document.createElement('img') as HTMLImageElement
        mult.src = `${server}/assets/images/more.svg`
        mult.alt = 'Three Dots'
        container.appendChild(mult)
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

            let tags: MyTag[] = sortedAnswers[i].tags;

            if (tags.length > 0) {
                getTagBadges(tagContainer, tags);
                if (tags.length >= tagLimit) {
                    moreTagsIcon(tagContainer)
                }
            } else {
                noTagWarning(tagContainer);
            }

            let usefulTypeContainer = document.createElement('div')
            usefulTypeContainer.classList.add('flex')
            let type = document.createElement('span')
            type.innerHTML = 'Typ: ' + sortedAnswers[i].answerType.name

            let usefulness = document.createElement('div');
            usefulness.classList.add('answerUsefulness');
            usefulness.innerHTML = (Math.round((sortedAnswers[i].averageUsefulness + Number.EPSILON) * 100)).toFixed(0) + '%';
            usefulness.style.background = getColor(sortedAnswers[i].averageUsefulness);

            usefulTypeContainer.append(usefulness, type)

            answerContainer.append(answerTopInfo, usefulTypeContainer, tagContainer);
            value += answerContainer.outerHTML;
        }

    } else if (value == null) {
        value = "Couldn't load Answers!";
    }

    checkPageExecute(() => {
        $(".allAnswersContainer").empty();
        document.getElementById("allAnswersContainer")!.innerHTML = String(value);
    }, 'answers')
}

function search(input: string) {
    const startsWith: MyAnswer[] = [];
    const contains: MyAnswer[] = [];
    const startsWithTags: MyAnswer[] = [];
    const containsTags: MyAnswer[] = [];

    input = input.toLowerCase();

    for (let answer of answers) {
        const title = answer.title.toLowerCase();
        if (title.startsWith(input)) {
            startsWith.push(answer);
        } else if (title.includes(input)) {
            contains.push(answer);
        } else {
            for (const tag of answer.tags) {
                const t = tag.tag.toLowerCase();
                if (t.startsWith(input)) {
                    startsWithTags.push(answer);
                    break;
                } else if (t.includes(input)) {
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