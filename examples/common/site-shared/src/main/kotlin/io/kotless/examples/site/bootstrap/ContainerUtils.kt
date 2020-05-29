package io.kotless.examples.site.bootstrap

import kotlinx.html.*

fun BODY.mainContainer(body: MAIN.() -> Unit) {
    main("container") {
        role = "main"
        body()
    }
}

fun BODY.mainDoc(body: DIV.() -> Unit) {
    mainContainer {
        div("doc") {
            body()
        }
    }
}

fun BODY.mainLanding(body: DIV.() -> Unit) {
    mainContainer {
        div("landing") {
            body()
        }
    }
}

fun FlowContent.row(body: DIV.() -> Unit) {
    div("row") {
        body()
    }
}

fun DIV.smCol(size: Int, body: DIV.() -> Unit) {
    div("col-sm-$size") {
        body()
    }
}

fun FlowContent.simpleCard(classes: String = "", body: DIV.() -> Unit) {
    div("card $classes") {
        div("card-body") {
            body()
        }
    }
}
