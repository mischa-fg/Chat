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
function reload() {
}
function loadUsers() {
    reload();
    fetch(`${server}/services/get/allUsers`)
        .then((response) => {
        return response.json();
    }).then((retrievedUser) => {
        saveUser(retrievedUser);
    });
}
function saveUser(retrievedUsers) {
    if (retrievedUsers != null) {
        users = [];
        for (let retrievedUser of retrievedUsers) {
            let userId = retrievedUser.id;
            let userEmail = retrievedUser.email;
            let userCreate = retrievedUser.canCreate;
            let lastLogin = retrievedUser.lastLoggedIn;
            let isCurrentUser = retrievedUser.isCurrentUser;
            let userList = new MyUser(userId, userEmail, userCreate, lastLogin, isCurrentUser);
            users.push(userList);
            if (userList.isCurrentUser) {
                loggedInUser = userList;
            }
        }
        createUserString(users);
    }
    else {
        createUserString(null);
    }
}
function createUserString(users, value = null) {
    if (users != null) {
        value = '';
        for (let i = 0; i < users.length; i++) {
            value += users[i].toUserHTML().outerHTML;
        }
    }
    else if (value == null) {
        value = "Couldn't load user!";
    }
    $(".allUserTableBody").empty();
    document.getElementById("allUserTableBody").innerHTML = value;
}
function deleteUser(id, element) {
    return __awaiter(this, void 0, void 0, function* () {
        if (loggedInUser.id !== id) {
            element.disabled = true;
            let confirmMessage = `Willst du den Nutzer wirklich löschen?`;
            if (confirm(confirmMessage)) {
                try {
                    yield fetch(`${server}/services/adminTool/deleteUser`, {
                        method: 'post',
                        body: `userId=${id}`
                    }).then((response) => {
                        var _a;
                        if (response.ok) {
                            const ids = document.getElementsByClassName('id-hidden');
                            for (const cid of ids) {
                                if (cid.innerHTML === String(id)) {
                                    (_a = cid.parentElement) === null || _a === void 0 ? void 0 : _a.remove();
                                }
                            }
                        }
                    });
                }
                catch (e) {
                    popup(false, 'Dieser Nutzer konnte nicht gelöscht werden!');
                }
            }
        }
        else {
            element.disabled = false;
            popup(false, 'Du kannst dich selber nicht löschen!');
        }
    });
}
