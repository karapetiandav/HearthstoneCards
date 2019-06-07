package ru.karapetiandav.hearthstonecards.base

import ru.karapetiandav.tinkoffintership.lifecycle.Event

class ShowFilterSheet(val types: List<String>) : Event
class ShowToast(val text: String ): Event