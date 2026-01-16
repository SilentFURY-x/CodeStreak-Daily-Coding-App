package com.fury.codestreak.di

import com.fury.codestreak.data.repository.AuthRepositoryImpl
import com.fury.codestreak.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(firebaseAuth: FirebaseAuth): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(app: android.app.Application): com.fury.codestreak.data.local.AppDatabase {
        return androidx.room.Room.databaseBuilder(
            app,
            com.fury.codestreak.data.local.AppDatabase::class.java,
            "codestreak_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideQuestionDao(db: com.fury.codestreak.data.local.AppDatabase): com.fury.codestreak.data.local.QuestionDao {
        return db.questionDao
    }

    @Provides
    @Singleton
    fun provideQuestionRepository(dao: com.fury.codestreak.data.local.QuestionDao): com.fury.codestreak.domain.repository.QuestionRepository {
        return com.fury.codestreak.data.repository.QuestionRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideCodeforcesApi(): com.fury.codestreak.data.remote.CodeforcesApi {
        return retrofit2.Retrofit.Builder()
            .baseUrl("https://codeforces.com/api/")
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(com.fury.codestreak.data.remote.CodeforcesApi::class.java)
    }
}