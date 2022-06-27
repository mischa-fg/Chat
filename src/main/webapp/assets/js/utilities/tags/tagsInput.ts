/**
 * create the tag in html
 * @param tag the tag
 * @return returns the html element
 */
function createTagForInput(tag: string) {
    // 
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

/**
 * remove all tags from the input
 */
function clearAllTagsFromInput() {
    // 
    document.querySelectorAll('.tag').forEach(tag => {
        tag.parentElement?.removeChild(tag);
    });
}

/**
 * write all the tags in to the input
 */
function putTagsInInput() {
    // 
    clearAllTagsFromInput();
    tagsInputTags.slice().reverse().forEach(tag => {
        const tagContainer = document.getElementById('tag-container-container') as HTMLElement;
        tagContainer.prepend(createTagForInput(tag));
    });

    let input = document.getElementById("tag-container-input") as HTMLTextAreaElement;
    input.value = '';
}

/**
 * create tags by getting the value of a html element, with a keyboard event
 * we find the element by checking the target of the keyboard event
 * @param e the keyboard event with which we find the html element
 */
function createTagKeyboardEvent(e: KeyboardEvent) {
    // 
    (e.target as HTMLTextAreaElement).value.split(',').forEach(tag => {
        createTag(tag);
    });
}

/**
 * create tags by getting the value of a html element
 * @param element the html text area element
 */
function createTagElement(element: HTMLTextAreaElement) {
    // 
    (element).value.split(',').forEach(tag => {
        createTag(tag);
    });
}

/**
 * create tags by getting the value of a html element, we look for the element with the id
 * @param elementID the id of the element
 */
function createTagByElementID(elementID: string) {
    // 
    let element = document.getElementById(elementID) as HTMLTextAreaElement;
    element.value.split(',').forEach(tag => {
        createTag(tag);
    });
}

/**
 * create tag and add it to the global array
 * this function removes all spaces and checks if the tag isn't empty
 * @param tag the tag we want to add
 */
function createTag(tag: string) {
    // 
    tag = tag.split(" ").join(""); // Replace ALL spaces
    if (tag !== "") {
        tagsInputTags.push(tag);
    }
}

/**
 * generate a string with all tags, seperated by a comma(,)
 */
function generateTagString() {
    // 
    let tagString: string = "";
    tagsInputTags.slice().reverse().forEach(tag => {
        tagString += tag + ",";
    });
    return tagString.slice(0, -1); // remove last ','
}

/**
 * removes the latest tag in the array, without updating all the rendered tags
 */
function removeLatestTag() {
    // 
    tagsInputTags.pop();
}

/**
 * removes a specific tag in the array, without updating all the rendered tags
 */
function removeSpecificTag(e: MouseEvent) {
    const tagLabel = (e.target as HTMLTextAreaElement).getAttribute('data-item');
    // 
    if (tagLabel != null) {
        const index = tagsInputTags.indexOf(tagLabel);
        tagsInputTags = [...tagsInputTags.slice(0, index), ...tagsInputTags.slice(index + 1)];
    }
}

/**
 * add event listener and check if
 */
document.getElementById('tag-container-input')?.addEventListener('keyup', (e: KeyboardEvent) => {
    const input: HTMLInputElement = document.getElementById('tag-container-input') as HTMLInputElement;
    // 
    if (e.key === 'Enter' || e.key === ' ' || e.key === ',') {
        createTagKeyboardEvent(e);

        putTagsInInput();

    } else if (e.key === 'Backspace') { // count the times we press backspace. if we press it (fast enough) 2 times, delete the last tag
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
    
    triggerInputEvent(input)
});

/**
 * add listener with which we remove the individual tag on click on the X
 */
document.addEventListener('click', (e: MouseEvent) => {
    if ((e.target as HTMLTextAreaElement).tagName === 'I') {
        removeSpecificTag(e);
        putTagsInInput();
    }
})