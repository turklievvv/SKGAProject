package com.example.skga.presentation

fun formatTopicName(name: String): String {
    val translitMap = mapOf(
        'А' to "a", 'а' to "a",
        'Б' to "b", 'б' to "b",
        'В' to "v", 'в' to "v",
        'Г' to "g", 'г' to "g",
        'Д' to "d", 'д' to "d",
        'Е' to "e", 'е' to "e",
        'Ё' to "yo", 'ё' to "yo",
        'Ж' to "zh", 'ж' to "zh",
        'З' to "z", 'з' to "z",
        'И' to "i", 'и' to "i",
        'Й' to "y", 'й' to "y",
        'К' to "k", 'к' to "k",
        'Л' to "l", 'л' to "l",
        'М' to "m", 'м' to "m",
        'Н' to "n", 'н' to "n",
        'О' to "o", 'о' to "o",
        'П' to "p", 'п' to "p",
        'Р' to "r", 'р' to "r",
        'С' to "s", 'с' to "s",
        'Т' to "t", 'т' to "t",
        'У' to "u", 'у' to "u",
        'Ф' to "f", 'ф' to "f",
        'Х' to "kh", 'х' to "kh",
        'Ц' to "ts", 'ц' to "ts",
        'Ч' to "ch", 'ч' to "ch",
        'Ш' to "sh", 'ш' to "sh",
        'Щ' to "sch", 'щ' to "sch",
        'Ъ' to "", 'ъ' to "",
        'Ы' to "y", 'ы' to "y",
        'Ь' to "", 'ь' to "",
        'Э' to "e", 'э' to "e",
        'Ю' to "yu", 'ю' to "yu",
        'Я' to "ya", 'я' to "ya"
    )

    return name
        .map { char -> translitMap[char] ?: char.toString() }
        .joinToString("")
        .replace("-", "_")
        .replace(" ", "_")
        .filter { it.isLetterOrDigit() || it == '_' || it == '.' || it == '~' }
        .lowercase()
}