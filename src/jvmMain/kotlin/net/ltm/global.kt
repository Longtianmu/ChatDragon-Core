package net.ltm

import androidx.compose.runtime.mutableStateListOf
import io.appoutlet.karavel.Karavel
import net.ltm.adaptors.mirai.BotSets
import net.ltm.contact.Contacts
import net.ltm.ui.MainPage
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Group
import org.jetbrains.exposed.sql.Database

val nav = Karavel(MainPage())
val groupListQQ = mutableStateListOf<Group>()
val contactListQQ = mutableStateListOf<Friend>()
val contactsMap = mutableMapOf<String, MutableMap<String, Contacts>>()

lateinit var dbPath:String
lateinit var dataDir:String
lateinit var userQQBot: BotSets
lateinit var relationQQ: Database
lateinit var chatHistoryQQ: Database