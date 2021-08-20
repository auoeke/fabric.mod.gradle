package net.auoeke.fabricmodgradle.contact

import groovy.transform.CompileStatic
import net.auoeke.fabricmodgradle.json.Container

import java.util.function.Predicate

@CompileStatic
class Contact implements Container {
    public String email
    public String irc
    public String homepage
    public String issues
    public String sources

    @Override
    boolean isEmpty() {
        return this.with {[email, irc, homepage, issues, sources].stream().allMatch(Objects::isNull as Predicate)}
    }
}
