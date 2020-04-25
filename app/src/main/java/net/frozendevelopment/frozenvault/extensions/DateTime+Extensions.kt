package net.frozendevelopment.frozenvault.extensions

import org.joda.time.DateTime

fun DateTime.toHumanDateTime(): String = this.toLocalDateTime().toString("MMMM dd, yyyy h:m a z")

fun DateTime.toHumanDate(): String = this.toLocalDate().toString("MMMM dd, yyyy z")
