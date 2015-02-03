// html user windows and form filling functions
var user = {}; // object, binded with logged author

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1);
        if (c.indexOf(name) == 0) return c.substring(name.length, c.length);
    }
    return "";
}

function validateLogin(extraParams) {
    var login = getCookie("qLandLogin");
    if (login != "") {
        $.ajax({
            type: 'POST',
            url: '/login',
            dataType: 'json',
            data: { type: 'author', value: login }
        }).done(function(retUser) {
            console.log("login YEAH!");
            user = retUser;
            user.extraParams = extraParams || {};
            console.log(user);
            initUser();
        });
    } else {
        if (extraParams && "questGoExit" in extraParams) {
            window.location = window.location.protocol + "//" + window.location.host + "/quest/" + url2id();
        } else {
            user.extraParams = extraParams || {};
            clearUserWindows();
        }
    }
}

function initUser() {
    $("#authorImgBox").css("display", "block");
    document.getElementById("authorImg")
        .setAttribute("src", "/images/"
            + (user.avatarURL
            ? "author/" + user.avatarURL
            : "none.png")
        );
    $("#authorName").css("display", "block");

    var authorHref = document.createElement('a');
    authorHref.setAttribute("style", "color: #fff;");
    authorHref.href = window.location.protocol + "//" + window.location.host + "/author/" + user.id;
    authorHref.innerHTML = user.login;

    document.getElementById("authorName").innerHTML = authorHref.outerHTML;
    $("#crQuest").css("display", "block");
    $("#logoutUser").css("display", "block");

    // working with extra params
    if (!user.extraParams) return;
    if ("avatar" in user.extraParams) {
        if (!user.avatarURL && url2id() == user.id) {
            $("#Ava").css("display", "none");
            $("#downloadAva").css("display", "block");
        } else {
            $("#Ava").css("display", "inline");
        }
    }
    if ("questForm" in user.extraParams) {
        var divsArr = user.extraParams.questForm;
        for (var i = 0; i < divsArr.length; i++) {
            $(divsArr[i]).css("display", "block");
        }
    }
}

function clearUserWindows() {
    // required params
    $("#authorImgBox").css("display", "none");
    $("#authorName").css("display", "none");
    $("#crQuest").css("display", "none");
    $("#logoutUser").css("display", "none");

    // working with extra params
    if (!user.extraParams) return;
    if ("loginForm" in user.extraParams) {
        document.getElementById("loginForm").setAttribute("style", "margin-left: 5%;");
    }
    if ("loginForm" in user.extraParams) {
        document.getElementById("loginForm").setAttribute("style", "margin-left: 5%;");
    }
    if ("avatar" in user.extraParams) {
        $("#Ava").css("display", "inline");
        $("#downloadAva").css("display", "none");
    }
    if ("questForm" in user.extraParams) {
        var divsArr = user.extraParams.questForm;
        for (var i = 0; i < divsArr.length; i++) {
            $(divsArr[i]).css("display", "none");
        }
    }

}

function exitUser(url) {
    $.ajax({
        type: 'POST',
        url: '/login',
        dataType: 'html',
        data: { type: 'logout', authorId: user.id }
    }).done(function() {
        clearUserWindows();
        user = null;

        if (url) {
            window.location = window.location.protocol + "//" + window.location.host + "/quest/" + url;
        }
    });
}

function addQuestChild(quest) { // creating quest table
    var questTable = document.getElementById("questsTable");

    console.log("rows: " + questTable.rows.length);
    var questRow = questTable.insertRow(questTable.rows.length);
    questRow.setAttribute("style", "height: 270px;");
    var questCell = questRow.insertCell(0);
    questCell.setAttribute("style", "width: 100%; height: 270px;");

    var innerTable = document.createElement("table");
    innerTable.setAttribute("class", "sto_sto");

    var infoRow = innerTable.insertRow(0);
    infoRow.setAttribute("class", "sto_sto");

    var univColors = ["#00b200", "#0000b2", "#ff4d4d", "#a64dff"];
    var universeCell = infoRow.insertCell(0);
    universeCell.setAttribute("style", "width: 6%;");
    universeCell.setAttribute("rowspan", "2");
    var universeDiv = document.createElement("div");
    universeDiv.setAttribute("style", "background-color: " + univColors[quest.universeID - 1]);
    universeDiv.setAttribute("class", "quest_univ");
    var universeText = document.createElement("div");
    universeText.setAttribute("class", "Txt_univ");
    universeText.innerHTML = quest.universe.split("").join(" ");
    universeDiv.appendChild(universeText);
    universeCell.appendChild(universeDiv);

    var imageCell = infoRow.insertCell(1);
    imageCell.setAttribute("style", "width: 30%; text-align: center;");
    imageCell.setAttribute("rowspan", "2");
    var questImage = document.createElement("img");
    questImage.setAttribute("class", "main_pict");
    questImage.setAttribute("src", "/images/" + (quest.imageURL ? "quest/" + quest.imageURL : "none.png"));
    questImage.setAttribute("alt", "quest image");
    imageCell.appendChild(questImage);

    var questBodyCell = infoRow.insertCell(2);
    var questBodyTable = document.createElement("table");
    questBodyTable.setAttribute("style", "width: 100%; height: 100%;");
    var headerRow = questBodyTable.insertRow(0);
    headerRow.setAttribute("style", "height: 30px;");
    var nameCell = headerRow.insertCell(0);
    var questNameDiv = document.createElement("div");
    questNameDiv.setAttribute("class", "Txt_quest");

    var questHref = document.createElement('a');
    questHref.href = window.location.protocol + "//" + window.location.host + "/quest/" + quest.id;
    questHref.innerHTML = quest.name;

    questNameDiv.appendChild(questHref);
    nameCell.appendChild(questNameDiv);

    var rateCell = headerRow.insertCell(1);
    rateCell.setAttribute("style", "width: 15%;");
    var rateDiv = document.createElement("div");
    rateDiv.setAttribute("class", "Txt_mark");
    rateDiv.innerHTML = (quest.rate > 0 ? '+' : '') + quest.rate;
    rateCell.appendChild(rateDiv);

    var descRow = questBodyTable.insertRow(1);
    var descCell = descRow.insertCell(0);
    descCell.setAttribute("colspan", 2);
    var questDescDiv = document.createElement("div");
    questDescDiv.setAttribute("class", "Txt_quest_author");

    var authorHref = document.createElement('a');
    authorHref.href = window.location.protocol + "//" + window.location.host + "/author/" + quest.authorID;
    authorHref.innerHTML = quest.author;

    questDescDiv.innerHTML
        = "Автор: " + authorHref.outerHTML + "<br>"
        + "Категория: " + quest.category + "<br>"
        + "Жанр: " + quest.genre
        + (("description" in quest) ? ("<br>" + "Описание: " + ellipsize(quest.description, 30)) : "");
    descCell.appendChild(questDescDiv);
    questBodyCell.appendChild(questBodyTable);

    var questDiv = document.createElement("div");
    questDiv.setAttribute("class", "quest_block");
    questDiv.appendChild(innerTable);
    questCell.appendChild(questDiv);
}

// utils useful functions
function ellipsize(s, maxSize) {
    if (s.length > maxSize) {
        var t = s.substring(0, maxSize);
        var j = t.lastIndexOf(" ");
        s = t.substring(0, j) + "...";
    }
    return s;
}

function url2id() {
    var url = window.location.href.toString();
    var pos = url.lastIndexOf('/');
    return url.substring(pos + 1);
}