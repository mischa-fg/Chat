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
document.title = adminToolPageTitleName + ' - Dateien';
function getFilesFromServer() {
    return __awaiter(this, void 0, void 0, function* () {
        const page = pageCheck;
        try {
            let response = yield fetch(`${server}/services/get/files`);
            let json = yield response.json();
            let noFiles = document.getElementById('noFiles');
            disableLoader();
            let container = document.getElementById('fileContainer');
            container.innerHTML = '';
            noFiles.innerHTML = '';
            if (json.files.length <= 0) {
                noFiles.innerHTML = 'Die Datenbank enthält keine Dateien.';
            }
            let files = [];
            for (let file of json.files) {
                files.push(new MyFile(file.id, file.name, file.type));
                let cell = document.createElement('div');
                let img = document.createElement('img');
                let anchor = document.createElement('a');
                let title = document.createElement('p');
                let deleteButton = document.createElement('button');
                img.classList.add('file__img');
                img.src = `${server}/assets/images/files/${file.name.substring(file.name.lastIndexOf('.') + 1).toLowerCase()}.svg`;
                title.classList.add('file__title');
                title.innerHTML = file.name;
                anchor.appendChild(img);
                anchor.appendChild(title);
                anchor.target = '_blank';
                anchor.classList.add('file__anchor');
                anchor.href = `${server}/file?id=${file.id}`;
                cell.classList.add('file__cell');
                cell.append(anchor);
                deleteButton.innerHTML = 'Löschen';
                deleteButton.classList.add('btn');
                deleteButton.classList.add('buttonSubmit');
                deleteButton.classList.add('file__delete');
                deleteButton.onclick = () => __awaiter(this, void 0, void 0, function* () {
                    if (confirm(`Willst du die Datei "${file.name}" wirklich löschen und von ${file.answerCount} Antworten entfernen?`)) {
                        let success = true;
                        try {
                            yield fetch(`${server}/services/adminTool/removeFile`, {
                                method: 'post',
                                body: `fileID=${file.id}`
                            });
                            checkPageExecute(() => __awaiter(this, void 0, void 0, function* () {
                                yield getFilesFromServer();
                            }), 'files');
                        }
                        catch (e) {
                            success = false;
                        }
                        checkPageExecute(() => popup(success, file.name + ' ' + ((success) ? 'wurde erfolgreich gelöscht!' : 'konnte nicht gelöscht werden!')), 'files');
                    }
                });
                checkPageExecute(() => {
                    cell.appendChild(deleteButton);
                    container === null || container === void 0 ? void 0 : container.appendChild(cell);
                }, 'files');
            }
        }
        catch (e) {
            checkPageExecute(() => popup(false, 'Diese Seite konnte leider nicht geladen werden!'), 'files');
        }
    });
}
function submitFileForm() {
    return __awaiter(this, void 0, void 0, function* () {
        let form = document.getElementById('fileAddForm');
        let statusParagraph = document.getElementById('uploadStatus');
        const page = pageCheck;
        let fd = new FormData(form);
        let file = fd.get('file');
        let blob = file.slice(0, file.size, file.type);
        let name = file.name.replace('ä', 'ae');
        name = name.replace('Ä', 'Ae');
        name = name.replace('ö', 'oe');
        name = name.replace('Ö', 'Oe');
        name = name.replace('ü', 'ue');
        name = name.replace('Ü', 'Ue');
        name = name.replace(/[^a-zA-Z0-9.\-_]/g, '');
        statusParagraph.innerHTML = 'Datei wird hochgeladen...';
        fd.set('file', new File([blob], name, { type: file.type }));
        try {
            let response = yield fetch(`${server}/FileUpload`, { method: 'post', body: fd });
            let json = yield response.json();
            statusParagraph.innerHTML = json.comment;
            checkPageExecute(() => __awaiter(this, void 0, void 0, function* () {
                if (json.code === 200) {
                    yield getFilesFromServer();
                    document.getElementById('fileInput').value = '';
                    document.getElementById('fileSelectButton').innerHTML = 'Datei auswählen';
                    popup(true, json.comment);
                }
                else {
                    popup(false, json.comment);
                }
            }), 'files');
        }
        catch (e) {
            checkPageExecute(() => popup(false, 'Datei konnte aus unerwarteten Gründen nicht hochgeladen werden!'), 'files');
        }
    });
}
function setFileText(inp) {
    let e = document.getElementById('fileSelectButton');
    e.innerHTML = inp.value.split(/([\\/])/g).pop();
}
getFilesFromServer().then();
