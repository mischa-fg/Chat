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
function loadUser() {
    return __awaiter(this, void 0, void 0, function* () {
        let fullSearchParams = window.location.search;
        const urlParams = new URLSearchParams(fullSearchParams);
        let userID = +urlParams.get("objectID");
        users = [];
        if (userID != null && !isNaN(userID) && userID > 0) {
            try {
                let response = yield fetch(`${server}/services/get/singleUser?userID=${userID}`);
                let json = yield response.json();
                let user = new MyUser(json[0].id, json[0].email, json[0].canCreate, json[0].lastLoggedIn, json[0].isCurrentUser);
                users.push(user);
                dataClass = user;
                if (user.isCurrentUser) {
                    loggedInUser = user;
                }
                yield initialiseUserEditPage(user);
            }
            catch (e) {
                popup(false, 'User konnte nicht geladen werden!');
            }
        }
    });
}
function initialiseUserEditPage(user) {
    return __awaiter(this, void 0, void 0, function* () {
        const userNameInput = document.getElementById("userName");
        const userCanCreateInput = document.getElementById("canCreateUserInput");
        const resetPasswordInput = document.getElementById('resetPassword');
        const labelOfUserCanCreateInput = document.getElementById('labelOfCanCreateUserInput');
        document.title = adminToolPageTitleName + ' - Benutzer bearbeiten';
        if (user != null) {
            if (user.isCurrentUser) {
                userCanCreateInput.remove();
                resetPasswordInput.remove();
                labelOfUserCanCreateInput.remove();
                popup(false, "Du Bearbeitest dich selber! Möglichkeiten wurden eingeschränkt!", 5000);
            }
            else {
                userCanCreateInput.checked = user.canCreateUser;
            }
            let submitButton = document.getElementById("userFormSubmitButton");
            submitButton.setAttribute("onClick", "updateUser();");
            userNameInput.value = user.email;
            disableLoader();
        }
        else {
            yield loadPage("settings.jsp", "settingsButton", false, -1, true);
        }
    });
}
function updateUser() {
    return __awaiter(this, void 0, void 0, function* () {
        let fullSearchParams = window.location.search;
        const urlParams = new URLSearchParams(fullSearchParams);
        let userId = +urlParams.get("objectID");
        let inputCreate = 0;
        let currentUser = dataClass;
        if (!currentUser.isCurrentUser) {
            if (document.getElementById("canCreateUserInput").checked) {
                inputCreate = 1;
            }
            else {
                inputCreate = 2;
            }
        }
        let userName = document.getElementById("userName").value;
        if (userName != '' && userName != null) {
            try {
                let response = yield fetch(server + '/services/adminTool/editUser', {
                    method: 'post',
                    body: `userId=${userId}&email=${userName}&canCreateUser=${inputCreate}`
                });
                if (response.ok) {
                    yield loadPage('userDetails.jsp', 'settingsButton', true, -1, true);
                }
                else {
                    popup(false, 'Dieser Nutzer konnte nicht aktualisiert werden!');
                }
            }
            catch (e) {
                popup(false, 'Dieser Nutzer konnte nicht aktualisiert werden!');
            }
        }
        else {
            document.getElementById('errorTag').innerHTML = 'Es wurde kein Nutzer gefunden';
        }
    });
}
document.getElementById('resetPassword').onclick = () => __awaiter(void 0, void 0, void 0, function* () {
    function copyToClipboard(content) {
        navigator.clipboard.writeText(content).then(() => {
        }, () => {
        });
    }
    if (confirm("Bist du sicher du willst das Passwort von diesem User zurücksetzen?")) {
        try {
            let fullSearchParams = window.location.search;
            const urlParams = new URLSearchParams(fullSearchParams);
            let userId = +urlParams.get("objectID");
            let response = yield fetch(`${server}/ResetPassword?userId=${userId}`);
            let json = yield response.json();
            const emailDiv = document.createElement('div');
            const emailTitle = document.createElement('span');
            emailTitle.innerHTML = 'Email: ';
            emailTitle.style.fontWeight = 'bold';
            const email = document.createElement('span');
            email.innerHTML = json.email;
            const copyEmailIcon = document.createElement('img');
            copyEmailIcon.src = `${server}/assets/images/copy.svg`;
            copyEmailIcon.classList.add('copyTextIcon');
            copyEmailIcon.onclick = () => {
                copyToClipboard(json.email);
            };
            emailDiv.append(emailTitle, email, copyEmailIcon);
            const newPasswordData = document.getElementById('newPasswordData');
            newPasswordData.innerHTML = '';
            const passwordDiv = document.createElement('div');
            const passwordTitle = document.createElement('span');
            passwordTitle.innerHTML = 'Passwort: ';
            passwordTitle.style.fontWeight = 'bold';
            const password = document.createElement('span');
            password.innerHTML = json.password;
            const copyPasswordIcon = document.createElement('img');
            copyPasswordIcon.src = `${server}/assets/images/copy.svg`;
            copyPasswordIcon.classList.add('copyTextIcon');
            copyPasswordIcon.onclick = () => {
                copyToClipboard(json.password);
            };
            passwordDiv.append(passwordTitle, password, copyPasswordIcon);
            newPasswordData === null || newPasswordData === void 0 ? void 0 : newPasswordData.append(emailDiv, passwordDiv);
        }
        catch (e) {
            popup(false, 'Nutzer konnte nicht aktualisiert werden!');
        }
    }
});
