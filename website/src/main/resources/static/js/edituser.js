function setTheme(userid, theme) {
    postUser(userid, {
        theme: theme
    })
}

async function postUser(userid, user) {
    // username: String? = null, avatar: String? = null, theme: String? = null
    window.fetch(`/user/${userid}`, {
        method: 'POST',
        credentials: 'include',
        body: JSON.stringify(user),
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    }).then(r => {
        if (r.ok) {
            window.localStorage.setItem('logged_in', Date.now().toString());
            window.location.reload();
        } else {
            /*const notif = document.createElement("notification");
            notif.innerHtml = "<div class='notification is-danger' onclick='document.getElementById(\"notification\").remove()'>" +
                    " <button class='delete'></button>" +
                    "  Primar lorem ipsum dolor sit amet, consectetur" +
                    "  adipiscing elit lorem ipsum dolor. <strong>Pellentesque risus mi</strong>, tempus quis placerat ut, porta nec nulla. Vestibulum rhoncus ac ex sit amet fringilla. Nullam gravida purus diam, et dictum <a>felis venenatis</a> efficitur. Sit amet," +
                    "  consectetur adipiscing elit" +
                    "</div>"*/
            alert("Something went wrong !")
        }
    });

}