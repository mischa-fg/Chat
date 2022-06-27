/**
 * How long (in seconds) we have to wait until we can reload again
 */
let secondsToWaitUntilNextReload: number = 3;

/**
 * How much time has passed by since the last reload
 */
let secondsSinceLastPageUpdate: number = secondsToWaitUntilNextReload;

let loggedInUser: MyUser;

/**
 * define how often the timer should be increased
 */
let increaseSeconds = setInterval(incrementSeconds, 500); // Every 0.5 seconds increase the timer

/**
 * Increment the timer so we know when we can reload the page again
 */
function incrementSeconds() {
    if (secondsSinceLastPageUpdate <= secondsToWaitUntilNextReload) {
        secondsSinceLastPageUpdate += 0.5;
    }
}

// Declare global variables!
// set the default first page
let firstPageFilename: string = "overview.html";
let firstPageActiveButton: string = "homeButton";

const targetPageActiveButtonID: string = "activeButtonID";
const targetPageParamName: string = "targetPage";
let objectIDParam: string = "objectID";

let pageCheck: string = '';
let tempTagData: MyTag[] = [];

let singleTagAnswers: SingleTagAnswer[] = [];
let answers: MyAnswer[] = [];
let allAnswers: MyAnswer[] = [];
let answerFiles: MyFile[] = [];

let tags: MyTag[] = [];
let specialTags: MyTag[] = [];
let tagsInputTags: string[] = [];
let tagsInputPressCount = 0;

let matches: Matches[] = [];

let blackListEntries: BlackListEntry[] = [];

let defaultQuestions: DefaultQuestions[] = [];
let unansweredQuestions: QuestionParent[] = [];
let answeredQuestions: MyAnsweredQuestion[] = [];

let files: MyFile[] = [];

let answerTypes: AnswerType[] = [];

let users: MyUser[] = [];

let answerChart: any;
let answeredQuestionVsUnansweredQuestionChart: any;

/**
 * check when the page is finished with loading then check the url
 *
 * @author Tim Irmler
 */
document.addEventListener("DOMContentLoaded", function () {
    checkURLForParamsAndLoadContent();
});

/**
 * Read the url and check for params, load the content according to the params
 *
 * @author Tim Irmler
 */
async function checkURLForParamsAndLoadContent(replaceState: boolean = true, forceReload: boolean = false) {
    let fullSearchParams: string = window.location.search;
    const urlParams: URLSearchParams = new URLSearchParams(fullSearchParams);

    let currentPage: string = urlParams.get(targetPageParamName)!;
    let currentActiveButtonID: string = urlParams.get(targetPageActiveButtonID)!;
    if (currentPage === null) {
        currentPage = firstPageFilename;
        currentActiveButtonID = firstPageActiveButton;
    }

    let currentObjectID: number = +urlParams.get(objectIDParam)!;

    let specificDetailsNeeded: boolean = true;
    if (currentObjectID == null || isNaN(currentObjectID) || currentObjectID <= 0) {
        specificDetailsNeeded = false;
    }

    if (replaceState) {
        const url = new URL(window.location.href);
        url.searchParams.set(targetPageParamName, currentPage);
        if (currentActiveButtonID != null) {
            url.searchParams.set(targetPageActiveButtonID, currentActiveButtonID);
        }
        const href = url.toString();
        window.history.replaceState({}, document.title, href);
    }

    await loadPage(currentPage, currentActiveButtonID, specificDetailsNeeded, currentObjectID, forceReload);
}

/**
 * If we go back or forth in the history, reload the page
 *
 * @author Tim Irmler
 */
$(window).on("popstate", async function () {
    if (history.state) {
        await checkURLForParamsAndLoadContent(false, true);
    }
});

let dataClass: IDParent | null = null;

/**
 * Load a html file inside of a html element
 * @param fileName the name of the file that we want to load
 * @param newActiveButtonID the id of the button in the nav (left side) that we want to be active
 * @param specificDataNeeded is it a detailspage that we want to load?
 * @param dataID the id of the class/object, needed if we load a detailspage
 * @param forceReload do we ignore the timer, and force the reload?
 * @author Tim Irmler
 */
