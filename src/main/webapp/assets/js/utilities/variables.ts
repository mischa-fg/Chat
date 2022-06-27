const server = window.location.origin + "/chatbot";
// const server = window.location.origin;
let selectedSuggestion: number = -1;
let keyListenerAdded = false;
const adminToolPageTitleName = "AdminTool";
const amountQuestions: number = 3;
const loadedTagsPerAnswer = 10;

const enum textInputs {
    ANSWER_TITLE = 'ANSWER_TITLE',
    ANSWER_TEXT = 'ANSWER_TEXT',
    TAG = 'TAG',
    QUESTION = 'USER_QUESTION_INPUT',
    BLACKLIST = 'BLACK_LIST_ENTRY',
}