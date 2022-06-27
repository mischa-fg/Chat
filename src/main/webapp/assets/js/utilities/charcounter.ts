async function initMaxLengths(page: string | null, elements: { elem: HTMLInputElement | HTMLTextAreaElement | null | undefined; name: string }[], forceExecution = false) {
    const response = await fetch(`${server}/services/get/maxInputLength`);
    const json = await response.json() as { types: { maxLength: number, name: string }[], success: boolean };

    if (!json.success) {
        return;
    }

    

    typeLoop:
        for (const element of elements) {
            for (const type of json.types) {
                if (type.name === element.name) {
                    const exec = () => {
                        if (element.elem === null || element.elem === undefined) {
                            return;
                        }
                        element.elem.maxLength = type.maxLength;
                        countCharacters(element.elem);
                        element.elem.addEventListener('input', () => {
                            countCharacters(element.elem);
                        })
                    };

                    if (forceExecution) {
                        exec()
                    } else {
                        checkPageExecute(exec, page)
                    }
                    continue typeLoop;
                }
            }
        }
}


function countCharacters(element: HTMLInputElement | HTMLTextAreaElement | null | undefined) {
    if (element == null) return;

    const max = element.maxLength;
    const length = element.value.length;
    const counter = max - length;
    const helper = nextElement(element, 'form-text');
    // Switch to the singular if there's exactly 1 character remaining
    if (helper == null) return;
    helper.innerHTML = counter + " Zeichen übrig";
    // Make it red if there are 0 characters remaining
    if (counter === 0) {
        helper?.classList.remove("text-muted");
        helper?.classList.add("text-danger");
    } else {
        helper?.classList.remove("text-danger");
        helper?.classList.add("text-muted");
    }
}

function nextElement(start: HTMLElement | Element | null | undefined, condition: string): Element | HTMLElement | null | undefined {
    if (start == null) {
        return null;
    } else if (start.classList.contains(condition)) {
        return start;
    } else if (start.children.length > 0) {
        return nextElement(start.children.item(0), condition);
    } else if (start.nextElementSibling != null) {
        return nextElement(start.nextElementSibling, condition);
    } else {
        if (start.parentElement?.classList.contains(condition)) return start.parentElement;

        let next = start.parentElement;
        for (let i = 0; i < 10; i++) {
            if (next?.nextElementSibling !== null) {
                return nextElement(next?.nextElementSibling, condition);
            }
            next = next.parentElement;
        }

        return nextElement(start.parentElement?.parentElement?.nextElementSibling, condition);
    }
}

function triggerInputEvent(element: HTMLInputElement | HTMLTextAreaElement) {
    element.dispatchEvent(new Event('input', {bubbles: true, cancelable: true}))
}