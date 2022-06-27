"use strict";
const server = window.location.origin + "/chatbot";
let selectedSuggestion = -1;
let keyListenerAdded = false;
const adminToolPageTitleName = "AdminTool";
const amountQuestions = 3;
const loadedTagsPerAnswer = 10;