async function loadPage(fileName: string, newActiveButtonID: string, specificDataNeeded: boolean = false, dataID: number | null = null, forceReload: boolean = false) {
    pageCheck = removeFileExtension(fileName)

    let fullSearchParams: string = window.location.search;

    const urlParams: URLSearchParams = new URLSearchParams(fullSearchParams);

    let currentPage: string = urlParams.get(targetPageParamName)!;
    let currentActiveButtonID: string = urlParams.get(targetPageActiveButtonID)!;

    if (dataID! <= 0 || isNaN(dataID!)) {
        dataID = +urlParams.get("objectID")!;
    }

    if (fileName == "" || fileName == null) {
        fileName = currentPage;
    }

    if (newActiveButtonID == "" || newActiveButtonID == null) {
        newActiveButtonID = currentActiveButtonID;
    }

    dataClass = null;

    if (currentPage != fileName) { // If we change the page
        secondsSinceLastPageUpdate = secondsToWaitUntilNextReload; // "force" the reload

        const url = new URL(window.location.href); // Get the url
        // Set the parameters
        url.searchParams.set(targetPageParamName, fileName);
        url.searchParams.set(targetPageActiveButtonID, newActiveButtonID);

        if (specificDataNeeded) { // If we load a detailsPage
            url.searchParams.set(objectIDParam, String(dataID)); // Set data ID
        } else {
            // Don't set data ID
            url.searchParams.delete(objectIDParam)
        }
        const href = url.toString();
        window.history.pushState({}, document.title, href);
    } else if (specificDataNeeded) {
        secondsSinceLastPageUpdate = secondsToWaitUntilNextReload; // "force" the reload

        const url = new URL(window.location.href); // Get the url
        // Set the parameters
        url.searchParams.set(targetPageParamName, fileName);
        url.searchParams.set(targetPageActiveButtonID, newActiveButtonID);

        url.searchParams.set(objectIDParam, String(dataID)); // Set data ID
        const href = url.toString();
        // window.history.pushState({}, document.title, href);
        window.history.replaceState({}, document.title, href);
    }
    if (secondsSinceLastPageUpdate >= secondsToWaitUntilNextReload || forceReload) { // if enough time has passed since the last reload
        actuallyLoadPage(fileName, newActiveButtonID, currentPage)

        secondsSinceLastPageUpdate = 0; // Reset the timer
    }

    const isLoggedIn = await checkLoggedIn();

    if (!isLoggedIn) {
        await logout(urlParams);
    }
}

async function loadPageWithUrl(pageName: string, buttonID: string) {
    let fullSearchParams: string = window.location.search;
    const urlParams: URLSearchParams = new URLSearchParams(fullSearchParams);
    const answerID: number = +urlParams.get("objectID")!;

    await loadPage(pageName, buttonID, true, answerID, true);
}

async function checkLoggedIn(): Promise<boolean> {
    const response = await fetch(`${server}/services/adminTool/checkLogin`, {
        method: 'POST'
    })
    const json = await response.json();
    return json.isLoggedIn;
}

/**
 * @author Tim Irmler
 * @param fileName
 * @param activeButtonID
 * @param currentPage
 */
function actuallyLoadPage(fileName: string, activeButtonID: string, currentPage: string) {
    let activeButtonElement = document.getElementById(activeButtonID)!;
    $("#pageContent").load(fileName, function (response, status, xhr) { // Load page into the div with id "pageContent"
        if (status === "error") {

            actuallyLoadPage(firstPageFilename, firstPageActiveButton, currentPage)
        } else if (status === "success") {
            $('.sidenavButton.active').removeClass('active'); // Remove the "active" class there where we had it now
            activeButtonElement.classList.add('active'); // Add the active class to the new element
        }
    });
}

/**
 * @author Tim Irmler
 * @param type
 * @param withAmountAnswers
 */
async function getSpecialTags(type: number, withAmountAnswers: boolean = false) {
    specialTags = [];
    await fetch(`${server}/services/get/allTagsByTypeWithInfos?type=${type}`)
        .then((response: Response) => {
            return response.json();
        })
        .then(response => {
            for (let tag of response) {
                let thisAmountAnswers: number
                if (withAmountAnswers) {
                    thisAmountAnswers = tag.amountAnswers;
                } else {
                    thisAmountAnswers = -1;
                }
                let newTag: MyTag = new MyTag(tag.tag.id, tag.tag.tag, tag.upvotes, tag.downvotes, tag.usage, thisAmountAnswers, []);
                specialTags.push(newTag);
            }
        })
}

/**
 * generate a random number inside of range
 * @param min our random number can't be smaller then this number (has default value if not set)
 * @param max our random number can't be bigger then this number (has default value if not set)
 */
function randomNumber(min = 1, max = 100) {
    return Math.floor(Math.random() * (max - min) + min);
}

function clamp(number: number, min: number, max: number) {
    return Math.min(Math.max(number, min), max);
}

/**
 * calculate a percentage between 2 numbers, returns a value between 0 and 1
 * @param value1 the first number (upvotes)
 * @param value2 the second number (downvotes)
 */
function calcPercentage(value1: number, value2: number): number {
    let value3: number = (+value1 + +value2);

    let percentage: number;
    if (value3 != 0) {
        percentage = (+value1 / +value3);
    } else {
        percentage = 0.5;
    }
    return percentage;
}

/**
 * Get a color, depending on a number (gradient)
 * @param percent the number with wich we generate the color
 * @param alpha the alpha value of the color if needed
 */
function getColor(percent: number): string {
    percent = 1 - percent;
    let hue: string = ((1 - percent) * 120).toString(10);
    return ["hsl(", hue, ",80%,70%)"].join("");
}

/**
 * Go through an array and search for the same id, if we found the id return the index, else if the id doesnt exist, return null
 * @param array the array where we look for the id
 * @param idToFind the id we want to find
 */
