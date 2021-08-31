package net.auoeke.fabricmodgradle.extension.contact

import net.auoeke.fabricmodgradle.extension.json.Container
import java.util.*

class Contact : Container {
    var email: String? = null
    var irc: String? = null
    var homepage: String? = null
    var issues: String? = null
    var sources: String? = null

    override val empty: Boolean get() = with(this) {listOf(this.email, this.irc, this.homepage, this.issues, this.sources)}.all(Objects::isNull)
}
