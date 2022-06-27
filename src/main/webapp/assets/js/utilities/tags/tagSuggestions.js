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
function getTagList(element, shouldPutTagsInInput = false) {
    return __awaiter(this, void 0, void 0, function* () {
        try {
            let response = yield fetch(`${server}/services/get/autocompleteList`);
            tagList = (yield response.json());
            getSuggestions(element, shouldPutTagsInInput);
        }
        catch (e) {
            popup(false, 'Tag Vorschläge konnten nicht vom Server geholt werden!');
        }
        if (typeof tagsInputTags !== 'undefined') {
            tagsInputTags = [];
        }
    });
}
function getSuggestions(element, shouldPutTagsInInput = false) {
    let inputElement = document.getElementById(element);
    let table = document.getElementById('suggestions');
    if (!keyListenerAdded) {
        addInputListener(inputElement, table, shouldPutTagsInInput, element);
        keyListenerAdded = true;
    }
    if (tagList === undefined) {
        return;
    }
    let input = inputElement.value.split(/\s+/);
    let word = input[input.length - 1];
    if ((input.length == 1 && word == '') || word == '') {
        table.innerHTML = '';
        return;
    }
    table.innerHTML = '';
    tagList.suggestions.forEach(item => {
        if (item.toLowerCase().startsWith(word.toLowerCase())) {
            let row = document.createElement('tr');
            let cell = document.createElement('td');
            cell.innerHTML = item;
            cell.onclick = function () {
                applySuggestion(element, item, shouldPutTagsInInput);
            };
            row.appendChild(cell);
            table === null || table === void 0 ? void 0 : table.appendChild(row);
        }
    });
}
function addInputListener(input, table, shouldPutTagsInInput, element) {
    input.addEventListener('keydown', (e) => {
        if (e.key === 'Tab' || e.key == 'ArrowDown') {
            e.preventDefault();
            if ((table === null || table === void 0 ? void 0 : table.children) !== undefined) {
                if (selectedSuggestion >= 0)
                    table.children[selectedSuggestion].classList.remove('currentSuggestion');
                if (selectedSuggestion + 1 < table.children.length) {
                    selectedSuggestion++;
                }
                else {
                    selectedSuggestion = 0;
                }
                if (table.children.length >= 1)
                    table === null || table === void 0 ? void 0 : table.children[selectedSuggestion].classList.add('currentSuggestion');
            }
        }
        else if (e.key === 'Enter') {
            e.preventDefault();
            if (table === null)
                return;
            else if (table.children[0] === undefined)
                return;
            else if (table.children[0].children === undefined)
                return;
            let tag = table.children[selectedSuggestion].children[0].innerHTML;
            applySuggestion(element, tag, shouldPutTagsInInput);
        }
        else if (e.key == 'ArrowUp') {
            e.preventDefault();
            if ((table === null || table === void 0 ? void 0 : table.children) !== undefined) {
                if (selectedSuggestion == -1)
                    selectedSuggestion = table.children.length;
                if (selectedSuggestion < table.children.length)
                    table.children[selectedSuggestion].classList.remove('currentSuggestion');
                if (selectedSuggestion > 0) {
                    selectedSuggestion--;
                }
                else {
                    selectedSuggestion = table.children.length - 1;
                }
                if (table.children.length >= 1)
                    table === null || table === void 0 ? void 0 : table.children[selectedSuggestion].classList.add('currentSuggestion');
            }
        }
        else {
            selectedSuggestion = -1;
        }
    });
}
function applySuggestion(element, tag, shouldPutTagsInInput) {
    let table = document.getElementById('suggestions');
    let input = document.getElementById(element);
    let content = input.value.split(/\s+/);
    content[content.length - 1] = tag;
    let out = '';
    for (let i = 0; i < content.length; i++)
        out += content[i] + ' ';
    input.value = out;
    if (shouldPutTagsInInput) {
        createTagByElementID(element);
        putTagsInInput();
    }
    table.innerHTML = '';
    triggerInputEvent(input);
    input.focus();
}