function findSameId(array: any[], idToFind: number | null | string): number | null {
    for (let i = 0; i < array.length; i++) {
        if (+array[i].id == +idToFind!) {
            return i;
        }
    }
    return null;
}

/**
 * gets all answertypes from the server and loads them into a selection
 */
async function setAllAnswerTypes(selectedAnswerTypeValue: number | null = null, selectID: string = "answerTypesSelect", needToCheckHidden: boolean = true, offsetValue: number = 0) {
    let selectAnswerTypes = document.getElementById(selectID) as HTMLSelectElement;
    const page = pageCheck

    try {
        let response = await fetch(`${server}/services/get/answerTypes`);
        let json = await response.json();

        answerTypes = [];
        for (let currentAnswerType of json.answerTypes) {
            let newAnswerType = new AnswerType(currentAnswerType.value, currentAnswerType.name, currentAnswerType.groupedTags, currentAnswerType.hidden, currentAnswerType.forceHidden);
            answerTypes.push(newAnswerType);

            let option = document.createElement("option");
            option.value = currentAnswerType.value + +offsetValue;
            option.textContent = currentAnswerType.name;
            selectAnswerTypes.appendChild(option)
            selectAnswerTypes.appendChild(option);
            if (selectedAnswerTypeValue != null && currentAnswerType.value + offsetValue == selectedAnswerTypeValue) {
                selectAnswerTypes.value = currentAnswerType.value;
            }
        }

        if (needToCheckHidden) {
            for (let currentAnswerType of answerTypes) {
                checkIsHiddenStatus(currentAnswerType, false, false, selectID);
            }
        }
    } catch (e) {
        checkPageExecute(() =>
            popup(false, 'Antwort Typen konnten nicht geladen werden!'), page)
    }
}

function checkIsHiddenStatus(answerType: AnswerType, isSet: boolean = false, setValue: boolean = false, selectID: string = "answerTypesSelect") {
    let selectAnswerTypes = document.getElementById("answerTypesSelect") as HTMLSelectElement;
    if (answerType.id == +selectAnswerTypes.value) {
        let hiddenInput = document.getElementById("isHiddenInput") as HTMLInputElement;
        if (!isSet) {
            hiddenInput.checked = answerType.isHiddenDefault;
        } else {
            hiddenInput.checked = setValue;
        }

        hiddenInput.disabled = answerType.forceIsHiddenDefault;
    }
}

/**
 * Logout to Login page
 */
async function logout(urlParams: URLSearchParams, onPurpose: boolean = true) {
    await fetch(`${server}/Logout`, {
        method: 'post'
    });

    if (urlParams === null) {
        window.location.replace(`${server}/pages/login/login.jsp`);
        return;
    }

    const targetPage = urlParams.get(targetPageParamName);
    const activeButtonID = urlParams.get(targetPageActiveButtonID);
    const objectID = urlParams.get(objectIDParam);

    window.location.replace(`${server}/pages/login/login.jsp?${(onPurpose) ? `targetPage=${targetPage}&activeButtonID=${activeButtonID}&objectID=${objectID}` : ''}`)
}

/**
 * Shows a popup
 * @param success if the popup is positive (true) or negative (false)
 * @param textContent the content which will be displayed on the screen
 * @param uptime the time the popup should be visible
 */
function popup(success: boolean, textContent: string, uptime: number = 3000) {
    uptime = clamp(uptime, 1050, 10000);

    let msg = document.createElement('div') as HTMLDivElement;
    msg.classList.add((success) ? 'successMessage' : 'failMessage');

    let img = document.createElement('img') as HTMLImageElement;
    img.src = `${server}/assets/images/${(success) ? 'success' : 'failed'}.svg`;
    img.classList.add('image');

    let content = document.createElement('h3') as HTMLParagraphElement;
    content.innerHTML = textContent;
    content.classList.add('message');

    msg.appendChild(img);
    msg.appendChild(content);

    document.getElementById('pageContent')?.appendChild(msg);

    setTimeout(() => {
        msg.style.animation = 'slideOut 1s ease-in-out';
        msg.style.animationPlayState = 'running';
        msg.addEventListener('animationend', () => {
            msg.remove();
        });
    }, uptime);

}

/**
 * after we finished loading all data, disable the loader
 * @author Tim Irmler
 */
function disableLoader() {
    $("#contentLoaderAnimation").remove();
}

/**
 * if we need to load new data, init the animation
 * @author Tim Irmler
 * @param idOfDivToPutItIn
 */
function initLoadingAnimation(idOfDivToPutItIn: string) {
    let container = document.createElement("div");
    container.id = "contentLoaderAnimation";
    container.classList.add("lds-roller");
    for (let i = 0; i < 8; i++) {
        let div = document.createElement("div");
        container.appendChild(div);
    }

    document.getElementById(idOfDivToPutItIn)?.appendChild(container);
}

function removeFileExtension(page: string) {
    return page.replace(/\.[^/.]+$/, "")
}

function checkPageExecute(fun: () => void, specifiedPage: string | null) {
    
    if (specifiedPage === pageCheck || specifiedPage === null || pageCheck === null) {
        fun()
    }
}