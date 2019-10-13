window.onload = function () {
    let btn = document.getElementById("shorten-button");
    let input = document.getElementById("shorten-input");

    function url(path, params) {
        return path + "?" + Object
            .keys(params)
            .map(function (key) {
                return `${key}=${encodeURIComponent(params[key])}`
            })
            .join("&")
    }

    function urlButtonListener() {
        let span = document.getElementById("shorten-button-text");
        span.textContent = "";
        span.classList.add("fas", "fa-spinner", "fa-spin");

        const http = new XMLHttpRequest();
        http.open('GET', url("/shorten", {value: input.value}), true);

        http.onload = function () {
            if (this.status >= 200 && this.status < 400) {
                span.textContent = "Copy";
                span.classList.remove("fas", "fa-spinner", "fa-spin");

                btn.classList.remove("url-button");
                btn.classList.add("copy-button");

                input.value = this.response;
                input.setAttribute("readonly", "true");
                btn.removeEventListener("click", urlButtonListener);
                btn.addEventListener("click", copyButtonListener);
            }
        };

        http.send();
    }

    function copyButtonListener() {
        input.select();
        input.setSelectionRange(0, 99999);
        document.execCommand("copy")
    }


    btn.addEventListener("click", urlButtonListener);
};


