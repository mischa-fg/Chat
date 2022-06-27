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
function selectText(element) {
    let range = document.createRange();
    range.selectNodeContents(element);
    let windowSelection = window.getSelection();
    windowSelection === null || windowSelection === void 0 ? void 0 : windowSelection.removeAllRanges();
    windowSelection === null || windowSelection === void 0 ? void 0 : windowSelection.addRange(range);
}
document.getElementById('createUserButton').onclick = () => __awaiter(void 0, void 0, void 0, function* () {
    const userEmail = document.getElementById('newUserEmailAddress').value;
    const userCanCreateUsers = document.getElementById('canCreateUserInput').checked;
    try {
        const response = yield fetch(`${server}/CreateUser?email=${userEmail}&canCreateUser=${userCanCreateUsers}`);
        const json = yield response.json();
        const newUserData = document.getElementById('newUserData');
        newUserData.innerHTML = '';
        if (json.error === '') {
            const emailContainer = document.createElement('div');
            const emailTitle = document.createElement('span');
            emailTitle.innerHTML = 'Email: ';
            emailTitle.style.fontWeight = 'bold';
            const email = document.createElement('span');
            email.innerHTML = json.email;
            emailContainer.append(emailTitle, email);
            const passwordContainer = document.createElement('div');
            const passwordTitle = document.createElement('span');
            passwordTitle.innerHTML = 'Passwort: ';
            passwordTitle.style.fontWeight = 'bold';
            const password = document.createElement('span');
            password.innerHTML = json.password;
            passwordContainer.append(passwordTitle, password);
            newUserData === null || newUserData === void 0 ? void 0 : newUserData.append(emailContainer, passwordContainer);
        }
        else {
            const error = document.createElement('p');
            error.innerHTML = json.error;
            error.style.fontWeight = 'bold';
            newUserData === null || newUserData === void 0 ? void 0 : newUserData.append(error);
        }
    }
    catch (e) {
        popup(false, 'Nutzer konnte aus unerwarteten gründen nicht erstellt werden!');
    }
    loadUsers();
});
