function reload() {
}

function loadUsers() {
    reload();
    fetch(`${server}/services/get/allUsers`)
        .then((response) => {
            return response.json();
        }).then((retrievedUser: any) => {
        saveUser(retrievedUser);
    })
    /*.catch(function () {

            //createAnswerString(null);
            return null;
        }
    );*/
}

function saveUser(retrievedUsers: any) {
    if (retrievedUsers != null) {
        users = [];
        for (let retrievedUser of retrievedUsers) {
            let userId: number = retrievedUser.id;
            let userEmail: string = retrievedUser.email;
            let userCreate: boolean = retrievedUser.canCreate;
            let lastLogin: string = retrievedUser.lastLoggedIn;
            let isCurrentUser: boolean = retrievedUser.isCurrentUser;

            let userList: MyUser = new MyUser(userId, userEmail, userCreate, lastLogin, isCurrentUser);
            users.push(userList);

            if (userList.isCurrentUser) {
                loggedInUser = userList;
            }
        }
        createUserString(users);
    } else {
        createUserString(null);
    }
}

function createUserString(users: MyUser[] | null, value: string | null = null) {
    if (users != null) {
        value = '';
        for (let i = 0; i < users.length; i++) {
            value += users[i].toUserHTML().outerHTML;
        }
    } else if (value == null) {
        value = "Couldn't load user!";
    }
    $(".allUserTableBody").empty();
    document.getElementById("allUserTableBody")!.innerHTML = value;
}

async function deleteUser(id: number, element: HTMLButtonElement) {
    if (loggedInUser.id !== id) {
        element.disabled = true;
        let confirmMessage: string = `Willst du den Nutzer wirklich löschen?`;
        if (confirm(confirmMessage)) {
            try {
                await fetch(`${server}/services/adminTool/deleteUser`, {
                    method: 'post',
                    body: `userId=${id}`
                }).then((response) => {
                    
                    if (response.ok) {
                        const ids = document.getElementsByClassName('id-hidden');
                        for (const cid of ids) {
                            if (cid.innerHTML === String(id)) {
                                cid.parentElement?.remove();
                            }
                        }
                    }
                });
            } catch (e) {
                
                popup(false, 'Dieser Nutzer konnte nicht gelöscht werden!');
            }
        }
    } else {
        element.disabled = false;
        popup(false, 'Du kannst dich selber nicht löschen!');
    }
}