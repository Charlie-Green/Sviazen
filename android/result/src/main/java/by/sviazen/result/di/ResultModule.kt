package by.sviazen.result.di

import by.sviazen.core.repo.cp.CoPrisonersRepository
import by.sviazen.core.injected.sync.AutomaticSyncManager
import by.sviazen.core.repo.sync.SynchronizationRepository
import by.sviazen.core.injected.sync.SynchronizationScheduler
import by.sviazen.core.injected.sync.SynchronizedDataManager
import by.sviazen.result.repo.cp.CoPrisonersRepositoryImpl
import by.sviazen.result.repo.sync.AutomaticSyncManagerImpl
import by.sviazen.result.repo.sync.SynchronizationRepositoryImpl
import by.sviazen.result.sync.data.SynchronizedDataManagerImpl
import by.sviazen.result.sync.scheduler.SynchronizationSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent


@Module
@InstallIn(ApplicationComponent::class)
interface ResultModule {

    @Binds
    fun synchronizedDataManager(
        impl: SynchronizedDataManagerImpl
    ): SynchronizedDataManager

    @Binds
    fun synchronizationScheduler(
        impl: SynchronizationSchedulerImpl
    ): SynchronizationScheduler

    @Binds
    fun syncFlagHolder(
        impl: AutomaticSyncManagerImpl
    ): AutomaticSyncManager

    @Binds
    fun synchronizationRepository(
        impl: SynchronizationRepositoryImpl
    ): SynchronizationRepository

    @Binds
    fun coPrisonersRepository(
        impl: CoPrisonersRepositoryImpl
    ): CoPrisonersRepository
}