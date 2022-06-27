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
function initMaxLengths(page, elements, forceExecution = false) {
    return __awaiter(this, void 0, void 0, function* () {
        const response = yield fetch(`${server}/services/get/maxInputLength`);
        const json = yield response.json();
        if (!json.success) {
            return;
        }
        typeLoop: for (const element of elements) {
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
                        });
                    };
                    if (forceExecution) {
                        exec();
                    }
                    else {
                        checkPageExecute(exec, page);
                    }
                    continue typeLoop;
                }
            }
        }
    });
}
function countCharacters(element) {
    if (element == null)
        return;
    const max = element.maxLength;
    const length = element.value.length;
    const counter = max - length;
    const helper = nextElement(element, 'form-text');
    if (helper == null)
        return;
    helper.innerHTML = counter + " Zeichen übrig";
    if (counter === 0) {
        helper === null || helper === void 0 ? void 0 : helper.classList.remove("text-muted");
        helper === null || helper === void 0 ? void 0 : helper.classList.add("text-danger");
    }
    else {
        helper === null || helper === void 0 ? void 0 : helper.classList.remove("text-danger");
        helper === null || helper === void 0 ? void 0 : helper.classList.add("text-muted");
    }
}
function nextElement(start, condition) {
    var _a, _b, _c;
    if (start == null) {
        return null;
    }
    else if (start.classList.contains(condition)) {
        return start;
    }
    else if (start.children.length > 0) {
        return nextElement(start.children.item(0), condition);
    }
    else if (start.nextElementSibling != null) {
        return nextElement(start.nextElementSibling, condition);
    }
    else {
        if ((_a = start.parentElement) === null || _a === void 0 ? void 0 : _a.classList.contains(condition))
            return start.parentElement;
        let next = start.parentElement;
        for (let i = 0; i < 10; i++) {
            if ((next === null || next === void 0 ? void 0 : next.nextElementSibling) !== null) {
                return nextElement(next === null || next === void 0 ? void 0 : next.nextElementSibling, condition);
            }
            next = next.parentElement;
        }
        return nextElement((_c = (_b = start.parentElement) === null || _b === void 0 ? void 0 : _b.parentElement) === null || _c === void 0 ? void 0 : _c.nextElementSibling, condition);
    }
}
function triggerInputEvent(element) {
    element.dispatchEvent(new Event('input', { bubbles: true, cancelable: true }));
}
