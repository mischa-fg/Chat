"use strict";
var _a;
function createTagForInput(tag) {
    const div = document.createElement('div');
    div.setAttribute('class', 'tag');
    const span = document.createElement('span');
    span.innerHTML = tag.toString();
    const closeIcon = document.createElement('i');
    closeIcon.innerHTML = '&times';
    closeIcon.setAttribute('class', 'tagCloseIcon');
    closeIcon.setAttribute('data-item', String(tag));
    div.appendChild(span);
    div.appendChild(closeIcon);
    return div;
}
function clearAllTagsFromInput() {
    document.querySelectorAll('.tag').forEach(tag => {
        var _a;
        (_a = tag.parentElement) === null || _a === void 0 ? void 0 : _a.removeChild(tag);
    });
}
function putTagsInInput() {
    clearAllTagsFromInput();
    tagsInputTags.slice().reverse().forEach(tag => {
        const tagContainer = document.getElementById('tag-container-container');
        tagContainer.prepend(createTagForInput(tag));
    });
    let input = document.getElementById("tag-container-input");
    input.value = '';
}
function createTagKeyboardEvent(e) {
    e.target.value.split(',').forEach(tag => {
        createTag(tag);
    });
}
function createTagElement(element) {
    (element).value.split(',').forEach(tag => {
        createTag(tag);
    });
}
function createTagByElementID(elementID) {
    let element = document.getElementById(elementID);
    element.value.split(',').forEach(tag => {
        createTag(tag);
    });
}
function createTag(tag) {
    tag = tag.split(" ").join("");
    if (tag !== "") {
        tagsInputTags.push(tag);
    }
}
function generateTagString() {
    let tagString = "";
    tagsInputTags.slice().reverse().forEach(tag => {
        tagString += tag + ",";
    });
    return tagString.slice(0, -1);
}
function removeLatestTag() {
    tagsInputTags.pop();
}
function removeSpecificTag(e) {
    const tagLabel = e.target.getAttribute('data-item');
    if (tagLabel != null) {
        const index = tagsInputTags.indexOf(tagLabel);
        tagsInputTags = [...tagsInputTags.slice(0, index), ...tagsInputTags.slice(index + 1)];
    }
}
(_a = document.getElementById('tag-container-input')) === null || _a === void 0 ? void 0 : _a.addEventListener('keyup', (e) => {
    const input = document.getElementById('tag-container-input');
    if (e.key === 'Enter' || e.key === ' ' || e.key === ',') {
        createTagKeyboardEvent(e);
        putTagsInInput();
    }
    else if (e.key === 'Backspace') {
        tagsInputPressCount++;
        setTimeout(function () {
            tagsInputPressCount = 0;
        }, 200);
        if (tagsInputPressCount >= 2 && input.value == "") {
            tagsInputPressCount = 0;
            removeLatestTag();
            putTagsInInput();
        }
    }
    triggerInputEvent(input);
});
document.addEventListener('click', (e) => {
    if (e.target.tagName === 'I') {
        removeSpecificTag(e);
        putTagsInInput();
    }
});
