// NOTE: Function to load all tags from the database
async function getTagList(element: string, shouldPutTagsInInput: boolean = false) {
    try {
        let response = await fetch(`${server}/services/get/autocompleteList`);
        tagList = await response.json() as Taglist;
        getSuggestions(element, shouldPutTagsInInput);
    } catch (e) {
        popup(false, 'Tag Vorschläge konnten nicht vom Server geholt werden!');
    }

    if (typeof tagsInputTags !== 'undefined') {
        tagsInputTags = [];
    }
}

/**
 * show suggestions depending on what is written in element
 * @param element the element id in which we look for text
 * @param shouldPutTagsInInput should the tags be put in to input as a tag later
 */
function getSuggestions(element: string, shouldPutTagsInInput: boolean = false) {
    

    let inputElement = document.getElementById(element) as HTMLInputElement;
    let table = document.getElementById('suggestions') as HTMLTableSectionElement; // Table containing all suggestions


    if (!keyListenerAdded) {
        addInputListener(inputElement, table, shouldPutTagsInInput, element);
        keyListenerAdded = true;
    }

    if (tagList === undefined) { // Return if data from database isn't loaded yet
        return;
    }
    let input = inputElement.value.split(/\s+/); // split input field content
    let word = input[input.length - 1]; // get the last word

    
    

    // If input field is empty remove all suggestions
    if ((input.length == 1 && word == '') || word == '') {
        table!.innerHTML = '';
        return;
    }

    table!.innerHTML = '';
    tagList.suggestions.forEach(item => {
        if (item.toLowerCase().startsWith(word.toLowerCase())) {
            let row = document.createElement('tr');
            let cell = document.createElement('td');
            cell.innerHTML = item;
            cell.onclick = function () {
                applySuggestion(element, item, shouldPutTagsInInput);
            }
            row.appendChild(cell);
            table?.appendChild(row);
        }
    })
}

function addInputListener(input: HTMLInputElement, table: HTMLTableSectionElement, shouldPutTagsInInput: boolean, element: string) {
    input.addEventListener('keydown', (e) => {
        if (e.key === 'Tab' || e.key == 'ArrowDown') {
            e.preventDefault();

            if (table?.children !== undefined) {
                if (selectedSuggestion >= 0)
                    table.children[selectedSuggestion].classList.remove('currentSuggestion');

                if (selectedSuggestion + 1 < table.children.length) {
                    selectedSuggestion++;
                } else {
                    selectedSuggestion = 0;
                }

                if (table.children.length >= 1)
                    table?.children[selectedSuggestion].classList.add('currentSuggestion');
            }
        } else if (e.key === 'Enter') {
            e.preventDefault();

            if (table === null) return;
            else if (table.children[0] === undefined) return;
            else if (table.children[0].children === undefined) return;

            let tag = table.children[selectedSuggestion].children[0].innerHTML;

            applySuggestion(element, tag, shouldPutTagsInInput);
        } else if (e.key == 'ArrowUp') {
            e.preventDefault();

            if (table?.children !== undefined) {
                if (selectedSuggestion == -1) selectedSuggestion = table.children.length;

                if (selectedSuggestion < table.children.length)
                    table.children[selectedSuggestion].classList.remove('currentSuggestion');

                if (selectedSuggestion > 0) {
                    selectedSuggestion--;
                } else {
                    selectedSuggestion = table.children.length - 1;
                }

                if (table.children.length >= 1)
                    table?.children[selectedSuggestion].classList.add('currentSuggestion');
            }
        } else {
            selectedSuggestion = -1;
        }
    })
}

function applySuggestion(element: string, tag: string, shouldPutTagsInInput: boolean) {
    let table = document.getElementById('suggestions'); // Table containing all suggestions

    let input = document.getElementById(element) as HTMLInputElement;
    let content = input.value.split(/\s+/);
    content[content.length - 1] = tag;

    let out = '';
    for (let i = 0; i < content.length; i++) out += content[i] + ' ';

    input.value = out;

    if (shouldPutTagsInInput) {
        createTagByElementID(element);
        putTagsInInput();
    }

    table!.innerHTML = '';
    triggerInputEvent(input)
    input.focus();
}