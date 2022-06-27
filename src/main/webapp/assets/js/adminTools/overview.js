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
document.title = adminToolPageTitleName + ' - Startseite';
function loadEverything(timeRange, buttonElement, initialization = false) {
    return __awaiter(this, void 0, void 0, function* () {
        yield loadSentAnswers(timeRange, buttonElement, initialization);
        yield loadAnsweredVsUnanswered(timeRange, buttonElement, initialization);
    });
}
function loadSentAnswers(timeRange, buttonElement, initialization = false, elementIdOfLoader = 'loaderAnimationContainer1') {
    return __awaiter(this, void 0, void 0, function* () {
        if (!initialization) {
            $('#sentAnswersTime .active').removeClass('active');
            buttonElement.classList.add('active');
        }
        initLoadingAnimation(elementIdOfLoader);
        yield getSentAnswersTime(timeRange, initialization);
        disableLoader();
    });
}
function loadAnsweredVsUnanswered(timeRange, buttonElement, initialization = false, elementIdOfLoader = 'loaderAnimationContainer2') {
    return __awaiter(this, void 0, void 0, function* () {
        if (!initialization) {
            $('#answeredVsUnansweredTime .active').removeClass('active');
            buttonElement.classList.add('active');
        }
        initLoadingAnimation(elementIdOfLoader);
        yield answeredAndUnansweredQuestionsTime(timeRange, initialization);
        disableLoader();
    });
}
function answeredAndUnansweredQuestionsTime(timeRange, initialization = false, canvasId = "chartAnsweredToUnanswered") {
    return __awaiter(this, void 0, void 0, function* () {
        const page = pageCheck;
        try {
            let response = yield fetch(`${server}/services/getStatistics/answeredVsUnansweredPerTime?timeRange=${timeRange}`);
            let responeJson = yield response.json();
            let jsonAnswered = yield responeJson.answered;
            let jsonUnanswered = yield responeJson.unanswered;
            let dates = yield responeJson.dates;
            checkPageExecute(() => createAnsweredQuestionChart(canvasId, [jsonAnswered, jsonUnanswered], dates, initialization), 'overview');
        }
        catch (e) {
            checkPageExecute(() => popup(false, 'Diese Seite konnte leider nicht geladen werden!'), 'overview');
        }
    });
}
function getSentAnswersTime(timeRange, initialization, canvasId = "myChart") {
    return __awaiter(this, void 0, void 0, function* () {
        const page = pageCheck;
        try {
            let response = yield fetch(`${server}/services/getStatistics/sentAnswersPerTime?timeRange=${timeRange}`);
            let json = yield response.json();
            checkPageExecute(() => createSentAnswersChart(canvasId, json, initialization), 'overview');
        }
        catch (e) {
            checkPageExecute(() => popup(false, 'Diese Seite konnte leider nicht geladen werden!'), 'overview');
        }
    });
}
function createSentAnswersChart(canvasId, data, initialization) {
    return __awaiter(this, void 0, void 0, function* () {
        let canvasElement = document.getElementById(canvasId);
        let ctx = canvasElement.getContext('2d');
        let labels = [];
        let amounts = [];
        for (let dataSet of data) {
            labels.push(dataSet.date);
            amounts.push(dataSet.amount);
        }
        let totalThisTime = 0;
        for (let num of amounts) {
            totalThisTime += num;
        }
        let totalAnswersElement = document.getElementById('totalAnswersInThisTime');
        totalAnswersElement.innerHTML = "Total gesendete Antworten in dieser Zeit = " + totalThisTime;
        if (initialization) {
            answerChart = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                            label: 'Anzahl gesendeter Antworten',
                            data: amounts,
                            backgroundColor: [
                                'rgba(255, 99, 132, 0.2)',
                                'rgba(54, 162, 235, 0.2)',
                                'rgba(255, 206, 86, 0.2)',
                                'rgba(75, 192, 192, 0.2)',
                                'rgba(153, 102, 255, 0.2)',
                                'rgba(255, 159, 64, 0.2)'
                            ],
                            borderColor: [
                                'rgba(255, 99, 132, 1)',
                                'rgba(54, 162, 235, 1)',
                                'rgba(255, 206, 86, 1)',
                                'rgba(75, 192, 192, 1)',
                                'rgba(153, 102, 255, 1)',
                                'rgba(255, 159, 64, 1)'
                            ],
                            borderWidth: 1
                        }]
                },
                options: {
                    scales: {
                        y: {
                            beginAtZero: true
                        }
                    }
                }
            });
        }
        else {
            answerChart.data.datasets[0].data = amounts;
            answerChart.data.labels = labels;
            answerChart.update();
        }
    });
}
function createAnsweredQuestionChart(canvasId, data, dates, initialization) {
    return __awaiter(this, void 0, void 0, function* () {
        let canvasElement = document.getElementById(canvasId);
        let ctx = canvasElement.getContext('2d');
        let amountsAnswered = [];
        for (let dataSet of data[0]) {
            amountsAnswered.push(dataSet.amount);
        }
        let totalAnsweredThisTime = 0;
        for (let num of amountsAnswered) {
            totalAnsweredThisTime += num;
        }
        let amountsUnanswered = [];
        for (let dataSet of data[1]) {
            amountsUnanswered.push(dataSet.amount);
        }
        let totalUnansweredThisTime = 0;
        for (let num of amountsUnanswered) {
            totalUnansweredThisTime += num;
        }
        let labels = [];
        for (let date of dates) {
            labels.push(date.date);
        }
        let totalAnsweredElement = document.getElementById('totalAnsweredInThisTime');
        totalAnsweredElement.innerHTML = "Total beantwortete Fragen in dieser Zeit = " + totalAnsweredThisTime;
        let totalUnansweredElement = document.getElementById('totalUnansweredInThisTime');
        totalUnansweredElement.innerHTML = "Total unbeantwortete Fragen in dieser Zeit = " + totalUnansweredThisTime;
        if (initialization) {
            answeredQuestionVsUnansweredQuestionChart = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                            label: 'Anzahl beantworteter Fragen',
                            data: amountsAnswered,
                            backgroundColor: [
                                'rgba(255, 99, 132, 0.9)',
                            ],
                            borderColor: [
                                'rgba(255, 99, 132, 1)',
                            ],
                            borderWidth: 1
                        }, {
                            label: 'Anzahl unbeantworteter Fragen',
                            data: amountsUnanswered,
                            backgroundColor: [
                                'rgba(54, 162, 235, 0.9)',
                            ],
                            borderColor: [
                                'rgba(54, 162, 235, 1)',
                            ],
                            borderWidth: 1
                        }]
                },
                options: {
                    scales: {
                        y: {
                            beginAtZero: true
                        }
                    }
                }
            });
        }
        else {
            answeredQuestionVsUnansweredQuestionChart.data.datasets[0].data = amountsAnswered;
            answeredQuestionVsUnansweredQuestionChart.data.datasets[1].data = amountsUnanswered;
            answeredQuestionVsUnansweredQuestionChart.data.labels = labels;
            answeredQuestionVsUnansweredQuestionChart.update();
        }
    });
}
