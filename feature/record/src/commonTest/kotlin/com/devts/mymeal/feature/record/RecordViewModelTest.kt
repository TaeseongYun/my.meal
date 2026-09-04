package com.devts.mymeal.feature.record

import com.devts.mymeal.core.data.photo.PhotoStore
import com.devts.mymeal.core.data.repository.MealRepository
import com.devts.mymeal.core.model.MealEntry
import com.devts.mymeal.core.model.MealType
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

private class FakeRepository : MealRepository {
    val upserts = mutableListOf<MealEntry>()
    var failUpsert = false
    override fun observeByDate(date: LocalDate): Flow<List<MealEntry>> = flowOf(emptyList())
    override suspend fun get(id: String): MealEntry? = upserts.lastOrNull { it.id == id }
    override suspend fun upsert(entry: MealEntry) {
        if (failUpsert) throw IllegalStateException("upsert 실패")
        upserts += entry
    }
    override suspend fun delete(id: String) { upserts.removeAll { it.id == id } }
}

private class FakePhotoStore : PhotoStore {
    val saved = mutableMapOf<String, ByteArray>()
    override suspend fun save(entryId: String, bytes: ByteArray): String {
        saved[entryId] = bytes
        return "/photos/$entryId.jpg"
    }
    override suspend fun delete(entryId: String) { saved.remove(entryId) }
    override fun pathOf(entryId: String): String? = if (entryId in saved) "/photos/$entryId.jpg" else null
}

@OptIn(ExperimentalCoroutinesApi::class)
class RecordViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeRepository()
    private val photoStore = FakePhotoStore()

    private fun viewModel() = RecordViewModel(repository, photoStore, decodePhoto = { null })

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun save_persistsSlotEmojiMemoAndTime() = runTest(dispatcher.scheduler) {
        val vm = viewModel()
        vm.onAction(RecordAction.SelectSlot(RecordSlot.SNACK))
        vm.onAction(RecordAction.SelectFood("🍰"))
        vm.onAction(RecordAction.ToggleRepresentative)
        vm.onAction(RecordAction.EditMemo("맛있었다"))
        vm.onAction(RecordAction.SetTime(LocalTime(15, 30)))
        vm.onAction(RecordAction.Save)
        assertEquals(RecordEffect.Saved, vm.effects.first())

        val entry = repository.upserts.single()
        assertEquals(MealType.SNACK, entry.mealType)
        assertEquals("🍰", entry.foodEmoji)
        assertEquals(true, entry.isRepresentative)
        assertEquals("맛있었다", entry.note)
        assertNull(entry.photoPath) // 사진 없이 저장 허용
        assertEquals(LocalTime(15, 30), vm.uiState.value.time)
    }

    @Test
    fun save_withPhoto_storesPhotoAndPath() = runTest(dispatcher.scheduler) {
        val vm = viewModel()
        vm.onAction(RecordAction.SetPhoto(byteArrayOf(1, 2, 3)))
        vm.onAction(RecordAction.Save)
        assertEquals(RecordEffect.Saved, vm.effects.first())

        val entry = repository.upserts.single()
        assertEquals("/photos/${entry.id}.jpg", entry.photoPath)
        assertTrue(entry.id in photoStore.saved)
    }

    @Test
    fun save_blankMemo_storesNullNote() = runTest(dispatcher.scheduler) {
        val vm = viewModel()
        vm.onAction(RecordAction.EditMemo("   "))
        vm.onAction(RecordAction.Save)
        assertEquals(RecordEffect.Saved, vm.effects.first())
        assertNull(repository.upserts.single().note)
    }

    @Test
    fun upsertFailure_rollsBackSavedPhoto() = runTest(dispatcher.scheduler) {
        repository.failUpsert = true
        val vm = viewModel()
        vm.onAction(RecordAction.SetPhoto(byteArrayOf(9)))
        vm.onAction(RecordAction.Save)
        dispatcher.scheduler.advanceUntilIdle()
        assertTrue(repository.upserts.isEmpty())
        assertTrue(photoStore.saved.isEmpty()) // ADR-R3: 고아 사진 롤백
    }
}
