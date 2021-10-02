package me.cniekirk.flex.data

import me.cniekirk.flex.data.remote.model.Comment
import me.cniekirk.flex.data.remote.model.Gildings

private val any = Any()
private val gildings = Gildings(1, 2)

fun provideComment(): Comment {
    return Comment(
        "1",
        "hello",
        null,
        "charlie",
        "comment",
        "comment",
        false,
        123456L,
        123456L,
        any,
        0,
        null,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        null,
        null,
        null,
        null,
        null,
        gildings,
        "t3_jfdsj",
        "https://i.redd.it/jfishgfudihfguids.jpg",
        null,
        null,
        "t3_hfdusk",
        123,
        "tommyinnit",
        "t2_fhhdjks",
        "r/tommyinnit"
    )
}

fun provideComments(num: Int = 3): List<Comment> {
    val comments = mutableListOf<Comment>()
    repeat(num) { comments.add(provideComment()) }
    return comments
}