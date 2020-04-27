package net.frozendevelopment.frozenvault.modules.recovery.manage.components.list

import org.joda.time.DateTime

data class RecoveryListState(val items: List<RecoveryKeyCellModel> = listOf(), val filter: Filter = Filter.UNUSED) {

    data class RecoveryKeyCellModel(
        val id: Int,
        val created: DateTime,
        val used: Boolean,
        val usedDate: DateTime?
    ) {
        val includedWithFilters: Set<Filter>
            get() {
                return if (used) setOf(Filter.USED, Filter.ALL)
                else setOf(Filter.UNUSED, Filter.ALL)
            }
    }

    enum class Filter {
        UNUSED,
        USED,
        ALL
    }

}
